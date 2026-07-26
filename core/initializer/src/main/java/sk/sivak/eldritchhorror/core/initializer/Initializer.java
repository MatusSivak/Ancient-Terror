package sk.sivak.eldritchhorror.core.initializer;

import sk.sivak.eldritchhorror.core.action.provider.*;
import sk.sivak.eldritchhorror.core.binder.*;
import sk.sivak.eldritchhorror.core.commandqueue.CommandQueueFacade;
import sk.sivak.eldritchhorror.core.controller.provider.ControllerProvider;
import sk.sivak.eldritchhorror.core.eventlistener.provider.*;
import sk.sivak.eldritchhorror.core.eventqueue.provider.EventQueueProvider;
import sk.sivak.eldritchhorror.core.model.provider.ModelProvider;
import sk.sivak.eldritchhorror.core.service.provider.ServiceProvider;
import sk.sivak.eldritchhorror.core.view.provider.ViewProvider;

/**
 * @author msivak
 */
public class Initializer {

    private ServiceProvider serviceProvider;
    private EventListenerProvider eventListenerProvider;
    private AssetListenerProvider assetListenerProvider;
    private ConditionListenerProvider conditionListenerProvider;
    private SpellListenerProvider spellListenerProvider;
    private ActionListenerProvider actionListenerProvider;
    private ControllerProvider controllerProvider;
    private ViewProvider viewProvider;
    private ModelProvider modelProvider;
    private EventQueueProvider eventQueueProvider;
    private CommandQueueFacade commandQueueFacade;
    private InvestigatorActionProvider investigatorActionProvider;

    private ActionProviderImpl actionProvider;
    private TestActionProviderImpl testActionProvider;
    private InitGameActionProviderImpl initGameActionProvider;
    private GameActionProviderImpl gameActionProvider;
    private ReserveActionProviderImpl reserveActionProvider;
    private TokenActionProviderImpl tokenActionProviderImpl;
    private TutorialActionProviderImpl tutorialActionProviderImpl;
    private DoomOmenActionProvider doomOmenActionProvider;
    private MonsterActionProvider monsterActionProvider;
    private EncounterActionProvider encounterActionProvider;
    private ArtifactListenerProviderImpl artifactListenerProvider;

    public void setTokenActionProviderImpl(TokenActionProviderImpl tokenActionProviderImpl) {
        this.tokenActionProviderImpl = tokenActionProviderImpl;
    }

    public void setTutorialActionProviderImpl(TutorialActionProviderImpl tutorialActionProviderImpl) {
        this.tutorialActionProviderImpl = tutorialActionProviderImpl;
    }

