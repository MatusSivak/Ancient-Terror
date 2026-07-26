package sk.sivak.eldritchhorror.core.binder;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.controller.provider.ControllerProvider;
import sk.sivak.eldritchhorror.core.eventlistener.provider.*;
import sk.sivak.eldritchhorror.core.eventqueue.provider.EventQueueProvider;
import sk.sivak.eldritchhorror.core.model.provider.ModelProvider;
import sk.sivak.eldritchhorror.core.service.provider.ServiceProvider;

/**
 * @author msivak
 */
public class ActionBinder {

    private EventListenerProvider eventListenerProvider;
    private AssetListenerProvider assetListenerProvider;
    private ArtifactListenerProvider artifactListenerProvider;
    private ConditionListenerProvider conditionListenerProvider;
    private SpellListenerProvider spellListenerProvider;
    private ActionListenerProvider actionListenerProvider;

    private EventQueueProvider eventQueueProvider;
    private ControllerProvider controllerProvider;
    private ModelProvider modelProvider;
    private ServiceProvider serviceProvider;

    public void setConditionListenerProvider(ConditionListenerProvider conditionListenerProvider) {
        this.conditionListenerProvider = conditionListenerProvider;
    }

    public void setSpellListenerProvider(SpellListenerProvider spellListenerProvider) {
        this.spellListenerProvider = spellListenerProvider;
    }

    public void setArtifactListenerProvider(ArtifactListenerProvider artifactListenerProvider) {
        this.artifactListenerProvider = artifactListenerProvider;
    }

    public void setAssetListenerProvider(AssetListenerProvider assetListenerProvider) {
        this.assetListenerProvider = assetListenerProvider;
    }

    public void setModelProvider(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    public void setEventListenerProvider(EventListenerProvider eventListenerProvider) {
        this.eventListenerProvider = eventListenerProvider;
    }

    public void setControllerProvider(ControllerProvider controllerProvider) {
        this.controllerProvider = controllerProvider;
    }

    public void setEventQueueProvider(EventQueueProvider eventQueueProvider) {
        this.eventQueueProvider = eventQueueProvider;
    }

    public void setActionListenerProvider(ActionListenerProvider actionListenerProvider) {
        this.actionListenerProvider = actionListenerProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public void bind() {
        ServicePlatform servicePlatform = ServicePlatform.get();
        servicePlatform.setActionListenerProvider(actionListenerProvider);
        servicePlatform.setAssetListenerProvider(assetListenerProvider);
        servicePlatform.setArtifactListenerProvider(artifactListenerProvider);
        servicePlatform.setConditionListenerProvider(conditionListenerProvider);
        servicePlatform.setSpellListenerProvider(spellListenerProvider);
        servicePlatform.setAssetsDeck(modelProvider.getAssetsDeck());
        servicePlatform.setArtifactsDeck(modelProvider.getArtifactsDeck());
        servicePlatform.setPerformedActions(modelProvider.getPerformedActions());
        servicePlatform.setPerformedEncounters(modelProvider.getPerformedEncounters());
        servicePlatform.setGeneralEncounterDeck(modelProvider.getGeneralEncounterDeck());
        servicePlatform.setResearchEncounterDeck(modelProvider.getResearchEncounterDeck());
        servicePlatform.setLocationEncounterDeck(modelProvider.getLocationEncounterDeck());
        servicePlatform.setConditionsDeck(modelProvider.getConditionsDeck());
        servicePlatform.setSpellsDeck(modelProvider.getSpellsDeck());
        servicePlatform.setCluePool(modelProvider.getCluePool());
        servicePlatform.setVortexes(modelProvider.getVortexes());
        servicePlatform.setController(controllerProvider.getController());
        servicePlatform.setEventListenerProvider(eventListenerProvider);
        servicePlatform.setEventQueue(eventQueueProvider.getEventQueue());
        servicePlatform.setOmenTrack(modelProvider.getOmenTrack());
        servicePlatform.setMysteryDeck(modelProvider.getMysteryDeck());
        servicePlatform.setRumors(modelProvider.getRumors());
        servicePlatform.setMythosDeck(modelProvider.getMythosDeck());
        servicePlatform.setDoomTrack(modelProvider.getDoomTrack());
        servicePlatform.setGateStack(modelProvider.getGateStack());
        servicePlatform.setInitGameController(controllerProvider.getInitGameController());
        servicePlatform.setInvestigators(modelProvider.getInvestigators());
        servicePlatform.setTestFlavor(modelProvider.getTestFlavor());
        servicePlatform.setLocationMap(modelProvider.getLocationMap());
        servicePlatform.setModel(modelProvider.getModel());
        servicePlatform.setService(serviceProvider.getService());
        servicePlatform.setInitGameService(serviceProvider.getInitGameService());
        servicePlatform.setGameService(serviceProvider.getGameService());
        servicePlatform.setTokenService(serviceProvider.getTokenService());
        servicePlatform.setExpeditionDeck(modelProvider.getExpeditionDeck());
        servicePlatform.setMonsterCup(modelProvider.getMonsterCup());
        servicePlatform.setPhaseWrite(modelProvider.getPhase());
        servicePlatform.setBasicActionService(serviceProvider.getActionService());
        servicePlatform.setInvestigatorService(serviceProvider.getInvestigatorActionService());
        servicePlatform.setMonsterService(serviceProvider.getMonsterService());
        servicePlatform.setEncounterService(serviceProvider.getEncounterService());
        servicePlatform.setMonsterController(controllerProvider.getMonsterController());
        servicePlatform.setTypewriterController(controllerProvider.getTypewriterController());
        servicePlatform.setTestService(serviceProvider.getTestService());
        servicePlatform.setTutorialService(serviceProvider.getTutorialService());
        servicePlatform.setCardService(serviceProvider.getReserveService());
        servicePlatform.setDoomOmenService(serviceProvider.getDoomOmenService());
        servicePlatform.setGameController(controllerProvider.getGameControllerImpl());
        servicePlatform.setTestController(controllerProvider.getTestControllerImpl());
        servicePlatform.setCardController(controllerProvider.getReserveController());
        servicePlatform.setDoomOmenController(controllerProvider.getDoomOmenController());
    }

}
