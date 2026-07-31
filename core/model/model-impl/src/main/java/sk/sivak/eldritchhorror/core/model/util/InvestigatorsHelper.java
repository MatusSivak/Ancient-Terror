package sk.sivak.eldritchhorror.core.model.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.model.Investigator;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SAN_FRANCISCO;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_16;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_19;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_21;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_3;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_5;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_6;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.THE_PYRAMIDS;

/**
 * @author msivak
 */
public class InvestigatorsHelper {

    private static final Logger logger = LogManager.getLogger(InvestigatorsHelper.class);

    public static List<InvestigatorInfo> initAvailableInvestigators(boolean bonusInvestigatorsPurchased) {
        List<InvestigatorInfoImpl> response = new ArrayList<>();
        response.add(createBountyHunter());
        response.add(createSpy());
        response.add(createTheEntertainer());
        response.add(createAstronomer());
        response.add(createSailor());
        response.add(createPsychic());
        response.add(createExpeditionLeader());
        response.add(createExConvict());
        response.add(createSoldier());
        response.add(createMusician());
        response.add(createPolitician());
        response.add(createMartialArtist());
        response.add(createRedeemedCultist());
        response.add(createActress());
        response.add(createShaman());
        response.add(createRookieCop());

        // In-App Purchase
        if (bonusInvestigatorsPurchased) {
            response.add(createBootlegger());
            response.add(createHandyman());
            response.add(createViolinist());
            response.add(createWaitress());
        }

        for (InvestigatorInfoImpl investigatorInfo : response) {
            investigatorInfo.setBio(InvestigatorBioHelper.getBio(investigatorInfo.getInvestigatorId()));
        }
        logger.info("Created " + response.size() + " available investigators");
        Collections.shuffle(response);
        return new LinkedList<>(response);
    }

    public static List<? extends InvestigatorInfo> initLockedInvestigators() {
        ArrayList<InvestigatorInfoImpl> response = new ArrayList<>();
        response.add(createBootlegger());
        response.add(createHandyman());
        response.add(createViolinist());
        response.add(createWaitress());
        for (InvestigatorInfoImpl investigatorInfo : response) {
            investigatorInfo.setBio(InvestigatorBioHelper.getBio(investigatorInfo.getInvestigatorId()));
        }
        return response;
    }


