package com.seleniumtask;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

import java.util.List;

public class MacysAutomateTest {
    WebDriver driver;
    ExtentSparkReporter htmlReport;
    ExtentReports extent;
    ExtentTest macyTest;

    // Full path of webdriver exe file
    final static String webdriverPath = "D:\\Quality Assurance\\Drivers\\chromedriver_win32\\chromedriver.exe";
    final static String macysUrl = "https://www.macys.com/";
    final static String productName = "Men Shoes";
    @BeforeSuite
    public void setUpBrowser(){
        try {
            htmlReport = new ExtentSparkReporter("macysTestReports.html");
            extent = new ExtentReports();
            extent.attachReporter(htmlReport);

            System.setProperty("webdriver.chrome.driver", webdriverPath);

            //Instantiate ChromeOptions
            ChromeOptions options = new ChromeOptions();

            //Adding argument to disable the AutomationControlled flag
            options.addArguments("--disable-blink-features=AutomationControlled");

            String[] arr = {"enable-automation"};

            //Exclude the collection of enable-automation switches
            options.setExperimentalOption("excludeSwitches", arr);

            //Turn-off userAutomationExtension
            options.setExperimentalOption("useAutomationExtension", false);
            driver = new ChromeDriver(options);

        }
        catch (Exception e){
            System.out.println("Error in MacysAutomateTest>>setUpBrowser: "+e);
        }
    }

