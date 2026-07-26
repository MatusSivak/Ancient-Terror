package sk.sivak.eldritchhorror.core.eventlistener.provider;

import sk.sivak.eldritchhorror.core.constants.MysteryCardId;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.mythos.MythosCard;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.MysteryListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.cthulhu.AwakenCthulhuInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.azathoth.AzathothInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.shubniggurath.AwakenShubNiggurathInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.cthulhu.CthulhuInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.azathoth.OccultResearchListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.azathoth.OmenOfDevastationListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.azathoth.SeedOfTheDaemonSultanListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.azathoth.TheGreenFlameListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.azathoth.TheTrueNameListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.azathoth.VoiceOfAzathothListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.cthulhu.QueenOfTheDeepOnesListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.cthulhu.RisenFromTheSeaListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.cthulhu.RlyehRisenListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.shubniggurath.ShubNiggurathInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.cthulhu.TheDeepOnesAttackListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.cthulhu.TheStarsAreRightListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.cthulhu.ThreateningSeasListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.cthulhu.WatchingTheStarsListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.shubniggurath.BattleInTheWoodsListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.shubniggurath.BlasphemyOfTheBlackGoatListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.shubniggurath.HourOfTheMoonLensMysteryListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.shubniggurath.HuntingTheThousandMysteryListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.shubniggurath.NatureOfTheAllMotherListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.shubniggurath.RitualsInTheWildMysteryListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.shubniggurath.SpawnOfTheBlackGoatMysteryListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.yogsothoth.ArcaneUnderstandingListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.yogsothoth.AwakenYogSothothInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.yogsothoth.SpawnOfYogSothothListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.yogsothoth.TheBeyondOneListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.yogsothoth.TheKeyAndTheGateListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.yogsothoth.TheStoneCirclesListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.yogsothoth.VoidBetweenWorldsListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.yogsothoth.WhereTheOldOnesBrokeThroughListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.yogsothoth.YogSothothInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.detained.DetainedConditionBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.detained.DetainedConditionBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.detained.DetainedConditionBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.detained.DetainedConditionBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.lostintimeandspace.LostInTimeAndSpaceBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.lostintimeandspace.LostInTimeAndSpaceBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.lostintimeandspace.LostInTimeAndSpaceBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.lostintimeandspace.LostInTimeAndSpaceBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.CollectCommonEncountersListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.defeated.DefeatedInvestigatorEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition.AbstractExpeditionEncounter;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.general.AbstractGeneralEncounter;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.location.AbstractLocationEncounter;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld.AbstractOtherWorldEncounter;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.research.AbstractResearchEncounter;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.rlyehrisen.AbstractRlyehRisenEncounter;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.thekeyandthegate.AbstractTheKeyAndTheGateEncounter;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.voidbetweenworlds.AbstractVoidBetweenWorldsEncounter;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.AbstractInvestigatorInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheActressInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheAstronomerInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheBootleggerInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheBountyHunterInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheEntertainerInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheExConvictInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheExpeditionLeaderInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheHandymanInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheMartialArtistInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheMusicianInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.ThePoliticianInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.ThePsychicInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheRedeemedCultistInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheRookieCopInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheSailorInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheShamanInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheSoldierInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheSpyInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheViolinistInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheWaitressInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.AbstractMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.ByakheeMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.ColourOutOfSpaceMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.CthonianMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.CthulhuMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.CthyllaMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.CultistMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.DarkYoungMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.DeepOneMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.DoppelgangerMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.DunwichHorrorMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.ElderThingMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.GhostMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.GhoulMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.GnophKehMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.GoatSpawnMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.GugMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.HoundOfTindalosMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.HydraMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.LloigorMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.ManiacMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.MiGoMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.MonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.MummyMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.NightGauntMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.NugMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.RiotMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.SerpentPeopleMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.ServitorOfTheOuterGodsMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.ShoggothMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.ShubNiggurathMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.SkeletonMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.StarSpawnMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.StarVampireMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.TickTockMenMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.TulzschaMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.VampireMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.WarlockMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.WerewolfMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.WindWalkerMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.WitchMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.WraithMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.YebMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.ZombieHordeMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.ZombieMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.mythos.BlueMythosCard1;
import sk.sivak.eldritchhorror.core.eventlistener.mythos.BlueMythosCard2;
import sk.sivak.eldritchhorror.core.eventlistener.mythos.BlueMythosCard3;
import sk.sivak.eldritchhorror.core.eventlistener.mythos.BlueMythosCard4;
import sk.sivak.eldritchhorror.core.eventlistener.mythos.BlueMythosCard5;
import sk.sivak.eldritchhorror.core.eventlistener.mythos.MythosCardEventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DefeatedInvestigatorEncounterData;

