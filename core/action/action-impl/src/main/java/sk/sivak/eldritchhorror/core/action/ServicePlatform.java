package sk.sivak.eldritchhorror.core.action;

import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsTracker;
import sk.sivak.eldritchhorror.core.controller.*;
import sk.sivak.eldritchhorror.core.eventlistener.provider.*;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueWrite;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;

/**
 * @author msivak
 */
public class ServicePlatform {

    private static ServicePlatform instance;
    private EventQueueWrite eventQueue;
    private Controller controller;
    private InitGameController initGameController;
    private AssetsDeckWrite assetsDeck;
    private ArtifactsDeckWrite artifactsDeck;
    private ConditionsDeckWrite conditionsDeck;
    private SpellsDeckWrite spellsDeck;
    private CluePoolWrite cluePool;
    private GateStackWrite gateStack;
    private InvestigatorsWrite investigators;
    private PhaseWrite phaseWrite;
    private LocationMapWrite locationMap;
    private ModelWrite model;
    private MonsterCupWrite monsterCup;
    private ActionListenerProvider actionListenerProvider;
    private AssetListenerProvider assetListenerProvider;
    private ArtifactListenerProvider artifactListenerProvider;
    private ConditionListenerProvider conditionListenerProvider;
    private SpellListenerProvider spellListenerProvider;
    private EventListenerProvider eventListenerProvider;
    private GameService gameService;
    private InitGameService initGameService;
    private Service service;
    private TutorialService tutorialService;
    private OmenTrackWrite omenTrack;
    private DoomTrackWrite doomTrack;
    private ExpeditionDeckWrite expeditionDeck;
    private GameController gameController;
    private MysteryDeckWrite mysteryDeck;
    private MythosDeckWrite mythosDeck;
    private BasicActionService basicActionService;
    private InvestigatorService investigatorService;
    private TestService testService;
    private TestController testController;
    private CardController cardController;
    private CardService cardService;
    private DoomOmenService doomOmenService;
    private DoomOmenController doomOmenController;
    private TokenService tokenService;
    private TestFlavorWrite testFlavor;
    private PerformedEncountersWrite performedEncounters;
    private PerformedActionsWrite performedActions;
    private MonsterService monsterService;
    private EncounterService encounterService;
    private MonsterController monsterController;
    private TypewriterController typewriterController;
    private GeneralEncounterDeckWrite generalEncounterDeck;
    private ResearchEncounterDeckWrite researchEncounterDeck;
    private LocationEncounterDeckWrite locationEncounterDeck;
    private RumorsWrite rumors;
    private VortexesWrite vortexes;

    public static ServicePlatform get() {
        if (instance == null) {
            instance = new ServicePlatform();
        }
        return instance;
    }

    public static void nullifyInstance() {
        instance = null;
    }


    public void setTutorialService(TutorialService tutorialService) {
        this.tutorialService = tutorialService;
    }

    public TutorialService getTutorialService() {
        return tutorialService;
    }

    public CardController getCardController() {
        return cardController;
    }

    public void setCardController(CardController cardController) {
        this.cardController = cardController;
    }

    public DoomOmenController getDoomOmenController() {
        return doomOmenController;
    }

    public void setDoomOmenController(DoomOmenController doomOmenController) {
        this.doomOmenController = doomOmenController;
    }

    public MysteryDeckWrite getMysteryDeck() {
        return mysteryDeck;
    }

    public void setMysteryDeck(MysteryDeckWrite mysteryDeck) {
        this.mysteryDeck = mysteryDeck;
    }

    public MythosDeckWrite getMythosDeck() {
        return mythosDeck;
    }

    public void setMythosDeck(MythosDeckWrite mythosDeck) {
        this.mythosDeck = mythosDeck;
    }

    public OmenTrackWrite getOmenTrack() {
        return omenTrack;
    }

    public void setOmenTrack(OmenTrackWrite omenTrack) {
        this.omenTrack = omenTrack;
    }

    public EventQueueWrite getEventQueue() {
        return eventQueue;
    }

