package sk.sivak.eldritchhorror.core.constants.question;

public class Answer<RD, AD> {
    private RD responseData;
    private AD additionalData;

    public RD getResponseData() {
        return responseData;
    }

    public void setResponseData(RD responseData) {
        this.responseData = responseData;
    }

    public AD getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(AD additionalData) {
        this.additionalData = additionalData;
    }
}
