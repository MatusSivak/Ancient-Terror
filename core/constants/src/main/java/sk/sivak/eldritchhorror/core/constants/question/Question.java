package sk.sivak.eldritchhorror.core.constants.question;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Question<RD> {
    private String title;
    private String portraitTextureName;
    private List<Option<RD>> options;
    private SelectComponentsTableData selectComponentsTableData;
    private boolean displayCurrentMysteryCard;

    private List<String> textureNameList = new LinkedList<>();
    private String displayOngoingRumorCard;

    public String getPortraitTextureName() {
        return portraitTextureName;
    }

    public void setPortraitBeforeTitle(InvestigatorId investigatorId) {
        this.portraitTextureName = "investigator/" + investigatorId.name() + ".png";
    }

    public void setPortraitBeforeTitle(ArtifactId artifactId) {
        this.portraitTextureName = "card/artifact/" + artifactId.name() + ".jpg";
    }

    public void setPortraitBeforeTitle(SpellId spellId) {
        this.portraitTextureName = "card/spell/" + spellId.name() + ".jpg";
    }

    public void setPortraitBeforeTitle(AncientOneId ancientOneId) {
        this.portraitTextureName = "ancient_one/button_"+ancientOneId.name().toLowerCase()+".jpg";
    }

    public void setPortraitBeforeTitle(MonsterInfo monsterInfo) {
        String className = monsterInfo.getClass().getSimpleName();
        String fileName = className.substring(0, className.length() - "Monster".length());
        if (monsterInfo.isEpic()) {
            this.portraitTextureName = "monster/epic/" + fileName + ".png";
        } else {
            this.portraitTextureName = "monster/" + fileName + ".png";
        }
    }

    public List<String> getTextureNameList() {
        return textureNameList;
    }

    public void addTextureName(String textureName) {
        this.textureNameList.add(textureName);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Option<RD>> getOptions() {
        return options;
    }

    public void setOptions(List<Option<RD>> options) {
        this.options = options;
    }

    public SelectComponentsTableData getSelectComponentsTableData() {
        return selectComponentsTableData;
    }

    public void setSelectComponentsTableData(SelectComponentsTableData selectComponentsTableData) {
        this.selectComponentsTableData = selectComponentsTableData;
    }

    public Question<RD> addOption(Option<RD> option) {
        if (options == null) {
            options = new LinkedList<>();
        }
        options.add(option);
        return this;
    }

    public void displayOngoingRumorCard(String rumorCardId) {
        this.displayOngoingRumorCard = rumorCardId;
    }

    public String getDisplayOngoingRumorCard() {
        return displayOngoingRumorCard;
    }



    public enum SelectComponentsTableType {
        INVESTIGATORS,
        CARDS,
        GATES
    }

    public static class SelectComponentsTableData<Key> {
        private List<? extends Key> availableKeys;
        private SelectComponentsTableType type;

        public List<? extends Key> getAvailableKeys() {
            return availableKeys;
        }

        public void setAvailableKeys(List<? extends Key> availableKeys) {
            this.availableKeys = availableKeys;
        }

        public SelectComponentsTableType getType() {
            return type;
        }

        public void setType(SelectComponentsTableType type) {
            this.type = type;
        }
    }

    public static class Option<RD> {

        private String name;
        private RD value;
        private int colorCode;

        public Option(String name, RD value) {
            this(name, value, 0x3f3f3fff);
        }

        public Option(String name, RD value, int colorCode) {
            this.name = name;
            this.value = value;
            this.colorCode = colorCode;
        }

        public int getColorCode() {
            return colorCode;
        }

        public void setColorCode(int colorCode) {
            this.colorCode = colorCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public RD getValue() {
            return value;
        }

        public void setValue(RD value) {
            this.value = value;
        }

        public static final List<Option<Boolean>> noYesOptions = Arrays.asList(
                new Option<>("No", false, 0x800000ff),
                new Option<>("Yes", true, 0x008000ff)
        );

        public static final List<Option<Object>> okOption = Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE));


    }

    public void displayCurrentMysteryCard() {
        this.displayCurrentMysteryCard = true;
    }

    public boolean isDisplayCurrentMysteryCard() {
        return displayCurrentMysteryCard;
    }
}