    private static InvestigatorInfoImpl createExpeditionLeader() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_EXPEDITION_LEADER);
        investigatorInfo.setInvestigatorName("Leo Anderson");
        investigatorInfo.setMaxHealth(6);
        investigatorInfo.setMaxSanity(6);
        investigatorInfo.setLocationId(LocationId.BUENOS_AIRES);
        investigatorInfo.setStats(2, 2, 3, 3, 3);
        investigatorInfo.setActionText("investigator.special.the_expedition_leader.action");
        investigatorInfo.setAbilityText("investigator.special.the_expedition_leader.ability");
        investigatorInfo.setQuote("Keep moving.\nYou can die on your own time.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createMusician() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_MUSICIAN);
        investigatorInfo.setInvestigatorName("Jim Culver");
        investigatorInfo.setMaxHealth(7);
        investigatorInfo.setMaxSanity(5);
        investigatorInfo.setLocationId(SPACE_6);
        investigatorInfo.setStats(3, 3, 2, 2, 3);
        investigatorInfo.setActionText("investigator.special.the_musician.action");
        investigatorInfo.setAbilityText("investigator.special.the_musician.ability");
        investigatorInfo.setQuote("No, not quiet at all.\nDead folks get downright rambunctious\nwhen I play my horn.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createMartialArtist() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_MARTIAL_ARTIST);
        investigatorInfo.setInvestigatorName("Lily Chen");
        investigatorInfo.setMaxHealth(6);
        investigatorInfo.setMaxSanity(6);
        investigatorInfo.setLocationId(LocationId.SHANGHAI);
        investigatorInfo.setStats(2, 2, 2, 4, 3);
        investigatorInfo.setActionText("investigator.special.the_martial_artist.action");
        investigatorInfo.setAbilityText("investigator.special.the_martial_artist.ability");
        investigatorInfo.setQuote("I have been preparing to confront this evil\nfor my entire life.\nMy focus must be absolute.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createRookieCop() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_ROOKIE_COP);
        investigatorInfo.setInvestigatorName("Tommy Muldoon");
        investigatorInfo.setMaxHealth(7);
        investigatorInfo.setMaxSanity(5);
        investigatorInfo.setLocationId(LocationId.SPACE_1);
        investigatorInfo.setStats(2, 3, 3, 3, 2);
        investigatorInfo.setActionText("investigator.special.the_rookie_cop.action");
        investigatorInfo.setAbilityText("investigator.special.the_rookie_cop.ability");
        investigatorInfo.setQuote("I took an oath to enforce the law.\nThat's what I'm doing.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createBootlegger() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_BOOTLEGGER);
        investigatorInfo.setInvestigatorName("Finn Edwards");
        investigatorInfo.setMaxHealth(6);
        investigatorInfo.setMaxSanity(6);
        investigatorInfo.setLocationId(LocationId.SPACE_5);
        investigatorInfo.setStats(2, 3, 3, 2, 3);
        investigatorInfo.setActionText("investigator.special.the_bootlegger.action");
        investigatorInfo.setAbilityText("investigator.special.the_bootlegger.ability");
        investigatorInfo.setQuote("Never lose track of the exit\nor the merchandise.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createHandyman() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_HANDYMAN);
        investigatorInfo.setInvestigatorName("Wilson Richards");
        investigatorInfo.setMaxHealth(8);
        investigatorInfo.setMaxSanity(4);
        investigatorInfo.setLocationId(LocationId.ARKHAM);
        investigatorInfo.setStats(1, 3, 2, 4, 3);
        investigatorInfo.setActionText("investigator.special.the_handyman.action");
        investigatorInfo.setAbilityText("investigator.special.the_handyman.ability");
        investigatorInfo.setQuote("It's nothing a good plan\nand some hard work can't fix.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createViolinist() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_VIOLINIST);
        investigatorInfo.setInvestigatorName("Patrice Hathaway");
        investigatorInfo.setMaxHealth(5);
        investigatorInfo.setMaxSanity(7);
        investigatorInfo.setLocationId(LocationId.SYDNEY);
        investigatorInfo.setStats(3, 2, 3, 1, 4);
        investigatorInfo.setActionText("investigator.special.the_violinist.action");
        investigatorInfo.setAbilityText("investigator.special.the_violinist.ability");
        investigatorInfo.setQuote("When I play the violin,\nthe music echoes in other worlds.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createWaitress() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_WAITRESS);
        investigatorInfo.setInvestigatorName("Agnes Baker");
        investigatorInfo.setMaxHealth(7);
        investigatorInfo.setMaxSanity(5);
        investigatorInfo.setLocationId(LocationId.LONDON);
        investigatorInfo.setStats(4, 3, 2, 2, 2);
        investigatorInfo.setActionText("investigator.special.the_waitress.action");
        investigatorInfo.setAbilityText("investigator.special.the_waitress.ability");
        investigatorInfo.setQuote("I remember another life,\none of sorcery and conquest.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createRedeemedCultist() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_REDEEMED_CULTIST);
        investigatorInfo.setInvestigatorName("Diana Stanley");
        investigatorInfo.setMaxHealth(7);
        investigatorInfo.setMaxSanity(5);
        investigatorInfo.setLocationId(LocationId.SPACE_7);
        investigatorInfo.setStats(4, 2, 3, 3, 1);
        investigatorInfo.setActionText("investigator.special.the_redeemed_cultist.action");
        investigatorInfo.setAbilityText("investigator.special.the_redeemed_cultist.ability");
        investigatorInfo.setQuote("The Lodge is not as innocent as they pretend.\n" +
                "I have learned that nothing is ever as it seems,\nmyself included.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createActress() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_ACTRESS);
        investigatorInfo.setInvestigatorName("Lola Hayes");
        investigatorInfo.setMaxHealth(5);
        investigatorInfo.setMaxSanity(7);
        investigatorInfo.setLocationId(LocationId.TOKYO);
        investigatorInfo.setStats(2, 4, 2, 2, 3);
        investigatorInfo.setActionText("investigator.special.the_actress.action");
        investigatorInfo.setAbilityText("investigator.special.the_actress.ability");
        investigatorInfo.setQuote("I've played so many roles.\n" +
                "Madness is to be expected.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createShaman() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_SHAMAN);
        investigatorInfo.setInvestigatorName("Akachi Onyele");
        investigatorInfo.setMaxHealth(5);
        investigatorInfo.setMaxSanity(7);
        investigatorInfo.setLocationId(LocationId.SPACE_15);
        investigatorInfo.setStats(3, 2, 2, 2, 4);
        investigatorInfo.setActionText("investigator.special.the_shaman.action");
        investigatorInfo.setAbilityText("investigator.special.the_shaman.ability");
        investigatorInfo.setQuote("I will journey to the lands beyond.\nI do not fear them.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createSpy() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_SPY);
        investigatorInfo.setInvestigatorName("Trish Scarborough");
        investigatorInfo.setMaxHealth(7);
        investigatorInfo.setMaxSanity(5);
        investigatorInfo.setLocationId(SPACE_16);
        investigatorInfo.setStats(1, 3, 4, 3, 2);
        investigatorInfo.setActionText("investigator.special.the_spy.action");
        investigatorInfo.setAbilityText("investigator.special.the_spy.ability");
        investigatorInfo.setQuote("We lie all the time.\nBut the truth is in there.\nYou just have to know how to decode people.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createAstronomer() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_ASTRONOMER);
        investigatorInfo.setInvestigatorName("Norman Withers");
        investigatorInfo.setMaxHealth(5);
        investigatorInfo.setMaxSanity(7);
        investigatorInfo.setLocationId(LocationId.ARKHAM);
        investigatorInfo.setStats(3, 1, 3, 2, 4);
        investigatorInfo.setActionText("investigator.special.the_astronomer.action");
        investigatorInfo.setAbilityText("investigator.special.the_astronomer.ability");
        investigatorInfo.setQuote("Let them call me a crackpot!\nSomething is happening to the stars,\nand I am not imagining it.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createTheEntertainer() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_ENTERTAINER);
        investigatorInfo.setInvestigatorName("Marie Lambeau");
        investigatorInfo.setMaxHealth(6);
        investigatorInfo.setMaxSanity(6);
        investigatorInfo.setLocationId(LocationId.SPACE_20);
        investigatorInfo.setStats(3, 4, 2, 2, 2);
        investigatorInfo.setActionText("investigator.special.the_entertainer.action");
        investigatorInfo.setAbilityText("investigator.special.the_entertainer.ability");
        investigatorInfo.setQuote("I used to sing as grand-mere made\npotions and charms.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createSoldier() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_SOLDIER);
        investigatorInfo.setInvestigatorName("Mark Harrigan");
        investigatorInfo.setMaxHealth(8);
        investigatorInfo.setMaxSanity(4);
        investigatorInfo.setLocationId(LocationId.SPACE_14);
        investigatorInfo.setStats(1, 2, 2, 4, 4);
        investigatorInfo.setActionText("investigator.special.the_soldier.action");
        investigatorInfo.setAbilityText("investigator.special.the_soldier.ability");
        investigatorInfo.setQuote("I'm walking out that door,\nand I'm taking this book.\nAnyone who wants to stop me\nis welcome to try.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createSailor() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_SAILOR);
        investigatorInfo.setInvestigatorName("Silas Marsh");
        investigatorInfo.setMaxHealth(8);
        investigatorInfo.setMaxSanity(4);
        investigatorInfo.setLocationId(LocationId.SYDNEY);
        investigatorInfo.setStats(1, 3, 3, 3, 3);
        investigatorInfo.setActionText("investigator.special.the_sailor.action");
        investigatorInfo.setAbilityText("investigator.special.the_sailor.ability");
        investigatorInfo.setQuote("Leave your fears on the docks, lads.\nI'll not carry that cargo.\nThe wind is up! Full sail!");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createPolitician() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_POLITICIAN);
        investigatorInfo.setInvestigatorName("Charlie Kane");
        investigatorInfo.setLocationId(LocationId.SAN_FRANCISCO);
        investigatorInfo.setMaxHealth(4);
        investigatorInfo.setMaxSanity(8);
        investigatorInfo.setStats(2, 4, 3, 2, 2);
        investigatorInfo.setActionText("investigator.special.the_politician.action");
        investigatorInfo.setAbilityText("investigator.special.the_politician.ability");
        investigatorInfo.setQuote("It can be arranged.\nIt's just a matter of acceptable terms.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createPsychic() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_PSYCHIC);
        investigatorInfo.setInvestigatorName("Jacqueline Fine");
        investigatorInfo.setLocationId(LocationId.SPACE_5);
        investigatorInfo.setMaxHealth(4);
        investigatorInfo.setMaxSanity(8);
        investigatorInfo.setStats(4, 2, 3, 1, 3);
        investigatorInfo.setActionText("investigator.special.the_psychic.action");
        investigatorInfo.setAbilityText("investigator.special.the_psychic.ability");
        investigatorInfo.setQuote("The visions are a warning.\nThe future can be rewritten.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createExConvict() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_EX_CONVICT);
        investigatorInfo.setInvestigatorName("\"Skids\" O' Toole");
        investigatorInfo.setLocationId(LocationId.BUENOS_AIRES);
        investigatorInfo.setMaxHealth(6);
        investigatorInfo.setMaxSanity(6);
        investigatorInfo.setStats(2, 1, 3, 4, 3);
        investigatorInfo.setActionText("investigator.special.the_ex_convict.action");
        investigatorInfo.setAbilityText("investigator.special.the_ex_convict.ability");
        investigatorInfo.setQuote("I didn't get out of the joint\njust to watch the world end.");
        return investigatorInfo;
    }

    private static InvestigatorInfoImpl createBountyHunter() {
        InvestigatorInfoImpl investigatorInfo = new InvestigatorInfoImpl();
        investigatorInfo.setInvestigatorId(InvestigatorId.THE_BOUNTY_HUNTER);
        investigatorInfo.setInvestigatorName("Tony Morgan");
        investigatorInfo.setMaxHealth(7);
        investigatorInfo.setLocationId(LocationId.SPACE_7);
        investigatorInfo.setMaxSanity(5);
        investigatorInfo.setStats(2, 2, 4, 3, 2);
        investigatorInfo.setActionText("investigator.special.the_bounty_hunter.action");
        investigatorInfo.setAbilityText("investigator.special.the_bounty_hunter.ability");
        investigatorInfo.setQuote("When I find the beast,\nI'll trap it or put it down for good.");
        return investigatorInfo;
    }

    public static void initSelectedInvestigators(List<InvestigatorWrite> selectedInvestigators, InvestigatorInfo[] investigatorInfo) {
        for (int i = 0; i < investigatorInfo.length; i++) {
            InvestigatorWrite investigator = selectedInvestigators.get(i);
            investigator.setInfo(investigatorInfo[i]);
            investigator.setCurrentHealth(investigatorInfo[i].getMaxHealth());
            investigator.setCurrentSanity(investigatorInfo[i].getMaxSanity());
            investigator.setCurrentLocationId(investigatorInfo[i].getLocationId());
        }
    }

    public static void removeFromAvailable(InvestigatorInfo[] selected, List<? extends InvestigatorInfo> availableInvestigators) {
        for (InvestigatorInfo info : selected) {
            availableInvestigators.remove(info);
        }
    }

    public static List<InvestigatorWrite> initWithPlayers(int players) {
        LinkedList<InvestigatorWrite> result = new LinkedList<>();
        for (int i = 0; i < players; i++) {
            result.add(new Investigator());
        }
        return result;
    }

    public static InvestigatorWrite findLeadInvestigator(List<InvestigatorWrite> selectedInvestigators, InvestigatorId investigatorId) {
        for (InvestigatorWrite selectedInvestigator : selectedInvestigators) {
            if (selectedInvestigator.getInfo().getInvestigatorId().equals(investigatorId)) {
                logger.info("Lead investigator is now " + investigatorId);
                return selectedInvestigator;
            }
        }
        throw new IllegalArgumentException("Lead investigator not found!");
    }

    public static class InvestigatorInfoImpl implements InvestigatorInfo {

        private InvestigatorId investigatorId;
        private String investigatorName;
        private int maxHealth;
        private int maxSanity;
        private HashMap<Stat, Integer> statMap = new HashMap<>();
        private LocationId locationId;
        private String actionText;
        private String abilityText;
        private String quote;
        private String bio;

        public InvestigatorId getInvestigatorId() {
            return investigatorId;
        }

        void setInvestigatorId(InvestigatorId investigatorId) {
            this.investigatorId = investigatorId;
        }

        @Override
        public String getInvestigatorName() {
            return investigatorName;
        }

        void setInvestigatorName(String investigatorName) {
            this.investigatorName = investigatorName;
        }

        @Override
        public int getMaxHealth() {
            return maxHealth;
        }

        void setMaxHealth(int maxHealth) {
            this.maxHealth = maxHealth;
        }

        @Override
        public LocationId getLocationId() {
            return locationId;
        }

        public void setLocationId(LocationId locationId) {
            this.locationId = locationId;
        }

        @Override
        public int getMaxSanity() {
            return maxSanity;
        }

        void setMaxSanity(int maxSanity) {
            this.maxSanity = maxSanity;
        }

        void setStats(int lore, int influence, int observation, int strength, int will) {
            statMap.put(Stat.LORE, lore);
            statMap.put(Stat.INFLUENCE, influence);
            statMap.put(Stat.OBSERVATION, observation);
            statMap.put(Stat.STRENGTH, strength);
            statMap.put(Stat.WILL, will);
        }

        @Override
        public int getBaseStat(Stat stat) {
            return statMap.get(stat);
        }

        @Override
        public String getActionText() {
            return actionText;
        }

        public void setActionText(String actionText) {
            this.actionText = actionText;
        }

        @Override
        public String getAbilityText() {
            return abilityText;
        }

        public void setAbilityText(String abilityText) {
            this.abilityText = abilityText;
        }

        @Override
        public String toString() {
            return investigatorId.toString();
        }

        @Override
        public String getQuote() {
            return quote;
        }

        public void setQuote(String quote) {
            this.quote = quote;
        }

        @Override
        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }
    }
}
