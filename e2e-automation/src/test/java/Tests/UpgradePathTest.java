package test.java.Tests;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import test.java.utility.IntSetup;

public class UpgradePathTest extends IntSetup {
    private test.java.funcModules.ConnectionModules objConnectionModules = new test.java.funcModules.ConnectionModules();
    private test.java.utility.LocalContext context = test.java.utility.LocalContext.getInstance();

    private String connectionName;
    private final String connectionInvitation = "connection-invitation-upt";
    private final String oobInvitation = "out-of-band-invitation-upt";

    @BeforeClass
    public void BeforeClassSetup() throws Exception {
        passCodePageNew.openApp();
    }

    // This part of UPT is identical to the AppElementTest class
    @Test
    public void checkHome() {
        homePageNew.checkHome();
    }

    @Test(dependsOnMethods = "checkHome")
    public void checkMenu() throws InterruptedException {
        homePageNew.tapOnBurgerMenu();
        menuPageNew.menuContainer.isDisplayed();
        menuPageNew.banner.isDisplayed();
        menuPageNew.logo.isDisplayed();
        menuPageNew.builtByFooter.isDisplayed();
        menuPageNew.versionFooter.isDisplayed();

        menuPageNew.userAvatar.click();
        if (!test.java.appModules.AppUtils.isElementAbsent(driverApp, menuPageNew.okButton)) {
            menuPageNew.okButton.click();
            menuPageNew.menuAllowButton.click();
        } else {
            System.out.println("Permissions already have been granted!");
        }

        Thread.sleep(1000);
        if ((test.java.utility.Config.Device_Type.equals("android") || test.java.utility.Config.Device_Type.equals("awsAndroid"))) {
            ((AndroidDriver) driverApp).pressKey(new KeyEvent(AndroidKey.BACK));
        } else {
            menuPageNew.cancelButton.click();
        }

        menuPageNew.homeButton.click();

        homePageNew.tapOnBurgerMenu();
        menuPageNew.myConnectionsButton.click();
        myConnectionsPageNew.myConnectionsHeader.isDisplayed();
        homePageNew.scanButton.isDisplayed();

        homePageNew.tapOnBurgerMenu();
        menuPageNew.myCredentialsButton.click();
        myCredentialsPageNew.myCredentialsHeader.isDisplayed();
        homePageNew.scanButton.isDisplayed();

        homePageNew.tapOnBurgerMenu();
        menuPageNew.settingsButton.click();

        homePageNew.tapOnBurgerMenu();
        menuPageNew.homeButton.click();
        homePageNew.homeHeader.isDisplayed();
    }

    @Test(dependsOnMethods = "checkMenu")
    public void checkQrScanner() throws Exception {
        homePageNew.scanButton.isDisplayed();
        homePageNew.scanButton.click();

        if (!test.java.appModules.AppUtils.isElementAbsent(driverApp, qrScannerPageNew.scannerAllowButton)) {
            qrScannerPageNew.scannerAllowButton.click();
        } else {
            System.out.println("Permissions already have been granted!");
        }

        Thread.sleep(1000);
        qrScannerPageNew.scannerCloseButton.click();
        Thread.sleep(1000);
    }

    @Test(dependsOnMethods = "checkQrScanner")
    public void checkSettings() throws Exception {
        homePageNew.tapOnBurgerMenu(); // go to Menu
        menuPageNew.settingsButton.click(); // go to Settings

        settingsPageNew.settingsContainer.isDisplayed();
        settingsPageNew.settingsHeader.isDisplayed();
        settingsPageNew.biometricsButton.click();
        settingsPageNew.passCodeButton.click();
        passCodePageNew.backArrow.click();
        settingsPageNew.chatButton.click();
        Thread.sleep(1000);
        try {
            chatPageNew.backArrow.click();
        } catch (Exception e) {
            if (test.java.utility.Config.iOS_Devices.contains(test.java.utility.Config.Device_Type)) {
                chatPageNew.backArrowAlt.click();
            } else {
                ((AndroidDriver) driverApp).pressKey(new KeyEvent(AndroidKey.BACK));
            }
        }

        settingsPageNew.aboutButton.click();
        aboutPageNew.backArrow.click();
    }

