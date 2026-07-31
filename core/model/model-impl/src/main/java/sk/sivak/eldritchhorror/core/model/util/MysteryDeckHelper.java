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
        azathothCard.setName("mystery.azathoth.seed.name");
        azathothCard.setFlavorText("mystery.azathoth.seed.flavor");
        azathothCard.setMysteryText("mystery.azathoth.seed.text");
        azathothCard.setMysteryComplexity(Math.round(nrOfInvestigators / 2f));
        azathothCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.TUNGUSKA));
        return azathothCard;
    }

    private MysteryCardInfo buildOmenOfDevestationMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl azathothCard = createAzathothCard();
        azathothCard.setMysteryCardId(OMEN_OF_DEVASTATION);
        azathothCard.setName("mystery.azathoth.omen.name");
        azathothCard.setFlavorText("mystery.azathoth.omen.flavor");
        azathothCard.setMysteryText("mystery.azathoth.omen.text");
        azathothCard.setMysteryComplexity(Math.round(nrOfInvestigators / 2f));
        return azathothCard;
    }

    private MysteryCardInfo buildOccultResearchMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl azathothCard = createAzathothCard();
        azathothCard.setMysteryCardId(OCCULT_RESEARCH);
        azathothCard.setName("mystery.azathoth.occult.name");
        azathothCard.setFlavorText("mystery.azathoth.occult.flavor");
        azathothCard.setMysteryText("mystery.azathoth.occult.text");
        azathothCard.setMysteryComplexity(nrOfInvestigators);
        return azathothCard;
    }

    private MysteryCardInfo buildTheTrueNameMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl azathothCard = createAzathothCard();
        azathothCard.setMysteryCardId(THE_TRUE_NAME);
        azathothCard.setName("mystery.azathoth.trueName.name");
        azathothCard.setFlavorText("mystery.azathoth.trueName.flavor");
        azathothCard.setMysteryText("mystery.azathoth.trueName.text");
        int complexity = Math.round(nrOfInvestigators / 2f);
        azathothCard.setMysteryComplexity(complexity);
        List<LocationId> pinLocations = LocationId.getRandomLocations(complexity);
        azathothCard.setPinLocationsSupplier(() -> pinLocations);
        return azathothCard;
    }

    private MysteryCardInfo buildTheGreenFlameMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl azathothCard = createAzathothCard();
        azathothCard.setName("mystery.azathoth.greenFlame.name");
        azathothCard.setMysteryCardId(THE_GREEN_FLAME);
        azathothCard.setFlavorText("mystery.azathoth.greenFlame.flavor");
        azathothCard.setMysteryText("mystery.azathoth.greenFlame.text");
        azathothCard.setMysteryComplexity(nrOfInvestigators + 2);
        List<LocationId> pinLocations = LocationId.getRandomLocations(1);
        azathothCard.setPinLocationsSupplier(() -> pinLocations);
        return azathothCard;
    }

    private MysteryCardInfo buildVoiceOfAzathothMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl azathothCard = createAzathothCard();
        azathothCard.setName("mystery.azathoth.voice.name");
        azathothCard.setMysteryCardId(VOICE_OF_AZATHOTH);
        azathothCard.setFlavorText("mystery.azathoth.voice.flavor");
        azathothCard.setMysteryText("mystery.azathoth.voice.text");
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
        mysteryCard.setName("mystery.cthulhu.queen.name");
        mysteryCard.setMysteryCardId(Cthulhu.QUEEN_OF_THE_DEEP_ONES);
        mysteryCard.setFlavorText("mystery.cthulhu.queen.flavor");
        mysteryCard.setMysteryText("mystery.cthulhu.queen.text");
        mysteryCard.setMysteryComplexity(nrOfInvestigators + 2);
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.SPACE_8));
        return mysteryCard;
    }

    private MysteryCardInfo buildThreateningSeasMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createCthulhuCard();
        mysteryCard.setName("mystery.cthulhu.threateningSeas.name");
        mysteryCard.setMysteryCardId(Cthulhu.THREATENING_SEAS);
        mysteryCard.setFlavorText("mystery.cthulhu.threateningSeas.flavor");
        mysteryCard.setMysteryComplexity(nrOfInvestigators);
        mysteryCard.setMysteryText("mystery.cthulhu.threateningSeas.text");
        // this will be replaced by listener,
        // it is here just that load function knows that it should override locations
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.SPACE_1));
        return mysteryCard;
    }

    private MysteryCardInfo buildTheDeepOnesAttackMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createCthulhuCard();
        mysteryCard.setName("mystery.cthulhu.deepOnesAttack.name");
        mysteryCard.setMysteryCardId(Cthulhu.THE_DEEP_ONES_ATTACK);
        mysteryCard.setFlavorText("mystery.cthulhu.deepOnesAttack.flavor");
        mysteryCard.setMysteryComplexity(nrOfInvestigators);
        mysteryCard.setMysteryText("mystery.cthulhu.deepOnesAttack.text");
        // this will be replaced by listener,
        // it is here just that load function knows that it should override locations
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.SPACE_1));
        return mysteryCard;
    }

    private MysteryCardInfo buildTheStarsAreRightMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createCthulhuCard();
        mysteryCard.setMysteryCardId(Cthulhu.THE_STARS_ARE_RIGHT);
        mysteryCard.setName("mystery.cthulhu.starsRight.name");
        mysteryCard.setFlavorText("mystery.cthulhu.starsRight.flavor");
        mysteryCard.setMysteryText("mystery.cthulhu.starsRight.text");
        mysteryCard.setMysteryComplexity(nrOfInvestigators);
        return mysteryCard;
    }

    private MysteryCardInfo buildWatchingTheStarsMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createCthulhuCard();
        mysteryCard.setName("mystery.cthulhu.watchingStars.name");
        mysteryCard.setMysteryCardId(Cthulhu.WATCHING_THE_STARS);
        mysteryCard.setFlavorText("mystery.cthulhu.watchingStars.flavor");
        mysteryCard.setMysteryText("mystery.cthulhu.watchingStars.text");
        mysteryCard.setMysteryComplexity(nrOfInvestigators + 2);
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.SPACE_12));
        return mysteryCard;
    }

    private MysteryCardInfo buildRlyehRisenMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createCthulhuCard();
        mysteryCard.setName("mystery.cthulhu.rlyehRisen.name");
        mysteryCard.setMysteryCardId(Cthulhu.RLYEH_RISEN);
        mysteryCard.setFlavorText("mystery.cthulhu.rlyehRisen.flavor");
        mysteryCard.setMysteryText("mystery.cthulhu.rlyehRisen.text");
        mysteryCard.setMysteryComplexity(Math.round(nrOfInvestigators /2f));
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.SPACE_3));
        return mysteryCard;
    }

    // Final Mystery
    private MysteryCardInfo buildRisenFromTheSeaMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createCthulhuCard();
        mysteryCard.setMysteryCardId(Cthulhu.RISEN_FROM_THE_SEA);
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.SPACE_3));
        mysteryCard.setMysteryText("mystery.cthulhu.risenSea.text");
        mysteryCard.setMysteryComplexity(nrOfInvestigators + 3);
        mysteryCard.setFlavorText("mystery.cthulhu.risenSea.flavor");
        mysteryCard.setName("mystery.cthulhu.risenSea.name");
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
        mysteryCard.setName("mystery.shub.blasphemy.name");
        mysteryCard.setMysteryCardId(BLASPHEMY_OF_THE_BLACK_GOAT);
        mysteryCard.setFlavorText("mystery.shub.blasphemy.flavor");

        mysteryCard.setMysteryText("mystery.shub.blasphemy.text");
        mysteryCard.setMysteryComplexity(nrOfInvestigators + 2);
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.SPACE_19));
        return mysteryCard;
    }

    private MysteryCardInfo buildHourOfTheMoonLensMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createShubNiggurathCard();
        mysteryCard.setMysteryCardId(HOUR_OF_THE_MOON_LENS);
        mysteryCard.setName("mystery.shub.hourLens.name");
        mysteryCard.setFlavorText("mystery.shub.hourLens.flavor");
        mysteryCard.setMysteryText("mystery.shub.hourLens.text");
        mysteryCard.setMysteryComplexity(Math.round(nrOfInvestigators / 2f));
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.LONDON));
        return mysteryCard;
    }

    private MysteryCardInfo buildSpawnOfTheBlackGoatMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createShubNiggurathCard();
        mysteryCard.setName("mystery.shub.spawn.name");
        mysteryCard.setMysteryCardId(SPAWN_OF_THE_BLACK_GOAT);
        mysteryCard.setFlavorText("mystery.shub.spawn.flavor");

        mysteryCard.setMysteryText("mystery.shub.spawn.text");
        mysteryCard.setMysteryComplexity(nrOfInvestigators + 2);
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.THE_AMAZON));
        return mysteryCard;
    }

    private MysteryCardInfo buildNatureOfTheAllMotherMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createShubNiggurathCard();
        mysteryCard.setMysteryCardId(NATURE_OF_THE_ALL_MOTHER);
        mysteryCard.setName("mystery.shub.nature.name");
        mysteryCard.setFlavorText("mystery.shub.nature.flavor");
        mysteryCard.setMysteryText("mystery.shub.nature.text");
        mysteryCard.setMysteryComplexity(nrOfInvestigators);
        return mysteryCard;
    }

    private MysteryCardInfo buildHuntingTheThousandMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createShubNiggurathCard();
        mysteryCard.setMysteryCardId(HUNTING_THE_THOUSAND);
        mysteryCard.setName("mystery.shub.hunting.name");
        mysteryCard.setFlavorText("mystery.shub.hunting.flavor");
        mysteryCard.setMysteryComplexity(nrOfInvestigators * 2);
        mysteryCard.setMysteryText("mystery.shub.hunting.text");
        return mysteryCard;
    }

    private MysteryCardInfo buildRitualsInTheWildMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createShubNiggurathCard();
        mysteryCard.setMysteryCardId(RITUALS_IN_THE_WILD);
        mysteryCard.setName("mystery.shub.rituals.name");
        mysteryCard.setFlavorText("mystery.shub.rituals.flavor");
        mysteryCard.setMysteryText("mystery.shub.rituals.text");
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
        mysteryCard.setMysteryText("mystery.shub.battleWoods.text");
        mysteryCard.setMysteryComplexity(nrOfInvestigators + 3);
        mysteryCard.setFlavorText("mystery.shub.battleWoods.flavor");
        mysteryCard.setName("mystery.shub.battleWoods.name");
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
        mysteryCard.setName("mystery.yog.spawn.name");
        mysteryCard.setMysteryCardId(SPAWN_OF_YOG_SOTHOTH);
        mysteryCard.setFlavorText("mystery.yog.spawn.flavor");

        mysteryCard.setMysteryText("mystery.yog.spawn.text");
        mysteryCard.setMysteryComplexity(nrOfInvestigators + 2);
        mysteryCard.setPinLocationsSupplier(() -> Collections.singletonList(LocationId.ARKHAM));
        return mysteryCard;
    }

    private MysteryCardInfo buildWhereTheOldOnesBrokeThroughMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl azathothCard = createYogSothothCard();
        azathothCard.setMysteryCardId(WHERE_THE_OLD_ONES_BROKE_THROUGH);
        azathothCard.setName("mystery.yog.whereBrokeThrough.name");
        azathothCard.setFlavorText("mystery.yog.whereBrokeThrough.flavor");
        azathothCard.setMysteryText("mystery.yog.whereBrokeThrough.text");
        azathothCard.setMysteryComplexity(Math.round(nrOfInvestigators / 2f));
        return azathothCard;
    }

    private MysteryCardInfo buildTheBeyondOneMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl azathothCard = createYogSothothCard();
        azathothCard.setMysteryCardId(THE_BEYOND_ONE);
        azathothCard.setName("mystery.yog.beyondOne.name");
        azathothCard.setFlavorText("mystery.yog.beyondOne.flavor");
        azathothCard.setMysteryText("mystery.yog.beyondOne.text");
        azathothCard.setMysteryComplexity(nrOfInvestigators);
        return azathothCard;
    }

    private MysteryCardInfo buildTheStoneCirclesMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createYogSothothCard();
        mysteryCard.setMysteryCardId(THE_STONE_CIRCLES);
        mysteryCard.setName("mystery.yog.stoneCircles.name");
        mysteryCard.setFlavorText("mystery.yog.stoneCircles.flavor");
        mysteryCard.setMysteryText("mystery.yog.stoneCircles.text");
        int complexity = Math.round(nrOfInvestigators / 2f);
        mysteryCard.setMysteryComplexity(complexity);
        List<LocationId> pinLocations = LocationId.getRandomLocations(complexity);
        mysteryCard.setPinLocationsSupplier(() -> pinLocations);
        return mysteryCard;
    }

    private MysteryCardInfo buildVoidBetweenWorldsMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCard = createYogSothothCard();
        mysteryCard.setName("mystery.yog.void.name");
        mysteryCard.setMysteryCardId(VOID_BETWEEN_WORLDS);
        mysteryCard.setFlavorText("mystery.yog.void.flavor");
        mysteryCard.setMysteryText("mystery.yog.void.text");
        mysteryCard.setMysteryComplexity(Math.round(nrOfInvestigators /2f));
        return mysteryCard;
    }

    private MysteryCardInfo buildArcaneUnderstandingMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCardInfo = createYogSothothCard();
        mysteryCardInfo.setFlavorText("mystery.yog.arcane.flavor");
        mysteryCardInfo.setMysteryText("mystery.yog.arcane.text");
        mysteryCardInfo.setName("mystery.yog.arcane.name");
        mysteryCardInfo.setMysteryCardId(ARCANE_UNDERSTANDING);
        mysteryCardInfo.setMysteryComplexity(nrOfInvestigators);
        return mysteryCardInfo;
    }

    // Final Mystery
    private MysteryCardInfo buildTheKeyAndTheGateMystery(Integer nrOfInvestigators) {
        MysteryCardInfoImpl mysteryCardInfo = createYogSothothCard();
        mysteryCardInfo.setFlavorText("mystery.yog.keyGate.flavor");
        mysteryCardInfo.setName("mystery.yog.keyGate.name");
        mysteryCardInfo.setMysteryComplexity(Math.round(nrOfInvestigators /2f));
        mysteryCardInfo.setMysteryCardId(THE_KEY_AND_THE_GATE);
        mysteryCardInfo.setMysteryText("mystery.yog.keyGate.text");
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
