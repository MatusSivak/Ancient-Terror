package sk.sivak.eldritchhorror.core.binder;

import sk.sivak.eldritchhorror.core.action.provider.*;
import sk.sivak.eldritchhorror.core.commandqueue.CommandQueueFacade;
import sk.sivak.eldritchhorror.core.service.*;
import sk.sivak.eldritchhorror.core.service.provider.ServiceProvider;

/**
 * @author msivak
 */
public class ServiceBinder {

    private ActionProvider actionProvider;
    private CommandQueueFacade commandQueueFacade;
    private ServiceProvider serviceProvider;
    private InitGameActionProvider initGameActionProvider;
    private GameActionProvider gameActionProvider;
    private ReserveActionProvider reserveActionProvider;
    private TokenActionProvider tokenActionProvider;
    private TutorialActionProvider tutorialActionProvider;
    private DoomOmenActionProvider doomOmenActionProvider;
    private InvestigatorActionProvider investigatorActionProvider;
    private MonsterActionProvider monsterActionProvider;
    private EncounterActionProvider encounterActionProvider;
    private TestActionProvider testActionProvider;

    public void setInvestigatorActionProvider(InvestigatorActionProvider investigatorActionProvider) {
        this.investigatorActionProvider = investigatorActionProvider;
    }

    public void setMonsterActionProvider(MonsterActionProvider monsterActionProvider) {
        this.monsterActionProvider = monsterActionProvider;
    }

    public void setEncounterActionProvider(EncounterActionProvider encounterActionProvider) {
        this.encounterActionProvider = encounterActionProvider;
    }

    public void setGameActionProvider(GameActionProvider gameActionProvider) {
        this.gameActionProvider = gameActionProvider;
    }

    public void setReserveActionProvider(ReserveActionProvider reserveActionProvider) {
        this.reserveActionProvider = reserveActionProvider;
    }

    public void setTokenActionProvider(TokenActionProvider tokenActionProvider) {
        this.tokenActionProvider = tokenActionProvider;
    }

    public void setTutorialActionProvider(TutorialActionProvider tutorialActionProvider) {
        this.tutorialActionProvider = tutorialActionProvider;
    }

    public void setDoomOmenActionProvider(DoomOmenActionProvider doomOmenActionProvider) {
        this.doomOmenActionProvider = doomOmenActionProvider;
    }

    public void setInitGameActionProvider(InitGameActionProvider initGameActionProvider) {
        this.initGameActionProvider = initGameActionProvider;
    }

    public void setActionProvider(ActionProvider actionProvider) {
        this.actionProvider = actionProvider;
    }

    public void setCommandQueueFacade(CommandQueueFacade commandQueueFacade) {
        this.commandQueueFacade = commandQueueFacade;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public void setTestActionProvider(TestActionProvider testActionProvider) {
        this.testActionProvider = testActionProvider;
    }

    public void bind() {
        bindService();
        bindInitGameService();
        bindGameService();
        bindReserveService();
        bindDoomOmenService();
        bindBasicActionService();
        bindInvestigatorActionService();
        bindMonsterService();
        bindEncounterService();
        bindTestService();
        bindTokenService();
        bindTutorialService();
    }

    private void bindTestService() {
        TestServiceImpl service = serviceProvider.getTestService();
        service.setActionProvider(actionProvider);
        service.setCommandQueueFacade(commandQueueFacade);
        service.setTestActionProvider(testActionProvider);
        service.setTokenService(serviceProvider.getTokenService());
        service.setService(serviceProvider.getService());
        service.setGameService(serviceProvider.getGameService());
        service.setMonsterService(serviceProvider.getMonsterService());
        service.setEncounterService(serviceProvider.getEncounterService());
    }

    private void bindService() {
        ServiceImpl service = serviceProvider.getService();
        service.setActionProvider(actionProvider);
        service.setCommandQueueFacade(commandQueueFacade);
    }

    private void bindGameService() {
        GameServiceImpl service = serviceProvider.getGameService();
        service.setActionProvider(actionProvider);
        service.setCommandQueueFacade(commandQueueFacade);
        service.setGameActionProvider(gameActionProvider);
        service.setInvestigatorService(serviceProvider.getInvestigatorActionService());
        service.setTokenService(serviceProvider.getTokenService());
        service.setInitGameService(serviceProvider.getInitGameService());
    }

    private void bindReserveService() {
        CardServiceImpl service = serviceProvider.getReserveService();
        service.setActionProvider(actionProvider);
        service.setCommandQueueFacade(commandQueueFacade);
        service.setReserveActionProvider(reserveActionProvider);
    }

    private void bindTokenService() {
        TokenServiceImpl service = serviceProvider.getTokenService();
        service.setActionProvider(actionProvider);
        service.setCommandQueueFacade(commandQueueFacade);
        service.setTokenActionProvider(tokenActionProvider);
    }

    private void bindTutorialService() {
        TutorialServiceImpl service = serviceProvider.getTutorialService();
        service.setActionProvider(actionProvider);
        service.setCommandQueueFacade(commandQueueFacade);
        service.setTutorialActionProvider(tutorialActionProvider);
        service.setInvestigatorService(serviceProvider.getInvestigatorActionService());
        service.setDoomOmenService(serviceProvider.getDoomOmenService());
        service.setMonsterService(serviceProvider.getMonsterService());
        service.setBasicActionService(serviceProvider.getActionService());
        service.setTokenService(serviceProvider.getTokenService());
        service.setEncounterService(serviceProvider.getEncounterService());
        service.setGameService(serviceProvider.getGameService());
        service.setService(serviceProvider.getService());
    }

    private void bindDoomOmenService() {
        DoomOmenServiceImpl service = serviceProvider.getDoomOmenService();
        service.setActionProvider(actionProvider);
        service.setCommandQueueFacade(commandQueueFacade);
        service.setDoomOmenActionProvider(doomOmenActionProvider);
    }

    private void bindBasicActionService() {
        BasicActionServiceImpl service = serviceProvider.getActionService();
        service.setActionProvider(actionProvider);
        service.setCommandQueueFacade(commandQueueFacade);
        service.setInvestigatorActionProvider(investigatorActionProvider);
    }

    private void bindInvestigatorActionService() {
        InvestigatorServiceImpl service = serviceProvider.getInvestigatorActionService();
        service.setActionProvider(actionProvider);
        service.setCommandQueueFacade(commandQueueFacade);
        service.setInvestigatorActionProvider(investigatorActionProvider);
        service.setGameService(serviceProvider.getGameService());
    }

    private void bindMonsterService() {
        MonsterServiceImpl service = serviceProvider.getMonsterService();
        service.setActionProvider(actionProvider);
        service.setCommandQueueFacade(commandQueueFacade);
        service.setMonsterActionProvider(monsterActionProvider);
        service.setTestService(serviceProvider.getTestService());
        service.setService(serviceProvider.getService());
    }

    private void bindEncounterService() {
        EncounterServiceImpl service = serviceProvider.getEncounterService();
        service.setActionProvider(actionProvider);
        service.setCommandQueueFacade(commandQueueFacade);
        service.setEncounterActionProvider(encounterActionProvider);
        service.setInvestigatorService(serviceProvider.getInvestigatorActionService());
        service.setGameService(serviceProvider.getGameService());
    }

    private void bindInitGameService() {
        InitGameServiceImpl initGameService = serviceProvider.getInitGameService();
        initGameService.setActionProvider(actionProvider);
        initGameService.setCommandQueueFacade(commandQueueFacade);
        initGameService.setInitGameActionProvider(initGameActionProvider);
    }

}
