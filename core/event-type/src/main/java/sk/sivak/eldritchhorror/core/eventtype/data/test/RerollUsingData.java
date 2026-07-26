package sk.sivak.eldritchhorror.core.eventtype.data.test;

import sk.sivak.eldritchhorror.core.constants.question.Question;

public class RerollUsingData {
    private TestData testData;
    private Question question;

    public RerollUsingData(TestData testData) {
        this.testData = testData;
    }

    public TestData getTestData() {
        return testData;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
