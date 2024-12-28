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
        TestLink testLink = testLinkRepository.findById(testId)
                .orElseThrow(() -> new Exception("TestLink not found for id: " + testId));
        CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
        boolean isLiveTest = testLink.getTestType().equalsIgnoreCase("RankBooster") || testLink.getTestType().equalsIgnoreCase("NormalLive");
        try {
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            // Parse the time strings into LocalDateTime objects
            LocalDateTime startTime = LocalDateTime.parse(testLink.getStartTime().toString(), formatter1);
            List<TestResult> testResults = csvReader.readAll().stream()
                    .skip(1) // Skip the header row
                    .filter(data -> userRepository.findByUserRollNo(data[3]).isPresent()) // Skip rows where user is not present
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
                           testResult.setUserEmail(user.get().getUserMailId());
                           testResult.setUserId(user.get().getUserId());
                           testResult.setUserSchoolOrCollegeName(user.get().getUserSchoolOrCollege());

                           System.out.println(startTime);
                           System.out.println(testResult.getSubmittedTime());
                           if(isLiveTest){
                               LocalDateTime submitTime = LocalDateTime.parse(testResult.getSubmittedTime(), formatter2);
                               long timeDuration = Duration.between(startTime, submitTime).toMinutes();// Implement this method
                               testResult.setTimeDuration(timeDuration);
                           }else{
                               testResult.setTimeDuration(0L);
                           }

                        return testResult;
                    })
                    .collect(Collectors.toList());

            // Calculate rank based on minimum timeDuration and maximum score (assuming result is the score)
            if(isLiveTest){
                testResults = calculateRank(testResults);
            }
              //Need analysis
//            createOutputCSV(testResults);

            if(testLink.getTestType().equalsIgnoreCase("Rankbooster")) {
                // Calculate the total marks from TestLink entity using testId
                double totalMarks = testLink.getTestTotalMarks(); // Assuming there's a getTotalMarks() method in TestLink

                // Calculate the average marks of all students
                int avgMarks = (int) testResults.stream()
                        .mapToDouble(TestResult::getMarks)
                        .average()
                        .orElse(0.0);

                for (TestResult result : testResults) {
                    int studentMarks = result.getMarks();
                    // Calculate the percentage difference
                    double percentageDifference = ((avgMarks - studentMarks) / (double) avgMarks) * 100;

                    // Set the percentage difference in the result
                    result.setDifferenceForRating((int) Math.round(percentageDifference)); // Store as an integer

                    // Optionally, log or return the percentage difference for debugging
                    System.out.println("Student Marks: " + studentMarks + ", Avg Marks: " + avgMarks + ", Percentage Difference: " + percentageDifference + "%");

                }
            }

            int rowsUpdted = testLinkRepository.updateResultFileUplodedByTestId(testId, true);
            System.out.println("rows updated In TestReslutServiceImpl : " + rowsUpdted);
            // Save to database
            return testResultRepository.saveAll(testResults);

            // Enhancement add resultuploadTime in to testLink

        }catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
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

    public List<TestResult> getTestResultsByTestId(String testId,boolean forAdmin) throws Exception {
        TestLink testLink = testLinkRepository.findById(testId)
                .orElseThrow(() -> new Exception("TestLink not found for id: " + testId));
        System.out.println("testLink available" + testLink);
        if(forAdmin){
            return testResultRepository.findByTestId(testId);
        }
        if(!testLink.isResultPublish()){
            throw new Exception("Result not published yet for Test ID: " + testId);
        }
        return testResultRepository.findByTestId(testId);
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
//        updateUserRankings();
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
