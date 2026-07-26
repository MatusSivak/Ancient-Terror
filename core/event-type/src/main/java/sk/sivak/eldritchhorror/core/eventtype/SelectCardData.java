package sk.sivak.eldritchhorror.core.eventtype;

import sk.sivak.eldritchhorror.core.constants.card.CardInfo;

import java.util.List;

public class SelectCardData {
    private String titleText;
    private String hideText;
    private List<? extends CardInfo> availableCards;

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getHideText() {
        return hideText;
    }

    public void setHideText(String hideText) {
        this.hideText = hideText;
    }

    public List<? extends CardInfo> getAvailableCards() {
        return availableCards;
    }

    public void setAvailableCards(List<? extends CardInfo> availableCards) {
        this.availableCards = availableCards;
    }
}
