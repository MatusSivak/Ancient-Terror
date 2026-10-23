package sk.sivak.eldritchhorror.core.service;

import java.util.*;
import java8.features.function.Consumer;
import java8.features.function.Supplier;
import org.junit.*;
import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.*;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.HideCombatTableAction;
import sk.sivak.eldritchhorror.core.action.provider.*;
import sk.sivak.eldritchhorror.core.commandqueue.*;
import sk.sivak.eldritchhorror.core.constants.monster.CrazedMobMonster;
import sk.sivak.eldritchhorror.core.constants.monster.FirebugMonster;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.controller.TestController;
import sk.sivak.eldritchhorror.core.eventlistener.monster.AbstractMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.CrazedMobMonsterListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.FirebugMonsterListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;
import sk.sivak.eldritchhorror.core.service.util.CommandNameResolver;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/** Passing the horror test defeats Firebug / Crazed Mob before the damage test. */
public class FirebugCombatDefeatTest {
    @After public void cleanup() {
        CommandQueueImpl.nullifyInstance();
        ServicePlatform.nullifyInstance();
        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.nullifyInstance();
    }

    @Test public void firebugIsDefeatedAfterPassedHorrorTest() {
        assertEquals(Arrays.asList("fade", "background", "defeated", "end of combat", "done"),
                runCombat(new FirebugMonster(), new FirebugMonsterListener(), true));
    }

    @Test public void crazedMobIsDefeatedAfterPassedHorrorTest() {
        assertEquals(Arrays.asList("fade", "background", "defeated", "end of combat", "done"),
                runCombat(new CrazedMobMonster(), new CrazedMobMonsterListener(), true));
    }

    @Test public void firebugFailedHorrorTestContinuesToDamageTest() {
        assertEquals(Arrays.asList("damage test", "after damage", "fade", "end of combat", "done"),
                runCombat(new FirebugMonster(), new FirebugMonsterListener(), false));
    }

    @SuppressWarnings("unchecked")
    private List<String> runCombat(MonsterInfo monster, AbstractMonsterListener listener, boolean horrorPassed) {
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
        when(testActions.hideCombatTable(any())).thenAnswer(call -> new HideCombatTableAction(call.getArgument(0)));
        tests.setTestActionProvider(testActions);
        EventQueueImpl eventQueue = new EventQueueImpl();
        ServicePlatform.get().setEventQueue(eventQueue);
        TestController controller = mock(TestController.class);
        when(controller.hideCombatTable()).thenAnswer(call -> {
            events.add("fade"); return Completable.complete();
        });
        ServicePlatform.get().setTestController(controller);

        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform p =
                sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.get();
        p.setEventQueue(eventQueue);
        p.setTestService(tests);
        Service service = mock(Service.class);
        doAnswer(call -> { tests.hold(); return null; }).when(service).hold();
        doAnswer(call -> { tests.release(); return null; }).when(service).release();
        doAnswer(call -> { tests.skipAfterEvent(call.getArgument(0), call.getArgument(1)); return null; })
                .when(service).skipAfterEvent(any(), any());
        doAnswer(call -> { tests.skipBeforeEvent(call.getArgument(0), call.getArgument(1)); return null; })
                .when(service).skipBeforeEvent(any(), any());
        doAnswer(call -> { tests.startInsertingAfterCommand((BeforeAfterEvent) call.getArgument(0)); return null; })
                .when(service).startInsertingAfterCommand(any(BeforeAfterEvent.class));
        doAnswer(call -> { tests.endInsertingAtCommand(); return null; }).when(service).endInsertingAtCommand();
        doAnswer(call -> { tests.convertTo(call.getArgument(0), call.getArgument(1)); return null; })
                .when(service).convertTo(any(), any(Supplier.class));
        doAnswer(call -> { tests.addEventCommand((Consumer<Object>) call.getArgument(0)); return null; })
                .when(service).addEventCommand(any(Consumer.class));
        doAnswer(call -> {
            tests.addEventCommand(in -> { events.add("background"); }); return null;
        }).when(service).hideBackground();
        p.setService(service);
        MonsterService monsters = mock(MonsterService.class);
        doAnswer(call -> {
            tests.addEventCommand(in -> { events.add("defeated"); }); return null;
        }).when(monsters).defeatMonster(any(), anyBoolean(), anyBoolean());
        p.setMonsterService(monsters);
        listener.justRegisterListeners(monster);

        CombatData combat = new CombatData(); combat.setMonsterInfo(monster);
        TestData result = new TestData();
        result.setDiceRolls(Collections.emptyList()); result.setScoreBonus(horrorPassed ? 1 : 0);
        combat.setHorrorTestResult(result);

        tests.hold();
        tests.addEventCommand(in -> { }); // Start the queue before dispatching a nested event.
        // Mirrors AbstractDirectEventAction: listeners are notified inside a hold.
        tests.addEventCommand(in -> {
            tests.hold();
            eventQueue.fireDirectEvent(DirectEvent.BEFORE_DAMAGE_TEST, combat);
            tests.release();
        });
        tests.execute(named("DAMAGE_TEST", () -> events.add("damage test")));
        tests.execute(named(CommandNameResolver.createAfterEventName(BeforeAfterEvent.AFTER_DAMAGE_CHECK),
                () -> events.add("after damage")));
        tests.hideCombatTable(combat);
        tests.execute(named(CommandNameResolver.createBeforeEventName(BeforeAfterEvent.END_OF_COMBAT_EVENT),
                () -> events.add("end of combat")));
        tests.addEventCommand(in -> { events.add("done"); });
        tests.release();
        for (int i = 0; i < 80; i++) queue.tick();
        assertTrue(errors.toString(), errors.isEmpty());
        listener.unregister();
        return events;
    }

    private static ActionCommand<Object, Object> named(String name, Runnable effect) {
        return new ActionCommand<>(new Action<Object, Object>() {
            private Object input;

            @Override public Single<Object> execute() {
                return Single.create(sub -> { effect.run(); sub.onSuccess(input); });
            }

            @Override public void setInput(Object input) { this.input = input; }
        }, name);
    }
}
