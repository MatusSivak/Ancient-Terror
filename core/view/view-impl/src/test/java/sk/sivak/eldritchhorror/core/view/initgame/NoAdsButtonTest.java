package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import org.junit.Test;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class NoAdsButtonTest {
    @Test public void freshInstallShowsWheelAndPurchaseHidesIt() {
        Application previous = Gdx.app;
        Files previousFiles = Gdx.files;
        Gdx.files = (Files) Proxy.newProxyInstance(Files.class.getClassLoader(),
                new Class[]{Files.class}, (proxy, method, args) -> null);
        Map<String, Boolean> owned = new HashMap<>();
        Preferences preferences = (Preferences) Proxy.newProxyInstance(Preferences.class.getClassLoader(),
                new Class[]{Preferences.class}, (proxy, method, args) -> owned.getOrDefault(args[0], false));
        Gdx.app = (Application) Proxy.newProxyInstance(Application.class.getClassLoader(),
                new Class[]{Application.class}, (proxy, method, args) -> preferences);
        try {
            BaseDrawable icon = new BaseDrawable();
            icon.setMinWidth(512);
            icon.setMinHeight(512);
            NoAdsButton wheel = new NoAdsButton(icon);
            wheel.validate();
            assertTrue(wheel.isVisible());
            assertEquals(Touchable.enabled, wheel.getTouchable());
            assertEquals(75f, wheel.getPrefWidth(), 0.01f);
            assertEquals(75f, wheel.getPrefHeight(), 0.01f);
            owned.put(InAppPurchaseManager.FULL_GAME, true);
            wheel.act(0);
            assertFalse(wheel.isVisible());
            assertEquals(Touchable.disabled, wheel.getTouchable());
            owned.clear();
            wheel.act(0);
            assertTrue(wheel.isVisible());
            owned.put("no_ads", true);
            wheel.act(0);
            assertFalse(wheel.isVisible());
        } finally {
            Gdx.app = previous;
            Gdx.files = previousFiles;
        }
    }
}
