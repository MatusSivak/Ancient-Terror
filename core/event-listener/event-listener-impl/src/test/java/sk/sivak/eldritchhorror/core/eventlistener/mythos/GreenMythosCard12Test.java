package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import org.junit.*;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.question.*;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;
import static sk.sivak.eldritchhorror.core.constants.GameProperties.MAX_FOCUS_TOKENS;

public class GreenMythosCard12Test {
    private int focus;
    private int clues;
    private int choice;
    private int holds;
    private final List<Integer> offered = new ArrayList<>();
    private final List<String> effects = new ArrayList<>();

    @Before public void setup() {
        ServicePlatform.nullifyInstance();
        ServicePlatform platform = ServicePlatform.get();
        InvestigatorInfo info = mock(InvestigatorInfo.class, (name, args) -> InvestigatorId.THE_SPY);
        InvestigatorRead investigator = mock(InvestigatorRead.class, (name, args) -> {
            if (name.equals("getInfo")) return info;
            if (name.equals("getFocusTokens")) return focus;
            throw new AssertionError(name);
        });
        platform.setInvestigators(mock(InvestigatorsRead.class, (name, args) -> Collections.singletonList(investigator)));
        platform.setCluePoolRead(mock(CluePoolRead.class, (name, args) -> clues));
        platform.setSpellsDeck(mock(SpellsDeckRead.class, (name, args) -> true));
        platform.setInvestigatorService(mock(InvestigatorService.class, (name, args) -> null));
        platform.setService(mock(Service.class, (name, args) -> {
            if (name.equals("hold")) holds++;
            if (name.equals("release")) holds--;
            return null;
        }));
        platform.setTokenService(mock(TokenService.class, (name, args) -> { effects.add(name); return null; }));
        platform.setGameService(mock(GameService.class, (name, args) -> {
            if (name.equals("gainSpell")) { effects.add(name); return null; }
            assertEquals("ask", name);
            Question<Integer> question = (Question<Integer>) args[0];
            for (Question.Option<Integer> option : question.getOptions()) {
                assertNotEquals("Nothing", option.getName());
                assertNotEquals(Integer.valueOf(0), option.getValue());
                offered.add(option.getValue());
            }
            Answer<Integer, Object> answer = new Answer<>();
            answer.setResponseData(question.getOptions().get(choice).getValue());
            return Single.just(answer);
        }));
    }

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void noResourcesOffersOnlyFreeFocus() {
        new GreenMythosCard12().execute();
        assertEquals(Arrays.asList(1), offered);
        assertEquals(Arrays.asList("gainFocus"), effects);
        assertEquals(0, holds);
    }

    @Test public void cappedFocusStillOffersTheClueTrade() {
        focus = MAX_FOCUS_TOKENS;
        new GreenMythosCard12().execute();
        assertEquals(Arrays.asList(2), offered);
        assertEquals(Arrays.asList("loseFocus", "gainClueFromPool"), effects);
        assertEquals(0, holds);
    }

    @Test public void allThreeUsefulChoicesRemainAvailable() {
        focus = 1; clues = 1; choice = 2;
        new GreenMythosCard12().execute();
        assertEquals(Arrays.asList(1, 2, 3), offered);
        assertEquals(Arrays.asList("loseClue", "gainSpell"), effects);
        assertEquals(0, holds);
    }

    private <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (proxy, method, args) -> handler.apply(method.getName(), args)));
    }
}
