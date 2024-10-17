package com.exam.serviceImpl;
import com.exam.entity.TestResult;
import com.exam.repository.TestResultRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestResultServiceImpl {
    private static final String OUTPUT_CSV_PATH = "TestResult.csv";
    @Autowired
    private TestResultRepository testResultRepository;

    /**
     * Delete old test results by testId and save the updated ones.
     */
    @Transactional // Ensure this method runs in a transactional context
    public List<TestResult> saveUpdatedTestResults(MultipartFile file, Long testId, String startTime) throws Exception {

        testResultRepository.deleteByTestId(testId);

        return saveTestResults(file,testId,startTime);
    }
    public List<TestResult> saveTestResults(MultipartFile file, Long testId, String startTime) throws Exception {
        CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
        List<TestResult> testResults = csvReader.readAll().stream()
                .skip(1) // Skip the header row
                .map(data -> {
                    TestResult testResult = new TestResult();
                    testResult.setSubmittedTime(data[1]);
                    testResult.setName(data[2]);
                    testResult.setUserId(data[3]);
                    testResult.setMarks(data[4]);
                    testResult.setResult(data[5]);
//                    testResult.setAnswerSheetLink(data[4]);
                    testResult.setTestId(testId);
//                    testResult.setStartTime(startTime);

                    // Calculate time duration (difference between startTime and submittedTime)
                    long timeDuration = calculateTimeDuration(startTime, testResult.getSubmittedTime()); // Implement this method
                    testResult.setTimeDuration(timeDuration);
                    testResult.setCreateDate(new Date());
                    return testResult;
                })
                .collect(Collectors.toList());

        // Calculate rank based on minimum timeDuration and maximum score (assuming result is the score)
        testResults = calculateRank(testResults);


        createOutputCSV(testResults);

        // Save to database
        return testResultRepository.saveAll(testResults);
    }

    private long calculateTimeDuration(String startTime, String submittedTime) {
        // Implement time difference calculation between startTime and submittedTime
        return 0L; // Placeholder logic
    }

    private List<TestResult> calculateRank(List<TestResult> testResults) {
        testResults.sort(Comparator.comparing(TestResult::getTimeDuration)
                .thenComparing(Comparator.comparing(TestResult::getMarks).reversed()));

        for (int i = 0; i < testResults.size(); i++) {
            testResults.get(i).setRank(i + 1);
        }
        return testResults;
    }

    public List<TestResult> getTestResultsByUserId(String userId) {
        return testResultRepository.findByUserId(userId);
    }

    public List<TestResult> getTestResultsByTestId(Long testId) {
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
                        result.getMarks(),
                        result.getResult(),
                        String.valueOf(result.getTimeDuration()),
                        String.valueOf(result.getRank())
                };
                writer.writeNext(row);
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
