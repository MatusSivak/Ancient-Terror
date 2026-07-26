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
        ancientOneInfo.setSetupText("Token is placed\non the Green Omen.");
        ancientOneInfo.setName("Azathoth");
        ancientOneInfo.setAltName("The Daemon Sultan");
        ancientOneInfo.setSpecialText("Doom advances for each\nToken on Green Omen.");
        ancientOneInfo.setMidnightText("The World is Devoured!");
        ancientOneInfo.setWinText("Solve 3 Mysteries.");
        ancientOneInfo.setMysteriesRequired(3);
        ancientOneInfo.setFlavorText("Out in the mindless void the daemon bore me,\n" +
                "Past the bright clusters of dimensioned space,\n" +
                "Till neither time nor matter stretched before me,\n" +
                "But only Chaos, without form or place.");
        ancientOneInfo.setEndGameText("[RED]The World is Devoured![]\n \n" +
                "Earthquakes and volcanic eruptions tear the Earth's crust apart.\nYou lost the game.");
        ancientOneInfo.setMythosCardCount(16);
        return ancientOneInfo;
    }

    public static AncientOneInfo createCthulhu() {
        AncientOneInfoImpl ancientOneInfo = new AncientOneInfoImpl();
        ancientOneInfo.setAncientOneId(AncientOneId.CTHULHU);
        ancientOneInfo.setStartingDoom(12);
        ancientOneInfo.setName("Cthulhu");
        ancientOneInfo.setAltName("The Great Dreamer");
        ancientOneInfo.setSpecialText("When an investigator moves into a vortex,\nhe becomes Delayed and loses one Sanity.");
        ancientOneInfo.setMidnightText("Cthulhu awakens!");
        ancientOneInfo.setWinText("Solve 3 Mysteries.");
        ancientOneInfo.setMysteriesRequired(3);
        ancientOneInfo.setReckoningText("Vortex is spawned under each investigator on a Sea.");
        ancientOneInfo.setFlavorText("In his house at R'lyeh\n" +
                "dead Cthulhu waits dreaming.\n" +
                "Not dead which eternal lie.\n" +
                "Stranger eons death may die.");
        ancientOneInfo.setEndGameText("[RED]The great priest Cthulhu rises and brings the earth beneath his sway.[]\n \nYou lost the game.");
        ancientOneInfo.setMythosCardCount(15);
        ancientOneInfo.setRemovedMonsters(Arrays.asList(NonEpicMonsterId.STAR_SPAWN, NonEpicMonsterId.DEEP_ONE));
        return ancientOneInfo;
    }

    public static AncientOneInfo createAwakenCthulhu() {
        AncientOneInfoImpl ancientOneInfo = new AncientOneInfoImpl();
        ancientOneInfo.setAncientOneId(AncientOneId.CTHULHU);
        ancientOneInfo.setAwaken(true);
        ancientOneInfo.setStartingDoom(0);
        ancientOneInfo.setName("Cthulhu - Awaken");
        ancientOneInfo.setAltName("High Priest of the Great Old Ones");
        ancientOneInfo.setSpecialText("When an investigator moves into a vortex, he becomes\nDelayed and loses one Sanity. Cthulhu's power rises!");
        ancientOneInfo.setMidnightText("Each time Doom would advance,\nCthulhu's power rises instead!");
        ancientOneInfo.setWinText("Solve 3 Mysteries.\nDefeat Cthulhu.");
        ancientOneInfo.setMysteriesRequired(4);
        ancientOneInfo.setReckoningText("Each investigator loses Sanity equal to Cthulhu's power.");
        ancientOneInfo.setFlavorText("...morals thrown aside and all men shouting and killing and revelling in joy.\n" +
                "...all the earth would flame with a holocaust of ecstasy and freedom.");
        ancientOneInfo.setEndGameText("[RED]The great priest Cthulhu rises and brings the earth beneath his sway.[]\n \nYou lost the game.");
        ancientOneInfo.setMythosCardCount(15);
        ancientOneInfo.setRemovedMonsters(Arrays.asList(NonEpicMonsterId.STAR_SPAWN, NonEpicMonsterId.DEEP_ONE));
        return ancientOneInfo;
    }

    public static AncientOneInfo createShubNiggurath() {
        AncientOneInfoImpl ancientOneInfo = new AncientOneInfoImpl();
        ancientOneInfo.setAncientOneId(AncientOneId.SHUB_NIGGURATH);
        ancientOneInfo.setStartingDoom(13);
        ancientOneInfo.setName("Shub-Niggurath");
        ancientOneInfo.setAltName("The Black Goat of the Woods");
        ancientOneInfo.setMidnightText("Shub-Niggurath awakens!");
        ancientOneInfo.setWinText("Solve 3 Mysteries.");
        ancientOneInfo.setMysteriesRequired(3);
        ancientOneInfo.setReckoningText("Monster is spawned on a random space.\nDoom advances twice if there are 10+ monsters.");
        ancientOneInfo.setFlavorText(
                "The hellish cloud-like entity Shub-Niggurath,\n" +
                "in whose honor nameless cults hold the rite\n" +
                "of the Goat with a Thousand Young.");
        ancientOneInfo.setEndGameText("[RED]From the wildest corners of the earth, the dark young emerge to overwhelm humanity.[]\n \nYou lost the game.");
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
        ancientOneInfo.setName("Shub-Niggurath - Awaken");
        ancientOneInfo.setAltName("The Black Goat of the Woods\nwith a Thousand Young");
        ancientOneInfo.setMidnightText("Each time Doom would advance, Shub-Niggurath spawns one monster.\n" +
                "If there are 5+ monsters with Shub-Niggurath, you lose the game.");
        ancientOneInfo.setWinText("Solve 3 Mysteries. Defeat Shub-Niggurath.");
        ancientOneInfo.setMysteriesRequired(4);
        ancientOneInfo.setReckoningText("Each investigator on a same space as Shub-Niggurath encounters it.");
        ancientOneInfo.setFlavorText("Ia! Shub-Niggurath! The Black Goat of the Woods with a Thousand Young!");
        ancientOneInfo.setEndGameText("[RED]From the wildest corners of the earth, the dark young emerge to overwhelm humanity.[]\n \nYou lost the game.");
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
        ancientOneInfo.setName("Yog-Sothoth");
        ancientOneInfo.setAltName("The Lurker at the Threshold");
        ancientOneInfo.setMidnightText("Yog-Sothoth awakens!");
        ancientOneInfo.setWinText("Solve 3 Mysteries.");
        ancientOneInfo.setMysteriesRequired(3);
        ancientOneInfo.setReckoningText("Doom advances for each investigator on a Gate,\nunless he discards one Spell.");
        ancientOneInfo.setFlavorText(
                "Yog-Sothoth knows the gate.\nYog-Sothoth is the gate.\nYog-Sothoth is the key and guardian of the gate.\nPast, present, future, all are one in Yog-Sothoth.");
        ancientOneInfo.setEndGameText("[RED]Yog-Sothoth sweeps away the sane mortal races, ushering in an age of darkness, madness, and utter, insane evil.[]\n \nYou lost the game.");
        ancientOneInfo.setMythosCardCount(16);
        return ancientOneInfo;
    }

    public static AncientOneInfo createAwakenYogSothoth() {
        AncientOneInfoImpl ancientOneInfo = new AncientOneInfoImpl();
        ancientOneInfo.setAncientOneId(AncientOneId.YOG_SOTHOTH);
        ancientOneInfo.setAwaken(true);
        ancientOneInfo.setStartingDoom(0);
        ancientOneInfo.setName("Yog-Sothoth - Awaken");
        ancientOneInfo.setAltName("The Eater of Souls");
        ancientOneInfo.setMidnightText("Each time Doom would advance, Yog-Sothoth's power rises instead!\nIn it's full power, you lose the game.");
        ancientOneInfo.setWinText("Solve 4 Mysteries.");
        ancientOneInfo.setMysteriesRequired(4);
        ancientOneInfo.setReckoningText("Yog-Sothoth's power rises for each investigator on a Gate,\nunless he discards one Spell.");
        ancientOneInfo.setFlavorText(
                "The noxious Yog-Sothoth,\nwho froths as primal slime in nuclear chaos\nbeyond the nethermost outposts of space and time!");
        ancientOneInfo.setEndGameText("[RED]Yog-Sothoth sweeps away the sane mortal races, ushering in an age of darkness, madness, and utter, insane evil.[]\n \nYou lost the game.");
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
