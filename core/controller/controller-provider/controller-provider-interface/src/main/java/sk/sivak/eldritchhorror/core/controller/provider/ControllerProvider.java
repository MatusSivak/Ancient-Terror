package sk.sivak.eldritchhorror.core.controller.provider;

import sk.sivak.eldritchhorror.core.controller.*;

/**
 * @author msivak
 */
public interface ControllerProvider {

    ControllerImpl getController();

    InitGameControllerImpl getInitGameController();

    GameControllerImpl getGameControllerImpl();

    TestControllerImpl getTestControllerImpl();

    CardControllerImpl getReserveController();

    DoomOmenControllerImpl getDoomOmenController();

    MonsterControllerImpl getMonsterController();

    TypewriterControllerImpl getTypewriterController();

}