import java.util.HashMap;
import java.util.Map;

public class EventListenerProviderImpl implements EventListenerProvider {

    private AzathothInitListener azathothInitListener;
    private CthulhuInitListener cthulhuInitListener;
    private AwakenCthulhuInitListener awakenCthulhuInitListener;
    private AwakenYogSothothInitListener awakenYogSothothInitListener;
    private ShubNiggurathInitListener shubNiggurathInitListener;
    private AwakenShubNiggurathInitListener awakenShubNiggurathInitListener;
    private YogSothothInitListener yogSothothInitListener;
    private ThePoliticianInitListener thePoliticianInitListener;
    private TheRedeemedCultistInitListener theRedeemedCultistInitListener;
    private TheSailorInitListener theSailorInitListener;
    private TheExpeditionLeaderInitListener theExpeditionLeaderInitListener;
    private TheMartialArtistInitListener theMartialArtistInitListener;
    private TheMusicianInitListener theMusicianInitListener;
    private TheSoldierInitListener theSoldierInitListener;
    private ThePsychicInitListener thePsychicInitListener;
    private TheBountyHunterInitListener theBountyHunterInitListener;
    private TheAstronomerInitListener theAstronomerInitListener;
    private TheSpyInitListener theSpyInitListener;
    private TheRookieCopInitListener theRookieCopInitListener;

    private TheBootleggerInitListener theBootleggerInitListener;
    private TheHandymanInitListener theHandymanInitListener;
    private TheViolinistInitListener theViolinistInitListener;
    private TheWaitressInitListener theWaitressInitListener;

    private TheActressInitListener theActressInitListener;
    private TheExConvictInitListener theExConvictInitListener;
    private TheShamanInitListener theShamanInitListener;
    private TheEntertainerInitListener theEntertainerInitListener;
    private Map<MonsterInfo, MonsterListener> monsterListenersMap;
    private Map<MysteryCardId, MysteryListener> mysteryListenerMap;

    public void init() {
        theBootleggerInitListener = new TheBootleggerInitListener();
        theHandymanInitListener = new TheHandymanInitListener();
        theViolinistInitListener = new TheViolinistInitListener();
        theWaitressInitListener = new TheWaitressInitListener();
        theBountyHunterInitListener = new TheBountyHunterInitListener();
        theAstronomerInitListener = new TheAstronomerInitListener();
        theSpyInitListener = new TheSpyInitListener();
        theRookieCopInitListener = new TheRookieCopInitListener();
        theActressInitListener = new TheActressInitListener();
        theExConvictInitListener = new TheExConvictInitListener();
        theShamanInitListener = new TheShamanInitListener();
        theEntertainerInitListener = new TheEntertainerInitListener();
        thePoliticianInitListener = new ThePoliticianInitListener();
        theSailorInitListener = new TheSailorInitListener();
        theRedeemedCultistInitListener = new TheRedeemedCultistInitListener();
        theExpeditionLeaderInitListener = new TheExpeditionLeaderInitListener();
        theMartialArtistInitListener = new TheMartialArtistInitListener();
        theMusicianInitListener = new TheMusicianInitListener();
        theSoldierInitListener = new TheSoldierInitListener();
        thePsychicInitListener = new ThePsychicInitListener();
        azathothInitListener = new AzathothInitListener();
        cthulhuInitListener = new CthulhuInitListener();
        awakenCthulhuInitListener = new AwakenCthulhuInitListener();
        awakenYogSothothInitListener = new AwakenYogSothothInitListener();
        shubNiggurathInitListener = new ShubNiggurathInitListener();
        yogSothothInitListener = new YogSothothInitListener();
        awakenShubNiggurathInitListener = new AwakenShubNiggurathInitListener();
        monsterListenersMap = new HashMap<>();
        mysteryListenerMap = new HashMap<>();
    }

    @Override
    public void registerCollectCommonEncountersListener() {
        ServicePlatform.get().getEventQueue().addDirectEventListener(new CollectCommonEncountersListener(), DirectEvent.COLLECT_COMMON_ENCOUNTERS);
    }

    @Override
    public void registerInvestigatorListeners(InvestigatorId investigator) {
        findEventListener(investigator).registerDirectEventListener(DirectEvent.INIT_INVESTIGATORS);
    }

