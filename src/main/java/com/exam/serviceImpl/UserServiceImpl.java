package com.exam.serviceImpl;

import com.exam.entity.TestLink;
import com.exam.entity.User;
import com.exam.entity.UserRole;
import com.exam.repository.RoleRepository;
import com.exam.repository.TestLinkRepository;
import com.exam.repository.UserRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;


@Service
public class UserServiceImpl {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TestLinkRepository testLinkRepository;

    public User createUser(User newUser , Set<UserRole> userRoles) throws Exception {
        // Check if the userId already exists
        Optional<User> existingUser = userRepository.findById(newUser.getUserId());

        if (existingUser.isPresent()) {
            throw new Exception("User with userId " + newUser.getUserId() + " already exists.");
        }else{
            for(UserRole ur:userRoles){
                roleRepository.save(ur.getRole());
            }
            newUser.getUserRoles().addAll(userRoles);
        }

        // Set default user rating and rank
        newUser.setUserRating(1000); // Default rating
        newUser.setUserRank(0);      // Default rank can be 0

        return userRepository.save(newUser);
    }

    public String automateQuizForm(String userName,String userId,Long testId) {

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
}
