package com.exam.serviceImpl;

import com.exam.Constants.AppConstants;
import com.exam.entity.TestLink;
import com.exam.entity.TestType;
import com.exam.entity.User;
import com.exam.repository.TestLinkRepository;
import com.exam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class TestLinkServiceImpl {
    @Autowired
    private TestLinkRepository testInfoRepository;

    @Autowired
    private UserRepository userRepository;

    public TestLink createTest(TestLink testLink,String userId) throws Exception{

        if(null==userId){
            throw new IllegalArgumentException("Please enter  User ID ");
        }

        if ((TestType.RankBooster.name().equalsIgnoreCase(testLink.getTestType())
                || TestType.PRACTICE.name().equalsIgnoreCase(testLink.getTestType())
                || TestType.NORMALLIVE.name().equalsIgnoreCase(testLink.getTestType()))
                && testLink.getTestLink().startsWith(AppConstants.QUIZZORY_URL_PREFIX)) {
            if(null!=testLink.getTestFor() && (AppConstants.TEST_FOR_SCHOOL_STUDENT.equalsIgnoreCase(testLink.getTestFor())
                    || AppConstants.TEST_FOR_COLLEGE_STUDENT.equalsIgnoreCase(testLink.getTestFor())
                    || AppConstants.TEST_FOR_PUBLIC.equalsIgnoreCase(testLink.getTestFor()))){
            }else{
                throw new IllegalArgumentException("Please enter Correct TestParticipant category");
            }
        }else{
            throw new IllegalArgumentException("Please enter Correct TestType Or Correct TestLink");
        }

        //add logic for testLink entites should not be null

        // Check if the start time is after the current time
        if (testLink.getStartTime() == null || (testLink.getStartTime() != null && testLink.getStartTime().isBefore(LocalDateTime.now()))) {
            System.out.println("Test start time must be in the future.");
            return null;
        }

        // Check if end time is at least 5 minutes after start time
        if ((TestType.RankBooster.name().equalsIgnoreCase(testLink.getTestType())
                || TestType.NORMALLIVE.name().equalsIgnoreCase(testLink.getTestType()))) {
            if(null != testLink.getEndTime()) {
                long startTimeInMillis = testLink.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long endTimeInMillis = testLink.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long diffInMinutes = (endTimeInMillis - startTimeInMillis) / (1000 * 60); // Convert milliseconds to minutes
                if (diffInMinutes < 5) {
                    throw new IllegalArgumentException("test duration will be minimum 5 minutes");
                }
                if(!(testLink.getTimeDuration()==diffInMinutes)){
                    throw new IllegalArgumentException("Please enter correct test Time duration or EndTime");
                }
            }else{
                throw new IllegalArgumentException("Please enter END TIME ");
            }
        }
        //Add logic for check userType and add TestProviderName (userName / OrganisationName)

       Optional<User> user =  userRepository.findByUserId(userId);
       if(user.isEmpty() || !Objects.equals(user.get().getUserType(), "ADMIN")){
           throw new IllegalArgumentException("Invalid User ID Or UserType");
       }

        // Generate unique test ID
        testLink.setCreateDate(new Date());
        testLink.setTestProviderName(null!=user.get().getUserOrganisation()?user.get().getUserOrganisation():user.get().getUserSchoolOrCollege());

        testLink.setUserId(userId);
        // Existing conditions for test type and test link
        return testInfoRepository.save(testLink);

    }

    public List<TestLink> getTestLink(String testType){
        return testInfoRepository.findTestIdsByTestTypeAndHideTestInfoFalse(testType);
    }

    public List<TestLink> getAllTestLink(String testType){
        return testInfoRepository.findBytestType(testType);
    }

    public boolean updateHideTestInfo(String testId, boolean hideTestInfo) {
        int rowsUpdated = testInfoRepository.updateHideTestInfoByTestId(testId, hideTestInfo);
        return rowsUpdated > 0; // Return true if at least one row was updated
    }

    public boolean publishResult(String testId, boolean resultPublish) {
        int rowsUpdated = testInfoRepository.updateResultPublishByTestId(testId, resultPublish);
        return rowsUpdated > 0; // Return true if at least one row was updated
    }

}
