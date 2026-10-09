package sk.sivak.eldritchhorror.core.controller;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.function.BiFunction;
import org.junit.Test;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.constants.location.*;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.view.GameView;
import static org.junit.Assert.*;

public class TicketChoiceTest {
    @Test public void buyingAlwaysWaitsForChoiceAndPreservesConnectionRestrictions() {
        for (boolean train : new boolean[]{false, true}) for (boolean ship : new boolean[]{false, true}) {
            if (!train && !ship) continue;
            GameControllerImpl controller = new GameControllerImpl();
            InvestigatorRead investigator = mock(InvestigatorRead.class, (n, a) -> LocationId.ROME);
            controller.setInvestigators(mock(InvestigatorsRead.class, (n, a) -> investigator));
            LocationInfo location = mock(LocationInfo.class, (n, a) ->
                    (n.equals("getTrainConnections") ? train : ship) ? Collections.singletonList(null) : Collections.emptyList());
            controller.setLocationMap(mock(LocationMapRead.class, (n, a) -> location));
            PublishSubject<PathType> choice = PublishSubject.create();
            controller.setView(mock(GameView.class, (n, a) -> {
                assertEquals("selectTravelTicket", n);
                assertEquals(train, a[0]); assertEquals(ship, a[1]);
                return choice.toSingle();
            }));
            PathType[] result = {null};
            controller.selectTravelTicket().subscribe(ticket -> result[0] = ticket);
            assertNull("No ticket until the player chooses", result[0]);
            PathType selected = train ? PathType.TRAIN : PathType.SHIP;
            choice.onNext(selected); choice.onCompleted();
            assertEquals(selected, result[0]);
        }
    }
    private <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (p, m, a) -> handler.apply(m.getName(), a)));
    }
}
