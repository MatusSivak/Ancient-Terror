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
public interface ViewProvider {

    CombatScreen getCombatScreen();

    HallOfFameScreen getHallOfFameScreen();

    ViewImpl getView();

    InitGameViewImpl getInitGameView();

    GameViewImpl getGameView();

    TestViewImpl getTestView();

    CardViewImpl getReserveView();

    DoomOmenViewImpl getDoomOmenView();

    MonsterViewImpl getMonsterView();

    TypewriterViewImpl getTypewriterView();

    EncounterViewImpl getEncounterView();
}