    @Override
    public void justRegisterInvestigatorListeners(InvestigatorId investigator) {
        findEventListener(investigator).justRegisterListeners();
    }

    @Override
    public void unregisterInvestigatorListeners(InvestigatorId investigator) {
        findEventListener(investigator).unregisterInvestigator();
    }

    private AbstractInvestigatorInitListener findEventListener(InvestigatorId investigator) {
        AbstractInvestigatorInitListener eventListener;
        switch (investigator) {
            case THE_BOUNTY_HUNTER:
                eventListener = theBountyHunterInitListener;
                break;
            case THE_ASTRONOMER:
                eventListener = theAstronomerInitListener;
                break;
            case THE_SOLDIER:
                eventListener = theSoldierInitListener;
                break;
            case THE_PSYCHIC:
                eventListener = thePsychicInitListener;
                break;
            case THE_SPY:
                eventListener = theSpyInitListener;
                break;
            case THE_ROOKIE_COP:
                eventListener = theRookieCopInitListener;
                break;
            case THE_ACTRESS:
                eventListener = theActressInitListener;
                break;
            case THE_ENTERTAINER:
                eventListener = theEntertainerInitListener;
                break;
            case THE_POLITICIAN:
                eventListener = thePoliticianInitListener;
                break;
            case THE_SAILOR:
                eventListener = theSailorInitListener;
                break;
            case THE_REDEEMED_CULTIST:
                eventListener = theRedeemedCultistInitListener;
                break;
            case THE_EXPEDITION_LEADER:
                eventListener = theExpeditionLeaderInitListener;
                break;
            case THE_MARTIAL_ARTIST:
                eventListener = theMartialArtistInitListener;
                break;
            case THE_MUSICIAN:
                eventListener = theMusicianInitListener;
                break;
            case THE_EX_CONVICT:
                eventListener = theExConvictInitListener;
                break;
            case THE_SHAMAN:
                eventListener = theShamanInitListener;
                break;
            case THE_BOOTLEGGER:
                eventListener = theBootleggerInitListener;
                break;
            case THE_HANDYMAN:
                eventListener = theHandymanInitListener;
                break;
            case THE_VIOLINIST:
                eventListener = theViolinistInitListener;
                break;
            case THE_WAITRESS:
                eventListener = theWaitressInitListener;
                break;
            default:
                throw new IllegalArgumentException(investigator.name());
        }
        return eventListener;
    }