    public void setEventQueue(EventQueueWrite eventQueue) {
        this.eventQueue = eventQueue;
    }

    public DoomTrackWrite getDoomTrack() {
        return doomTrack;
    }

    public void setDoomTrack(DoomTrackWrite doomTrack) {
        this.doomTrack = doomTrack;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public InitGameController getInitGameController() {
        return initGameController;
    }

    public void setInitGameController(InitGameController initGameController) {
        this.initGameController = initGameController;
    }

    public AssetsDeckWrite getAssetsDeck() {
        return assetsDeck;
    }

    public void setAssetsDeck(AssetsDeckWrite assetsDeck) {
        this.assetsDeck = assetsDeck;
    }

    public ArtifactsDeckWrite getArtifactsDeck() {
        return artifactsDeck;
    }

    public void setArtifactsDeck(ArtifactsDeckWrite artifactsDeck) {
        this.artifactsDeck = artifactsDeck;
    }

    public ConditionsDeckWrite getConditionsDeck() {
        return conditionsDeck;
    }

    public void setConditionsDeck(ConditionsDeckWrite conditionsDeck) {
        this.conditionsDeck = conditionsDeck;
    }

    public SpellsDeckWrite getSpellsDeck() {
        return spellsDeck;
    }

    public void setSpellsDeck(SpellsDeckWrite spellsDeck) {
        this.spellsDeck = spellsDeck;
    }

    public CluePoolWrite getCluePool() {
        return cluePool;
    }

    public void setCluePool(CluePoolWrite cluePool) {
        this.cluePool = cluePool;
    }

    public GateStackWrite getGateStack() {
        return gateStack;
    }

    public void setGateStack(GateStackWrite gateStack) {
        this.gateStack = gateStack;
    }

    public InvestigatorsWrite getInvestigators() {
        return investigators;
    }

    public void setInvestigators(InvestigatorsWrite investigators) {
        this.investigators = investigators;
    }

    public LocationMapWrite getLocationMap() {
        return locationMap;
    }

    public void setLocationMap(LocationMapWrite locationMap) {
        this.locationMap = locationMap;
    }

    public ModelWrite getModel() {
        return model;
    }

    public void setModel(ModelWrite model) {
        this.model = model;
    }

    public ActionListenerProvider getActionListenerProvider() {
        return actionListenerProvider;
    }

    public void setActionListenerProvider(ActionListenerProvider actionListenerProvider) {
        this.actionListenerProvider = actionListenerProvider;
    }

    public AssetListenerProvider getAssetListenerProvider() {
        return assetListenerProvider;
    }

    public void setAssetListenerProvider(AssetListenerProvider assetListenerProvider) {
        this.assetListenerProvider = assetListenerProvider;
    }

    public ArtifactListenerProvider getArtifactListenerProvider() {
        return artifactListenerProvider;
    }

    public void setArtifactListenerProvider(ArtifactListenerProvider artifactListenerProvider) {
        this.artifactListenerProvider = artifactListenerProvider;
    }

    public ConditionListenerProvider getConditionListenerProvider() {
        return conditionListenerProvider;
    }

    public void setConditionListenerProvider(ConditionListenerProvider conditionListenerProvider) {
        this.conditionListenerProvider = conditionListenerProvider;
    }

    public SpellListenerProvider getSpellListenerProvider() {
        return spellListenerProvider;
    }

    public void setSpellListenerProvider(SpellListenerProvider spellListenerProvider) {
        this.spellListenerProvider = spellListenerProvider;
    }

    public EventListenerProvider getEventListenerProvider() {
        return eventListenerProvider;
    }

    public void setEventListenerProvider(EventListenerProvider eventListenerProvider) {
        this.eventListenerProvider = eventListenerProvider;
    }

    public GameService getGameService() {
        return gameService;
    }

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    public InitGameService getInitGameService() {
        return initGameService;
    }

    public void setInitGameService(InitGameService initGameService) {
        this.initGameService = initGameService;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }


    public ExpeditionDeckWrite getExpeditionDeck() {
        return expeditionDeck;
    }

    public void setExpeditionDeck(ExpeditionDeckWrite expeditionDeck) {
        this.expeditionDeck = expeditionDeck;
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public MonsterCupWrite getMonsterCup() {
        return monsterCup;
    }

    public void setMonsterCup(MonsterCupWrite monsterCup) {
        this.monsterCup = monsterCup;
    }

    public BasicActionService getBasicActionService() {
        return basicActionService;
    }

    public void setBasicActionService(BasicActionService basicActionService) {
        this.basicActionService = basicActionService;
    }

    public InvestigatorService getInvestigatorService() {
        return investigatorService;
    }

    public void setInvestigatorService(InvestigatorService investigatorService) {
        this.investigatorService = investigatorService;
    }

    public TestController getTestController() {
        return testController;
    }

    public void setTestController(TestController testController) {
        this.testController = testController;
    }

    public TestService getTestService() {
        return testService;
    }

    public void setTestService(TestService testService) {
        this.testService = testService;
    }

    public CardService getCardService() {
        return cardService;
    }

    public void setCardService(CardService cardService) {
        this.cardService = cardService;
    }

    public DoomOmenService getDoomOmenService() {
        return doomOmenService;
    }

    public void setDoomOmenService(DoomOmenService doomOmenService) {
        this.doomOmenService = doomOmenService;
    }

    public TokenService getTokenService() {
        return tokenService;
    }

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public PhaseWrite getPhase() {
        return phaseWrite;
    }

    public void setPhaseWrite(PhaseWrite phaseWrite) {
        this.phaseWrite = phaseWrite;
    }

    public TestFlavorWrite getTestFlavor() {
        return testFlavor;
    }

    public void setTestFlavor(TestFlavorWrite testFlavor) {
        this.testFlavor = testFlavor;
    }


    public PerformedEncountersWrite getPerformedEncounters() {
        return performedEncounters;
    }

    public void setPerformedEncounters(PerformedEncountersWrite performedEncounters) {
        this.performedEncounters = performedEncounters;
    }

    public void setPerformedActions(PerformedActionsWrite performedActions) {
        this.performedActions = performedActions;
    }

    public PerformedActionsWrite getPerformedActions() {
        return performedActions;
    }

    public MonsterController getMonsterController() {
        return monsterController;
    }

    public void setMonsterController(MonsterController monsterController) {
        this.monsterController = monsterController;
    }

    public MonsterService getMonsterService() {
        return monsterService;
    }

    public void setMonsterService(MonsterService monsterService) {
        this.monsterService = monsterService;
    }

    public EncounterService getEncounterService() {
        return encounterService;
    }

    public void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    public void setGeneralEncounterDeck(GeneralEncounterDeckWrite generalEncounterDeck) {
        this.generalEncounterDeck = generalEncounterDeck;
    }

    public void setResearchEncounterDeck(ResearchEncounterDeckWrite researchEncounterDeck) {
        this.researchEncounterDeck = researchEncounterDeck;
    }

    public void setLocationEncounterDeck(LocationEncounterDeckWrite locationEncounterDeck) {
        this.locationEncounterDeck = locationEncounterDeck;
    }

    public LocationEncounterDeckWrite getLocationEncounterDeck() {
        return locationEncounterDeck;
    }

    public GeneralEncounterDeckWrite getGeneralEncounterDeck() {
        return generalEncounterDeck;
    }

    public ResearchEncounterDeckWrite getResearchEncounterDeck() {
        return researchEncounterDeck;
    }

    public TypewriterController getTypewriterController() {
        return typewriterController;
    }

    public void setTypewriterController(TypewriterController typewriterController) {
        this.typewriterController = typewriterController;
    }

    public void setRumors(RumorsWrite rumors) {
        this.rumors = rumors;
    }

    public RumorsWrite getRumors() {
        return rumors;
    }

    public void setVortexes(VortexesWrite vortexes) {
        this.vortexes = vortexes;
    }

    public VortexesWrite getVortexes() {
        return vortexes;
    }
}
