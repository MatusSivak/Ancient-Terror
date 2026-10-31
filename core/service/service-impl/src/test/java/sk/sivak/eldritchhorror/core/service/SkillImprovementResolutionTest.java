package sk.sivak.eldritchhorror.core.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Completable;
import rx.Single;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.action.*;
import sk.sivak.eldritchhorror.core.action.impl.action.investigator.*;
import sk.sivak.eldritchhorror.core.action.impl.game.AskAction;
import sk.sivak.eldritchhorror.core.action.provider.*;
import sk.sivak.eldritchhorror.core.commandqueue.*;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.question.*;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheExplorerInitListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.*;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Runs the report's acknowledgement -> before-event -> skill selection path. */
public class SkillImprovementResolutionTest {
    private CommandQueueImpl queue;
    private ServiceImpl service;
    private InvestigatorServiceImpl investigatorService;
    private EventQueueImpl events;
    private GameController controller;
    private InvestigatorWrite investigator;
    private InvestigatorId active;
    private final List<Throwable> errors = new ArrayList<>();

    @After public void cleanup() {
        CommandQueueImpl.nullifyInstance();
        ServicePlatform.nullifyInstance();
        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.nullifyInstance();
    }

    @Before public void setup() {
        cleanup();
        active = InvestigatorId.THE_SAILOR;
        queue = CommandQueueImpl.get();
        queue.setThrowableConsumer(errors::add);
        CommandQueueFacadeImpl facade = new CommandQueueFacadeImpl();
        events = new EventQueueImpl();
        ActionProvider actions = mock(ActionProvider.class);
        when(actions.getInitValueAction(any())).thenAnswer(c -> new InitValueActionImpl<>(c.getArgument(0)));
        when(actions.getBeforeEventAction(any())).thenAnswer(c -> {
            BeforeEventActionImpl<Object> action = new BeforeEventActionImpl<>();
            action.setBeforeAfterEvent(c.getArgument(0)); return action;
        });
        when(actions.getAfterEventAction(any())).thenAnswer(c -> {
            AfterEventActionImpl<Object> action = new AfterEventActionImpl<>();
            action.setBeforeAfterEvent(c.getArgument(0)); return action;
        });
        service = spy(new ServiceImpl());
        service.setActionProvider(actions); service.setCommandQueueFacade(facade);
        InvestigatorActionProvider investigatorActions = mock(InvestigatorActionProvider.class);
        when(investigatorActions.improveSkill()).thenAnswer(c -> new ImproveSkillAction());
        when(investigatorActions.changeActiveInvestigator(any())).thenAnswer(c -> new ChangeActiveInvestigatorAction(c.getArgument(0)));
        when(investigatorActions.findActiveInvestigator()).thenAnswer(c -> new FindActiveInvestigatorAction());
        when(investigatorActions.showActiveInvestigator(false)).thenAnswer(c -> new ShowActiveInvestigatorAction(false));
        investigatorService = new InvestigatorServiceImpl();
        investigatorService.setActionProvider(actions); investigatorService.setCommandQueueFacade(facade);
        investigatorService.setInvestigatorActionProvider(investigatorActions);
        GameActionProvider gameActions = mock(GameActionProvider.class);
        when(gameActions.ask()).thenAnswer(c -> new AskAction<>());
        GameServiceImpl game = new GameServiceImpl();
        game.setActionProvider(actions); game.setCommandQueueFacade(facade); game.setGameActionProvider(gameActions);
        investigator = mock(InvestigatorWrite.class);
        InvestigatorsWrite investigators = mock(InvestigatorsWrite.class);
        when(investigators.getActiveInvestigator()).thenReturn(investigator);
        when(investigators.getInvestigator(any())).thenReturn(investigator);
        when(investigators.getActiveInvestigatorId()).thenAnswer(c -> active);
        doAnswer(c -> { active = c.getArgument(0); return null; })
                .when(investigators).changeActiveInvestigator(any(InvestigatorId.class));
        ModelWrite model = mock(ModelWrite.class);
        when(model.getBackgroundModel()).thenReturn(mock(BackgroundModelWrite.class));
        controller = mock(GameController.class);
        when(controller.showActiveInvestigator(false, false)).thenReturn(Completable.complete());
        ServicePlatform p = ServicePlatform.get();
        p.setService(service); p.setEventQueue(events); p.setInvestigators(investigators);
        p.setGameController(controller); p.setGameService(game); p.setModel(model);
        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform listeners =
                sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.get();
        listeners.setService(service); listeners.setEventQueue(events); listeners.setInvestigators(investigators);
        listeners.setInvestigatorService(investigatorService); listeners.setGameService(game);
    }

