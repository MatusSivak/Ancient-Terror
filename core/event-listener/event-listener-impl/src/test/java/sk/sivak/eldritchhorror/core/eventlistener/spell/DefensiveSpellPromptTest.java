package sk.sivak.eldritchhorror.core.eventlistener.spell;

import org.junit.After;
import org.junit.Test;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.fleshward.FleshWardSpell;
import sk.sivak.eldritchhorror.core.constants.spell.instillbravery.InstillBraverySpell;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.*;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import static org.junit.Assert.*;

/** Exercise real event registration and both answers without displaying an owner sheet. */
public class DefensiveSpellPromptTest {
    private final InvestigatorId owner = InvestigatorId.THE_SAILOR;
    private final InvestigatorId target = InvestigatorId.THE_MUSICIAN;
    private InvestigatorId active;
    private final List<Runnable> pending = new ArrayList<>();
    private final List<String> effects = new ArrayList<>();
    private Question<Boolean> prompt;
    private LoseTokenData restored;

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void fleshWardDeclineKeepsTargetAndLoss() { check(false, false, false); }
    @Test public void braveryDeclineKeepsTargetAndLoss() { check(true, false, false); }
    @Test public void fleshWardAcceptUsesCasterAndRestoresTarget() { check(false, true, false); }
    @Test public void braveryAcceptUsesCasterAndRestoresTarget() { check(true, true, false); }
    @Test public void fleshWardCanProtectItsOwner() { check(false, true, true); }
    @Test public void braveryCanProtectItsOwner() { check(true, true, true); }

    @SuppressWarnings("unchecked")
    private void check(boolean sanity, boolean accept, boolean self) {
        ServicePlatform.nullifyInstance();
        ServicePlatform platform = ServicePlatform.get();
        active = self ? owner : target;
        InvestigatorId protectedId = active;
        EventQueueImpl events = new EventQueueImpl();
        platform.setEventQueue(events);
        LoseTokenData loss = new LoseTokenData(3, sanity ? TokenType.SANITY : TokenType.HEALTH);
        platform.setService(mock(Service.class, (name, a) -> {
            if (name.equals("addEventCommand")) pending.add(() -> ((java8.features.function.Consumer<Object>)a[0]).accept(loss));
            if (name.equals("convertTo")) pending.add(() -> restored = (LoseTokenData)((java8.features.function.Supplier<?>)a[1]).get());
            if (name.equals("disableCard")) pending.add(() -> effects.add("disable"));
            return null;
        }));
        platform.setInvestigators(mock(InvestigatorsRead.class, (name, a) -> {
            if (name.equals("getActiveInvestigatorId")) return active;
            if (name.equals("getInvestigator")) return mock(InvestigatorRead.class, (n, args) -> false);
            throw new AssertionError(name);
        }));
        platform.setInvestigatorService(mock(InvestigatorService.class, (name, a) -> {
            if (name.equals("changeActiveInvestigator")) pending.add(() -> { active = (InvestigatorId)a[0]; effects.add("change"); });
            else if (name.equals("showActiveInvestigator")) {
                pending.add(() -> {
                    assertEquals(Boolean.TRUE.equals(a[0]) ? owner : protectedId, active);
                    if (Boolean.TRUE.equals(a[0])) effects.add("focusCaster");
                });
            } else throw new AssertionError("Unexpected investigator UI: " + name);
            return null;
        }));
        platform.setGameService(mock(GameService.class, (name, a) -> {
            assertEquals("ask", name);
            prompt = (Question<Boolean>)a[0];
            assertEquals(protectedId, active);
            assertTrue(effects.isEmpty());
            Answer<Boolean, Object> answer = new Answer<>(); answer.setResponseData(accept);
            return Single.just(answer);
        }));
        platform.setTestService(mock(TestService.class, (name, a) -> {
            assertEquals("test", name); assertEquals(Stat.LORE, a[0]);
            return Single.create(sub -> pending.add(() -> {
                assertEquals(owner, active); effects.add("test"); sub.onSuccess(new TestData());
            }));
        }));
        SpellInfo spell = sanity ? new InstillBraverySpell() : new FleshWardSpell();
        AbstractSpellListener<?> listener = sanity ? new InstillBraveryListener() {
            @Override public AbstractSpellBackListener findSpellBackListener() { return back(spell, protectedId); }
        } : new FleshWardListener() {
            @Override public AbstractSpellBackListener findSpellBackListener() { return back(spell, protectedId); }
        };
        listener.register(spell, owner);
        events.fireBeforeEvent(sanity ? BeforeAfterEvent.LOSE_SANITY : BeforeAfterEvent.LOSE_HEALTH, loss);
        while (!pending.isEmpty()) pending.remove(0).run();
        assertEquals("investigator/" + owner.name() + ".png", prompt.getPortraitTextureName());
        assertTrue(prompt.getTitle().contains(spell.getName()));
        assertTrue(prompt.getTextureNameList().isEmpty());
        assertTrue(prompt.isHideTestUi());
        assertEquals(2, prompt.getOptions().size());
        assertEquals(Boolean.TRUE, prompt.getOptions().get(0).getValue());
        assertEquals(Boolean.FALSE, prompt.getOptions().get(1).getValue());
        assertSame(loss, restored);
        assertEquals(protectedId, active);
        assertEquals(accept ? 1 : 3, loss.getAmount());
        assertEquals(accept ? (self ? 4 : 6) : 0, effects.size());
        if (accept) assertTrue(effects.indexOf("focusCaster") < effects.indexOf("test"));
    }

    private AbstractSpellBackListener back(SpellInfo spell, InvestigatorId protectedId) {
        return new AbstractSpellBackListener(spell) {
            @Override public void executeWhole() {
                assertEquals(owner, active);
                assertEquals(protectedId, getData("targetInvestigatorId"));
                effects.add("back"); runMainSpellAction();
            }
            @Override protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> effects) { }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return (T)Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (p, method, args) -> handler.apply(method.getName(), args));
    }
}
