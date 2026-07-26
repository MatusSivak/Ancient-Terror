package sk.sivak.eldritchhorror.core.service.provider;

import sk.sivak.eldritchhorror.core.service.*;

/**
 * @author msivak
 */
public class ServiceProviderImpl implements ServiceProvider {

    private TutorialServiceImpl tutorialService;
    private TestServiceImpl testService;
    private InvestigatorServiceImpl investigatorActionService;
    private MonsterServiceImpl monsterService;
    private BasicActionServiceImpl actionService;
    private GameServiceImpl gameService;
    private TokenServiceImpl tokenService;
    private CardServiceImpl reserveService;
    private InitGameServiceImpl initGameService;
    private ServiceImpl service;
    private DoomOmenServiceImpl doomOmenService;
    private EncounterServiceImpl encounterService;

    public ServiceProviderImpl() {
        service = new ServiceImpl();
        initGameService = new InitGameServiceImpl();
        gameService = new GameServiceImpl();
        tokenService = new TokenServiceImpl();
        reserveService = new CardServiceImpl();
        actionService = new BasicActionServiceImpl();
        investigatorActionService = new InvestigatorServiceImpl();
        monsterService = new MonsterServiceImpl();
        testService = new TestServiceImpl();
        tutorialService = new TutorialServiceImpl();
        doomOmenService = new DoomOmenServiceImpl();
        encounterService = new EncounterServiceImpl();
    }

    @Override
    public ServiceImpl getService() {
        return service;
    }

    @Override
    public InitGameServiceImpl getInitGameService() {
        return initGameService;
    }

    public GameServiceImpl getGameService() {
        return gameService;
    }

    public TokenServiceImpl getTokenService() {
        return tokenService;
    }

    @Override
    public CardServiceImpl getReserveService() {
        return reserveService;
    }

    @Override
    public DoomOmenServiceImpl getDoomOmenService() {
        return doomOmenService;
    }

    @Override
    public BasicActionServiceImpl getActionService() {
        return actionService;
    }

    @Override
    public InvestigatorServiceImpl getInvestigatorActionService() {
        return investigatorActionService;
    }

    @Override
    public TestServiceImpl getTestService() {
        return testService;
    }

    public TutorialServiceImpl getTutorialService() {
        return tutorialService;
    }

    @Override
    public MonsterServiceImpl getMonsterService() {
        return monsterService;
    }

    @Override
    public EncounterServiceImpl getEncounterService() {
        return encounterService;
    }
}