    @Test public void explorerAcknowledgementWaitsForSelectionAndRestoresPreviousInvestigator() {
        doNothing().when(service).gainAssetFromDeck(InvestigatorId.THE_EXPLORER, AssetId.MINERALOGY_RESEARCH);
        doNothing().when(service).registerNewInvestigatorAction(InvestigatorId.THE_EXPLORER);
        PublishSubject<Answer<Object, Object>> acknowledgement = PublishSubject.create();
        PublishSubject<Stat> selection = PublishSubject.create();
        when(controller.ask(any())).thenAnswer(c -> {
            Question<?> question = c.getArgument(0);
            assertEquals("The Explorer starts with one skill improved.", question.getTitle());
            assertEquals(InvestigatorId.THE_EXPLORER, active);
            return acknowledgement.toSingle();
        });
        when(controller.improveSkill(isNull())).thenAnswer(c -> {
            assertEquals(InvestigatorId.THE_EXPLORER, active);
            return selection.toSingle();
        });
        TheExplorerInitListener listener = new TheExplorerInitListener();
        listener.onNotify(InvestigatorId.THE_EXPLORER);
        tick();
        service.convertToNull();
        service.addEventCommand(in -> { events.fireAfterEvent(BeforeAfterEvent.SHOW_PHASE, null); });
        tick();
        verify(controller, never()).improveSkill(any());
        acknowledgement.onNext(new Answer<>()); acknowledgement.onCompleted();
        tick();
        assertEquals(InvestigatorId.THE_EXPLORER, active);
        verify(investigator, never()).improveStat(any());
        selection.onNext(Stat.OBSERVATION); selection.onCompleted();
        tick();
        verify(investigator).improveStat(Stat.OBSERVATION);
        assertEquals(InvestigatorId.THE_SAILOR, active);
        service.addEventCommand(in -> { events.fireAfterEvent(BeforeAfterEvent.SHOW_PHASE, null); });
        tick();
        listener.unregisterInvestigator(); listener.justRegisterListeners();
        service.addEventCommand(in -> { events.fireAfterEvent(BeforeAfterEvent.SHOW_PHASE, null); });
        tick();
        verify(controller, times(1)).ask(any());
        verify(controller, times(1)).improveSkill(isNull());
    }

    @Test public void choosingSkillDoesNotReusePreviousSkill() {
        improveAfter(Stat.WILL, null, Stat.LORE);
    }

    @Test public void choosingSkillDoesNotReusePreviousAnswer() {
        improveAfter(new Answer<>(), null, Stat.OBSERVATION);
    }

    @Test public void specifiedSkillOverridesPreviousAnswer() {
        improveAfter(new Answer<>(), Stat.STRENGTH, Stat.STRENGTH);
    }

    private void improveAfter(Object previous, Stat requested, Stat selected) {
        when(controller.improveSkill(any())).thenAnswer(c -> {
            assertEquals(requested, c.getArgument(0)); return Single.just(selected);
        });
        service.hold();
        service.convertTo(Object.class, () -> previous);
        investigatorService.improveSkill(requested);
        List<Object> outputs = new ArrayList<>();
        service.addEventCommand(in -> { outputs.add(in); });
        service.release();
        tick();
        verify(investigator).improveStat(selected);
        assertEquals(Collections.singletonList(selected), outputs);
    }

    private void tick() {
        for (int i = 0; i < 150 && errors.isEmpty(); i++) queue.tick();
        assertTrue(errors.toString(), errors.isEmpty());
    }
}
