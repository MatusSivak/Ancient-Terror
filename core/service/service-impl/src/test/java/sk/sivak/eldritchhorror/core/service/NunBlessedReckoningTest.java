package sk.sivak.eldritchhorror.core.service;

import java.util.*;
import org.junit.*;
import rx.Completable;
import rx.Single;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.action.*;
import sk.sivak.eldritchhorror.core.action.impl.action.investigator.ChangeActiveInvestigatorAction;
import sk.sivak.eldritchhorror.core.action.impl.test.RollAndShowDieAction;
import sk.sivak.eldritchhorror.core.action.provider.*;
import sk.sivak.eldritchhorror.core.commandqueue.*;
import sk.sivak.eldritchhorror.core.constants.condition.blessed.BlessedCondition;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.controller.TestController;
import sk.sivak.eldritchhorror.core.eventlistener.condition.BlessedListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Uses the real reckoning listener, command queue, owner switch and die action. */
public class NunBlessedReckoningTest {
    private InvestigatorId active;

    @After public void cleanup() {
        CommandQueueImpl.nullifyInstance();
        ServicePlatform.nullifyInstance();
        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.nullifyInstance();
    }

    @Test public void nunRawTwoDisplaysThreeAndKeepsBlessed() {
        reckon(InvestigatorId.THE_NUN, 2, 3, false);
    }

    @Test public void nunRawOneDisplaysTwoAndDiscardsBlessed() {
        reckon(InvestigatorId.THE_NUN, 1, 2, true);
    }

    @Test public void nunHigherRollsKeepBlessedAndCapAtSix() {
        for (int raw = 3; raw <= 6; raw++) {
            reckon(InvestigatorId.THE_NUN, raw, Math.min(6, raw + 1), false);
        }
    }

    @Test public void otherInvestigatorRawTwoStillDiscardsBlessed() {
        reckon(InvestigatorId.THE_PRIEST, 2, 2, true);
    }

    private void reckon(InvestigatorId owner, int raw, int displayed, boolean discard) {
        cleanup();
        active = InvestigatorId.THE_SPY;
        List<Throwable> errors = new ArrayList<>();
        CommandQueueImpl queue = CommandQueueImpl.get();
        queue.setThrowableConsumer(errors::add);
        CommandQueueFacadeImpl facade = new CommandQueueFacadeImpl();
        EventQueueImpl events = new EventQueueImpl();
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
        ServiceImpl service = spy(new ServiceImpl());
        service.setActionProvider(actions); service.setCommandQueueFacade(facade);
        ServicePlatform p = ServicePlatform.get();
        p.setService(service); p.setEventQueue(events);
        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform listeners =
                sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.get();
        listeners.setService(service); listeners.setEventQueue(events);
        listeners.setDoomOmenService(mock(DoomOmenService.class));
        InvestigatorWrite investigator = mock(InvestigatorWrite.class, RETURNS_DEEP_STUBS);
        when(investigator.getInfo().getInvestigatorId()).thenReturn(owner);
        InvestigatorsWrite investigators = mock(InvestigatorsWrite.class);
        when(investigators.getInvestigator(owner)).thenReturn(investigator);
        when(investigators.isOnBoard(owner)).thenReturn(true);
        when(investigators.getActiveInvestigatorId()).thenAnswer(c -> active);
        when(investigators.getActiveInvestigator()).thenAnswer(c -> {
            assertEquals("Owner must be active before rolling", owner, active);
            return investigator;
        });
        doAnswer(c -> { active = c.getArgument(0); return null; })
                .when(investigators).changeActiveInvestigator(any());
        p.setInvestigators(investigators); listeners.setInvestigators(investigators);
        InvestigatorService switcher = mock(InvestigatorService.class);
        doAnswer(c -> {
            facade.execute(new ActionCommand<>(new ChangeActiveInvestigatorAction(c.getArgument(0)), "OWNER"));
            return null;
        }).when(switcher).changeActiveInvestigator(any());
        listeners.setInvestigatorService(switcher);
        ModelWrite model = mock(ModelWrite.class);
        when(model.rollDie()).thenReturn(raw); p.setModel(model);

        TestActionProvider rolls = mock(TestActionProvider.class);
        when(rolls.rollAndShowDie()).thenAnswer(c -> new RollAndShowDieAction());
        TestServiceImpl tests = new TestServiceImpl();
        tests.setActionProvider(actions); tests.setCommandQueueFacade(facade);
        tests.setTestActionProvider(rolls); listeners.setTestService(tests);
        TestController controller = mock(TestController.class);
        PublishSubject<Void> rawAnimation = PublishSubject.create();
        PublishSubject<Void> bonusAnimation = PublishSubject.create();
        when(controller.showRolledDices(anyList())).thenAnswer(c -> {
            List<DiceRoll> dice = c.getArgument(0);
            assertEquals(1, dice.size());
            assertEquals(raw, dice.get(0).getDiceValue());
            assertEquals(raw <= 2 ? DiceRoll.Score.BAD : DiceRoll.Score.GOOD, dice.get(0).getScore());
            return rawAnimation.toCompletable();
        });
        when(controller.showNunDiceBonus(anyList())).thenAnswer(c -> {
            List<DiceRoll> dice = c.getArgument(0);
            assertEquals(displayed, dice.get(0).getDiceValue());
            assertEquals(discard ? DiceRoll.Score.BAD : DiceRoll.Score.GOOD, dice.get(0).getScore());
            return bonusAnimation.toCompletable();
        });
        when(controller.confirmRollResult(anyInt())).thenReturn(Completable.complete());
        p.setTestController(controller);
        GameService game = mock(GameService.class);
        when(game.showCard(any(ShowCardRequest.class))).thenAnswer(c ->
                Single.create(sub -> service.addEventCommand(in -> { sub.onSuccess(ShowCardResponse.OK); })));
        listeners.setGameService(game);
        BlessedCondition blessed = new BlessedCondition();
        doNothing().when(service).discardConditionFromInvestigator(owner, blessed);
        BlessedListener listener = new BlessedListener();
        listener.register(blessed, owner);
        service.addEventCommand(in -> { events.fireDirectEvent(DirectEvent.RECKONING_CARDS_3, ReckoningFireType.CHARGE); });
        service.addEventCommand(in -> { events.fireDirectEvent(DirectEvent.RECKONING_CARDS_3, ReckoningFireType.FIRE); });
        for (int i = 0; i < 250 && errors.isEmpty(); i++) queue.tick();
        assertTrue(errors.toString(), errors.isEmpty());
        verify(controller, never()).showNunDiceBonus(anyList());
        verify(controller, never()).confirmRollResult(anyInt());
        rawAnimation.onCompleted();
        for (int i = 0; i < 250 && errors.isEmpty(); i++) queue.tick();
        if (owner == InvestigatorId.THE_NUN) {
            verify(controller).showNunDiceBonus(anyList());
            verify(controller, never()).confirmRollResult(anyInt());
            verify(service, never()).discardConditionFromInvestigator(owner, blessed);
        } else {
            verify(controller, never()).showNunDiceBonus(anyList());
        }
        bonusAnimation.onCompleted();
        for (int i = 0; i < 250 && errors.isEmpty(); i++) queue.tick();
        assertTrue(errors.toString(), errors.isEmpty());
        verify(model).rollDie();
        verify(controller).showRolledDices(anyList());
        verify(controller).confirmRollResult(discard ? 0 : 2);
        verify(service, times(discard ? 1 : 0)).discardConditionFromInvestigator(owner, blessed);
        assertEquals(owner, active);
        listener.unregister();
    }
}
