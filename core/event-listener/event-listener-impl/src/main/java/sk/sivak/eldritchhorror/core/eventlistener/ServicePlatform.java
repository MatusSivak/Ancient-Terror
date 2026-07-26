package sk.sivak.eldritchhorror.core.eventlistener;

import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsTracker;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueRead;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;

/**
 * @author msivak
 */
public class ServicePlatform {

    private static ServicePlatform instance;
    private EventQueueRead eventQueue;
    private InvestigatorsRead investigators;
    private LocationMapRead locationMap;
    private AssetDeckRead assetDeck;
    private MythosDeckRead mythosDeck;
    private MysteryDeckRead mysteryDeck;
    private RumorsRead rumors;
    private ArtifactsDeckRead artifactsDeck;
    private ModelRead model;
    private GameService gameService;
    private EncounterService encounterService;
    private TokenService tokenService;
    private CardService cardService;
    private InitGameService initGameService;
    private Service service;
    private TestService testService;
    private BasicActionService basicActionService;
    private InvestigatorService investigatorService;
    private MonsterCupRead monsterCup;
    private GateStackRead gateStackRead;
    private CluePoolRead cluePoolRead;
    private ConditionsDeckRead conditionsDeck;
    private SpellsDeckRead spellsDeck;
    private DoomOmenService doomOmenService;
    private TestFlavorRead testFlavor;
    private PerformedActionsRead performedActions;
    private PerformedEncountersRead performedEncounters;
    private MonsterService monsterService;
    private OmenTrackRead omenTrack;
    private ExpeditionDeckRead expeditionDeck;
    private VortexesRead vortexes;
    private DoomTrackRead doomTrack;
    private TutorialService tutorialService;

    public static ServicePlatform get() {
        if (instance == null) {
            instance = new ServicePlatform();
        }
        return instance;
    }

    public static void nullifyInstance() {
        instance = null;
    }

    public void setMythosDeck(MythosDeckRead mythosDeck) {
        this.mythosDeck = mythosDeck;
    }

    public MythosDeckRead getMythosDeck() {
        return mythosDeck;
    }

    public void setMysteryDeck(MysteryDeckRead mysteryDeck) {
        this.mysteryDeck = mysteryDeck;
    }

    public MysteryDeckRead getMysteryDeck() {
        return mysteryDeck;
    }

    public void setRumors(RumorsRead rumors) {
        this.rumors = rumors;
    }

    public RumorsRead getRumors() {
        return rumors;
    }

    public ExpeditionDeckRead getExpeditionDeck() {
        return expeditionDeck;
    }

    public void setExpeditionDeck(ExpeditionDeckRead expeditionDeck) {
        this.expeditionDeck = expeditionDeck;
    }

    public EncounterService getEncounterService() {
        return encounterService;
    }

    public void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    public GateStackRead getGateStackRead() {
        return gateStackRead;
    }

    public void setGateStackRead(GateStackRead gateStackRead) {
        this.gateStackRead = gateStackRead;
    }

    public EventQueueRead getEventQueue() {
        return eventQueue;
    }

    public void setEventQueue(EventQueueRead eventQueue) {
        this.eventQueue = eventQueue;
    }

    public InvestigatorsRead getInvestigators() {
        return investigators;
    }

    public void setInvestigators(InvestigatorsRead investigators) {
        this.investigators = investigators;
    }

    public LocationMapRead getLocationMap() {
        return locationMap;
    }

    public void setLocationMap(LocationMapRead locationMap) {
        this.locationMap = locationMap;
    }

    public AssetDeckRead getAssetDeck() {
        return assetDeck;
    }

    public void setAssetDeck(AssetDeckRead assetDeck) {
        this.assetDeck = assetDeck;
    }

    public ArtifactsDeckRead getArtifactsDeck() {
        return artifactsDeck;
    }

    public void setArtifactsDeck(ArtifactsDeckRead artifactsDeck) {
        this.artifactsDeck = artifactsDeck;
    }

    public ModelRead getModel() {
        return model;
    }

    public void setModel(ModelRead model) {
        this.model = model;
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

    public MonsterCupRead getMonsterCup() {
        return monsterCup;
    }

    public void setMonsterCup(MonsterCupRead monsterCup) {
        this.monsterCup = monsterCup;
    }

    public CluePoolRead getCluePool() {
        return cluePoolRead;
    }

    public void setCluePoolRead(CluePoolRead cluePoolRead) {
        this.cluePoolRead = cluePoolRead;
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

    public PerformedActionsRead getPerformedActions() {
        return performedActions;
    }

    public void setPerformedActions(PerformedActionsRead performedActions) {
        this.performedActions = performedActions;
    }

    public PerformedEncountersRead getPerformedEncounters() {
        return performedEncounters;
    }

    public void setPerformedEncounters(PerformedEncountersRead performedEncounters) {
        this.performedEncounters = performedEncounters;
    }

    public ConditionsDeckRead getConditionsDeck() {
        return conditionsDeck;
    }

    public void setConditionsDeck(ConditionsDeckRead conditionsDeck) {
        this.conditionsDeck = conditionsDeck;
    }

    public SpellsDeckRead getSpellsDeck() {
        return spellsDeck;
    }

    public void setSpellsDeck(SpellsDeckRead spellsDeck) {
        this.spellsDeck = spellsDeck;
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

    public TestFlavorRead getTestFlavor() {
        return testFlavor;
    }

    public void setTestFlavor(TestFlavorRead testFlavor) {
        this.testFlavor = testFlavor;
    }

    public MonsterService getMonsterService() {
        return monsterService;
    }

    public void setMonsterService(MonsterService monsterService) {
        this.monsterService = monsterService;
    }

    public void setOmenTrack(OmenTrackRead omenTrack) {
        this.omenTrack = omenTrack;
    }

    public OmenTrackRead getOmenTrack() {
        return omenTrack;
    }

    public void setVortexes(VortexesRead vortexes) {
        this.vortexes = vortexes;
    }

    public VortexesRead getVortexes() {
        return vortexes;
    }

    public DoomTrackRead getDoomTrack() {
        return doomTrack;
    }

    public void setDoomTrack(DoomTrackRead doomTrack) {
        this.doomTrack = doomTrack;
    }

    public TutorialService getTutorialService() {
        return tutorialService;
    }

    public void setTutorialService(TutorialService tutorialService) {
        this.tutorialService = tutorialService;
    }
}
