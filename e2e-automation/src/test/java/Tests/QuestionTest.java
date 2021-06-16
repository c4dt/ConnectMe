package test.java.Tests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import test.java.appModules.AppUtils;
import test.java.appModules.VASApi;
import test.java.utility.IntSetup;
import test.java.utility.LocalContext;
import test.java.utility.Helpers;

import java.util.Arrays;
import java.util.List;

public class QuestionTest extends IntSetup {
	private AppUtils objAppUtlis = new AppUtils();
	private LocalContext context = LocalContext.getInstance();

	private VASApi VAS;
	private String DID;
	private String connectionName;
	private String text = "How much?";
	private String detail = "How much do you want";
	private List<String> oneOption = Arrays.asList(Helpers.randomString());
	private List<String> twoOptions = Arrays.asList(Helpers.randomString(), Helpers.randomString());
	private List<String> threeOptions = Arrays.asList(Helpers.randomString(), Helpers.randomString(), Helpers.randomString());

	@BeforeClass
	public void BeforeClassSetup() throws Exception {
		DID = context.getValue("DID");
		connectionName = context.getValue("connectionName");
    passCodePageNew.openApp();
		VAS = VASApi.getInstance();
	}

	private void answerQuestionFromHome(List<String> validResponses) throws Exception {
    AppUtils.DoSomethingEventually(
        () -> VAS.askQuestion(DID, text, detail, validResponses)
//      () -> questionPage.header(driverApp).isDisplayed()
//		    () -> AppUtils.waitForElementNew(driverApp, questionPageNew.header)
    );
    AppUtils.waitForElementNew(driverApp, questionPageNew.header);
		validateQuestionWindow(validResponses);
	}

//	private void answerQuestionFromConnectionHistory(List<String> validResponses) throws Exception {
//		VAS.askQuestion(DID, text, detail, validResponses);
//
//    connectionHistoryPage.questionReceivedRecord(driverApp, text).isDisplayed();
//		connectionHistoryPage.questionReceivedRecordDescription(driverApp, detail).isDisplayed();
//		connectionHistoryPage.viewReceivedQuestionButton(driverApp).click();
//
//		validateQuestionWindow(validResponses);
//	}


	private void validateQuestionWindow(List<String> validResponses) throws Exception {
//    questionPageNew.senderLogo.isDisplayed(); // FIXME this doesn't work
    questionPageNew.findParameterizedElement(context.getValue("connectionName")).isDisplayed();
//    questionPageNew.findParameterizedElement(text).isDisplayed(); // FIXME this doesn't work for ios
    questionPageNew.questionText.isDisplayed();
    questionPageNew.findParameterizedElement(detail).isDisplayed();

		for (String validResponse : validResponses) {
      questionPageNew.findParameterizedElement(validResponse).isDisplayed();
		}
	}

	@Test
	public void answerQuestionWithOneOptionFromHome() throws Exception {
		answerQuestionFromHome(oneOption);

		String answer = oneOption.get(0);
    questionPageNew.findParameterizedElement(answer).click();
		homePageNew.questionRespondedEvent(answer).isDisplayed();
	}

	@Test(dependsOnMethods = "answerQuestionWithOneOptionFromHome")
	public void answerQuestionWithTwoOptionsFromHome() throws Exception {
		answerQuestionFromHome(twoOptions);

		String answer = twoOptions.get(0);
    questionPageNew.findParameterizedElement(answer).click();
    homePageNew.questionRespondedEvent(answer).isDisplayed();
	}

	@Test(dependsOnMethods = "answerQuestionWithTwoOptionsFromHome")
	public void answerQuestionWithThreeOptionsFromHome() throws Exception {
		answerQuestionFromHome(threeOptions);

		String answer = threeOptions.get(0);
    questionPageNew.findParameterizedElement(answer).click();
    questionPageNew.submitButton.click();
    homePageNew.questionRespondedEvent(answer).isDisplayed();
	}

	@Test(dependsOnMethods = "answerQuestionWithThreeOptionsFromHome")
	public void validateConnectionHistory() throws Exception {
		homePageNew.burgerMenuButton.click();
		menuPageNew.myConnectionsButton.click();
		myConnectionsPageNew.testConnection(connectionName).click();
//		// TODO: move this logic to helper
//		try {
			connectionHistoryPageNew.questionAnswerRecord.isDisplayed();
//		} catch (Exception ex) {
//			AppUtils.pullScreenUp(driverApp);
//			connectionHistoryPage.questionAnswerRecord(driverApp).isDisplayed();
//		}

		connectionHistoryPageNew.questionAnswerRecordDescription(oneOption.get(0)).isDisplayed();
    connectionHistoryPageNew.questionAnswerRecordDescription(twoOptions.get(0)).isDisplayed();
    connectionHistoryPageNew.questionAnswerRecordDescription(threeOptions.get(0)).isDisplayed();
	}

	/*
	* NOTE: These tests don't work for iOS simulator because of lack of push notifications
	* */
	/*
	@Test(dependsOnMethods = "validateConnectionHistory")
	public void answerQuestionWithOneOptionFromConnectionHistory() throws Exception {
	  List<String> oneOption = Arrays.asList(Helpers.randomString());
		answerQuestionFromConnectionHistory(oneOption);

		String answer = oneOption.get(0);
		questionPage.answer_Button(driverApp, answer).click();

		connectionHistoryPage.questionAnswerRecordDescription(driverApp, answer).isDisplayed();
	}

	@Test(dependsOnMethods = "answerQuestionWithOneOptionFromConnectionHistory")
	public void answerQuestionWithTwoOptionsFromConnectionHistory() throws Exception {
	  List<String> twoOptions = Arrays.asList(Helpers.randomString(), Helpers.randomString());
		answerQuestionFromConnectionHistory(twoOptions);

		String answer = twoOptions.get(0);
		questionPage.answer_Button(driverApp, answer).click();

		connectionHistoryPage.questionAnswerRecordDescription(driverApp, answer).isDisplayed();
	}

	@Test(dependsOnMethods = "answerQuestionWithTwoOptionsFromConnectionHistory")
	public void answerQuestionWithThreeOptionsFromConnectionHistory() throws Exception {
	  List<String> threeOptions = Arrays.asList(Helpers.randomString(), Helpers.randomString(), Helpers.randomString());
		answerQuestionFromConnectionHistory(threeOptions);

		String answer = threeOptions.get(0);
		questionPage.answer_Option(driverApp, answer).click();
		questionPage.submit_Button(driverApp).click();

		connectionHistoryPage.questionAnswerRecordDescription(driverApp, answer).isDisplayed();
	}
	*/

	@AfterClass
	public void AfterClass() {
		driverApp.closeApp();
	}
}
