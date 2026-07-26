package sk.sivak.eldritchhorror.core.model.util;

import java8.features.function.Supplier;
import sk.sivak.eldritchhorror.core.constants.MysteryCardId;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.Azathoth.Cthulhu;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.Azathoth.OCCULT_RESEARCH;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.Azathoth.OMEN_OF_DEVASTATION;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.Azathoth.SEED_OF_THE_DAEMON_SULTAN;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.Azathoth.THE_GREEN_FLAME;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.Azathoth.THE_TRUE_NAME;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.Azathoth.VOICE_OF_AZATHOTH;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.ShubNiggurath.BATTLE_IN_THE_WOODS;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.ShubNiggurath.BLASPHEMY_OF_THE_BLACK_GOAT;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.ShubNiggurath.HOUR_OF_THE_MOON_LENS;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.ShubNiggurath.HUNTING_THE_THOUSAND;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.ShubNiggurath.NATURE_OF_THE_ALL_MOTHER;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.ShubNiggurath.RITUALS_IN_THE_WILD;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.ShubNiggurath.SPAWN_OF_THE_BLACK_GOAT;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.YogSothoth.ARCANE_UNDERSTANDING;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.YogSothoth.SPAWN_OF_YOG_SOTHOTH;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.YogSothoth.THE_BEYOND_ONE;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.YogSothoth.THE_KEY_AND_THE_GATE;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.YogSothoth.THE_STONE_CIRCLES;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.YogSothoth.VOID_BETWEEN_WORLDS;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.YogSothoth.WHERE_THE_OLD_ONES_BROKE_THROUGH;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_10;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_21;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_4;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.TUNGUSKA;

/**
 * @author msivak
 */
public class MysteryDeckHelper {

    public List<MysteryCardInfo> initMysteryDeck(AncientOneId ancientOneId, Integer nrOfInvestigators) {
        switch (ancientOneId) {
            case AZATHOTH:
                return initAzathothMysteryDeck(nrOfInvestigators);
            case CTHULHU:
                return initCthulhuMysteryDeck(nrOfInvestigators);
            case SHUB_NIGGURATH:
                return initShubNiggurathMysteryDeck(nrOfInvestigators);
            case YOG_SOTHOTH:
                return initYogSothothMysteryDeck(nrOfInvestigators);
        }
        throw new IllegalArgumentException(ancientOneId.name());
    }

    private List<MysteryCardInfo> initAzathothMysteryDeck(Integer nrOfInvestigators) {
        LinkedList<MysteryCardInfo> result = new LinkedList<>();

        result.add(buildTheGreenFlameMystery(nrOfInvestigators));
        result.add(buildOccultResearchMystery(nrOfInvestigators));
        result.add(buildSeedOfTheDaemonSultanMystery(nrOfInvestigators));
        result.add(buildOmenOfDevestationMystery(nrOfInvestigators));
        result.add(buildTheTrueNameMystery(nrOfInvestigators));
        result.add(buildVoiceOfAzathothMystery(nrOfInvestigators));

        Collections.shuffle(result);

        if (!GoogleServicesHolder.isTutorialPassed()) {
            result.clear();
            result.add(0, buildSeedOfTheDaemonSultanMystery(nrOfInvestigators));
        }

        return result;
    }

