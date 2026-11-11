package sk.sivak.eldritchhorror.core.eventlistener.ancientone.azathoth;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java8.features.function.Consumer;
import org.junit.After;
import org.junit.Test;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.CultistMonster;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth.AbstractAzathothResearchEncounter;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.service.EncounterService;
import sk.sivak.eldritchhorror.core.service.GameService;
import sk.sivak.eldritchhorror.core.service.MonsterService;
import sk.sivak.eldritchhorror.core.service.Service;
import sk.sivak.eldritchhorror.core.service.TestService;
import static org.junit.Assert.*;

/** Plays every Azathoth research encounter through both outcomes and checks the narration. */
public class AzathothResearchNarrationTest {
    private static final String PACKAGE = "sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth.";
    private static final Set<String> NO_TEST = new HashSet<>(Arrays.asList("22.city", "4.sea", "20.sea"));

    private final List<Runnable> pending = new ArrayList<>();
    private List<Runnable> inserted;
    private final List<String> flavors = new ArrayList<>(), infos = new ArrayList<>();
    private SingleSubscriber<? super Integer> button;
    private SingleSubscriber<? super TestData> test;
    private SingleSubscriber<? super CombatData> combat;
    private SingleSubscriber<? super Answer> question;
    private String[] buttons;
    private boolean waiting;
    private int papers;

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void everyResearchCardHasIntroAndBothOutcomesInText() throws Exception {
        Properties text = loadText();
        for (LocationType type : LocationType.values()) {
            for (int page = 1; page <= 24; page++) {
                String key = page + "." + type.name().toLowerCase(Locale.ROOT);
                assertFalse(key, text.getProperty(key + ".flavor", "").trim().isEmpty());
                if (NO_TEST.contains(key)) {
                    assertNotNull(key, text.getProperty(key + ".info.1"));
                    continue;
                }
                for (String outcome : new String[]{".pass", ".fail"}) {
                    assertFalse(key + outcome, text.getProperty(key + outcome + ".flavor", "").trim().isEmpty());
                    assertTrue(key + outcome + " rules", text.getProperty(key + outcome + ".info") != null
                            || text.getProperty(key + outcome + ".info.1") != null);
                }
            }
        }
        for (String name : text.stringPropertyNames()) {
            String value = text.getProperty(name);
            assertFalse(name, value.contains("\\n") || value.contains("\n"));
            assertEquals(name + " markup", count(value, "[#"), count(value, "[]"));
        }
    }

    @Test public void everyResearchCardNarratesBothOutcomes() throws Exception {
        Properties text = loadText();
        for (LocationType type : LocationType.values()) {
            for (int page = 1; page <= 24; page++) {
                String key = page + "." + type.name().toLowerCase(Locale.ROOT);
                for (boolean passed : new boolean[]{true, false}) {
                    play(type, page, passed);
                    String label = key + (passed ? " pass" : " fail");
                    for (String typed : flavors) assertFalse(label + ": " + typed, typed.contains("null"));
                    for (String typed : infos) assertFalse(label + ": " + typed, typed.contains("null"));
                    assertEquals(label + " paper finished", 0, papers);
                    assertFalse(label + " still waiting", waiting);
                    if (!NO_TEST.contains(key)) {
                        String expected = text.getProperty(key + (passed ? ".pass" : ".fail") + ".flavor").trim();
                        assertEquals(label, expected, flavors.get(flavors.size() - 1).trim());
                    }
                    cleanup();
                }
            }
        }
    }

    private void play(LocationType type, int page, boolean passed) throws Exception {
        reset();
        String typeName = type.name().charAt(0) + type.name().substring(1).toLowerCase(Locale.ROOT);
        AbstractAzathothResearchEncounter encounter = (AbstractAzathothResearchEncounter)
                Class.forName(PACKAGE + "Research" + typeName + page + "Encounter").getConstructor().newInstance();
        encounter.setLocationId(LocationId.values()[0]);
        encounter.executeWhole();
        drain();
        for (int guard = 0; waiting; guard++) {
            assertTrue("Encounter loop", guard < 20);
            if (button != null) {
                boolean noYes = buttons.length == 2 && buttons[1].contains("Yes");
                click(noYes && passed ? 1 : 0);
            } else if (test != null) {
                SingleSubscriber<? super TestData> current = test; test = null;
                resume(() -> current.onSuccess(testResult(passed)));
            } else if (combat != null) {
                SingleSubscriber<? super CombatData> current = combat; combat = null;
                CultistMonster monster = new CultistMonster(); monster.setAlive(true);
                CombatData result = new CombatData(); result.setMonsterInfo(monster);
                result.setHorrorTestResult(testResult(passed)); result.setDamageTestResult(testResult(passed));
                resume(() -> current.onSuccess(result));
            } else if (question != null) {
                SingleSubscriber<? super Answer> current = question; question = null;
                Answer<Boolean, Object> answer = new Answer<>(); answer.setResponseData(false);
                resume(() -> current.onSuccess(answer));
            } else {
                fail("Waiting without a prompt");
            }
        }
    }

