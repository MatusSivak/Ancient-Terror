package sk.sivak.eldritchhorror.core.service.provider;

import sk.sivak.eldritchhorror.core.service.*;

/**
 * @author msivak
 */
public interface ServiceProvider {

    ServiceImpl getService();

    InitGameServiceImpl getInitGameService();

    GameServiceImpl getGameService();

    CardServiceImpl getReserveService();

    DoomOmenServiceImpl getDoomOmenService();

    BasicActionServiceImpl getActionService();

    InvestigatorServiceImpl getInvestigatorActionService();

    TestServiceImpl getTestService();

    TutorialServiceImpl getTutorialService();

    TokenServiceImpl getTokenService();

    MonsterServiceImpl getMonsterService();

    EncounterServiceImpl getEncounterService();
}
