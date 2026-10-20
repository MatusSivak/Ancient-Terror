package sk.sivak.eldritchhorror.core.action.impl.test;
import org.junit.Test;
import org.junit.After;
import java.util.Collections;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.controller.TestController;
import java.lang.reflect.Proxy;
import java.util.function.BiFunction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.constants.condition.*;
import sk.sivak.eldritchhorror.core.constants.condition.blessed.BlessedCondition;
import sk.sivak.eldritchhorror.core.constants.condition.cursed.CursedCondition;
import sk.sivak.eldritchhorror.core.constants.condition.debt.DebtCondition;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import static org.junit.Assert.*;

public class InvestigatorDiceModifiersTest {
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void boardRollWithoutActiveInvestigatorKeepsUnmodifiedDie() {
        ServicePlatform platform = ServicePlatform.get();
        platform.setInvestigators(mock(InvestigatorsWrite.class, (name, args) -> null));
        DiceRoll die = new DiceRoll(); die.setDiceValue(4);
        InvestigatorDiceModifiers.applyAfterRoll(Collections.singletonList(die), null, 3).await();
        InvestigatorDiceModifiers.applyAfterRoll(Collections.singletonList(die), new BlessedCondition(), 3).await();
        assertEquals(4, die.getDiceValue());
    }

    @Test public void activeMaryStillGetsConditionFlavorBonus() {
        ServicePlatform platform = ServicePlatform.get();
        InvestigatorInfo info = mock(InvestigatorInfo.class, (name, args) -> InvestigatorId.THE_NUN);
        InvestigatorWrite investigator = mock(InvestigatorWrite.class, (name, args) ->
                name.equals("getInfo") ? info : false);
        platform.setInvestigators(mock(InvestigatorsWrite.class, (name, args) -> investigator));
        platform.setTestFlavor(mock(TestFlavorWrite.class, (name, args) ->
                name.equals("getFlavorType") ? TestFlavorType.CONDITION : new BlessedCondition()));
        DiceRoll die = new DiceRoll(); die.setDiceValue(4);
        platform.setTestController(mock(TestController.class, (name, args) -> {
            assertEquals("showNunDiceBonus", name);
            assertEquals(4, die.getDiceValue());
            return Completable.complete();
        }));
        InvestigatorDiceModifiers.applyAfterRoll(Collections.singletonList(die), null, 5).await();
        assertEquals(5, die.getDiceValue());
        assertEquals(DiceRoll.Score.GOOD, die.getScore());
    }

    private <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (proxy, method, args) -> handler.apply(method.getName(), args)));
    }
    @Test public void maryAddsOneToEachBoonAndBaneDieWithoutExceedingSix() {
        for(ConditionInfo condition:new ConditionInfo[]{new BlessedCondition(),new CursedCondition()})
            for(int value=1;value<=6;value++)
                assertEquals(Math.min(6,value+1),InvestigatorDiceModifiers.adjust(value,InvestigatorId.THE_NUN,false,condition));
    }
    @Test public void unrelatedRollsAndOtherInvestigatorsAreUnaffected() {
        assertEquals(2,InvestigatorDiceModifiers.adjust(2,InvestigatorId.THE_NUN,false,null));
        assertEquals(2,InvestigatorDiceModifiers.adjust(2,InvestigatorId.THE_NUN,false,new DebtCondition()));
        assertEquals(2,InvestigatorDiceModifiers.adjust(2,InvestigatorId.THE_PRIEST,false,new BlessedCondition()));
        assertEquals(2,InvestigatorDiceModifiers.adjust(2,InvestigatorId.THE_NUN,true,new BlessedCondition()));
    }
}

