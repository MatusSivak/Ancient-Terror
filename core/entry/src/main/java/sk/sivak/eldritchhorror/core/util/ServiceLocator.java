package sk.sivak.eldritchhorror.core.util;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import sk.sivak.eldritchhorror.core.Game;
import sk.sivak.eldritchhorror.core.action.provider.*;
import sk.sivak.eldritchhorror.core.commandqueue.CommandQueueFacadeImpl;
import sk.sivak.eldritchhorror.core.commandqueue.CommandQueueImpl;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.controller.provider.ControllerProviderImpl;
import sk.sivak.eldritchhorror.core.eventlistener.provider.*;
import sk.sivak.eldritchhorror.core.eventqueue.provider.EventQueueProviderImpl;
import sk.sivak.eldritchhorror.core.initializer.Initializer;
import sk.sivak.eldritchhorror.core.model.provider.ModelProviderImpl;
import sk.sivak.eldritchhorror.core.service.DiscoveredArtifactsRepository;
import sk.sivak.eldritchhorror.core.service.DiscoveredAssetsRepository;
import sk.sivak.eldritchhorror.core.service.DiscoveredConditionsRepository;
import sk.sivak.eldritchhorror.core.service.DiscoveredSpellsRepository;
import sk.sivak.eldritchhorror.core.service.UnlockedArtifactsRepository;
import sk.sivak.eldritchhorror.core.service.UnlockedAssetsRepository;
import sk.sivak.eldritchhorror.core.service.provider.ServiceProvider;
import sk.sivak.eldritchhorror.core.service.provider.ServiceProviderImpl;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.provider.ViewProviderImpl;

import java.util.List;

/**
 * @author msivak
 */
public class ServiceLocator {

    private static ServiceLocator instance;

    private Game game;
    private ServiceProviderImpl serviceProvider;
    private ViewProviderImpl viewProvider;
    private Skin defaultSkin;
    private ModelProviderImpl modelProvider;
    private UnlockedAssetsRepository unlockedAssetsRepository;
    private UnlockedArtifactsRepository unlockedArtifactsRepository;

    private DiscoveredAssetsRepository discoveredAssetsRepository;
    private DiscoveredArtifactsRepository discoveredArtifactsRepository;
    private DiscoveredSpellsRepository discoveredSpellsRepository;
    private DiscoveredConditionsRepository discoveredConditionsRepository;
    private GlobalThrowableHandler globalThrowableHandler;

    public ServiceLocator() {
        init();
    }

    private static ServiceLocator get() {
        if (instance == null) {
            instance = new ServiceLocator();
        }
        return instance;
    }

    public static void nullifyInstance() {
        instance = null;
    }

    public static void setGame(Game game) {
        get().game = game;
    }

    public static ServiceProvider getServiceProvider() {
        return get().serviceProvider;
    }

    public static ViewProviderImpl getViewProvider() {
        return get().viewProvider;
    }

    public static ModelProviderImpl getModelProvider() {
        return get().modelProvider;
    }

    public static GlobalThrowableHandler getGlobalThrowableHandler() {
        return get().globalThrowableHandler;
    }

    public static Skin getDefaultSkin() {
        return get().defaultSkin;
    }

    public void init() {
        defaultSkin = CustomAssetManager.getSkin();
        Initializer initializer = new Initializer();
        serviceProvider = new ServiceProviderImpl();
        ControllerProviderImpl controllerProvider = new ControllerProviderImpl();
        EventListenerProviderImpl eventListenerProvider = new EventListenerProviderImpl();
        AssetListenerProviderImpl assetListenerProvider = new AssetListenerProviderImpl();
        ArtifactListenerProviderImpl artifactListenerProviderImpl = new ArtifactListenerProviderImpl();
        ConditionListenerProviderImpl conditionListenerProvider = new ConditionListenerProviderImpl();
        SpellListenerProviderImpl spellListenerProvider = new SpellListenerProviderImpl();
        ActionListenerProvider actionListenerProvider = new ActionListenerProviderImpl();
        EventQueueProviderImpl eventQueueProvider = new EventQueueProviderImpl();
        CommandQueueFacadeImpl commandQueueFacade = new CommandQueueFacadeImpl();
        globalThrowableHandler = new GlobalThrowableHandler(defaultSkin);
        CommandQueueImpl.get().setThrowableConsumer(globalThrowableHandler::handleThrowable);
        modelProvider = new ModelProviderImpl();
        viewProvider = new ViewProviderImpl();
        ActionProviderImpl actionProvider = new ActionProviderImpl();
        InitGameActionProviderImpl initGameActionProvider = new InitGameActionProviderImpl();
        GameActionProviderImpl gameActionProvider = new GameActionProviderImpl();
        ReserveActionProviderImpl reserveActionProvider = new ReserveActionProviderImpl();
        TokenActionProviderImpl tokenActionProvider = new TokenActionProviderImpl();
        TutorialActionProviderImpl tutorialActionProviderImpl = new TutorialActionProviderImpl();
        DoomOmenActionProviderImpl doomOmenActionProvider = new DoomOmenActionProviderImpl();
        MonsterActionProviderImpl monsterActionProvider = new MonsterActionProviderImpl();
        EncounterActionProviderImpl encounterActionProvider = new EncounterActionProviderImpl();
        InvestigatorActionProviderImpl actionsActionProvider = new InvestigatorActionProviderImpl();
        TestActionProviderImpl testActionProvider = new TestActionProviderImpl();

        initUnlockedAndDiscoveredService();

        initializer.setEventListenerProvider(eventListenerProvider);
        initializer.setAssetListenerProvider(assetListenerProvider);
        initializer.setArtifactListenerProvider(artifactListenerProviderImpl);
        initializer.setConditionListenerProvider(conditionListenerProvider);
        initializer.setSpellListenerProvider(spellListenerProvider);
        initializer.setServiceProvider(serviceProvider);
        initializer.setInvestigatorActionProvider(actionsActionProvider);
        initializer.setControllerProvider(controllerProvider);
        initializer.setEventQueueProvider(eventQueueProvider);
        initializer.setActionProvider(actionProvider);
        initializer.setActionListenerProvider(actionListenerProvider);
        initializer.setInitGameActionProvider(initGameActionProvider);
        initializer.setGameActionProvider(gameActionProvider);
        initializer.setReserveActionProvider(reserveActionProvider);
        initializer.setTokenActionProviderImpl(tokenActionProvider);
        initializer.setTutorialActionProviderImpl(tutorialActionProviderImpl);
        initializer.setDoomOmenActionProvider(doomOmenActionProvider);
        initializer.setMonsterActionProvider(monsterActionProvider);
        initializer.setEncounterActionProvider(encounterActionProvider);
        initializer.setTestActionProvider(testActionProvider);
        initializer.setModelProvider(modelProvider);
        initializer.setViewProvider(viewProvider);
        initializer.setCommandQueueFacade(commandQueueFacade);
        initializer.init();
    }

