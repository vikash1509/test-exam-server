package com.exam.serviceImpl;

import com.exam.controller.UserController;
import com.exam.entity.TestLink;
import com.exam.entity.User;
//import com.exam.entity.UserRole;
import com.exam.entity.UserType;
//import com.exam.repository.RoleRepository;
import com.exam.repository.TestLinkRepository;
import com.exam.repository.UserRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class UserServiceImpl {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestLinkRepository testLinkRepository;
    @Autowired
    private EmailServiceImpl emailService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  //  public User createUser(User newUser, Set<UserRole> userRoles) throws Exception {
     public User createUser(User newUser) throws Exception {
        long startTime = System.currentTimeMillis(); // Start time

        logger.info("Starting createUser method");
        String emailId = newUser.getUserMailId();
        if (emailId == null || !emailId.contains("@")) {
            throw new IllegalArgumentException("Invalid email ID");
        }
        checkTypeOfUser(newUser);
        newUser.setUserName(emailId.substring(0, emailId.indexOf("@")));

        // Check if the userMailId already exists
        Optional<User> existingUser = userRepository.findByUserMailId(newUser.getUserMailId());
        if (existingUser.isPresent()) {
            throw new Exception("User with the same email already exists.");
        }

        newUser.setUserId(String.valueOf(UUID.randomUUID()));

        // Generate unique rollNo as a 7-digit random number with prefix MP
        String rollNo;
        do {
            rollNo = "MP" + String.format("%07d", new Random().nextInt(10000000));
            logger.info("Generated roll number: {}", rollNo);
        } while (userRepository.findByUserRollNo(rollNo).isPresent());
        newUser.setUserRollNo(rollNo);

        // Save roles
//        for (UserRole ur : userRoles) {
//            roleRepository.save(ur.getRole());
//        }
//        newUser.getUserRoles().addAll(userRoles);

        // Set default user rating and rank
        newUser.setUserRating(1000); // Default rating
        newUser.setUserRank(0);      // Default rank can be 0
        newUser.setCreateDate(new Date());
        newUser.setUserLastLogin(LocalDateTime.now());

        long endTime = System.currentTimeMillis(); // End time
        logger.info("createUser method completed in {} ms", (endTime - startTime)); // Log time taken

        return userRepository.save(newUser);
    }

    public Optional<User> getUser (String userId){
        Optional<User> existingUser = userRepository.findByUserId(userId);
        System.out.println(existingUser);
        return existingUser;
    }

    private void checkTypeOfUser(User user) throws Exception {
        String userType = user.getUserType().toUpperCase();

        switch (userType) {
            case "SCHOOLSTUDENT":
                user.setUserType(UserType.SCHOOLSTUDENT.name());
                break;
            case "COLLEGESTUDENT":
                user.setUserType(UserType.COLLEGESTUDENT.name());
                break;
            case "SCHOOLTEACHER":
                user.setUserType(UserType.SCHOOLTEACHER.name());
                break;
            case "COLLEGETEACHER":
                user.setUserType(UserType.COLLEGETEACHER.name());
                break;
            case "ADMIN":    // added only for production
                user.setUserType("ADMIN");
                break;
            default:
                throw new Exception("User is not available for this type");
        }
    }


    public User loginUser(String email, String userPassword) throws Exception {
        // Find user by username or email
        Optional<User> optionalUser = userRepository.findByUserMailId(email);

        if (optionalUser.isEmpty()) {
            throw new Exception("User not found with provided email.");
        }

        User user = optionalUser.get();

        // Validate password
        if (!user.getUserPassword().equals(userPassword)) {
            throw new Exception("Invalid password. Please try again.");
        }
        userRepository.updateUserLastLogin(user.getUserId(),LocalDateTime.now());
        // If password matches, return the user
        return user;
    }

    public String automateQuizForm(String userName,String userId,String testId) {

        Optional<TestLink> testLink = testLinkRepository.findById(testId);
        String testUrl = null;
        if(testLink.isPresent()){
             testUrl = testLink.get().getTestLink();
        }else{
            //customize exception
            return "testLink not found";
        }

        // Initialize WebDriver
        WebDriverManager.chromedriver().setup();
        System.out.println("in-funtion---0");

        WebDriver driver = new ChromeDriver();
        System.out.println("in-funtion---1");
        try {
            // Open the quiz page URL
            driver.get(testUrl);
            System.out.println("in-funtion---2");

            // Find the name input field and fill it with the passed userName
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Wait for up to 10 seconds
            WebElement userNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-Name"))); // Replace with actual ID
            userNameField.sendKeys(userName);
//            WebElement userNameField = driver.findElement(By.id("input-Name")); // Replace with actual ID
//            userNameField.sendKeys(userName);
            WebElement userIdField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-userId"))); // Replace with actual ID
            userIdField.sendKeys(userId);
//            WebElement userIdField = driver.findElement(By.id("input-userId")); // Replace with actual ID
//            userIdField.sendKeys(userId);
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String dateTimeString = currentDateTime.format(formatter);
            WebElement userStartTimeField = driver.findElement(By.id("input-startTime")); // Replace with actual ID
            userStartTimeField.sendKeys(dateTimeString);
            System.out.println("in-funtion---3");


//            WebElement submitButton = driver.findElement(By.id("Start Quiz")); // Replace with actual ID
//            submitButton.click();
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("Start Quiz"))); // Replace with actual ID
            submitButton.click();
            System.out.println("in-funtion---5");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return "test link is not found  ";
        }
        return "test is successfully automated : ";
    }


    private final SecureRandom random = new SecureRandom();

    public void generateAndSendOtp(String email) {
        User user = userRepository.findByUserMailId(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String otp = String.format("%05d", random.nextInt(100000));
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5)); // OTP valid for 5 minutes
        userRepository.save(user);

        emailService.sendEmail(email, "Your OTP Code", "Your OTP is: " + otp);
    }

    public void verifyOtp(String email, String otp) {
        User user = userRepository.findByUserMailId(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getOtp() == null || user.getOtpExpiry() == null ||
                user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP is expired or invalid.");
        }

        if (!user.getOtp().equals(otp)) {
            throw new IllegalArgumentException("Incorrect OTP.");
        }

        user.setOtp(null); // Clear OTP after successful verification
        user.setOtpExpiry(null);
        userRepository.save(user);
    }

    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByUserMailId(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getUserPassword().equals(newPassword)) {
            throw new IllegalArgumentException("New password cannot be the same as the old password.");
        }

        user.setUserPassword(newPassword); // Use a secure hashing algorithm in production
        userRepository.save(user);
    }
}