    private MysteryCardInfo buildSeedOfTheDaemonSultanMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl azathothCard = createAzathothCard();
        azathothCard.setMysteryCardId(SEED_OF_THE_DAEMON_SULTAN);
        azathothCard.setName("Seed of the Daemon Sultan");
        azathothCard.setFlavorText("A strange green comet has fallen from the sky.\n" +
                "Even after the impact,\n" +
                "the meteorite continues to slowly burrow itself deeper into the earth.");
        azathothCard.setMysteryText("As an encounter, an investigator on Tunguska\n" +
                "may attempt to search for signs of the impact\n" +
                "in the dead forest (Test Observation).\n" +
                "If he passes, he discovers the strange green meteorite;\n" +
                "he may spend 2 Clues to advance the Active Mystery.");
        azathothCard.setMysteryComplexity(Math.round(nrOfInvestigators / 2f));
        azathothCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.TUNGUSKA));
        return azathothCard;
    }

    private MysteryCardInfo buildOmenOfDevestationMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl azathothCard = createAzathothCard();
        azathothCard.setMysteryCardId(OMEN_OF_DEVASTATION);
        azathothCard.setName("Omen of Devastation");
        azathothCard.setFlavorText("The Nemesis Moon appears in the night sky,\nvisible to all and heralding an imminent doom.");
        azathothCard.setMysteryText("When an investigator closes a Gate\nrepresenting current Omen,\nhe may spend 1 Clue to advance the Active Mystery.");
        azathothCard.setMysteryComplexity(Math.round(nrOfInvestigators / 2f));
        return azathothCard;
    }

    private MysteryCardInfo buildOccultResearchMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl azathothCard = createAzathothCard();
        azathothCard.setMysteryCardId(OCCULT_RESEARCH);
        azathothCard.setName("Occult Research");
        azathothCard.setFlavorText("The shan are controlling innocent victims everywhere,\nusing them to call forth Azathoth");
        azathothCard.setMysteryText("After an investigator resolves a Research Encounter,\nhe may spend 1 Clue he gained from that encounter\nto advance the Active Mystery.");
        azathothCard.setMysteryComplexity(nrOfInvestigators);
        return azathothCard;
    }

    private MysteryCardInfo buildTheTrueNameMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl azathothCard = createAzathothCard();
        azathothCard.setMysteryCardId(THE_TRUE_NAME);
        azathothCard.setName("The True Name");
        azathothCard.setFlavorText("Fragments of Azathoth's true name exist in long forgotten tomes.\n" +
                "It is said that if a person speaks his name aloud three times,\nthe blasphemous sound will grant the speaker great power.");
        azathothCard.setMysteryText("As an encounter, an investigator on a Pin may attempt to find information about Azathoth's true name;\n" +
                "he may spend 2 Clues to advance the Active Mystery.");
        int complexity = Math.round(nrOfInvestigators / 2f);
        azathothCard.setMysteryComplexity(complexity);
        List<LocationId> pinLocations = LocationId.getRandomLocations(complexity);
        azathothCard.setPinLocationsSupplier(() -> pinLocations);
        return azathothCard;
    }

    private MysteryCardInfo buildTheGreenFlameMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl azathothCard = createAzathothCard();
        azathothCard.setName("The Green Flame");
        azathothCard.setMysteryCardId(THE_GREEN_FLAME);
        azathothCard.setFlavorText("In response to the cult's invocation,\n" +
                "a jet of green flame emerged from the fissure,\n" +
                "racing across the ceiling and walls as if a living thing.");
        azathothCard.setMysteryText("At the end of the Mythos Phase, if the Tulzscha Epic Monster has been defeated, solve this Mystery.");
        azathothCard.setMysteryComplexity(nrOfInvestigators + 2);
        List<LocationId> pinLocations = LocationId.getRandomLocations(1);
        azathothCard.setPinLocationsSupplier(() -> pinLocations);
        return azathothCard;
    }

    private MysteryCardInfo buildVoiceOfAzathothMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl azathothCard = createAzathothCard();
        azathothCard.setName("Voice of Azathoth");
        azathothCard.setMysteryCardId(VOICE_OF_AZATHOTH);
        azathothCard.setFlavorText("Most musicians describe this 18th century Italian opera as unplayable.\n" +
                "It comes as no surprise that the composer was executed for heresy.");
        azathothCard.setMysteryText("When an investigator would gain an Artifact, he may gain\n" +
                "the 'Requiem per Shuggay' Artifact instead.\n" +
                "At the end of the Mythos Phase,\n" +
                "an investigator may spend COMPLEXITY Clues\n" +
                "and discard the 'Requiem per Shuggay' Artifact\n" +
                "to solve this Mystery.");
        azathothCard.setMysteryComplexity(nrOfInvestigators);
        return azathothCard;
    }

    // Cthulhu
    private List<MysteryCardInfo> initCthulhuMysteryDeck(Integer nrOfInvestigators) {
        LinkedList<MysteryCardInfo> result = new LinkedList<>();

        result.add(buildQueenOfTheDeepOnesMystery(nrOfInvestigators));
        result.add(buildThreateningSeasMystery(nrOfInvestigators));
        result.add(buildTheDeepOnesAttackMystery(nrOfInvestigators));
        result.add(buildTheStarsAreRightMystery(nrOfInvestigators));
        result.add(buildWatchingTheStarsMystery(nrOfInvestigators));
        result.add(buildRlyehRisenMystery(nrOfInvestigators));

        Collections.shuffle(result);

        result.add(3, buildRisenFromTheSeaMystery(nrOfInvestigators));

        return result;
    }

    private MysteryCardInfo buildQueenOfTheDeepOnesMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createCthulhuCard();
        mysteryCard.setName("Queen of the Deep Ones");
        mysteryCard.setMysteryCardId(Cthulhu.QUEEN_OF_THE_DEEP_ONES);
        mysteryCard.setFlavorText("Mother Hydra rises from her home in Y'ha-nthlei\n" +
                "to oversee the teeming masses of her children.");
        mysteryCard.setMysteryText("When an investigator would gain an Artifact, he may gain\n" +
                "the 'Sword of Y'ha-Talla' Artifact instead.\n" +
                "At the end of the Mythos Phase,\n" +
                "if the Hydra Epic Monster has been defeated,\n" +
                "solve this Mystery.");
        mysteryCard.setMysteryComplexity(nrOfInvestigators + 2);
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.SPACE_8));
        return mysteryCard;
    }

    private MysteryCardInfo buildThreateningSeasMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createCthulhuCard();
        mysteryCard.setName("Threatening Seas");
        mysteryCard.setMysteryCardId(Cthulhu.THREATENING_SEAS);
        mysteryCard.setFlavorText("All across the world, storms have formed\n" +
                        "over the many submerged cities that deep ones call home,\n" +
                        "such as Y'ha-nthlei and Ahu-Y'hloa.");
        mysteryCard.setMysteryComplexity(nrOfInvestigators);
        mysteryCard.setMysteryText("As an encounter, an investigator on a space with a vortex\n" +
                "may attempt to prolong Cthulhu's slumber (Test Lore-1).\n" +
                "If he passes, he may spend 1 Clue and discard 1 Spell\n" +
                "to close the vortex and advance the Active Mystery.");
        // this will be replaced by listener,
        // it is here just that load function knows that it should override locations
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.SPACE_1));
        return mysteryCard;
    }

    private MysteryCardInfo buildTheDeepOnesAttackMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createCthulhuCard();
        mysteryCard.setName("The Deep Ones Attack!");
        mysteryCard.setMysteryCardId(Cthulhu.THE_DEEP_ONES_ATTACK);
        mysteryCard.setFlavorText("Terrible storms have battered fishing villages across the globe,\n" +
                "but it's merely the prelude to an invading army of deep ones!");
        mysteryCard.setMysteryComplexity(nrOfInvestigators);
        mysteryCard.setMysteryText("As an encounter, an investigator on a space with a vortex\n" +
                "may attempt to fend off the deep one invasion.\n" +
                "If he defeats ambushing Deep One, he may spend 1 Clue\n" +
                "to close the vortex and advance the Active Mystery.");
        // this will be replaced by listener,
        // it is here just that load function knows that it should override locations
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.SPACE_1));
        return mysteryCard;
    }

    private MysteryCardInfo buildTheStarsAreRightMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createCthulhuCard();
        mysteryCard.setMysteryCardId(Cthulhu.THE_STARS_ARE_RIGHT);
        mysteryCard.setName("The Stars Are Right!");
        mysteryCard.setFlavorText("All around the world, Cthulhu's worshipers and\n" +
                "those sensitive to his dreams have been plagued by madness.\n" +
                "They rush to the sea to witness the Ancient One's return.");
        mysteryCard.setMysteryText("After an investigator resolves a Research Encounter,\nhe may spend 1 Clue he gained from that encounter\nto advance the Active Mystery.");
        mysteryCard.setMysteryComplexity(nrOfInvestigators);
        return mysteryCard;
    }

    private MysteryCardInfo buildWatchingTheStarsMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createCthulhuCard();
        mysteryCard.setName("Watching The Stars");
        mysteryCard.setMysteryCardId(Cthulhu.WATCHING_THE_STARS);
        mysteryCard.setFlavorText("From the sunken city of Yhe, Cthylla rises to fulfill her destiny.\n" +
                "Daughter of Cthulhu, she will give birth to the reincarnation\n" +
                "of her father should his body ever be destroyed.");
        mysteryCard.setMysteryText("At the end of the Mythos Phase, if the Cthylla Epic Monster has been defeated, solve this Mystery.");
        mysteryCard.setMysteryComplexity(nrOfInvestigators + 2);
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.SPACE_12));
        return mysteryCard;
    }

    private MysteryCardInfo buildRlyehRisenMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createCthulhuCard();
        mysteryCard.setName("R'lyeh Risen");
        mysteryCard.setMysteryCardId(Cthulhu.RLYEH_RISEN);
        mysteryCard.setFlavorText(
                "Eons ago, the city of R'lyeh was plunged to the bottom of the ocean\n" +
                "by some forgotten disaster. It has remained trapped there,\n" +
                "waiting for the right time to resurface.\n" +
                "Now, that moment has finally arrived.");
        mysteryCard.setMysteryText("As an encounter, an investigator on a Pin\n" +
                "may explore the island city of R'lyeh.\n" +
                "Successful encounter advances the Active Mystery.");
        mysteryCard.setMysteryComplexity(Math.round(nrOfInvestigators /2f));
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.SPACE_3));
        return mysteryCard;
    }

    // Final Mystery
    private MysteryCardInfo buildRisenFromTheSeaMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createCthulhuCard();
        mysteryCard.setMysteryCardId(Cthulhu.RISEN_FROM_THE_SEA);
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.SPACE_3));
        mysteryCard.setMysteryText("When the Cthulhu Epic Monster has been defeated,\nsolve this Mystery.");
        mysteryCard.setMysteryComplexity(nrOfInvestigators + 3);
        mysteryCard.setFlavorText("At last the time has come,\n" +
                "the deep ones have broken the elder sign\n" +
                "that kept their master asleep.\n" +
                "Cthulhu rises again,\n" +
                "and madness fills the dreams of every living thing.");
        mysteryCard.setName("Risen From the Sea");
        return mysteryCard;
    }

    // Shub-Niggurath
    private List<MysteryCardInfo> initShubNiggurathMysteryDeck(Integer nrOfInvestigators) {
        LinkedList<MysteryCardInfo> result = new LinkedList<>();

        result.add(buildBlasphemyOfTheBlackGoatMystery(nrOfInvestigators)); //ok
        result.add(buildHourOfTheMoonLensMystery(nrOfInvestigators)); // ok
        result.add(buildSpawnOfTheBlackGoatMystery(nrOfInvestigators)); //ok
        result.add(buildNatureOfTheAllMotherMystery(nrOfInvestigators)); //ok
        result.add(buildRitualsInTheWildMystery(nrOfInvestigators)); // ok
        result.add(buildHuntingTheThousandMystery(nrOfInvestigators));

        Collections.shuffle(result);

        result.add(3, buildBattleInTheWoodsMystery(nrOfInvestigators));

        return result;
    }

    private MysteryCardInfo buildBlasphemyOfTheBlackGoatMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createShubNiggurathCard();
        mysteryCard.setName("Blasphemy of the Black Goat");
        mysteryCard.setMysteryCardId(BLASPHEMY_OF_THE_BLACK_GOAT);
        mysteryCard.setFlavorText("The Black Litanies have been intoned\n" +
                "and Yeb has been called forth from the Black Fire\n" +
                "to purge the earth in preparation for Shub-Niggurath's arrival.");

        mysteryCard.setMysteryText("At the end of the Mythos Phase,\n" +
                "if the Yeb Epic Monster has been defeated,\n" +
                "solve this Mystery.");
        mysteryCard.setMysteryComplexity(nrOfInvestigators + 2);
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.SPACE_19));
        return mysteryCard;
    }

    private MysteryCardInfo buildHourOfTheMoonLensMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createShubNiggurathCard();
        mysteryCard.setMysteryCardId(HOUR_OF_THE_MOON_LENS);
        mysteryCard.setName("Hour of the Moon Lens");
        mysteryCard.setFlavorText("In the small village of Goatswood,\n" +
                        "a series of peculiar mirrors are arranged on top of a pole\n" +
                        "to focus moonlight through a lens.\n" +
                        "It is said that the Cult of the Black Goat\n" +
                        "uses this lens during their dark rites.");
        mysteryCard.setMysteryText("As an encounter, an investigator on London\n" +
                "may watch a ritual that transforms the worshipers\n" +
                "of Shub-Niggurath into goat spawn.\n" +
                "If he defeats ambushing Goat Spawn, he may spend 2 Clues\n" +
                "to advance the Active Mystery.");
        mysteryCard.setMysteryComplexity(Math.round(nrOfInvestigators / 2f));
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.LONDON));
        return mysteryCard;
    }

    private MysteryCardInfo buildSpawnOfTheBlackGoatMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createShubNiggurathCard();
        mysteryCard.setName("Spawn of the Black Goat");
        mysteryCard.setMysteryCardId(SPAWN_OF_THE_BLACK_GOAT);
        mysteryCard.setFlavorText(
                        "Shub-Niggurath's twin blasphemies\n" +
                        "will prepare the world for the Black Goat,\n" +
                        "destroying any potential challenge to their mother's reign.\n" +
                        "One of the pair, Nug, has begun its dreadful task.");

        mysteryCard.setMysteryText("At the end of the Mythos Phase,\n" +
                "if the Nug Epic Monster has been defeated,\n" +
                "solve this Mystery.");
        mysteryCard.setMysteryComplexity(nrOfInvestigators + 2);
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.THE_AMAZON));
        return mysteryCard;
    }

    private MysteryCardInfo buildNatureOfTheAllMotherMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createShubNiggurathCard();
        mysteryCard.setMysteryCardId(NATURE_OF_THE_ALL_MOTHER);
        mysteryCard.setName("Nature of the All-Mother");
        mysteryCard.setFlavorText("Shub-Niggurath naturally corrupts all life that encounters her.\n" +
                "Everywhere her worshipers are found,\n" +
                "all living things grow more vicious and feral.");
        mysteryCard.setMysteryText("After an investigator resolves a Research Encounter,\nhe may spend 1 Clue he gained from that encounter\nto advance the Active Mystery.");
        mysteryCard.setMysteryComplexity(nrOfInvestigators);
        return mysteryCard;
    }

    private MysteryCardInfo buildHuntingTheThousandMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createShubNiggurathCard();
        mysteryCard.setMysteryCardId(HUNTING_THE_THOUSAND);
        mysteryCard.setName("Hunting the Thousand");
        mysteryCard.setFlavorText("Shub-Niggurath's spawn, the Thousand Young,\n" +
                "infest the planet in greater and great numbers,\n" +
                "threatening to overrun humanity.");
        mysteryCard.setMysteryComplexity(nrOfInvestigators * 2);
        mysteryCard.setMysteryText("When a non Epic-Monster is defeated,\nthe active investigator may spend 2 Clues\nto advance the Active Mystery\nbased on monster's toughness.");
        return mysteryCard;
    }

    private MysteryCardInfo buildRitualsInTheWildMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createShubNiggurathCard();
        mysteryCard.setMysteryCardId(RITUALS_IN_THE_WILD);
        mysteryCard.setName("Rituals in the Wild");
        mysteryCard.setFlavorText(
                        "Bonfires roar in the wildest corners of the planet\n" +
                        "as the Cult of the Black Goat performs their bestial rituals.\n" +
                        "The cultist howl to Shub-Niggurath\n" +
                        "to let loose her children upon the Earth.");
        mysteryCard.setMysteryText("As an encounter, an investigator on a Pin\n" +
                "may attempt to determine\n" +
                "the ritual's purpose (Test Observation).\n" +
                "If he passes he may spend 2 Clues\n" +
                "to advance the Active Mystery.");
        int complexity = Math.round(nrOfInvestigators / 2f);
        mysteryCard.setMysteryComplexity(complexity);
        List<LocationId> pinLocations = Arrays.asList(SPACE_4, SPACE_10, SPACE_21, TUNGUSKA);
        mysteryCard.setPinLocationsSupplier(() -> pinLocations);
        return mysteryCard;
    }

    // Final Mystery
    private MysteryCardInfo buildBattleInTheWoodsMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createCthulhuCard();
        mysteryCard.setMysteryCardId(BATTLE_IN_THE_WOODS);
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.THE_HEART_OF_AFRICA));
        mysteryCard.setMysteryText("When the Shub-Niggurath Epic Monster\n has been defeated, solve this Mystery.");
        mysteryCard.setMysteryComplexity(nrOfInvestigators + 3);
        mysteryCard.setFlavorText(
                "The feral mother of all monstrosities has manifested!\n" +
                "She is an enormous cloud\n" +
                        "from which dozens of tentacles extend and retract.\n" +
                "Wherever she goes, she leaves behind large hoof prints.\n" +
                "To behold her true, terrifying form\n" +
                "is to come face to face with true, primal horror.");
        mysteryCard.setName("Battle in the Woods");
        return mysteryCard;
    }

    // Yog-Sothoth
    private List<MysteryCardInfo> initYogSothothMysteryDeck(Integer nrOfInvestigators) {
        LinkedList<MysteryCardInfo> result = new LinkedList<>();

        result.add(buildSpawnOfYogSothothMystery(nrOfInvestigators)); // ok
        result.add(buildWhereTheOldOnesBrokeThroughMystery(nrOfInvestigators)); // ok
        result.add(buildTheBeyondOneMystery(nrOfInvestigators)); // ok
        result.add(buildTheStoneCirclesMystery(nrOfInvestigators)); // ok
        result.add(buildVoidBetweenWorldsMystery(nrOfInvestigators)); // ok
        result.add(buildArcaneUnderstandingMystery(nrOfInvestigators)); // ok

        Collections.shuffle(result);

        result.add(3, buildTheKeyAndTheGateMystery(nrOfInvestigators));

        return result;
    }

    private MysteryCardInfo buildSpawnOfYogSothothMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createYogSothothCard();
        mysteryCard.setName("Spawn of Yog-Sothoth");
        mysteryCard.setMysteryCardId(SPAWN_OF_YOG_SOTHOTH);
        mysteryCard.setFlavorText("Lavinia Whateley has given birth to something horrible,\nand now the creature calls for its father...");

        mysteryCard.setMysteryText("At the end of the Mythos Phase,\n" +
                "if the Dunwich Horror Epic Monster has been defeated,\n" +
                "solve this Mystery.");
        mysteryCard.setMysteryComplexity(nrOfInvestigators + 2);
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.ARKHAM));
        return mysteryCard;
    }

    private MysteryCardInfo buildWhereTheOldOnesBrokeThroughMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl azathothCard = createYogSothothCard();
        azathothCard.setMysteryCardId(WHERE_THE_OLD_ONES_BROKE_THROUGH);
        azathothCard.setName("Where the Old Ones Broke Through");
        azathothCard.setFlavorText("Yog-Sothoth struggles to free itself\n" +
                "from the space between dimensions,\n" +
                "tearing holes in our reality in the process.");
        azathothCard.setMysteryText("When an investigator closes a Gate\nhe may discard two Spells\n" +
                "to seal the rift and advance the Active Mystery.");
        azathothCard.setMysteryComplexity(Math.round(nrOfInvestigators / 2f));
        return azathothCard;
    }

    private MysteryCardInfo buildTheBeyondOneMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl azathothCard = createYogSothothCard();
        azathothCard.setMysteryCardId(THE_BEYOND_ONE);
        azathothCard.setName("The Beyond One");
        azathothCard.setFlavorText("Yog-Sothoth exists in all places at all times.\n" +
                "To any soul foolish enough to seek it,\n" +
                "the Ancient One can unlock limitless knowledge and power,\n" +
                "but it always comes at a price.");
        azathothCard.setMysteryText("After an investigator resolves a Research Encounter,\nhe may spend 1 Clue he gained from that encounter\nto advance the Active Mystery.");
        azathothCard.setMysteryComplexity(nrOfInvestigators);
        return azathothCard;
    }

    private MysteryCardInfo buildTheStoneCirclesMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createYogSothothCard();
        mysteryCard.setMysteryCardId(THE_STONE_CIRCLES);
        mysteryCard.setName("The Stone Circles");
        mysteryCard.setFlavorText("Across the globe, Yog-Sothoth's worshipers\n" +
                "gather at ancient places of power,\n" +
                "each marked by a circle of massive stones,\n" +
                "placed by unknown forces thousands of years ago.");
        mysteryCard.setMysteryText("As an encounter, an investigator on a Pin\n" +
                "may attempt to disrupt the ritual (Test Lore-1).\n" +
                "If he passes, he may spend 2 Clues\n" +
                "to advance the Active Mystery.\n" +
                "If he fails, a Cultist Monster ambushes him!");
        int complexity = Math.round(nrOfInvestigators / 2f);
        mysteryCard.setMysteryComplexity(complexity);
        List<LocationId> pinLocations = LocationId.getRandomLocations(complexity);
        mysteryCard.setPinLocationsSupplier(() -> pinLocations);
        return mysteryCard;
    }

    private MysteryCardInfo buildVoidBetweenWorldsMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createYogSothothCard();
        mysteryCard.setName("Void Between Worlds");
        mysteryCard.setMysteryCardId(VOID_BETWEEN_WORLDS);
        mysteryCard.setFlavorText("It is said that long ago,\n" +
                        "Yog-Sothoth was imprisoned in a place utterly without existence,\n" +
                        "a strange void that occupies the space between dimensions.");
        mysteryCard.setMysteryText("As an encounter, an investigator on a Gate\n" +
                "may travel to the void between worlds.\n" +
                "Successful encounter advances the Active Mystery.");
        mysteryCard.setMysteryComplexity(Math.round(nrOfInvestigators /2f));
        return mysteryCard;
    }

    private MysteryCardInfo buildArcaneUnderstandingMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCardInfo = createYogSothothCard();
        mysteryCardInfo.setFlavorText("Humanity must not tamper with arcane energies.\n" +
                "Yog-Sothoth is the very essence of magic,\n" +
                "and a person under the influence of sorcery\n" +
                "will inescapably become the Ancient One's puppet.");
        mysteryCardInfo.setMysteryText("When an investigator passes a Lore test\n" +
                "while resolving a Spell effect,\n" +
                "he may spend one Clue and discard that Spell\n" +
                "to advance the Active Mystery.");
        mysteryCardInfo.setName("Arcane Understanding");
        mysteryCardInfo.setMysteryCardId(ARCANE_UNDERSTANDING);
        mysteryCardInfo.setMysteryComplexity(nrOfInvestigators);
        return mysteryCardInfo;
    }

    // Final Mystery
    private MysteryCardInfo buildTheKeyAndTheGateMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCardInfo = createYogSothothCard();
        mysteryCardInfo.setFlavorText("The ancient horror tears apart the walls between worlds\n" +
                "pouring itself through the cracks in reality.");
        mysteryCardInfo.setName("The Key and the Gate");
        mysteryCardInfo.setMysteryComplexity(Math.round(nrOfInvestigators /2f));
        mysteryCardInfo.setMysteryCardId(THE_KEY_AND_THE_GATE);
        mysteryCardInfo.setMysteryText("As an encounter, an investigator on a Gate\n" +
                "may confront Yog-Sothoth.\n" +
                "Successful encounter advances the Active Mystery.");
        return mysteryCardInfo;
    }

    private MysteryCardInfoImpl createAzathothCard() {
        MysteryCardInfoImpl card = new MysteryCardInfoImpl();
        card.setAncientOneId(AncientOneId.AZATHOTH);
        return card;
    }

    private MysteryCardInfoImpl createCthulhuCard() {
        MysteryCardInfoImpl card = new MysteryCardInfoImpl();
        card.setAncientOneId(AncientOneId.CTHULHU);
        return card;
    }

    private MysteryCardInfoImpl createShubNiggurathCard() {
        MysteryCardInfoImpl card = new MysteryCardInfoImpl();
        card.setAncientOneId(AncientOneId.SHUB_NIGGURATH);
        return card;
    }

    private MysteryCardInfoImpl createYogSothothCard() {
        MysteryCardInfoImpl card = new MysteryCardInfoImpl();
        card.setAncientOneId(AncientOneId.YOG_SOTHOTH);
        return card;
    }

    private class MysteryCardInfoImpl implements MysteryCardInfo {

        private AncientOneId ancientOneId;
        private MysteryCardId mysteryCardId;
        private String name;
        private String flavorText;
        private String mysteryText;
        private Integer mysteryComplexity;
        private Supplier<Integer> progressSupplier;
        private Supplier<List<LocationId>> pinLocationsSupplier;

        public Integer getMysteryComplexity() {
            return mysteryComplexity;
        }

        @Override
        public Integer getProgress() {
            return progressSupplier.get();
        }

        @Override
        public void setProgressSupplier(Supplier<Integer> progressSupplier) {
            this.progressSupplier = progressSupplier;
        }

        public void setMysteryComplexity(Integer mysteryComplexity) {
            this.mysteryComplexity = mysteryComplexity;
        }

        @Override
        public String getMysteryText() {
            return mysteryText;
        }

        public void setMysteryText(String mysteryText) {
            this.mysteryText = mysteryText;
        }

        @Override
        public List<LocationId> getPinLocations() {
            if (pinLocationsSupplier == null) {
                return null;
            }
            return pinLocationsSupplier.get();
        }

        public void setPinLocationsSupplier(Supplier<List<LocationId>> pinLocationsSupplier) {
            this.pinLocationsSupplier = pinLocationsSupplier;
        }

        public String getFlavorText() {
            return flavorText;
        }

        public void setFlavorText(String flavorText) {
            this.flavorText = flavorText;
        }

        @Override
        public AncientOneId getAncientOneId() {
            return ancientOneId;
        }

        public void setAncientOneId(AncientOneId ancientOneId) {
            this.ancientOneId = ancientOneId;
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public MysteryCardId getMysteryCardId() {
            return mysteryCardId;
        }

        public void setMysteryCardId(MysteryCardId mysteryCardId) {
            this.mysteryCardId = mysteryCardId;
        }
    }
}
