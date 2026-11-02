package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.asset.*;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorsRead;
import sk.sivak.eldritchhorror.core.model.TestFlavorRead;
import sk.sivak.eldritchhorror.core.service.Service;

import static org.junit.Assert.*;

public class TestAssetDefaultsTest {
    private EventQueueImpl events;
    private TestData input;
    private TestFlavorType flavor = TestFlavorType.COMBAT;
    private final List<Runnable> pending = new ArrayList<>();

    @Before @SuppressWarnings("unchecked") public void setup() {
        ServicePlatform.nullifyInstance();
        ServicePlatform platform = ServicePlatform.get();
        events = new EventQueueImpl();
        platform.setEventQueue(events);
        platform.setTestFlavor((TestFlavorRead) Proxy.newProxyInstance(TestFlavorRead.class.getClassLoader(),
                new Class[]{TestFlavorRead.class}, (proxy, method, args) -> flavor));
        InvestigatorRead investigator = (InvestigatorRead) Proxy.newProxyInstance(InvestigatorRead.class.getClassLoader(),
                new Class[]{InvestigatorRead.class}, (proxy, method, args) -> false);
        platform.setInvestigators((InvestigatorsRead) Proxy.newProxyInstance(InvestigatorsRead.class.getClassLoader(),
                new Class[]{InvestigatorsRead.class}, (proxy, method, args) ->
                        method.getName().equals("getInvestigator") ? investigator : InvestigatorId.THE_SPY));
        platform.setService((Service) Proxy.newProxyInstance(Service.class.getClassLoader(),
                new Class[]{Service.class}, (proxy, method, args) -> {
                    assertEquals("Default selection must not use or consume cards", "addEventCommand", method.getName());
                    pending.add(() -> ((java8.features.function.Consumer<TestData>) args[0]).accept(input));
                    return null;
                }));
    }

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void personalAssistantStartsSelectedOnEveryRelevantTest() {
        new PersonalAssistantListener().registerWithOwner(InvestigatorId.THE_SPY, new PersonalAssistantAsset());
        for (int i = 0; i < 2; i++) {
            List<UsableAsset> assets = register(Stat.INFLUENCE);
            assertEquals(1, assets.size());
            assertTrue(assets.get(0).isUpByDefault());
        }
        assertTrue(register(Stat.LORE).isEmpty());
    }

    @Test public void consumableAndOncePerRoundBonusesRemainUnselected() {
        new KeroseneListener().registerWithOwner(InvestigatorId.THE_SPY, new KeroseneAsset());
        new LanternListener().registerWithOwner(InvestigatorId.THE_SPY, new LanternAsset());
        new Dot45ColtRevolverListener().registerWithOwner(InvestigatorId.THE_SPY, new Dot45ColtRevolverAsset());
        new CarbineRifleListener().registerWithOwner(InvestigatorId.THE_SPY, new CarbineRifleAsset());
        new RitualDaggerListener().registerWithOwner(InvestigatorId.THE_SPY, new RitualDaggerAsset());
        List<UsableAsset> assets = register(Stat.STRENGTH);
        assertEquals(5, assets.size());
        for (UsableAsset asset : assets) assertFalse(asset.getCardInfo().getName(), asset.isUpByDefault());
    }

    @Test public void ritualDaggerSpellBonusAlsoRemainsUnselected() {
        flavor = TestFlavorType.SPELL;
        new RitualDaggerListener().registerWithOwner(InvestigatorId.THE_SPY, new RitualDaggerAsset());
        List<UsableAsset> assets = register(Stat.LORE);
        assertEquals(1, assets.size());
        assertFalse(assets.get(0).isUpByDefault());
    }

    @Test public void exhaustedLanternIsNotOffered() {
        LanternListener listener = new LanternListener();
        listener.registerWithOwner(InvestigatorId.THE_SPY, new LanternAsset());
        listener.disable();
        assertTrue(register(Stat.INFLUENCE).isEmpty());
    }

    @Test public void reusableWeaponWithOnUseBookkeepingStillStartsSelected() {
        new BlunderbussListener().registerWithOwner(InvestigatorId.THE_SPY, new BlunderbussAsset());
        List<UsableAsset> assets = register(Stat.STRENGTH);
        assertEquals(1, assets.size());
        assertTrue(assets.get(0).isUpByDefault());
    }

    private List<UsableAsset> register(Stat stat) {
        input = new TestData();
        input.setStat(stat);
        events.fireDirectEvent(DirectEvent.REGISTER_USABLE_ASSETS, input);
        while (!pending.isEmpty()) pending.remove(0).run();
        return input.getUsableAssets();
    }
}