    private void reset() throws Exception {
        ServicePlatform.nullifyInstance();
        pending.clear(); flavors.clear(); infos.clear();
        button = null; test = null; combat = null; question = null; waiting = false; papers = 0;
        ServicePlatform platform = ServicePlatform.get();
        for (Method setter : ServicePlatform.class.getMethods()) {
            Class<?>[] params = setter.getParameterTypes();
            if (setter.getName().startsWith("set") && params.length == 1 && params[0].isInterface()) {
                setter.invoke(platform, deepMock(params[0]));
            }
        }
        platform.setEventQueue(new EventQueueImpl());
        platform.setService(mock(Service.class, (method, args) -> {
            if (method.getName().equals("addEventCommand")) queue(() -> ((Consumer<Object>) args[0]).accept(null));
            return defaultValue(method.getGenericReturnType());
        }));
        platform.setEncounterService(mock(EncounterService.class, (method, args) -> {
            switch (method.getName()) {
                case "showTypewriterPaper": queue(() -> { if ((Boolean) args[0]) papers++; }); break;
                case "finishTypewriterPaper": queue(() -> papers--); break;
                case "typeFlavor": queue(() -> flavors.add(String.valueOf(args[0]))); break;
                case "typeInfo": queue(() -> infos.add(String.valueOf(args[0]))); break;
                case "displayButtons": return Single.<Integer>create(sub -> queue(() -> {
                    buttons = (String[]) args[0]; button = sub; waiting = true;
                }));
                default: break;
            }
            return defaultValue(method.getGenericReturnType());
        }));
        platform.setTestService(mock(TestService.class, (method, args) -> method.getName().equals("test")
                ? Single.<TestData>create(sub -> queue(() -> { test = sub; waiting = true; }))
                : defaultValue(method.getGenericReturnType())));
        platform.setMonsterService(mock(MonsterService.class, (method, args) -> method.getName().equals("ambush")
                ? Single.<CombatData>create(sub -> queue(() -> { combat = sub; waiting = true; }))
                : defaultValue(method.getGenericReturnType())));
        platform.setGameService(mock(GameService.class, (method, args) -> method.getName().equals("ask")
                ? Single.<Answer>create(sub -> queue(() -> { question = sub; waiting = true; }))
                : defaultValue(method.getGenericReturnType())));
    }

    private Object deepMock(Class<?> type) {
        return mock(type, (method, args) -> defaultValue(method.getGenericReturnType()));
    }

    private Object defaultValue(Type type) {
        Class<?> raw = type instanceof ParameterizedType
                ? (Class<?>) ((ParameterizedType) type).getRawType() : type instanceof Class ? (Class<?>) type : Object.class;
        if (raw == void.class || raw == Void.class) return null;
        if (raw == boolean.class || raw == Boolean.class) return false;
        if (raw == int.class || raw == Integer.class) return 0;
        if (raw == long.class || raw == Long.class) return 0L;
        if (raw == float.class || raw == Float.class) return 0f;
        if (raw == double.class || raw == Double.class) return 0d;
        if (raw == String.class) return "";
        if (List.class.isAssignableFrom(raw) || raw == Collection.class) return new ArrayList<>();
        if (Set.class.isAssignableFrom(raw)) return new HashSet<>();
        if (Map.class.isAssignableFrom(raw)) return new HashMap<>();
        if (raw == Completable.class) return Completable.complete();
        if (raw == Observable.class) return Observable.empty();
        if (raw == Single.class) {
            Type value = type instanceof ParameterizedType ? ((ParameterizedType) type).getActualTypeArguments()[0] : Object.class;
            return Single.just(defaultValue(value));
        }
        if (raw.isEnum()) return raw.getEnumConstants()[0];
        if (raw.isInterface()) return deepMock(raw);
        return instantiate(raw);
    }

    private Object instantiate(Class<?> type) {
        try {
            Object value = type.getConstructor().newInstance();
            for (Method method : type.getMethods()) {
                if (method.getName().equals("setHasEnough")) method.invoke(value, true);
                if (method.getName().equals("setPayAction")) method.invoke(value, (rx.functions.Action0) () -> { });
            }
            return value;
        } catch (ReflectiveOperationException | RuntimeException e) {
            return null;
        }
    }

    private interface Handler { Object handle(Method method, Object[] args); }

    @SuppressWarnings("unchecked")
    private <T> T mock(Class<T> type, Handler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, (proxy, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
                switch (method.getName()) {
                    case "equals": return proxy == args[0];
                    case "hashCode": return System.identityHashCode(proxy);
                    default: return type.getSimpleName() + " mock";
                }
            }
            return handler.handle(method, args == null ? new Object[0] : args);
        });
    }

    private TestData testResult(boolean passed) {
        TestData result = new TestData(); result.setDiceRolls(Collections.emptyList());
        result.setScoreBonus(passed ? 1 : 0); result.setMinScoreToBeSuccessful(1);
        return result;
    }

    private void click(int index) {
        SingleSubscriber<? super Integer> current = button; button = null;
        resume(() -> current.onSuccess(index));
    }

    private void queue(Runnable action) { (inserted == null ? pending : inserted).add(action); }

    private void resume(Runnable action) {
        waiting = false; inserted = new ArrayList<>(); action.run();
        pending.addAll(0, inserted); inserted = null; drain();
    }

    private void drain() {
        int limit = 1000;
        while (!waiting && !pending.isEmpty()) {
            if (--limit == 0) throw new AssertionError("Command loop");
            Runnable action = pending.remove(0); inserted = new ArrayList<>(); action.run();
            pending.addAll(0, inserted); inserted = null;
        }
    }

    private Properties loadText() throws Exception {
        Properties text = new Properties();
        try (Reader reader = new InputStreamReader(getClass().getClassLoader()
                .getResourceAsStream("encounter/research_AZATHOTH.properties"), StandardCharsets.UTF_8)) {
            text.load(reader);
        }
        return text;
    }

    private static int count(String value, String token) {
        int count = 0;
        for (int i = value.indexOf(token); i >= 0; i = value.indexOf(token, i + token.length())) count++;
        return count;
    }
}
