package com.seleniumtask;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.FileReader;
import java.util.List;

public class DarazAutomateTest {
    WebDriver driver;
    ExtentSparkReporter htmlReports;
    ExtentReports extentReports;
    ExtentTest darazTest;
    final static String darazUrl = "https://www.daraz.com.np/";

    //Name displayed after logged in :: Example: USERNAME'S ACCOUNT
    String loggedAccountName = "";

    @BeforeSuite
    public void setUpBrowser(){
        try {
            htmlReports = new ExtentSparkReporter("darazTestReport.html");
            extentReports = new ExtentReports();
            extentReports.attachReporter(htmlReports);

            System.setProperty("webdriver.chrome.driver", "D:\\Quality Assurance\\Drivers\\chromedriver_win32\\chromedriver.exe");
            driver = new ChromeDriver();
        }
        catch (Exception e){
            System.out.println("Error in DarazAutomateTest>>setUpBrowser: "+e);
        }

    }
    @Test
    public void darazTestCases(){
        try{
            /***
             Navigate to daraz.com
             ***/
            darazTest = extentReports.createTest("Navigate to daraz.com", "The user navigates to \""+darazUrl+"\"")
                    .info("********** Starting Test Cases **************")
                    .info("Navigating to "+darazUrl+"........");

            driver.get("https://www.daraz.com.np/");

            // Maximize the window
            driver.manage().window().maximize();

            // Verify if the page is loaded
            if(driver.getCurrentUrl().equals("https://www.daraz.com.np/")){
                handlePassCase("Navigated to "+darazUrl+" Test Passed!", "daraz_navigation_pass");
            }
            else{
                handleFailedCase("Navigate to "+darazUrl+" Test Failed!", "daraz_navigation_failed");
            }

            /***
              Navigate to Login Page
             ***/
            darazTest = extentReports.createTest("Navigate to Login Page", "This is to test if the user can navigate to login page")
                    .info("Locating Login Button........");
            WebElement loginButton = Utility.getElement(driver, "id", "anonLogin", true);
            System.out.println("LOGIN BUTTON TEXT:................."+loginButton.getText());
            if(loginButton.getText().equals("LOGIN")){
                Actions action = new Actions(driver);
                action.moveToElement(loginButton).perform();
                handlePassCase("Login button located", "login_button_located");
            }
            else {
                handleFailedCase("Login button can not be located", "login_button_locate_failed");
            }
            darazTest.info("Clicking login button...............");
            loginButton.click();
            darazTest.info("Login button clicked!");
            darazTest.info("Navigating to login page...............");

            // Locating login page welcome text
            String welcomeTitle = Utility.getElement(driver, "className", "login-title", false)
                    .findElement(By.tagName("h3")).getText();

            // Verifying login page welcome text
            if(welcomeTitle.equalsIgnoreCase("Welcome to Daraz! Please login.")){
                handlePassCase("Navigated to login page. Test Passed!", "daraz_login_page");
            }
            else{
                handleFailedCase("Navigate to login page. Test Failed!", "daraz_login_page_failed");
            }

            //Login Test
            darazTest = extentReports.createTest("Login Test", "This checks the login functionality");

            // Create FileReader object to read the test data
            FileReader fileReader = new FileReader("D:\\Quality Assurance\\repo_verisk_selenium_task\\src\\test\\java\\com\\seleniumtask\\darazLoginTestCases.json");
            JSONParser parser = new JSONParser();
            // Parsing the returned data to JSONObject
            JSONObject jsonObject = (JSONObject)parser.parse(fileReader);
            List<JSONObject> objList = (List<JSONObject>) jsonObject.get("testData");
            for(int i=0; i<objList.size(); i++){
                JSONObject obj = objList.get(i);
                String username = (String) obj.get("username");
                String password = (String) obj.get("password");
                String description = (String) obj.get("description");
                String imgName = "login_test_"+(i+1);
                login(username, password, description, imgName);
                driver.navigate().refresh();

            }


        }
        catch (Exception e){
            System.out.println("Error in DarazAutomateTest>>darazTestCases: "+e);
        }
    }

    @AfterSuite
    public void close(){
        try{

            driver.close();
            driver.quit();
            darazTest.info("Closed the Browser");
            darazTest.info("************** Test Completed **************");

            extentReports.flush();

        }
        catch (Exception e){
            System.out.println("Error in DarazAutomateTest>>close: "+e);
        }
    }

    public void handlePassCase(String message, String imgName){
        try {
            darazTest.pass(message, MediaEntityBuilder.createScreenCaptureFromPath(Utility.takeScreenshot(driver, imgName)).build());
        }
        catch (Exception e){
            System.out.println("Error in handlePassCase: "+e);
        }
    }

    public void handleFailedCase(String message, String imgName){
        try{
            darazTest.fail(message,MediaEntityBuilder.createScreenCaptureFromPath(Utility.takeScreenshot(driver, imgName)).build());
        }
        catch (Exception e){
            System.out.println("Error in handleFailedCase: "+e);
        }
    }

    public void login(String username, String password, String description, String imgName){
        try {
            Utility.getElement(driver, "xpath", "//input[@type='text' and @placeholder='Please enter your Phone Number or Email']", false).sendKeys(username);
            Utility.getElement(driver, "xpath", "//input[@type='password' and @placeholder='Please enter your password']", false).sendKeys(password);
            Utility.getElement(driver, "className","mod-login-btn", true).click();

            String expectedResult = loggedAccountName;
            String actualResult ="";
            //Logged
            try{
                WebElement accountElement = Utility.getElement(driver, "id", "myAccountTrigger", true);
                actualResult = accountElement.getText();
            }catch (Exception e){
                handleFailedCase(description+" FAILED!",imgName);
            }


            if(actualResult.equals(expectedResult)){
                handlePassCase(description+" PASSED!",imgName);
            }
            else {
                handleFailedCase(description+" FAILED!",imgName);
            }
        }
        catch (Exception e){
            System.out.println("Error in DarazAutomateTest>>login"+e);
            handleFailedCase("Daraz Login Test Failed. Error Occurred:: "+e,"daraz_login_error");
        }
    }

}
