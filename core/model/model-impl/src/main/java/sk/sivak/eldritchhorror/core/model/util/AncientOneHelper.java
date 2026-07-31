package sk.sivak.eldritchhorror.core.model.util;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class AncientOneHelper {



    public static List<AncientOneInfo> initAvailableAncientOnes() {
        ArrayList<AncientOneInfo> response = new ArrayList<>();
        response.add(createAzathoth());
        response.add(createCthulhu());
        response.add(createShubNiggurath());
        response.add(createYogSothoth());
        return response;
    }

    public static AncientOneInfo createAzathoth() {
        AncientOneInfoImpl ancientOneInfo = new AncientOneInfoImpl();
        ancientOneInfo.setAncientOneId(AncientOneId.AZATHOTH);
        ancientOneInfo.setStartingDoom(15);
        ancientOneInfo.setSetupText("ancientOne.azathoth.setup");
        ancientOneInfo.setName("ancientOne.azathoth.name");
        ancientOneInfo.setAltName("ancientOne.azathoth.alt");
        ancientOneInfo.setSpecialText("ancientOne.azathoth.special");
        ancientOneInfo.setMidnightText("ancientOne.azathoth.midnight");
        ancientOneInfo.setWinText("ancientOne.azathoth.win");
        ancientOneInfo.setMysteriesRequired(3);
        ancientOneInfo.setFlavorText("ancientOne.azathoth.flavor");
        ancientOneInfo.setEndGameText("ancientOne.azathoth.endGame");
        ancientOneInfo.setMythosCardCount(16);
        return ancientOneInfo;
    }

    public static AncientOneInfo createCthulhu() {
        AncientOneInfoImpl ancientOneInfo = new AncientOneInfoImpl();
        ancientOneInfo.setAncientOneId(AncientOneId.CTHULHU);
        ancientOneInfo.setStartingDoom(12);
        ancientOneInfo.setName("ancientOne.cthulhu.name");
        ancientOneInfo.setAltName("ancientOne.cthulhu.alt");
        ancientOneInfo.setSpecialText("ancientOne.cthulhu.special");
        ancientOneInfo.setMidnightText("ancientOne.cthulhu.midnight");
        ancientOneInfo.setWinText("ancientOne.cthulhu.win");
        ancientOneInfo.setMysteriesRequired(3);
        ancientOneInfo.setReckoningText("ancientOne.cthulhu.reckoning");
        ancientOneInfo.setFlavorText("ancientOne.cthulhu.flavor");
        ancientOneInfo.setEndGameText("ancientOne.cthulhu.endGame");
        ancientOneInfo.setMythosCardCount(15);
        ancientOneInfo.setRemovedMonsters(Arrays.asList(NonEpicMonsterId.STAR_SPAWN, NonEpicMonsterId.DEEP_ONE));
        return ancientOneInfo;
    }

    public static AncientOneInfo createAwakenCthulhu() {
        AncientOneInfoImpl ancientOneInfo = new AncientOneInfoImpl();
        ancientOneInfo.setAncientOneId(AncientOneId.CTHULHU);
        ancientOneInfo.setAwaken(true);
        ancientOneInfo.setStartingDoom(0);
        ancientOneInfo.setName("ancientOne.cthulhu.awaken.name");
        ancientOneInfo.setAltName("ancientOne.cthulhu.awaken.alt");
        ancientOneInfo.setSpecialText("ancientOne.cthulhu.awaken.special");
        ancientOneInfo.setMidnightText("ancientOne.cthulhu.awaken.midnight");
        ancientOneInfo.setWinText("ancientOne.cthulhu.awaken.win");
        ancientOneInfo.setMysteriesRequired(4);
        ancientOneInfo.setReckoningText("ancientOne.cthulhu.awaken.reckoning");
        ancientOneInfo.setFlavorText("ancientOne.cthulhu.awaken.flavor");
        ancientOneInfo.setEndGameText("ancientOne.cthulhu.endGame");
        ancientOneInfo.setMythosCardCount(15);
        ancientOneInfo.setRemovedMonsters(Arrays.asList(NonEpicMonsterId.STAR_SPAWN, NonEpicMonsterId.DEEP_ONE));
        return ancientOneInfo;
    }

    public static AncientOneInfo createShubNiggurath() {
        AncientOneInfoImpl ancientOneInfo = new AncientOneInfoImpl();
        ancientOneInfo.setAncientOneId(AncientOneId.SHUB_NIGGURATH);
        ancientOneInfo.setStartingDoom(13);
        ancientOneInfo.setName("ancientOne.shubNiggurath.name");
        ancientOneInfo.setAltName("ancientOne.shubNiggurath.alt");
        ancientOneInfo.setMidnightText("ancientOne.shubNiggurath.midnight");
        ancientOneInfo.setWinText("ancientOne.shubNiggurath.win");
        ancientOneInfo.setMysteriesRequired(3);
        ancientOneInfo.setReckoningText("ancientOne.shubNiggurath.reckoning");
        ancientOneInfo.setFlavorText("ancientOne.shubNiggurath.flavor");
        ancientOneInfo.setEndGameText("ancientOne.shubNiggurath.endGame");
        ancientOneInfo.setMythosCardCount(16);
        ancientOneInfo.setRemovedMonsters(Arrays.asList(
                NonEpicMonsterId.GHOUL, NonEpicMonsterId.GHOUL,
                NonEpicMonsterId.GOAT_SPAWN, NonEpicMonsterId.GOAT_SPAWN, NonEpicMonsterId.DARK_YOUNG));
        return ancientOneInfo;
    }

    public static AncientOneInfo createAwakenShubNiggurath() {
        AncientOneInfoImpl ancientOneInfo = new AncientOneInfoImpl();
        ancientOneInfo.setAncientOneId(AncientOneId.SHUB_NIGGURATH);
        ancientOneInfo.setAwaken(true);
        ancientOneInfo.setStartingDoom(0);
        ancientOneInfo.setName("ancientOne.shubNiggurath.awaken.name");
        ancientOneInfo.setAltName("ancientOne.shubNiggurath.awaken.alt");
        ancientOneInfo.setMidnightText("ancientOne.shubNiggurath.awaken.midnight");
        ancientOneInfo.setWinText("ancientOne.shubNiggurath.awaken.win");
        ancientOneInfo.setMysteriesRequired(4);
        ancientOneInfo.setReckoningText("ancientOne.shubNiggurath.awaken.reckoning");
        ancientOneInfo.setFlavorText("ancientOne.shubNiggurath.awaken.flavor");
        ancientOneInfo.setEndGameText("ancientOne.shubNiggurath.endGame");
        ancientOneInfo.setMythosCardCount(16);
        ancientOneInfo.setRemovedMonsters(Arrays.asList(
                NonEpicMonsterId.GHOUL, NonEpicMonsterId.GHOUL,
                NonEpicMonsterId.GOAT_SPAWN, NonEpicMonsterId.GOAT_SPAWN, NonEpicMonsterId.DARK_YOUNG));
        return ancientOneInfo;
    }

    public static AncientOneInfo createYogSothoth() {
        AncientOneInfoImpl ancientOneInfo = new AncientOneInfoImpl();
        ancientOneInfo.setAncientOneId(AncientOneId.YOG_SOTHOTH);
        ancientOneInfo.setStartingDoom(14);
        ancientOneInfo.setName("ancientOne.yogSothoth.name");
        ancientOneInfo.setAltName("ancientOne.yogSothoth.alt");
        ancientOneInfo.setMidnightText("ancientOne.yogSothoth.midnight");
        ancientOneInfo.setWinText("ancientOne.yogSothoth.win");
        ancientOneInfo.setMysteriesRequired(3);
        ancientOneInfo.setReckoningText("ancientOne.yogSothoth.reckoning");
        ancientOneInfo.setFlavorText("ancientOne.yogSothoth.flavor");
        ancientOneInfo.setEndGameText("ancientOne.yogSothoth.endGame");
        ancientOneInfo.setMythosCardCount(16);
        return ancientOneInfo;
    }

    public static AncientOneInfo createAwakenYogSothoth() {
        AncientOneInfoImpl ancientOneInfo = new AncientOneInfoImpl();
        ancientOneInfo.setAncientOneId(AncientOneId.YOG_SOTHOTH);
        ancientOneInfo.setAwaken(true);
        ancientOneInfo.setStartingDoom(0);
        ancientOneInfo.setName("ancientOne.yogSothoth.awaken.name");
        ancientOneInfo.setAltName("ancientOne.yogSothoth.awaken.alt");
        ancientOneInfo.setMidnightText("ancientOne.yogSothoth.awaken.midnight");
        ancientOneInfo.setWinText("ancientOne.yogSothoth.awaken.win");
        ancientOneInfo.setMysteriesRequired(4);
        ancientOneInfo.setReckoningText("ancientOne.yogSothoth.awaken.reckoning");
        ancientOneInfo.setFlavorText("ancientOne.yogSothoth.awaken.flavor");
        ancientOneInfo.setEndGameText("ancientOne.yogSothoth.endGame");
        ancientOneInfo.setMythosCardCount(16);
        return ancientOneInfo;
    }




    public static class AncientOneInfoImpl implements AncientOneInfo {

        private AncientOneId ancientOneId;
        private int startingDoom;
        private int mythosCardCount;
        private boolean awaken;
        private String setupText;
        private String name;
        private String altName;
        private String specialText;
        private String midnightText;
        private String reckoningText;
        private String winText;
        private String flavorText;
        private List<MonsterId> removedMonsters = new LinkedList<>();
        private int power;
        private int mysteriesRequired;
        private String endGameText;

        @Override
        public AncientOneId getAncientOneId() {
            return ancientOneId;
        }

        void setAncientOneId(AncientOneId ancientOneId) {
            this.ancientOneId = ancientOneId;
        }

        public void setAwaken(boolean awaken) {
            this.awaken = awaken;
        }

        @Override
        public boolean isAwaken() {
            return awaken;
        }

        @Override
        public int getStartingDoom() {
            return startingDoom;
        }

        @Override
        public int getMythosCardCount() {
            return mythosCardCount;
        }

        public void setMythosCardCount(int mythosCardCount) {
            this.mythosCardCount = mythosCardCount;
        }

        void setStartingDoom(int startingDoom) {
            this.startingDoom = startingDoom;
        }

        @Override
        public String toString() {
            return ancientOneId.name();
        }

        @Override
        public String getSetupText() {
            return setupText;
        }

        public void setSetupText(String setupText) {
            this.setupText = setupText;
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getAltName() {
            return altName;
        }

        public void setAltName(String altName) {
            this.altName = altName;
        }

        @Override
        public String getSpecialText() {
            return specialText;
        }

        public void setSpecialText(String specialText) {
            this.specialText = specialText;
        }

        @Override
        public String getMidnightText() {
            return midnightText;
        }

        public void setMidnightText(String midnightText) {
            this.midnightText = midnightText;
        }

        @Override
        public String getReckoningText() {
            return reckoningText;
        }

        public void setReckoningText(String reckoningText) {
            this.reckoningText = reckoningText;
        }

        @Override
        public String getWinText() {
            return winText;
        }

        public void setWinText(String winText) {
            this.winText = winText;
        }

        @Override
        public String getFlavorText() {
            return flavorText;
        }

        public void setFlavorText(String flavorText) {
            this.flavorText = flavorText;
        }

        @Override
        public List<MonsterId> getRemovedMonsters() {
            return removedMonsters;
        }

        public void setRemovedMonsters(List<MonsterId> removedMonsters) {
            this.removedMonsters = removedMonsters;
        }

        @Override
        public int getPower() {
            return power;
        }

        public void setPower(int power) {
            this.power = power;
        }

        @Override
        public int getMysteriesRequired() {
            return mysteriesRequired;
        }

        public void setMysteriesRequired(int mysteriesRequired) {
            this.mysteriesRequired = mysteriesRequired;
        }

        @Override
        public String getEndGameText() {
            return endGameText;
        }

        public void setEndGameText(String endGameText) {
            this.endGameText = endGameText;
        }
    }
}