    @Test
    public void macyTestCases() throws InterruptedException {
        try {
            /***
                Navigate to macys.com
             ***/
            macyTest = extent.createTest("Navigate to Macys.com"," The user navigates to \""+macysUrl+"\"");

            macyTest.log(Status.INFO, "********** Starting Test Cases **************");
            macyTest.log(Status.INFO,"Navigating to "+macysUrl+"........");
            driver.get(macysUrl);

            //Maximize the window size
            driver.manage().window().maximize();

            // Verify if the page is loaded
            if(driver.getTitle().equals("Macy's - Shop Fashion Clothing & Accessories - Official Site - Macys.com")){
                handlePassCase("Navigated to "+macysUrl+" Test Passed!","macys_navigation_pass");
            }else {
                handleFailedCase("Navigate to" + macysUrl+" Test Failed!","macys_navigation_fail");
            }

            //Close pop-up
            macyTest.log(Status.INFO,"Closing popup........");
            Utility.getElement(driver,"id","closeButton",true).click();
            Thread.sleep(1000);
            handlePassCase("Popup Closed Successfully","popup_close_pass");

            /***
                Navigate to Home Page
             ***/
            macyTest=extent.createTest("Navigate to Home Page", "This is to test if the user can navigate to home page or not");
            macyTest.log(Status.INFO,"Navigating to Home Page..........");
            // Wait for the element to become clickable


            Utility.getElement(driver,"partialLinkText","Home",true).click();
            if(driver.getCurrentUrl().equals("https://www.macys.com/shop/for-the-home?id=22672&cm_sp=intl_hdr-_-home-_-22672_home")){
                handlePassCase("Navigate to the Home page Test Passed!","home_page_navigation_pass");
            }else{
                handleFailedCase("Navigate to Home Page Test Failed!","home_page_navigation_failed");
            }

            /***
                Search Product
             ***/
            macyTest = extent.createTest("Search Product", "This is to test if the user can search product or not");

            macyTest.log(Status.INFO,"Locating Search Box........");
            WebElement searchBox = Utility.getElement(driver,"id","globalSearchInputField",false);
            macyTest.log(Status.INFO,"Search Box Located");

            macyTest.log(Status.INFO,"Clicking on search box .......");
            searchBox.click();
            handlePassCase("SearchBox Clicked","search_box_clicked");

            macyTest.log(Status.INFO,"Inserting product name........");
            searchBox.sendKeys(productName);
            handlePassCase("Product to Search Inserted Successfully ","product_name_inserted");

            searchBox.submit();
            macyTest.log(Status.INFO,"Searching "+productName+"...... ");

            String expectedTitle = productName+" - Macy's";
            String actualTitle = driver.getTitle();

            if (expectedTitle.equals(actualTitle)) {
                handlePassCase("Search Product Test Passed!", "product_search_pass");
            } else {
                handleFailedCase("Search Product Test Failed!","product_search_failed");
            }

            /***
                Add product to Bag
             ***/
            macyTest = extent.createTest("Add Product to Bag", "This is to test if the user can add searched product to bag or not");

            //Scroll window
            macyTest.log(Status.INFO,"Scrolling down to the product: Men's Grayson Lace-Up Sneakers, Created for Macy's....");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scroll(0,1000)");
            handlePassCase("Scroll down to the product: Men's Grayson Lace-Up Sneakers, Created for Macy's Test Passed","product_scroll_pass");

            macyTest.log(Status.INFO,"Selecting product: Men's Grayson Lace-Up Sneakers, Created for Macy's.....");

            //select the product
            Utility.getElement(driver,"xpath","//*[@id=\"11836963\"]/div[2]/div[2]/a",true).click();

            String expectedResult = "Alfani Men's Grayson Lace-Up Sneakers, Created for Macy's & Reviews - All Men's Shoes - Men - Macy's";
            String actualResult = driver.getTitle();
            if(expectedResult.equals(actualResult)){
                handlePassCase("Select product: Men's Grayson Lace-Up Sneakers, Created for Macy's Test Passed!","product_select_pass");
            }else {
                handleFailedCase("Select product: Men's Grayson Lace-Up Sneakers, Created for Macy's Test Failed!","product_select_failed");
            }

            macyTest.log(Status.INFO,"Selecting Shoe Size:7.5 ....");
            List<WebElement> sizes = driver.findElements(By.tagName("li"));

            WebElement shoeSizeElement = null;
            for (WebElement size : sizes) {
                if (size.getText().equals("7.5")) {
                    shoeSizeElement = size;
                    break;
                }
            }
            if(shoeSizeElement != null){
                shoeSizeElement.click();
                handlePassCase("Shoe size select test Passed!","shoe_size_select_pass");
            }else{
                handleFailedCase("Shoe size select Test Failed!","shoe_size_select_failed");
            }

            // Add to cart
            macyTest.log(Status.INFO, "Adding Product to Bag.........");
            WebElement addToBagButton = Utility.getElement(driver,"id","bag-add-11836963",true);

            // Scroll down to twice of windows inner height to make add to bag button visible
            Long scrollHeight = (Long) js.executeScript("return window.innerHeight;");
            js.executeScript("window.scrollTo(0,"+(scrollHeight*2)+");");

            // Click add to bag
            addToBagButton.click();

            //Verify if add to bag successful by finding checkout button
            WebElement checkOutButton = Utility.getElement(driver,"id","atbIntCheckout",true);
            if(checkOutButton.getText().equals("checkout")){
                handlePassCase("Add Product to Bag Test Passed!","add_to_bag_pass");
            }else{
                handleFailedCase("Add Product to Bag Failed!","add_to_bag_failed");
            }

            Thread.sleep(4000);
        }
        catch (Exception e){
            System.out.println("Error in MacysAutomateTest>>macyTestCases: "+e);
            Thread.sleep(1000);
            handleFailedCase("Test Failed. Error Occurred:: "+e,"error");
        }
    }

    @AfterSuite
    public void close(){
        try {
            driver.close();
            driver.quit();

            macyTest.pass("Closed the Browser");
            macyTest.info("Test Completed");

            extent.flush();
        }
        catch (Exception e){
            System.out.println("Error in MacysAutomateTest>>close: "+e);
        }
    }

    public void handlePassCase(String message,String imgName){
        try {
            macyTest.pass(message, MediaEntityBuilder.createScreenCaptureFromPath(Utility.takeScreenshot(driver,imgName)).build());
        }catch (Exception e){
            System.out.println("Error in handlePassCase: "+e);
        }
    }

    public void handleFailedCase(String message,String imgName){
        try {
            macyTest.fail(message, MediaEntityBuilder.createScreenCaptureFromPath(Utility.takeScreenshot(driver,imgName)).build());
        }catch (Exception e){
            System.out.println("Error in handleFailedCase:: "+e);
        }
    }


}
