package sk.sivak.eldritchhorror.core.view.provider;

import sk.sivak.eldritchhorror.core.view.ViewImpl;
import sk.sivak.eldritchhorror.core.view.components.combat.CombatScreen;
import sk.sivak.eldritchhorror.core.view.components.halloffame.HallOfFameScreen;
import sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterViewImpl;
import sk.sivak.eldritchhorror.core.view.doomomen.DoomOmenViewImpl;
import sk.sivak.eldritchhorror.core.view.encounter.EncounterViewImpl;
import sk.sivak.eldritchhorror.core.view.game.GameViewImpl;
import sk.sivak.eldritchhorror.core.view.initgame.InitGameViewImpl;
import sk.sivak.eldritchhorror.core.view.monster.MonsterViewImpl;
import sk.sivak.eldritchhorror.core.view.reserve.CardViewImpl;
import sk.sivak.eldritchhorror.core.view.test.TestViewImpl;

/**
 * @author msivak
 */
public class ViewProviderImpl implements ViewProvider {

    private final ViewImpl view;
    private final InitGameViewImpl initGameView;
    private final GameViewImpl gameView;
    private final CombatScreen combatScreen;
    private final HallOfFameScreen hallOfFameScreen;
    private final TestViewImpl testView;
    private final CardViewImpl reserveView;
    private final DoomOmenViewImpl doomOmenView;
    private final MonsterViewImpl monsterView;
    private final TypewriterViewImpl typewriterView;
    private final EncounterViewImpl encounterView;

    public ViewProviderImpl() {
        view = new ViewImpl();
        initGameView = new InitGameViewImpl();
        combatScreen = new CombatScreen();
        hallOfFameScreen = new HallOfFameScreen();
        gameView = new GameViewImpl();
        testView = new TestViewImpl();
        reserveView = new CardViewImpl();
        doomOmenView = new DoomOmenViewImpl();
        monsterView = new MonsterViewImpl();
        typewriterView = new TypewriterViewImpl();
        encounterView = new EncounterViewImpl();
    }

    @Override
    public InitGameViewImpl getInitGameView() {
        return initGameView;
    }

    @Override
    public CombatScreen getCombatScreen() {
        return combatScreen;
    }

    @Override
    public HallOfFameScreen getHallOfFameScreen() {
        return hallOfFameScreen;
    }

    @Override
    public ViewImpl getView() {
        return view;
    }

    @Override
    public GameViewImpl getGameView() {
        return gameView;
    }

    @Override
    public EncounterViewImpl getEncounterView() {
        return encounterView;
    }

    @Override
    public TestViewImpl getTestView() {
        return testView;
    }

    @Override
    public CardViewImpl getReserveView() {
        return reserveView;
    }

    @Override
    public DoomOmenViewImpl getDoomOmenView() {
        return doomOmenView;
    }

    @Override
    public MonsterViewImpl getMonsterView() {
        return monsterView;
    }

    @Override
    public TypewriterViewImpl getTypewriterView() {
        return typewriterView;
    }
}
