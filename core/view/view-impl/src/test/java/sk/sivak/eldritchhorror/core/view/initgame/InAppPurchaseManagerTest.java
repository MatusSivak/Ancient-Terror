package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.pay.*;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Rule;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import java.lang.reflect.Proxy;
import java.util.*;
import static org.junit.Assert.*;

public class InAppPurchaseManagerTest {
    @Rule public final org.junit.rules.Timeout timeout = org.junit.rules.Timeout.seconds(10);

    private final Map<String, Object> values = new HashMap<>();
    private final FakeStore store = new FakeStore();
    private Application oldApp;
    private Net oldNet;
    private PurchaseManager oldStore;
    private int assetCount;
    private int artifactCount;
    private Application.ApplicationType applicationType = Application.ApplicationType.Android;

    @Before public void setup() {
        oldApp = Gdx.app;
        oldNet = Gdx.net;
        oldStore = GoogleServicesHolder.getPurchaseManager();
        Preferences preferences = (Preferences) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Preferences.class}, (proxy, method, args) -> {
            if (method.getName().equals("getBoolean")) return values.getOrDefault(args[0], args.length > 1 ? args[1] : false);
            if (method.getName().equals("putBoolean")) { values.put((String) args[0], args[1]); return proxy; }
            if (method.getName().equals("flush")) return null;
            throw new UnsupportedOperationException(method.getName());
        });
        Gdx.app = (Application) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Application.class}, (proxy, method, args) -> {
            if (method.getName().equals("getType")) return applicationType;
            if (method.getName().equals("getPreferences")) return preferences;
            if (method.getName().equals("postRunnable")) ((Runnable) args[0]).run();
            return null;
        });
        Gdx.net = (Net) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Net.class}, (proxy, method, args) -> null);
        GoogleServicesHolder.setPurchaseManager(store);
        GoogleServicesHolder.setOverwriteUnlockedAssetsFunction(ids -> assetCount = ids.size());
        GoogleServicesHolder.setOverwriteUnlockedArtifactsFunction(ids -> artifactCount = ids.size());
    }

    @After public void cleanup() {
        Gdx.app = oldApp;
        Gdx.net = oldNet;
        if (oldStore != null) GoogleServicesHolder.setPurchaseManager(oldStore);
        GoogleServicesHolder.setOverwriteUnlockedAssetsFunction(ids -> {});
        GoogleServicesHolder.setOverwriteUnlockedArtifactsFunction(ids -> {});
    }

    @Test public void desktopRestoreWithoutPurchaseCompletesWithoutUnlocking() {
        GoogleServicesHolder.setPurchaseManager(
                new sk.sivak.eldritchhorror.core.constants.tracker.DummyPurchaseManager());
        assertFalse(new InAppPurchaseManager().isProductPurchased("full_game").toBlocking().value());
        assertTrue(values.isEmpty());
    }

    @Test public void desktopPurchaseUnlocksAndPersistsFullGame() {
        applicationType = Application.ApplicationType.Desktop;
        GoogleServicesHolder.setPurchaseManager(
                new sk.sivak.eldritchhorror.core.constants.tracker.DummyPurchaseManager());
        assertTrue(new InAppPurchaseManager().purchaseProduct("full_game").toBlocking().value());
        assertUnlocked();
        GoogleServicesHolder.setPurchaseManager(
                new sk.sivak.eldritchhorror.core.constants.tracker.DummyPurchaseManager());
        assertTrue(new InAppPurchaseManager().isProductPurchased("full_game").toBlocking().value());
        assertUnlocked();
    }

    @Test public void desktopFullGamePurchaseBypassesStoreAndNetwork() {
        applicationType = Application.ApplicationType.Desktop;
        store.fail = true;
        Gdx.net = (Net) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Net.class},
                (proxy, method, args) -> { throw new AssertionError("Desktop purchase must not use the network"); });
        assertTrue(new InAppPurchaseManager().purchaseProduct("full_game").toBlocking().value());
        assertNull(store.requested);
        assertUnlocked();
    }

    @Test public void purchaseBeforeSelectionRefreshesStaleRoster() {
        GoogleServicesHolder.setPurchaseManager(
                new sk.sivak.eldritchhorror.core.constants.tracker.DummyPurchaseManager());
        List<InvestigatorInfo> stale = Collections.emptyList();
        new InAppPurchaseManager().purchaseProduct("full_game").toBlocking().value();
        List<InvestigatorInfo> refreshed = bonusRoster();
        assertSame(refreshed, InitGameViewImpl.refreshPurchasedInvestigators(stale, () -> refreshed)
                .toBlocking().value());
    }

    @Test public void restoreBeforeSelectionRefreshesStaleRoster() {
        store.restored = new Transaction[]{transaction("full_game")};
        List<InvestigatorInfo> refreshed = bonusRoster();
        assertSame(refreshed, InitGameViewImpl.refreshPurchasedInvestigators(
                Collections.emptyList(), () -> refreshed).toBlocking().value());
        assertUnlocked();
    }

    @Test public void freeSelectionKeepsOriginalRoster() {
        List<InvestigatorInfo> original = Collections.emptyList();
        assertSame(original, InitGameViewImpl.refreshPurchasedInvestigators(original, () -> {
            fail("Free selection must not unlock investigators");
            return bonusRoster();
        }).toBlocking().value());
    }

    private List<InvestigatorInfo> bonusRoster() {
        List<InvestigatorInfo> roster = new ArrayList<>();
        for (InvestigatorId id : new InvestigatorId[]{InvestigatorId.THE_HANDYMAN,
                InvestigatorId.THE_BOOTLEGGER, InvestigatorId.THE_VIOLINIST, InvestigatorId.THE_WAITRESS}) {
            roster.add((InvestigatorInfo) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class[]{InvestigatorInfo.class}, (proxy, method, args) -> id));
        }
        return roster;
    }

    @Test public void purchaseUnlocksEveryFeature() {
        assertTrue(new InAppPurchaseManager().purchaseProduct("full_game").toBlocking().value());
        assertEquals("full_game", store.requested);
        assertUnlocked();
    }

    @Test public void restoreFullGameUnlocksLegacyFeatureChecks() {
        store.restored = new Transaction[]{transaction("full_game")};
        assertTrue(new InAppPurchaseManager().isProductPurchased("investigators_1").toBlocking().value());
        assertUnlocked();
    }

    @Test public void cancellationDoesNotUnlock() {
        store.cancel = true;
        assertFalse(new InAppPurchaseManager().purchaseProduct("full_game").toBlocking().value());
        assertTrue(values.isEmpty());
    }

    @Test public void failureCanBeRetried() {
        store.fail = true;
        List<Throwable> errors = new ArrayList<>();
        new InAppPurchaseManager().purchaseProduct("full_game").subscribe(result -> fail("Unexpected result"), errors::add);
        assertEquals(1, errors.size());
        assertTrue(values.isEmpty());
        store.fail = false;
        assertTrue(new InAppPurchaseManager().purchaseProduct("full_game").toBlocking().value());
        assertUnlocked();
    }

    @Test public void unrelatedTransactionCannotGrantFullGame() {
        store.unrelated = true;
        List<Boolean> results = new ArrayList<>();
        new InAppPurchaseManager().purchaseProduct("full_game").subscribe(results::add);
        assertTrue(values.isEmpty());
        assertTrue(results.isEmpty());
        store.observer.handlePurchaseCanceled();
        assertEquals(Collections.singletonList(false), results);
    }

    @Test public void reversedRestoreDoesNotUnlock() {
        Transaction refunded = transaction("full_game");
        refunded.setReversalTime(new Date());
        store.restored = new Transaction[]{refunded};
        assertFalse(new InAppPurchaseManager().isProductPurchased("full_game").toBlocking().value());
        assertTrue(values.isEmpty());
    }

    private void assertUnlocked() {
        for (String key : new String[]{"full_game", "no_ads", "investigators_1", "cthulhu", "shub_niggurath", "yog_sothoth"}) assertEquals(key, true, values.get(key));
        assertEquals(sk.sivak.eldritchhorror.core.constants.asset.AssetId.values().length, assetCount);
        assertEquals(sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId.values().length, artifactCount);
    }

    private static Transaction transaction(String id) {
        Transaction t = new Transaction();
        t.setIdentifier(id);
        t.setPurchaseTime(new Date());
        return t;
    }

    private static class FakeStore implements PurchaseManager {
        PurchaseObserver observer;
        Transaction[] restored = new Transaction[0];
        boolean cancel, fail, unrelated;
        String requested;
        public String storeName() { return "test"; }
        public void install(PurchaseObserver observer, PurchaseManagerConfig config, boolean fetch) { this.observer = observer; }
        public boolean installed() { return true; }
        public void dispose() { }
        public Information getInformation(String id) { return null; }
        public void purchaseRestore() { observer.handleRestore(restored); }
        public void purchase(String id) {
            requested = id;
            if (cancel) observer.handlePurchaseCanceled();
            else if (fail) observer.handlePurchaseError(new IllegalStateException("offline"));
            else observer.handlePurchase(transaction(unrelated ? "cthulhu" : id));
        }
    }
}
