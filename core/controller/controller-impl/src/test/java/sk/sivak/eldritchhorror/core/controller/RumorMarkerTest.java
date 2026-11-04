package sk.sivak.eldritchhorror.core.controller;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.model.RumorsRead;
import sk.sivak.eldritchhorror.core.view.GameView;
import static org.junit.Assert.*;

public class RumorMarkerTest {
    @Test public void markerOpensMatchingRumorAndIgnoresResolvedRumors() {
        List<RumorCardInfo> active = new ArrayList<>();
        for (String id : new String[]{"first", "second"}) active.add((RumorCardInfo) Proxy.newProxyInstance(
                RumorCardInfo.class.getClassLoader(), new Class[]{RumorCardInfo.class}, (p,m,a) -> id));
        GameControllerImpl controller = new GameControllerImpl();
        controller.setRumors((RumorsRead) Proxy.newProxyInstance(RumorsRead.class.getClassLoader(),
                new Class[]{RumorsRead.class}, (p,m,a) -> active));
        AtomicInteger opened = new AtomicInteger();
        controller.setView((GameView) Proxy.newProxyInstance(GameView.class.getClassLoader(),
                new Class[]{GameView.class}, (p,m,a) -> {
                    assertEquals("showRumorCard", m.getName());
                    assertSame(active.get(1), a[0]);
                    opened.incrementAndGet();
                    return Completable.complete();
                }));
        Completable click = controller.showRumorCard("second");
        assertEquals(0, opened.get());
        click.subscribe();
        assertEquals(1, opened.get());
        active.clear();
        click.subscribe();
        assertEquals(1, opened.get());
    }
}
