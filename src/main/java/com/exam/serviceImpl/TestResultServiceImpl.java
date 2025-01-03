package com.exam.serviceImpl;
import com.exam.controller.TestResultController;
import com.exam.entity.TestLink;
import com.exam.entity.TestResult;
import com.exam.entity.User;
import com.exam.repository.TestResultRepository;
import com.exam.repository.TestLinkRepository;
import com.exam.repository.UserRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.SQLOutput;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestResultServiceImpl {
    private static final String OUTPUT_CSV_PATH = "TestResult.csv";
    @Autowired
    private TestResultRepository testResultRepository;
    @Autowired
    private TestLinkRepository testLinkRepository;
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(TestResultController.class);
    /**
     * Delete old test results by testId and save the updated ones.
     */
    @Transactional // Ensure this method runs in a transactional context
    public List<TestResult> saveUpdatedTestResults(MultipartFile file, String testId) throws Exception {
        testResultRepository.deleteByTestId(testId);
        return saveTestResults(file,testId);
    }
    @Transactional
    public List<TestResult> saveTestResults(MultipartFile file, String testId) throws Exception {
        logger.info("Entering saveTestResults with testId: {}", testId);

        // Retrieve the TestLink associated with the testId
        TestLink testLink = testLinkRepository.findById(testId)
                .orElseThrow(() -> {
                    logger.error("TestLink not found for id: {}", testId);
                    return new Exception("TestLink not found for id: " + testId);
                });

        logger.debug("Retrieved TestLink: {}", testLink);

        CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
        boolean isLiveTest = testLink.getTestType().equalsIgnoreCase("RankBooster") || testLink.getTestType().equalsIgnoreCase("NormalLive");
        logger.info("Test type is '{}'. Is live test: {}", testLink.getTestType(), isLiveTest);

        try {
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(testLink.getStartTime().toString(), formatter1);
            logger.debug("Parsed start time: {}", startTime);

            // Read and process CSV file
            List<TestResult> testResults = csvReader.readAll().stream()
                    .skip(1) // Skip the header row
                    .filter(data -> {
                        boolean userExists = userRepository.findByUserRollNo(data[3]).isPresent();
                        if (!userExists) {
                            logger.warn("Skipping row as user with roll number {} is not found", data[3]);
                        }
                        return userExists;
                    })
                    .filter(data -> {
                        boolean alreadyExists = testResultRepository.existsByAnswerSheetLink(data[data.length - 1]);
                        if (alreadyExists) {
                            logger.warn("Skipping row as answer sheet link {} already exists in database", data[data.length - 1]);
                        }
                        return !alreadyExists;
                    })
                    .map(data -> {
                        Optional<User> user = userRepository.findByUserRollNo(data[3]);
                        TestResult testResult = new TestResult();
                        testResult.setSubmittedTime(data[1]);
                        testResult.setName(data[2]);
                        testResult.setUserRollNo(data[3]);
                        testResult.setMarks(Integer.parseInt(data[4]));
                        testResult.setResult(data[5]);
                        testResult.setAnswerSheetLink(data[data.length - 1]);
                        testResult.setTestId(testId);
                        user.ifPresent(u -> {
                            testResult.setUserEmail(u.getUserMailId());
                            testResult.setUserId(u.getUserId());
                            testResult.setUserSchoolOrCollegeName(u.getUserSchoolOrCollege());
                        });

                        if (isLiveTest) {
                            LocalDateTime submitTime = LocalDateTime.parse(testResult.getSubmittedTime(), formatter2);
                            long timeDuration = Duration.between(startTime, submitTime).toMinutes();
                            testResult.setTimeDuration(timeDuration);
                            logger.debug("Calculated time duration: {} minutes for user roll number {}", timeDuration, data[3]);
                        } else {
                            testResult.setTimeDuration(0L);
                        }
                        return testResult;
                    })
                    .collect(Collectors.toList());

            logger.info("Processed {} test results from CSV", testResults.size());

            // Calculate ranks for live tests
            if (isLiveTest) {
                logger.info("Calculating ranks for live test");
                testResults = calculateRank(testResults);
            } else {
                logger.info("Skipping rank calculation for non-live test");
            }

            // Additional processing for "RankBooster" test type
            if (testLink.getTestType().equalsIgnoreCase("Rankbooster")) {
                double totalMarks = testLink.getTestTotalMarks();
                int avgMarks = (int) testResults.stream()
                        .mapToDouble(TestResult::getMarks)
                        .average()
                        .orElse(0.0);
                logger.debug("Total marks: {}, Average marks: {}", totalMarks, avgMarks);

                for (TestResult result : testResults) {
                    int studentMarks = result.getMarks();
                    double percentageDifference = ((avgMarks - studentMarks) / (double) avgMarks) * 100;
                    result.setDifferenceForRating((int) Math.round(percentageDifference));
                    logger.debug("Calculated percentage difference for user {}: {}", result.getUserRollNo(), percentageDifference);
                }
            }

            // Update TestLink to indicate the results file is uploaded
            int rowsUpdated = testLinkRepository.updateResultFileUplodedByTestId(testId, true);
            logger.info("Updated result file uploaded status for testId: {}. Rows affected: {}", testId, rowsUpdated);

            // Save test results to the database
            List<TestResult> savedResults = testResultRepository.saveAll(testResults);
            logger.info("Saved {} test results to the database", savedResults.size());

            logger.info("Exiting saveTestResults with testId: {}", testId);
            return savedResults;

        } catch (Exception e) {
            logger.error("Error occurred while saving test results for testId: {}: {}", testId, e.getMessage());
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }



    private List<TestResult> calculateRank(List<TestResult> testResults) {
        if (testResults == null || testResults.isEmpty()) {
            // Log a message or handle this scenario accordingly
            logger.warn("Test results list is null or empty, cannot calculate rank.");
            return Collections.emptyList();  // Return an empty list if there are no results
        }

        // Sort the test results by time duration and then by marks in descending order
        testResults.sort(Comparator.comparing(TestResult::getTimeDuration, Comparator.nullsLast(Long::compare))
                .thenComparing(Comparator.comparing(TestResult::getMarks, Comparator.nullsLast(Comparator.reverseOrder()))));

        // Assign ranks to each test result
        for (int i = 0; i < testResults.size(); i++) {
            TestResult result = testResults.get(i);
            if (result != null) {
                result.setRank(i + 1);
            } else {
                logger.warn("Encountered null test result at index {}", i);
            }
        }

        return testResults;
    }


    public List<TestResult> getTestResultsByUserId(String userId) {
        System.out.println(userId);
        return testResultRepository.findByUserId(userId);
    }

    public List<TestResult> getTestResultsByTestId(String testId, boolean forAdmin) throws Exception {
        logger.info("Entering getTestResultsByTestId with testId: {}, forAdmin: {}", testId, forAdmin);

        // Fetch the TestLink associated with the testId
        TestLink testLink = testLinkRepository.findById(testId)
                .orElseThrow(() -> {
                    logger.error("TestLink not found for testId: {}", testId);
                    return new Exception("TestLink not found for id: " + testId);
                });

        logger.debug("Retrieved TestLink: {}", testLink);

        if (forAdmin) {
            logger.info("Admin request detected, retrieving all test results for testId: {}", testId);
            List<TestResult> results = testResultRepository.findByTestId(testId);
            logger.info("Found {} test results for testId: {}", results.size(), testId);
            return results;
        }

        if (!testLink.isResultPublish()) {
            logger.warn("Results not published for testId: {}", testId);
            throw new Exception("Result not published yet for Test ID: " + testId);
        }

        logger.info("Results published for testId: {}, retrieving results for users.", testId);
        List<TestResult> results = testResultRepository.findByTestId(testId);
        logger.info("Found {} test results for testId: {}", results.size(), testId);

        logger.info("Exiting getTestResultsByTestId with testId: {}", testId);
        return results;
    }


    private void createOutputCSV(List<TestResult> testResults) throws Exception {
        // Create CSVWriter to write the output CSV file
        try (CSVWriter writer = new CSVWriter(new FileWriter(OUTPUT_CSV_PATH))) {
            // Write header
            String[] header = {"User ID", "Name", "Submitted Time", "Marks", "Result", "Time Duration", "Rank"};
            writer.writeNext(header);

            // Write each TestResult to CSV
            for (TestResult result : testResults) {
                String[] row = {
                        result.getUserId(),
                        result.getName(),
                        result.getSubmittedTime(),
                        String.valueOf(result.getMarks()),
                        result.getResult(),
                        String.valueOf(result.getTimeDuration()),
                        String.valueOf(result.getRank())
                };
                writer.writeNext(row);
            }
        }
    }


    @Transactional
    public void updateUserRatings(String testId) {
        List<TestResult> testResults = testResultRepository.findByTestId(testId);
       for (TestResult result : testResults) {
            String userId = result.getUserId();
            int marksDifference = result.getDifferenceForRating();

            Optional<User> optionalUser = userRepository.findByUserId(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                int currentRating = user.getUserRating();
                int updatedRating = calculateNewRating(currentRating, marksDifference);
               int rowsupdated =  userRepository.updateUserRating(userId,updatedRating);
                System.out.println("in ---- updateUserRatings----3   rowsupdated" + rowsupdated + " " + userId + " "+updatedRating + "MM" + marksDifference);
            }
            // If user is not found, skip to next iteration without throwing an error
        }
        // After updating all user ratings, update the rank for all users based on descending order of rating
        //updateUserRankings();
    }

    // Method to calculate new rating based on existing rating and marks difference
    private int calculateNewRating(int currentRating, int marksDifference) {
        // Implement your rating calculation logic here
        return currentRating + marksDifference;  // Example logic
    }

    // Method to update user ranks based on rating
    private void updateUserRankings() {
        // Fetch all users and sort by rating in descending order
        System.out.println("in ---- updateUserRankings");
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "userRating"));
        System.out.println("in ---- updateUserRankings---2" + users);

        // Assign ranks based on sorted order
        for (int i = 0; i < users.size(); i++) {
            System.out.println("size--" + users.size());

            User user = users.get(i);
            if (user != null) {
                user.setUserRank(i + 1);
                userRepository.save(user); // Save the updated rank
            } else {
                System.out.println("User at index " + i + " is null.");
            }
        }
    }
    /**
     * Return the generated output CSV file.
     */
    public File getOutputCSVFile() {
        return new File(OUTPUT_CSV_PATH);
    }
}
