package sk.sivak.eldritchhorror.core.controller.provider;

import sk.sivak.eldritchhorror.core.controller.*;

/**
 * @author msivak
 */
public class ControllerProviderImpl implements ControllerProvider {

    private final ControllerImpl controller;
    private final InitGameControllerImpl initGameController;
    private final GameControllerImpl gameController;
    private final TestControllerImpl testController;
    private final CardControllerImpl reserveController;
    private final DoomOmenControllerImpl doomOmenController;
    private final MonsterControllerImpl monsterController;
    private final TypewriterControllerImpl typewriterController;

    public ControllerProviderImpl() {
        controller = new ControllerImpl();
        initGameController = new InitGameControllerImpl();
        gameController = new GameControllerImpl();
        testController = new TestControllerImpl();
        reserveController = new CardControllerImpl();
        doomOmenController = new DoomOmenControllerImpl();
        monsterController = new MonsterControllerImpl();
        typewriterController = new TypewriterControllerImpl();
    }

    @Override
    public ControllerImpl getController() {
        return controller;
    }

    @Override
    public InitGameControllerImpl getInitGameController() {
        return initGameController;
    }

    @Override
    public GameControllerImpl getGameControllerImpl() {
        return gameController;
    }

    @Override
    public TestControllerImpl getTestControllerImpl() {
        return testController;
    }

    @Override
    public CardControllerImpl getReserveController() {
        return reserveController;
    }

    @Override
    public DoomOmenControllerImpl getDoomOmenController() {
        return doomOmenController;
    }

    @Override
    public MonsterControllerImpl getMonsterController() {
        return monsterController;
    }

    @Override
    public TypewriterControllerImpl getTypewriterController() {
        return typewriterController;
    }
}
