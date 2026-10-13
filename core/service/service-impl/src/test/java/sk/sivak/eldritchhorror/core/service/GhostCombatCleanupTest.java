package sk.sivak.eldritchhorror.core.service;

import java.util.*;
import org.junit.*;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.action.*;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.HideCombatTableAction;
import sk.sivak.eldritchhorror.core.action.provider.*;
import sk.sivak.eldritchhorror.core.commandqueue.*;
import sk.sivak.eldritchhorror.core.constants.monster.GhostMonster;
import sk.sivak.eldritchhorror.core.controller.TestController;
import sk.sivak.eldritchhorror.core.eventlistener.monster.GhostMonsterListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

public class GhostCombatCleanupTest {
    @After public void cleanup() {
        CommandQueueImpl.nullifyInstance();
        ServicePlatform.nullifyInstance();
        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.nullifyInstance();
    }

    @Test public void ghostWaitsForCombatFadeBeforeBackgroundAndDamageAndCleansUpOnlyOnce() {
        cleanup();
        CommandQueueImpl queue = CommandQueueImpl.get();
        List<Throwable> errors = new ArrayList<>();
        List<String> events = new ArrayList<>();
        queue.setThrowableConsumer(errors::add);
        TestServiceImpl tests = new TestServiceImpl();
        tests.setCommandQueueFacade(new CommandQueueFacadeImpl());
        ActionProvider actions = mock(ActionProvider.class);
        when(actions.getBeforeEventAction(any())).thenAnswer(call -> {
            BeforeEventActionImpl<Object> action = new BeforeEventActionImpl<>();
            action.setBeforeAfterEvent(call.getArgument(0)); return action;
        });
        when(actions.getAfterEventAction(any())).thenAnswer(call -> {
            AfterEventActionImpl<Object> action = new AfterEventActionImpl<>();
            action.setBeforeAfterEvent(call.getArgument(0)); return action;
        });
        tests.setActionProvider(actions);
        TestActionProvider testActions = mock(TestActionProvider.class);
        when(testActions.hideCombatTable()).thenAnswer(call -> new HideCombatTableAction());
        tests.setTestActionProvider(testActions);
        EventQueueImpl eventQueue = new EventQueueImpl();
        ServicePlatform.get().setEventQueue(eventQueue);
        PublishSubject<Void> fading = PublishSubject.create();
        TestController controller = mock(TestController.class);
        when(controller.hideCombatTable()).thenAnswer(call -> {
            events.add("fade"); return fading.toCompletable();
        });
        ServicePlatform.get().setTestController(controller);

        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform p =
                sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.get();
        p.setEventQueue(eventQueue);
        p.setTestService(tests);
        Service service = mock(Service.class);
        doAnswer(call -> { tests.hold(); return null; }).when(service).hold();
        doAnswer(call -> { tests.release(); return null; }).when(service).release();
        doAnswer(call -> {
            tests.addEventCommand(in -> { events.add("background"); }); return null;
        }).when(service).hideBackground();
        p.setService(service);
        MonsterService monsters = mock(MonsterService.class);
        doAnswer(call -> {
            tests.addEventCommand(in -> { events.add("damage"); }); return null;
        }).when(monsters).dealDamageToMonsterInCombat(any(), anyInt(), anyBoolean());
        p.setMonsterService(monsters);
        GhostMonster ghost = new GhostMonster();
        GhostMonsterListener listener = new GhostMonsterListener();
        listener.register(ghost);
        CombatData combat = new CombatData(); combat.setMonsterInfo(ghost);
        TestData result = new TestData();
        result.setDiceRolls(Collections.emptyList()); result.setScoreBonus(2); result.setMinScoreToBeSuccessful(1);
        combat.setHorrorTestResult(result);
        tests.hold();
        tests.addEventCommand(in -> { }); // Start the queue before dispatching a nested event.
        tests.addEventCommand(in -> { eventQueue.fireAfterEvent(BeforeAfterEvent.AFTER_HORROR_CHECK, combat); });
        tests.hideCombatTable(combat); // The usual end-of-combat cleanup follows the special ability.
        tests.addEventCommand(in -> { events.add("done"); });
        tests.release();
        for (int i = 0; i < 30; i++) queue.tick();
        assertTrue(errors.toString(), errors.isEmpty());
        assertEquals(Collections.singletonList("fade"), events);
        fading.onCompleted();
        for (int i = 0; i < 50; i++) queue.tick();
        assertTrue(errors.toString(), errors.isEmpty());
        assertEquals(Arrays.asList("fade", "background", "damage", "done"), events);
        verify(controller, times(1)).hideCombatTable();
        verify(monsters).dealDamageToMonsterInCombat(ghost, 2, true);
    }
}
