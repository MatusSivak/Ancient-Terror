package sk.sivak.eldritchhorror.core.controller;

import java.lang.reflect.Proxy;
import java.util.function.BiFunction;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.ancientone.*;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.view.GameView;
import static org.junit.Assert.*;

public class YigDoomDisplayTest {
    @Test public void awakenedNegativeDoomDisplaysTimeRemainingOnTheClock() {
        for (int doom : new int[]{-8,-5,-1,0}) {
            assertEquals(-doom, display(AncientOneId.YIG,true,doom));
        }
        assertEquals(10,display(AncientOneId.YIG,false,10));
        assertEquals(0,display(AncientOneId.CTHULHU,true,0));
    }
    private int display(AncientOneId id,boolean awake,int doom) {
        GameControllerImpl controller=new GameControllerImpl();
        AncientOneInfo info=mock(AncientOneInfo.class,(n,a)->n.equals("isAwaken")?awake:id);
        controller.setAncientOne(mock(AncientOneRead.class,(n,a)->info));
        controller.setDoomTrackRead(mock(DoomTrackRead.class,(n,a)->doom));
        int[] result={Integer.MIN_VALUE};
        controller.setView(mock(GameView.class,(n,a)->{assertEquals("updateDoom",n);result[0]=(int)a[0];return null;}));
        controller.updateDoom();return result[0];
    }
    private <T>T mock(Class<T> type,BiFunction<String,Object[],Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(),new Class[]{type},(p,m,a)->handler.apply(m.getName(),a)));
    }
}