    @Override
    public void registerAncientOneListeners(AncientOneInfo ancientOneInfo) {
        AncientOneId ancientOneId = ancientOneInfo.getAncientOneId();
        switch (ancientOneId) {
            case AZATHOTH:
                azathothInitListener.register();
                break;
            case CTHULHU:
                if (!ancientOneInfo.isAwaken()) {
                    cthulhuInitListener.register();
                } else {
                    awakenCthulhuInitListener.register();
                }
                break;
            case SHUB_NIGGURATH:
                if (!ancientOneInfo.isAwaken()) {
                    shubNiggurathInitListener.register();
                } else {
                    awakenShubNiggurathInitListener.register();
                }
                break;
            case YOG_SOTHOTH:
                if (!ancientOneInfo.isAwaken()) {
                    yogSothothInitListener.register();
                } else {
                    awakenYogSothothInitListener.register();
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void loadAncientOneListeners(AncientOneInfo ancientOneInfo) {
        AncientOneId ancientOneId = ancientOneInfo.getAncientOneId();
        switch (ancientOneId) {
            case AZATHOTH:
                azathothInitListener.load();
                break;
            case CTHULHU:
                if (!ancientOneInfo.isAwaken()) {
                    cthulhuInitListener.load();
                } else {
                    awakenCthulhuInitListener.load();
                }
                break;
            case SHUB_NIGGURATH:
                if (!ancientOneInfo.isAwaken()) {
                    shubNiggurathInitListener.load();
                } else {
                    awakenShubNiggurathInitListener.load();
                }
                break;
            case YOG_SOTHOTH:
                if (!ancientOneInfo.isAwaken()) {
                    yogSothothInitListener.load();
                } else {
                    awakenYogSothothInitListener.load();
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    @Override
    public void registerMysteryListeners(MysteryCardInfo mysteryCardInfo) {
        if (mysteryCardInfo == null) {
            return;
        }
        MysteryCardId mysteryCardId = mysteryCardInfo.getMysteryCardId();
        MysteryListener mysteryListener = findMysteryListener(mysteryCardInfo);
        mysteryListenerMap.put(mysteryCardId, mysteryListener);
        mysteryListenerMap.get(mysteryCardId).register();
    }

    @Override
    public void justRegisterMysteryListeners(MysteryCardInfo mysteryCardInfo, int progress) {
        if (mysteryCardInfo == null) {
            return;
        }
        MysteryCardId mysteryCardId = mysteryCardInfo.getMysteryCardId();
        MysteryListener mysteryListener = findMysteryListener(mysteryCardInfo);
        mysteryListenerMap.put(mysteryCardId, mysteryListener);
        mysteryListenerMap.get(mysteryCardId).justRegisterListeners(progress);
    }

    @Override
    public void justAddRedPins(MysteryCardInfo mysteryCardInfo) {
        if (mysteryCardInfo == null) {
            return;
        }
        mysteryListenerMap.get(mysteryCardInfo.getMysteryCardId()).justAddRedPins();
    }

    private MysteryListener findMysteryListener(MysteryCardInfo mysteryCardInfo) {
        if (mysteryCardInfo.getMysteryCardId() instanceof MysteryCardId.Azathoth) {
            switch ((MysteryCardId.Azathoth) mysteryCardInfo.getMysteryCardId()) {
                case SEED_OF_THE_DAEMON_SULTAN:
                    return new SeedOfTheDaemonSultanListener(mysteryCardInfo);
                case OMEN_OF_DEVASTATION:
                    return new OmenOfDevastationListener(mysteryCardInfo);
                case OCCULT_RESEARCH:
                    return new OccultResearchListener(mysteryCardInfo);
                case THE_TRUE_NAME:
                    return new TheTrueNameListener(mysteryCardInfo);
                case THE_GREEN_FLAME:
                    return new TheGreenFlameListener(mysteryCardInfo);
                case VOICE_OF_AZATHOTH:
                    return new VoiceOfAzathothListener(mysteryCardInfo);
                default:
                    throw new IllegalArgumentException();
            }
        } else if (mysteryCardInfo.getMysteryCardId() instanceof MysteryCardId.Cthulhu) {
            switch ((MysteryCardId.Cthulhu) mysteryCardInfo.getMysteryCardId()) {
                case QUEEN_OF_THE_DEEP_ONES:
                    return new QueenOfTheDeepOnesListener(mysteryCardInfo);
                case THREATENING_SEAS:
                    return new ThreateningSeasListener(mysteryCardInfo);
                case THE_DEEP_ONES_ATTACK:
                    return new TheDeepOnesAttackListener(mysteryCardInfo);
                case THE_STARS_ARE_RIGHT:
                    return new TheStarsAreRightListener(mysteryCardInfo);
                case WATCHING_THE_STARS:
                    return new WatchingTheStarsListener(mysteryCardInfo);
                case RLYEH_RISEN:
                    return new RlyehRisenListener(mysteryCardInfo);
                case RISEN_FROM_THE_SEA:
                    return new RisenFromTheSeaListener(mysteryCardInfo);
                default:
                    throw new IllegalArgumentException();
            }
        } else if (mysteryCardInfo.getMysteryCardId() instanceof MysteryCardId.ShubNiggurath) {
            switch ((MysteryCardId.ShubNiggurath) mysteryCardInfo.getMysteryCardId()) {
                case BLASPHEMY_OF_THE_BLACK_GOAT:
                    return new BlasphemyOfTheBlackGoatListener(mysteryCardInfo);
                case HOUR_OF_THE_MOON_LENS:
                    return new HourOfTheMoonLensMysteryListener(mysteryCardInfo);
                case SPAWN_OF_THE_BLACK_GOAT:
                    return new SpawnOfTheBlackGoatMysteryListener(mysteryCardInfo);
                case NATURE_OF_THE_ALL_MOTHER:
                    return new NatureOfTheAllMotherListener(mysteryCardInfo);
                case RITUALS_IN_THE_WILD:
                    return new RitualsInTheWildMysteryListener(mysteryCardInfo);
                case HUNTING_THE_THOUSAND:
                    return new HuntingTheThousandMysteryListener(mysteryCardInfo);
                case BATTLE_IN_THE_WOODS:
                    return new BattleInTheWoodsListener(mysteryCardInfo);
                default:
                    throw new IllegalArgumentException();
            }
        } else if (mysteryCardInfo.getMysteryCardId() instanceof MysteryCardId.YogSothoth) {
            switch ((MysteryCardId.YogSothoth) mysteryCardInfo.getMysteryCardId()) {
                case SPAWN_OF_YOG_SOTHOTH:
                    return new SpawnOfYogSothothListener(mysteryCardInfo);
                case WHERE_THE_OLD_ONES_BROKE_THROUGH:
                    return new WhereTheOldOnesBrokeThroughListener(mysteryCardInfo);
                case THE_BEYOND_ONE:
                    return new TheBeyondOneListener(mysteryCardInfo);
                case THE_STONE_CIRCLES:
                    return new TheStoneCirclesListener(mysteryCardInfo);
                case VOID_BETWEEN_WORLDS:
                    return new VoidBetweenWorldsListener(mysteryCardInfo);
                case ARCANE_UNDERSTANDING:
                    return new ArcaneUnderstandingListener(mysteryCardInfo);
                case THE_KEY_AND_THE_GATE:
                    return new TheKeyAndTheGateListener(mysteryCardInfo);
                default:
                    throw new IllegalArgumentException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void unregisterMysteryListeners(MysteryCardInfo currentMysteryCard) {
        mysteryListenerMap.get(currentMysteryCard.getMysteryCardId()).unregister();
    }

    @Override
    public void advanceActiveMysteryAction(MysteryCardInfo mysteryCardInfo) {
        mysteryListenerMap.get(mysteryCardInfo.getMysteryCardId()).advanceActiveMystery();
    }

    @Override
    public void beforeSpawnMonsterInit(MonsterInfo monsterInfo) {
        AbstractMonsterListener listener = findMonsterListener(monsterInfo.getMonsterId());
        if (listener != null) {
            monsterListenersMap.put(monsterInfo, listener);
            listener.beforeSpawnMonsterInit(monsterInfo);
        }
    }

    @Override
    public void registerMonsterListeners(MonsterInfo monsterInfo) {
        AbstractMonsterListener listener = findMonsterListener(monsterInfo.getMonsterId());
        if (listener != null) {
            monsterListenersMap.put(monsterInfo, listener);
            listener.register(monsterInfo);
        }
    }

    @Override
    public void justRegisterMonsterListeners(MonsterInfo monsterInfo) {
        AbstractMonsterListener listener = findMonsterListener(monsterInfo.getMonsterId());
        if (listener != null) {
            monsterListenersMap.put(monsterInfo, listener);
            listener.justRegisterListeners(monsterInfo);
        }
    }

    private AbstractMonsterListener findMonsterListener(MonsterId monsterId) {
        AbstractMonsterListener listener = null;
        if (EpicMonsterId.TULZSCHA == monsterId) {
            listener = new TulzschaMonsterListener();
        } else if (EpicMonsterId.HYDRA == monsterId) {
            listener = new HydraMonsterListener();
        } else if (EpicMonsterId.YEB == monsterId) {
            listener = new YebMonsterListener();
        } else if (EpicMonsterId.NUG == monsterId) {
            listener = new NugMonsterListener();
        } else if (EpicMonsterId.CTHYLLA == monsterId) {
            listener = new CthyllaMonsterListener();
        } else if (EpicMonsterId.CTHULHU == monsterId) {
            listener = new CthulhuMonsterListener();
        } else if (EpicMonsterId.SHUB_NIGGURATH == monsterId) {
            listener = new ShubNiggurathMonsterListener();
        } else if (EpicMonsterId.ZOMBIE_HORDE == monsterId) {
            listener = new ZombieHordeMonsterListener();
        } else if (EpicMonsterId.WIND_WALKER== monsterId) {
            listener = new WindWalkerMonsterListener();
        } else if (EpicMonsterId.TICK_TOCK_MEN== monsterId) {
            listener = new TickTockMenMonsterListener();
        } else if (EpicMonsterId.DUNWICH_HORROR== monsterId) {
            listener = new DunwichHorrorMonsterListener();
        } else if (EpicMonsterId.DOPPELGANGER == monsterId) {
            listener = new DoppelgangerMonsterListener();
        } else if (NonEpicMonsterId.CULTIST == monsterId) {
            listener = new CultistMonsterListener();
        } else if (NonEpicMonsterId.BYAKHEE == monsterId) {
            listener = new ByakheeMonsterListener();
        } else if (NonEpicMonsterId.COLOUR_OUT_OF_SPACE == monsterId) {
            listener = new ColourOutOfSpaceMonsterListener();
        } else if (NonEpicMonsterId.GHOST == monsterId) {
            listener = new GhostMonsterListener();
        } else if (NonEpicMonsterId.GHOUL == monsterId) {
            listener = new GhoulMonsterListener();
        } else if (NonEpicMonsterId.GOAT_SPAWN == monsterId) {
            listener = new GoatSpawnMonsterListener();
        } else if (NonEpicMonsterId.MANIAC == monsterId) {
            listener = new ManiacMonsterListener();
        } else if (NonEpicMonsterId.MI_GO == monsterId) {
            listener = new MiGoMonsterListener();
        } else if (NonEpicMonsterId.RIOT == monsterId) {
            listener = new RiotMonsterListener();
        } else if (NonEpicMonsterId.SHOGGOTH == monsterId) {
            listener = new ShoggothMonsterListener();
        } else if (NonEpicMonsterId.SKELETON == monsterId) {
            listener = new SkeletonMonsterListener();
        } else if (NonEpicMonsterId.GNOPH_KEH == monsterId) {
            listener = new GnophKehMonsterListener();
        } else if (NonEpicMonsterId.DEEP_ONE == monsterId) {
            listener = new DeepOneMonsterListener();
        } else if (NonEpicMonsterId.SERPENT_PEOPLE == monsterId) {
            listener = new SerpentPeopleMonsterListener();
        } else if (NonEpicMonsterId.STAR_SPAWN == monsterId) {
            listener = new StarSpawnMonsterListener();
        } else if (NonEpicMonsterId.ZOMBIE == monsterId) {
            listener = new ZombieMonsterListener();
        } else if (NonEpicMonsterId.DARK_YOUNG == monsterId) {
            listener = new DarkYoungMonsterListener();
        } else if (NonEpicMonsterId.HOUND_OF_TINDALOS == monsterId) {
            listener = new HoundOfTindalosMonsterListener();
        } else if (NonEpicMonsterId.NIGHT_GAUNT == monsterId) {
            listener = new NightGauntMonsterListener();
        } else if (NonEpicMonsterId.STAR_VAMPIRE == monsterId) {
            listener = new StarVampireMonsterListener();
        } else if (NonEpicMonsterId.SERVITOR_OF_THE_OUTER_GODS == monsterId) {
            listener = new ServitorOfTheOuterGodsMonsterListener();
        } else if (NonEpicMonsterId.VAMPIRE == monsterId) {
            listener = new VampireMonsterListener();
        } else if (NonEpicMonsterId.WARLOCK == monsterId) {
            listener = new WarlockMonsterListener();
        } else if (NonEpicMonsterId.WITCH == monsterId) {
            listener = new WitchMonsterListener();
        } else if (NonEpicMonsterId.MUMMY == monsterId) {
            listener = new MummyMonsterListener();
        } else if (NonEpicMonsterId.CTHONIAN == monsterId) {
            listener = new CthonianMonsterListener();
        } else if (NonEpicMonsterId.ELDER_THING == monsterId) {
            listener = new ElderThingMonsterListener();
        } else if (NonEpicMonsterId.WEREWOLF == monsterId) {
            listener = new WerewolfMonsterListener();
        } else if (NonEpicMonsterId.WRAITH == monsterId) {
            listener = new WraithMonsterListener();
        } else if (NonEpicMonsterId.GUG == monsterId) {
            listener = new GugMonsterListener();
        } else if (NonEpicMonsterId.LLOIGOR == monsterId) {
            listener = new LloigorMonsterListener();
        }
        return listener;
    }

    @Override
    public void unregisterMonsterListeners(MonsterInfo monsterInfo) {
        if (monsterListenersMap.get(monsterInfo) != null) {
            monsterListenersMap.get(monsterInfo).unregister();
            monsterListenersMap.remove(monsterInfo);
        }
    }

    @Override
    public void executeConditionBack(ConditionInfo conditionInfo) {
        int conditionBackId = conditionInfo.getConditionBack().getConditionBackId();
        switch (conditionInfo.getId()) {
            case DETAINED:
                ServicePlatform.get().getService().showDetainedBackground();
                if (conditionBackId == 1) {
                    new DetainedConditionBackListener1(conditionInfo).executeWhole();
                } else if (conditionBackId == 2) {
                    new DetainedConditionBackListener2(conditionInfo).executeWhole();
                } else if (conditionBackId == 3) {
                    new DetainedConditionBackListener3(conditionInfo).executeWhole();
                } else if (conditionBackId == 4) {
                    new DetainedConditionBackListener4(conditionInfo).executeWhole();
                } else {
                    throw new IllegalArgumentException();
                }
                break;
            case LOST_IN_TIME_AND_SPACE:
                if (conditionBackId == 1) {
                    new LostInTimeAndSpaceBackListener1(conditionInfo).executeWhole();
                } else if (conditionBackId == 2) {
                    new LostInTimeAndSpaceBackListener2(conditionInfo).executeWhole();
                } else if (conditionBackId == 3) {
                    new LostInTimeAndSpaceBackListener3(conditionInfo).executeWhole();
                } else if (conditionBackId == 4) {
                    new LostInTimeAndSpaceBackListener4(conditionInfo).executeWhole();
                } else {
                    throw new IllegalArgumentException();
                }
                break;
        }
    }

    @Override
    public void executeGeneralEncounter(int page, LocationType locationType) {
        try {
            String className = "sk.sivak.eldritchhorror.core.eventlistener.encounter.general." + locationType.getValue() + page + "Encounter";
            AbstractGeneralEncounter generalEncounter = (AbstractGeneralEncounter) Class.forName(className).newInstance();
            generalEncounter.executeWhole();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void registerDefeatedListener(InvestigatorId investigatorId, boolean health) {
        findEventListener(investigatorId).registerDefeatedListener(health);
    }

    @Override
    public void unregisterDefeatedListener(InvestigatorId investigatorId) {
        findEventListener(investigatorId).unregisterDefeatedListener();
    }

    @Override
    public void executeResearchEncounter(Integer page, LocationType locationType, LocationId locationId) {
        String ancientOneToLowercase = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo().getAncientOneId().toString().toLowerCase();

        try {
            String className = "sk.sivak.eldritchhorror.core.eventlistener.encounter.research." + ancientOneToLowercase+".Research" + locationType.getValue() + page + "Encounter";
            AbstractResearchEncounter researchEncounter = (AbstractResearchEncounter) Class.forName(className).newInstance();
            researchEncounter.setLocationId(locationId);
            researchEncounter.executeWhole();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void executeDefeatedInvestigatorEncounter(DefeatedInvestigatorEncounterData data) {
        switch (data.getInvestigatorId()) {
            case THE_SPY:
                data.setCrippledTestStat(Stat.INFLUENCE);
                data.setInsaneTestStat(Stat.OBSERVATION);
                break;
            case THE_SOLDIER:
                data.setCrippledTestStat(Stat.STRENGTH);
                data.setInsaneTestStat(Stat.INFLUENCE);
                break;
            case THE_ASTRONOMER:
                data.setCrippledTestStat(Stat.WILL);
                data.setInsaneTestStat(Stat.INFLUENCE);
                break;
            case THE_POLITICIAN:
                data.setCrippledTestStat(Stat.INFLUENCE);
                data.setInsaneTestStat(Stat.INFLUENCE);
                break;
            case THE_ENTERTAINER:
                data.setCrippledTestStat(Stat.INFLUENCE);
                data.setInsaneTestStat(Stat.LORE);
                break;
            case THE_PSYCHIC:
                data.setCrippledTestStat(Stat.INFLUENCE);
                data.setInsaneTestStat(Stat.LORE);
                break;
            case THE_SHAMAN:
                data.setCrippledTestStat(Stat.INFLUENCE);
                data.setInsaneTestStat(Stat.WILL);
                break;
            case THE_MUSICIAN:
                data.setCrippledTestStat(Stat.INFLUENCE);
                data.setInsaneTestStat(Stat.WILL);
                break;
            case THE_SAILOR:
                data.setCrippledTestStat(Stat.OBSERVATION);
                data.setInsaneTestStat(Stat.INFLUENCE);
                break;
            case THE_MARTIAL_ARTIST:
                data.setCrippledTestStat(Stat.STRENGTH);
                data.setInsaneTestStat(Stat.INFLUENCE);
                break;
            case THE_REDEEMED_CULTIST:
                data.setCrippledTestStat(Stat.LORE);
                data.setInsaneTestStat(Stat.INFLUENCE);
                break;
            case THE_BOUNTY_HUNTER:
                data.setCrippledTestStat(Stat.OBSERVATION);
                data.setInsaneTestStat(Stat.INFLUENCE);
                break;
            case THE_EX_CONVICT:
                data.setCrippledTestStat(Stat.INFLUENCE);
                data.setInsaneTestStat(Stat.STRENGTH);
                break;
            case THE_EXPEDITION_LEADER:
                data.setCrippledTestStat(Stat.INFLUENCE);
                data.setInsaneTestStat(Stat.STRENGTH);
                break;
            case THE_ACTRESS:
                data.setCrippledTestStat(Stat.INFLUENCE);
                data.setInsaneTestStat(Stat.INFLUENCE);
                break;
            case THE_ROOKIE_COP:
                data.setCrippledTestStat(Stat.OBSERVATION);
                data.setInsaneTestStat(Stat.INFLUENCE);
                break;
            case THE_VIOLINIST:
                data.setCrippledTestStat(Stat.INFLUENCE);
                data.setInsaneTestStat(Stat.WILL);
                break;
            case THE_BOOTLEGGER:
                data.setCrippledTestStat(Stat.INFLUENCE);
                data.setInsaneTestStat(Stat.OBSERVATION);
                break;
            case THE_HANDYMAN:
                data.setCrippledTestStat(Stat.STRENGTH);
                data.setInsaneTestStat(Stat.INFLUENCE);
                break;
            case THE_WAITRESS:
                data.setCrippledTestStat(Stat.INFLUENCE);
                data.setInsaneTestStat(Stat.LORE);
                break;

        }
        new DefeatedInvestigatorEncounterTemplate(data).execute();
    }

    @Override
    public void executeExpeditionEncounter(int page, LocationId locationId) {
        try {
            String className = "sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition." + locationId.lastWordToCamelCase() + page + "Encounter";
            AbstractExpeditionEncounter expeditionEncounter = (AbstractExpeditionEncounter) Class.forName(className).newInstance();
            expeditionEncounter.executeWhole();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void executeOtherWorldEncounter(int page) {
        try {
            String className = "sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld.OtherWorld" + page + "Encounter";
            AbstractOtherWorldEncounter otherWorldEncounter = (AbstractOtherWorldEncounter) Class.forName(className).newInstance();
            otherWorldEncounter.executeWhole();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void executeRlyehRisenEncounter(Integer page) {
        try {
            String className = "sk.sivak.eldritchhorror.core.eventlistener.encounter.rlyehrisen.RlyehRisen" + page + "Encounter";
            AbstractRlyehRisenEncounter encounter = (AbstractRlyehRisenEncounter) Class.forName(className).newInstance();
            encounter.executeWhole();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void executeVoidBetweenWorldsEncounter(Integer page) {
        try {
            String className = "sk.sivak.eldritchhorror.core.eventlistener.encounter.voidbetweenworlds.VoidBetweenWorlds" + page + "Encounter";
            AbstractVoidBetweenWorldsEncounter encounter = (AbstractVoidBetweenWorldsEncounter) Class.forName(className).newInstance();
            encounter.executeWhole();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void executeTheKeyAndTheGateEncounter(Integer page) {
        try {
            String className = "sk.sivak.eldritchhorror.core.eventlistener.encounter.thekeyandthegate.TheKeyAndTheGate" + page + "Encounter";
            AbstractTheKeyAndTheGateEncounter encounter = (AbstractTheKeyAndTheGateEncounter) Class.forName(className).newInstance();
            encounter.executeWhole();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void executeLocationEncounter(Integer page, LocationEncounter.LocationEncounterType locationType) {
        try {
            String className = "sk.sivak.eldritchhorror.core.eventlistener.encounter.location." + locationType.getLocationName().replaceAll(" ","") + page + "Encounter";
            AbstractLocationEncounter locationEncounter = (AbstractLocationEncounter) Class.forName(className).newInstance();
            locationEncounter.executeWhole();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void executeMythosText(MythosCard.MythosColor mythosColor, int id) {
        try {
            String className = "sk.sivak.eldritchhorror.core.eventlistener.mythos." + mythosColor.getValue() +"MythosCard" + id;
            MythosCardEventListener mythosCardEventListener = (MythosCardEventListener) Class.forName(className).newInstance();
            mythosCardEventListener.execute();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }


    @Override
    public void justLoadRumor(String rumorId) {
        switch (rumorId) {
            case "TheWindWalker" : {
                new BlueMythosCard1().justLoad();
                break;
            }
            case "StarsAligned" : {
                new BlueMythosCard2().justLoad();
                break;
            }
            case "FracturedReality" : {
                new BlueMythosCard3().justLoad();
                break;
            }
            case "LostKnowledge" : {
                new BlueMythosCard4().justLoad();
                break;
            }
            case "GrowingMadness" : {
                new BlueMythosCard5().justLoad();
                break;
            }
            default:
                throw new IllegalArgumentException("RumorId not recognized: " + rumorId);
        }
    }
}
