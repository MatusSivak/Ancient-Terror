package sk.sivak.eldritchhorror.core.eventtype;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

import java.util.List;

public class SelectMonsterData {
    private String titleText = "Select Monster";
    private String hideText = "Display Monsters?";
    private List<? extends MonsterInfo> availableMonsters;

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

    public List<? extends MonsterInfo> getAvailableMonsters() {
        return availableMonsters;
    }

    public void setAvailableMonsters(List<? extends MonsterInfo> availableMonsters) {
        this.availableMonsters = availableMonsters;
    }
}
