package sk.sivak.eldritchhorror.core.binder;

import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueRead;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;

/**
 * @author msivak
 */
public class EventListenerBinder {

    private Service service;
    private GameService gameService;
    private CardService cardService;
    private TokenService tokenService;
    private EncounterService encounterService;
    private MonsterService monsterService;
    private OmenTrackRead omenTrack;
    private DoomOmenService doomOmenService;
    private BasicActionService basicActionService;
    private InvestigatorService investigatorService;
    private EventQueueRead eventQueueRead;
    private ModelRead model;
    private InvestigatorsRead investigators;
    private LocationMapRead locationMap;
    private AssetDeckRead assetDeck;
    private ArtifactsDeckRead artifactsDeckRead;
    private InitGameService initGameService;
    private TutorialService tutorialService;
    private MonsterCupRead monsterCup;
    private CluePoolRead cluePool;
    private ConditionsDeckRead conditionsDeck;
    private SpellsDeckRead spellsDeck;
    private TestService testService;
    private TestFlavorRead testFlavor;
    private PerformedActionsRead performedActions;
    private PerformedEncountersRead performedEncounters;
    private ExpeditionDeckRead expeditionDeck;
    private MythosDeckRead mythosDeck;
    private MysteryDeckRead mysteryDeck;
    private RumorsRead rumors;
    private GateStackRead gateStackRead;
    private VortexesRead vortexes;
    private DoomTrackRead doomTrack;

    public void setDoomTrack(DoomTrackRead doomTrack) {
        this.doomTrack = doomTrack;
    }

    public void setMythosDeck(MythosDeckRead mythosDeck) {
        this.mythosDeck = mythosDeck;
    }

    public void setMysteryDeck(MysteryDeckRead mysteryDeck) {
        this.mysteryDeck = mysteryDeck;
    }

    public void setOmenTrack(OmenTrackRead omenTrack) {
        this.omenTrack = omenTrack;
    }

    public void setExpeditionDeck(ExpeditionDeckRead expeditionDeck) {
        this.expeditionDeck = expeditionDeck;
    }

    public void setGateStackRead(GateStackRead gateStackRead) {
        this.gateStackRead = gateStackRead;
    }

    public void setRumors(RumorsRead rumors) {
        this.rumors = rumors;
    }

    public void setPerformedActions(PerformedActionsRead performedActions) {
        this.performedActions = performedActions;
    }

    public void setPerformedEncounters(PerformedEncountersRead performedEncounters) {
        this.performedEncounters = performedEncounters;
    }

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    public void setConditionsDeck(ConditionsDeckRead conditionsDeck) {
        this.conditionsDeck = conditionsDeck;
    }

    public void setSpellsDeck(SpellsDeckRead spellsDeck) {
        this.spellsDeck = spellsDeck;
    }

    public void setMonsterCup(MonsterCupRead monsterCup) {
        this.monsterCup = monsterCup;
    }

    public void setInvestigators(InvestigatorsRead investigators) {
        this.investigators = investigators;
    }

    public void setLocationMap(LocationMapRead locationMap) {
        this.locationMap = locationMap;
    }

    public void setAssetDeck(AssetDeckRead assetDeck) {
        this.assetDeck = assetDeck;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setInitGameService(InitGameService initGameService) {
        this.initGameService = initGameService;
    }

    public void setTutorialService(TutorialService tutorialService) {
        this.tutorialService = tutorialService;
    }

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    public void setCardService(CardService cardService) {
        this.cardService = cardService;
    }

    public void setDoomOmenService(DoomOmenService doomOmenService) {
        this.doomOmenService = doomOmenService;
    }

    public void setEventQueue(EventQueueRead eventQueueRead) {
        this.eventQueueRead = eventQueueRead;
    }

    public void setModel(ModelRead model) {
        this.model = model;
    }

    public void setBasicActionService(BasicActionService basicActionService) {
        this.basicActionService = basicActionService;
    }

    public void setArtifactsDeckRead(ArtifactsDeckRead artifactsDeckRead) {
        this.artifactsDeckRead = artifactsDeckRead;
    }

    public void setInvestigatorService(InvestigatorService investigatorService) {
        this.investigatorService = investigatorService;
    }

    public void setCluePool(CluePoolRead cluePool) {
        this.cluePool = cluePool;
    }

    public void setTestService(TestService testService) {
        this.testService = testService;
    }

    public void setTestFlavor(TestFlavorRead testFlavor) {
        this.testFlavor = testFlavor;
    }

    public void setMonsterService(MonsterService monsterService) {
        this.monsterService = monsterService;
    }


    public void setVortexes(VortexesRead vortexes) {
        this.vortexes = vortexes;
    }


    public void bind() {
        ServicePlatform servicePlatform = ServicePlatform.get();
        servicePlatform.setEventQueue(eventQueueRead);
        servicePlatform.setGateStackRead(gateStackRead);
        servicePlatform.setExpeditionDeck(expeditionDeck);
        servicePlatform.setMythosDeck(mythosDeck);
        servicePlatform.setMysteryDeck(mysteryDeck);
        servicePlatform.setRumors(rumors);
        servicePlatform.setEncounterService(encounterService);
        servicePlatform.setService(service);
        servicePlatform.setGameService(gameService);
        servicePlatform.setCardService(cardService);
        servicePlatform.setPerformedActions(performedActions);
        servicePlatform.setPerformedEncounters(performedEncounters);
        servicePlatform.setTokenService(tokenService);
        servicePlatform.setMonsterService(monsterService);
        servicePlatform.setVortexes(vortexes);
        servicePlatform.setDoomTrack(doomTrack);
        servicePlatform.setTutorialService(tutorialService);
        servicePlatform.setOmenTrack(omenTrack);
        servicePlatform.setInitGameService(initGameService);
        servicePlatform.setInvestigators(investigators);
        servicePlatform.setBasicActionService(basicActionService);
        servicePlatform.setInvestigatorService(investigatorService);
        servicePlatform.setMonsterCup(monsterCup);
        servicePlatform.setTestFlavor(testFlavor);
        servicePlatform.setCluePoolRead(cluePool);
        servicePlatform.setConditionsDeck(conditionsDeck);
        servicePlatform.setSpellsDeck(spellsDeck);
        servicePlatform.setDoomOmenService(doomOmenService);
        servicePlatform.setTestService(testService);
        servicePlatform.setLocationMap(locationMap);
        servicePlatform.setAssetDeck(assetDeck);
        servicePlatform.setArtifactsDeck(artifactsDeckRead);
        servicePlatform.setModel(model);
        servicePlatform.setService(service);
    }
}
