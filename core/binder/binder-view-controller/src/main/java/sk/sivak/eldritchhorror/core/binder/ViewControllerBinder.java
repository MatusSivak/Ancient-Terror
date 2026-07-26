package sk.sivak.eldritchhorror.core.binder;

import sk.sivak.eldritchhorror.core.controller.*;
import sk.sivak.eldritchhorror.core.controller.provider.ControllerProvider;
import sk.sivak.eldritchhorror.core.model.provider.ModelProvider;
import sk.sivak.eldritchhorror.core.view.ViewImpl;
import sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterViewImpl;
import sk.sivak.eldritchhorror.core.view.doomomen.DoomOmenViewImpl;
import sk.sivak.eldritchhorror.core.view.game.GameViewImpl;
import sk.sivak.eldritchhorror.core.view.initgame.InitGameViewImpl;
import sk.sivak.eldritchhorror.core.view.monster.MonsterViewImpl;
import sk.sivak.eldritchhorror.core.view.provider.ViewProvider;
import sk.sivak.eldritchhorror.core.view.reserve.CardViewImpl;
import sk.sivak.eldritchhorror.core.view.test.TestViewImpl;

/**
 * @author msivak
 */
public class ViewControllerBinder {

    private ViewProvider viewProvider;
    private ControllerProvider controllerProvider;
    private ModelProvider modelProvider;

    public void setViewProvider(ViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }

    public void setControllerProvider(ControllerProvider controllerProvider) {
        this.controllerProvider = controllerProvider;
    }

    public void setModelProvider(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    public void bind() {
        bindControllerView();
        bindInitGameControllerView();
        bindDoomOmenControllerView();
        bindMonsterControllerView();
        bindTypewriterControllerView();
        bindGameControllerView();
        bindTestControllerView();
        bindReserveControllerView();
    }

    private void bindGameControllerView() {
        GameViewImpl view = viewProvider.getGameView();
        GameControllerImpl controller = controllerProvider.getGameControllerImpl();
        controller.setExpeditionDeck(modelProvider.getExpeditionDeck());
        controller.setAncientOne(modelProvider.getModel().getAncientOne());
        controller.setMysteryDeck(modelProvider.getMysteryDeck());
        controller.setAssetDeck(modelProvider.getAssetsDeck());
        controller.setConditionsDeck(modelProvider.getConditionsDeck());
        controller.setSpellsDeck(modelProvider.getSpellsDeck());
        controller.setRumors(modelProvider.getRumors());
        controller.setArtifactsDeck(modelProvider.getArtifactsDeck());
        controller.setDoomTrackRead(modelProvider.getDoomTrack());
        controller.setOmenTrack(modelProvider.getOmenTrack());
        controller.setInvestigators(modelProvider.getInvestigators());
        controller.setLocationMap(modelProvider.getLocationMap());
        controller.setCluePoolRead(modelProvider.getCluePool());
        controller.setMonsterCup(modelProvider.getMonsterCup());
        controller.setBackgroundModel(modelProvider.getModel().getBackgroundModel());
        controller.setView(view);
        controller.setEncounterView(viewProvider.getEncounterView());
        view.setController(controller);
    }

    private void bindTestControllerView() {
        TestViewImpl view = viewProvider.getTestView();
        TestControllerImpl controller = controllerProvider.getTestControllerImpl();
        controller.setInvestigators(modelProvider.getInvestigators());
        controller.setView(view);
        view.setController(controller);
    }

    private void bindReserveControllerView() {
        CardViewImpl view = viewProvider.getReserveView();
        CardControllerImpl controller = controllerProvider.getReserveController();
        controller.setAssetDeck(modelProvider.getAssetsDeck());
        controller.setView(view);
        view.setController(controller);
    }

    private void bindDoomOmenControllerView() {
        DoomOmenViewImpl view = viewProvider.getDoomOmenView();
        DoomOmenControllerImpl controller = controllerProvider.getDoomOmenController();
        controller.setView(view);
        controller.setGameView(viewProvider.getGameView());
        view.setController(controller);
    }

    private void bindMonsterControllerView() {
        MonsterViewImpl view = viewProvider.getMonsterView();
        MonsterControllerImpl controller = controllerProvider.getMonsterController();
        controller.setMonsterView(view);
        controller.setMysteryDeck(modelProvider.getMysteryDeck());
        view.setMonsterController(controller);
        view.setGameController(controllerProvider.getGameControllerImpl());
    }

    private void bindTypewriterControllerView() {
        TypewriterViewImpl view = viewProvider.getTypewriterView();
        TypewriterControllerImpl controller = controllerProvider.getTypewriterController();
        controller.setTypewriterView(view);
    }

    private void bindInitGameControllerView() {
        InitGameViewImpl view = viewProvider.getInitGameView();
        InitGameControllerImpl controller = controllerProvider.getInitGameController();
        controller.setView(view);
        controller.setModel(modelProvider.getModel());
        controller.setMysteryDeck(modelProvider.getMysteryDeck());
        controller.setInvestigatorsRead(modelProvider.getInvestigators());
        view.setController(controller);
    }

    private void bindControllerView() {
        ViewImpl view = viewProvider.getView();
        ControllerImpl controller = controllerProvider.getController();
        view.setController(controller);
        controller.setView(view);
        controller.setModel(modelProvider.getModel());
    }
}
