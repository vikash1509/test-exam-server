package com.exam.serviceImpl;
import com.exam.entity.TestLink;
import com.exam.entity.TestResult;
import com.exam.entity.User;
import com.exam.repository.TestResultRepository;
import com.exam.repository.TestLinkRepository;
import com.exam.repository.UserRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
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
        CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
        TestLink testLink = testLinkRepository.findById(testId)
                .orElseThrow(() -> new Exception("TestLink not found for id: " + testId));
        boolean isLiveTest = Objects.equals(testLink.getTestType(), "RankBooster");
        try {
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            // Parse the time strings into LocalDateTime objects
            LocalDateTime startTime = LocalDateTime.parse(testLink.getStartTime().toString(), formatter1);
            List<TestResult> testResults = csvReader.readAll().stream()
                    .skip(1) // Skip the header row
                    .map(data -> {
                        Optional<User> user = userRepository.findByUserRollNo(data[3]);
                        TestResult testResult = new TestResult();
                       if(user.isPresent()){
                           testResult.setSubmittedTime(data[1]);
                           testResult.setName(data[2]);
                           testResult.setUserRollNo(data[3]);
                           testResult.setMarks(Integer.parseInt(data[4]));
                           testResult.setResult(data[5]);
                           testResult.setAnswerSheetLink(data[data.length - 1]);
                           testResult.setTestId(testId);
                           testResult.setUserEmail(user.get().getUserMailId());

                           System.out.println(startTime);
                           System.out.println(testResult.getSubmittedTime());
                           if(isLiveTest){
                               LocalDateTime submitTime = LocalDateTime.parse(testResult.getSubmittedTime(), formatter2);
                               long timeDuration = Duration.between(startTime, submitTime).toMinutes();// Implement this method
                               testResult.setTimeDuration(timeDuration);
                           }else{
                               testResult.setTimeDuration(0L);
                           }
                       }
                        return testResult;
                    })
                    .collect(Collectors.toList());

            System.out.println(testResults.size());
            // Calculate rank based on minimum timeDuration and maximum score (assuming result is the score)
            testResults = calculateRank(testResults);
              //Need analysis
//            createOutputCSV(testResults);
            System.out.println(testResults.size());
            // Calculate the total marks from TestLink entity using testId
            double totalMarks = testLink.getTestTotalMarks(); // Assuming there's a getTotalMarks() method in TestLink
            int rowsUpdted = testLinkRepository.updateResultFileUplodedByTestId(testId, true);
            System.out.println("rows updated In TestReslutServiceImpl : " + rowsUpdted);

            // Calculate the average marks of all students
            int avgMarks = (int) testResults.stream()
                    .mapToDouble(TestResult::getMarks)
                    .average()
                    .orElse(0.0);

            for (TestResult result : testResults) {
                int studentMarks = result.getMarks();
                int marksDifference = (int) (((studentMarks - avgMarks) / totalMarks) * 100);
                result.setMarksDifference(marksDifference);
            }

            // Save to database
            return testResultRepository.saveAll(testResults);

            // Enhancement add resultuploadTime in to testLink

        }catch (Exception e) {
            throw new IllegalArgumentException("Invalid time format: " + e.getMessage());
        }

    }


    private List<TestResult> calculateRank(List<TestResult> testResults) {
        for (int i = 0; i < testResults.size(); i++) {
            System.out.println(testResults.get(i).getUserRollNo());
            System.out.println(testResults.get(i).getUserEmail());

        }
        testResults.sort(Comparator.comparing(TestResult::getTimeDuration)
                .thenComparing(Comparator.comparing(TestResult::getMarks).reversed()));

        for (int i = 0; i < testResults.size(); i++) {
            testResults.get(i).setRank(i + 1);
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
        // Fetch test results for the given testId
        List<TestResult> testResults = testResultRepository.findByTestId(testId);

        // Iterate over each test result and update the user's rating
        for (TestResult result : testResults) {
            String userId = result.getUserId();
            int marksDifference = result.getMarksDifference();

            // Fetch the user entity by userId
            Optional<User> optionalUser = userRepository.findByUserId(userId);
            if (optionalUser.isPresent()) {
                System.out.println("in ---- updateUserRatings");

                User user = optionalUser.get();
                System.out.println("in ---- updateUserRatings----2");

                // Update user's rating based on marksDifference logic
                int currentRating = user.getUserRating();
                int updatedRating = calculateNewRating(currentRating, marksDifference);
                user.setUserRating(updatedRating);

                // Save updated user entity
                userRepository.save(user);
            }
            // If user is not found, skip to next iteration without throwing an error
        }

        // After updating all user ratings, update the rank for all users based on descending order of rating
        updateUserRankings();
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
