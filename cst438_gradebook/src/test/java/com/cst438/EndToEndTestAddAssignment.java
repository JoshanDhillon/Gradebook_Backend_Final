package com.cst438;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

import static org.junit.jupiter.api.Assertions.*;

/*
 * This example shows how to use selenium testing using the web driver
 * with Chrome browser.
 *
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 *
 *  In SpringBootTest environment, the test program may use Spring repositories to
 *  setup the database for the test and to verify the result.
 */

@SpringBootTest
public class EndToEndTestAddAssignment {

    public static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedriver.exe";

    public static final String URL = "http://localhost:3000";

    public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb";

    public static final int TEST_COURSE_ID = 999001;

    public static final String TEST_ASSIGNMENT_NAME = "NEW TEST 1";

    public static final String TEST_ASSIGNMENT_DATE = "2022-05-01";

    public static final int SLEEP_DURATION = 1000; // 1 second.

    /*
     * When running in @SpringBootTest environment, database repositories can be used
     * with the actual database.
     */

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Test
    public void addAssigmentTest() throws Exception {

        /*
         * if assignment is already exist, then delete the assignment.
         */
        List<Assignment> assignments = assignmentRepository.findAssignmentsByCourseId(TEST_COURSE_ID);
        for (Assignment assignment : assignments){
            if (assignment.getName().equals(TEST_ASSIGNMENT_NAME)){
                assignmentRepository.delete(assignment);
                break;
            }
        }

        // set the driver location and start driver
        //@formatter:off
        // browser	property name 				Java Driver Class
        // edge 	webdriver.edge.driver 		EdgeDriver
        // FireFox 	webdriver.firefox.driver 	FirefoxDriver
        // IE 		webdriver.ie.driver 		InternetExplorerDriver
        //@formatter:on

        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        WebDriver driver = new ChromeDriver();
        // Puts an Implicit wait for 10 seconds before throwing exception
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        try {

            driver.get(URL);
            Thread.sleep(SLEEP_DURATION);

            // select the first of the radio buttons on the list of semesters page.

            WebElement we = driver.findElements(By.xpath("(//input[@type='radio'])")).get(0);
            we.click();

            // Locate and click "Add" button which is second anchor tag in the page

            driver.findElements(By.xpath("//a")).get(1).click();
            Thread.sleep(SLEEP_DURATION);

            // Enter assignment name and due date
            driver.findElements(By.xpath("//input")).get(0).sendKeys(TEST_ASSIGNMENT_NAME);     // Get the text field which is first input field
            driver.findElements(By.xpath("//input")).get(1).clear();     // Get the date field which is second input field and clear the initial values
            driver.findElements(By.xpath("//input")).get(1).sendKeys(TEST_ASSIGNMENT_DATE);     // Get the date field which is second input field and send the du date value

            // Locate and click "Add" button which is the first and only button on the page to Add the assignment for selected course.
            driver.findElement(By.xpath("//button")).click();
            Thread.sleep(SLEEP_DURATION);

            // enter course no and click Add button

            driver.get(URL);
            Thread.sleep(SLEEP_DURATION*2);

            /*
             * verify that new assignment shows.
             * get the title of all assignment listed.
             */

            List<WebElement> elements  = driver.findElements(By.xpath("(//div[@data-field='assignmentName'])"));
            boolean found = false;
            for (WebElement e : elements) {
                //WebElement titleElement = e.findElement(By.xpath("//div[@class='MuiDataGrid-row']"));
                System.out.println(e.getText()); // for debug
                if (e.getText().equals(TEST_ASSIGNMENT_NAME)) {
                    found=true;
                    break;
                }
            }
            assertTrue( found, "Assignment added but not listed.");

            // verify that enrollment row has been inserted to database.

            //assertNotNull(e, "Course enrollment not found in database.");

        } catch (Exception ex) {
            throw ex;
        } finally {

            // clean up database.

            assignments = assignmentRepository.findAssignmentsByCourseId(TEST_COURSE_ID);
            for (Assignment assignment : assignments){
                if (assignment.getName().equals(TEST_ASSIGNMENT_NAME)){
                    assignmentRepository.delete(assignment);
                    break;
                }
            }

            driver.quit();
        }
    }
}