    public void setInvestigatorActionProvider(InvestigatorActionProvider investigatorActionProvider) {
        this.investigatorActionProvider = investigatorActionProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public void setEventListenerProvider(EventListenerProvider eventListenerProvider) {
        this.eventListenerProvider = eventListenerProvider;
    }

    public void setAssetListenerProvider(AssetListenerProvider assetListenerProvider) {
        this.assetListenerProvider = assetListenerProvider;
    }

    public void setArtifactListenerProvider(ArtifactListenerProviderImpl artifactListenerProvider) {
        this.artifactListenerProvider = artifactListenerProvider;
    }

    public void setConditionListenerProvider(ConditionListenerProvider conditionListenerProvider) {
        this.conditionListenerProvider = conditionListenerProvider;
    }

    public void setSpellListenerProvider(SpellListenerProvider spellListenerProvider) {
        this.spellListenerProvider = spellListenerProvider;
    }

    public void setActionListenerProvider(ActionListenerProvider actionListenerProvider) {
        this.actionListenerProvider = actionListenerProvider;
    }

    public void setCommandQueueFacade(CommandQueueFacade commandQueueFacade) {
        this.commandQueueFacade = commandQueueFacade;
    }

    public void setControllerProvider(ControllerProvider controllerProvider) {
        this.controllerProvider = controllerProvider;
    }

    public void setViewProvider(ViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }

    public void setModelProvider(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    public void setEventQueueProvider(EventQueueProvider eventQueueProvider) {
        this.eventQueueProvider = eventQueueProvider;
    }

    public void setActionProvider(ActionProviderImpl actionProvider) {
        this.actionProvider = actionProvider;
    }

    public void setInitGameActionProvider(InitGameActionProviderImpl initGameActionProvider) {
        this.initGameActionProvider = initGameActionProvider;
    }

    public void setGameActionProvider(GameActionProviderImpl gameActionProvider) {
        this.gameActionProvider = gameActionProvider;
    }

    public void setReserveActionProvider(ReserveActionProviderImpl reserveActionProvider) {
        this.reserveActionProvider = reserveActionProvider;
    }

    public void setDoomOmenActionProvider(DoomOmenActionProvider doomOmenActionProvider) {
        this.doomOmenActionProvider = doomOmenActionProvider;
    }

    public void setMonsterActionProvider(MonsterActionProvider monsterActionProvider) {
        this.monsterActionProvider = monsterActionProvider;
    }

    public void setEncounterActionProvider(EncounterActionProvider encounterActionProvider) {
        this.encounterActionProvider = encounterActionProvider;
    }

    public void setTestActionProvider(TestActionProviderImpl testActionProvider) {
        this.testActionProvider = testActionProvider;
    }

    public void init() {
        bindControllerModel();
        bindViewController();
        bindEventListenerProvider();
        bindServiceProvider();
        bindActionProvider();
    }

    private void bindActionProvider() {
        ActionBinder actionBinder = new ActionBinder();
        actionBinder.setAssetListenerProvider(assetListenerProvider);
        actionBinder.setArtifactListenerProvider(artifactListenerProvider);
        actionBinder.setConditionListenerProvider(conditionListenerProvider);
        actionBinder.setSpellListenerProvider(spellListenerProvider);
        actionBinder.setControllerProvider(controllerProvider);
        actionBinder.setEventListenerProvider(eventListenerProvider);
        actionBinder.setActionListenerProvider(actionListenerProvider);
        actionBinder.setServiceProvider(serviceProvider);
        actionBinder.setEventQueueProvider(eventQueueProvider);
        actionBinder.setModelProvider(modelProvider);
        actionBinder.bind();
    }

    private void bindViewController() {
        ViewControllerBinder viewControllerBinder = new ViewControllerBinder();
        viewControllerBinder.setControllerProvider(controllerProvider);
        viewControllerBinder.setModelProvider(modelProvider);
        viewControllerBinder.setViewProvider(viewProvider);
        viewControllerBinder.bind();
    }

    private void bindControllerModel() {
        ControllerModelBinder controllerModelBinder = new ControllerModelBinder();
        controllerModelBinder.setControllerProvider(controllerProvider);
        controllerModelBinder.setModelProvider(modelProvider);
        controllerModelBinder.bind();
    }

    private void bindEventListenerProvider() {
        EventListenerBinder eventListenerBinder = new EventListenerBinder();
        eventListenerBinder.setModel(modelProvider.getModel());
        eventListenerBinder.setBasicActionService(serviceProvider.getActionService());
        eventListenerBinder.setInvestigatorService(serviceProvider.getInvestigatorActionService());
        eventListenerBinder.setCluePool(modelProvider.getCluePool());
        eventListenerBinder.setVortexes(modelProvider.getVortexes());
        eventListenerBinder.setConditionsDeck(modelProvider.getConditionsDeck());
        eventListenerBinder.setSpellsDeck(modelProvider.getSpellsDeck());
        eventListenerBinder.setTokenService(serviceProvider.getTokenService());
        eventListenerBinder.setEncounterService(serviceProvider.getEncounterService());
        eventListenerBinder.setPerformedActions(modelProvider.getPerformedActions());
        eventListenerBinder.setPerformedEncounters(modelProvider.getPerformedEncounters());
        eventListenerBinder.setExpeditionDeck(modelProvider.getExpeditionDeck());
        eventListenerBinder.setOmenTrack(modelProvider.getOmenTrack());
        eventListenerBinder.setMythosDeck(modelProvider.getMythosDeck());
        eventListenerBinder.setMysteryDeck(modelProvider.getMysteryDeck());
        eventListenerBinder.setDoomTrack(modelProvider.getDoomTrack());
        eventListenerBinder.setGateStackRead(modelProvider.getGateStack());
        eventListenerBinder.setRumors(modelProvider.getRumors());
        eventListenerBinder.setTestService(serviceProvider.getTestService());
        eventListenerBinder.setMonsterService(serviceProvider.getMonsterService());
        eventListenerBinder.setInvestigators(modelProvider.getInvestigators());
        eventListenerBinder.setTestFlavor(modelProvider.getTestFlavor());
        eventListenerBinder.setMonsterCup(modelProvider.getMonsterCup());
        eventListenerBinder.setLocationMap(modelProvider.getLocationMap());
        eventListenerBinder.setAssetDeck(modelProvider.getAssetsDeck());
        eventListenerBinder.setArtifactsDeckRead(modelProvider.getArtifactsDeck());
        eventListenerBinder.setService(serviceProvider.getService());
        eventListenerBinder.setInitGameService(serviceProvider.getInitGameService());
        eventListenerBinder.setTutorialService(serviceProvider.getTutorialService());
        eventListenerBinder.setGameService(serviceProvider.getGameService());
        eventListenerBinder.setCardService(serviceProvider.getReserveService());
        eventListenerBinder.setDoomOmenService(serviceProvider.getDoomOmenService());
        eventListenerBinder.setEventQueue(eventQueueProvider.getEventQueue());
        eventListenerBinder.bind();
    }

    private void bindServiceProvider() {
        ServiceBinder serviceBinder = new ServiceBinder();
        serviceBinder.setActionProvider(actionProvider);
        serviceBinder.setTestActionProvider(testActionProvider);
        serviceBinder.setInitGameActionProvider(initGameActionProvider);
        serviceBinder.setGameActionProvider(gameActionProvider);
        serviceBinder.setInvestigatorActionProvider(investigatorActionProvider);
        serviceBinder.setReserveActionProvider(reserveActionProvider);
        serviceBinder.setTokenActionProvider(tokenActionProviderImpl);
        serviceBinder.setTutorialActionProvider(tutorialActionProviderImpl);
        serviceBinder.setDoomOmenActionProvider(doomOmenActionProvider);
        serviceBinder.setMonsterActionProvider(monsterActionProvider);
        serviceBinder.setEncounterActionProvider(encounterActionProvider);
        serviceBinder.setCommandQueueFacade(commandQueueFacade);
        serviceBinder.setServiceProvider(serviceProvider);
        serviceBinder.bind();
    }

}