    private void initUnlockedAndDiscoveredService() {
        unlockedAssetsRepository = new UnlockedAssetsRepository();
        unlockedArtifactsRepository = new UnlockedArtifactsRepository();

        discoveredAssetsRepository = new DiscoveredAssetsRepository();
        discoveredArtifactsRepository = new DiscoveredArtifactsRepository();
        discoveredSpellsRepository = new DiscoveredSpellsRepository();
        discoveredConditionsRepository = new DiscoveredConditionsRepository();

        CustomAssetManager.setCardAssetLoadedCallback(value -> {
            AssetId discoveredAssetId = AssetId.valueOf(value.substring(11, value.length()-4));
            List<AssetId> discoveredAssets = discoveredAssetsRepository.getDiscovered();
            if (discoveredAssets.contains(discoveredAssetId)) {
                return;
            }
            discoveredAssets.add(discoveredAssetId);
            discoveredAssetsRepository.saveDiscovered(discoveredAssets);
        });

        CustomAssetManager.setCardArtifactLoadedCallback(value -> {
            ArtifactId received = ArtifactId.valueOf(value.substring(14, value.length()-4));
            List<ArtifactId> discovered = discoveredArtifactsRepository.getDiscovered();
            if (discovered.contains(received)) {
                return;
            }
            discovered.add(received);
            discoveredArtifactsRepository.saveDiscovered(discovered);
        });

        CustomAssetManager.setCardSpellLoadedCallback(value -> {
            SpellId received = SpellId.valueOf(value.substring(11, value.length()-4));
            List<SpellId> discovered = discoveredSpellsRepository.getDiscovered();
            if (discovered.contains(received)) {
                return;
            }
            discovered.add(received);
            discoveredSpellsRepository.saveDiscovered(discovered);
        });

        CustomAssetManager.setCardConditionLoadedCallback(value -> {
            ConditionId received = ConditionId.valueOf(value.substring(15, value.length()-4));
            List<ConditionId> discovered = discoveredConditionsRepository.getDiscovered();
            if (discovered.contains(received)) {
                return;
            }
            discovered.add(received);
            discoveredConditionsRepository.saveDiscovered(discovered);
        });

        modelProvider.setUnlockedAssetsSupplier(unlockedAssetsRepository::getUnlockedAssets);
        modelProvider.setUnlockedArtifactsSupplier(unlockedArtifactsRepository::getUnlockedArtifacts);
    }



    public static UnlockedAssetsRepository getUnlockedAssetsRepository() {
        return get().unlockedAssetsRepository;
    }

    public static UnlockedArtifactsRepository getUnlockedArtifactsRepository() {
        return get().unlockedArtifactsRepository;
    }

    public static DiscoveredAssetsRepository getDiscoveredAssetsRepository() {
        return get().discoveredAssetsRepository;
    }

    public static DiscoveredArtifactsRepository getDiscoveredArtifactsRepository() {
        return get().discoveredArtifactsRepository;
    }

    public static DiscoveredSpellsRepository getDiscoveredSpellsRepository() {
        return get().discoveredSpellsRepository;
    }

    public static DiscoveredConditionsRepository getDiscoveredConditionsRepository() {
        return get().discoveredConditionsRepository;
    }
}