    @DataProvider(name = "data1")
    public Object[][] createData() {
        return new Object[][]{
            {connectionInvitation},
            {oobInvitation},
        };
    }

    // This part of the UPT is identical to the ConnectionTest class
    @Test(dataProvider = "data1")
    public void rejectConnectionTest(String invitationType) throws Exception {
        driverBrowser = test.java.utility.BrowserDriver.getDriver();

        test.java.appModules.AppUtils.DoSomethingEventuallyNew(
            () -> objConnectionModules.getConnectionInvitation(driverBrowser, driverApp, test.java.utility.Helpers.randomString(), invitationType),
            () -> objConnectionModules.acceptPushNotificationRequest(driverApp),
            () -> test.java.appModules.AppUtils.waitForElementNew(driverApp, invitationPageNew.title),
            () -> objConnectionModules.rejectConnectionInvitation(driverApp)
        );

        Thread.sleep(1000);
        test.java.utility.BrowserDriver.closeApp();
    }

    @Test(dataProvider = "data1", dependsOnMethods = "rejectConnectionTest")
    public void setUpConnectionTest(String invitationType) throws Exception {
        connectionName = invitationType;

        driverBrowser = test.java.utility.BrowserDriver.getDriver();

        test.java.appModules.AppUtils.DoSomethingEventuallyNew(
            () -> objConnectionModules.getConnectionInvitation(driverBrowser, driverApp, connectionName, invitationType),
            () -> test.java.appModules.AppUtils.waitForElementNew(driverApp, invitationPageNew.title),
            () -> objConnectionModules.acceptConnectionInvitation(driverApp)
        );

        try {
            switch (connectionName) {
                case connectionInvitation:
                    test.java.appModules.AppUtils.waitForElementNew(driverApp, homePageNew.commonConnectedEvent);
                    break;
                case oobInvitation:
                    test.java.appModules.AppUtils.waitForElementNew(driverApp, homePageNew.oobConnectedEvent);
                    break;
            }
        } catch (Exception e) {
            System.exit(1); // don't run other tests if this fails
        }

        Thread.sleep(1000);
        test.java.utility.BrowserDriver.closeApp();
    }

    @Test(dependsOnMethods = "setUpConnectionTest")
    public void validateMyConnectionRecordAppeared() throws Exception {
        passCodePageNew.openApp();

        homePageNew.tapOnBurgerMenu();
        menuPageNew.myConnectionsButton.click();
        Thread.sleep(1000); // FIXME MSDK workaround: it goes to Settings without sleep
        myConnectionsPageNew.getConnectionByName(connectionName).click();
    }

    @Test(dependsOnMethods = "validateMyConnectionRecordAppeared")
    public void validateConnectionHistory() throws Exception {
        connectionHistoryPageNew.connectionLogo.isDisplayed();
        connectionHistoryPageNew.oobConnectionName.isDisplayed();
        connectionHistoryPageNew.connectedRecord.isDisplayed();
        connectionHistoryPageNew.oobConnectedRecordDescription.isDisplayed();
    }

    @Test(dependsOnMethods = "validateConnectionHistory")
    public void validateConnectionDetails() throws Exception {
        connectionHistoryPageNew.threeDotsButton.isDisplayed();
        connectionHistoryPageNew.threeDotsButton.click();

        connectionDetailPageNew.closeButton.isDisplayed();
        connectionDetailPageNew.deleteButton.isDisplayed();

        connectionDetailPageNew.closeButton.click();
        connectionHistoryPageNew.backButton.click();
    }

    @AfterClass
    public void AfterClass() {
        context.setValue("connectionName", connectionName);
        System.out.println("Connection name in context: " + connectionName);
        System.out.println("Device ID in context: " + context.getValue("DID"));

        driverApp.closeApp();
        System.out.println("Connection Test Suite has been finished!");
    }
}
