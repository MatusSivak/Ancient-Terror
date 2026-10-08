package sk.sivak.eldritchhorror.core.action.impl.test;
import org.junit.Test;
import org.junit.After;
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

    @Test public void boardRollWithoutActiveInvestigatorReturnsUnmodifiedDie() {
        ServicePlatform platform = ServicePlatform.get();
        int[] rolls = {0};
        platform.setModel(mock(ModelWrite.class, (name, args) -> { rolls[0]++; return 4; }));
        platform.setInvestigators(mock(InvestigatorsWrite.class, (name, args) -> null));
        assertEquals(4, InvestigatorDiceModifiers.roll(null));
        assertEquals(4, InvestigatorDiceModifiers.roll(new BlessedCondition()));
        assertEquals(2, rolls[0]);
    }

    @Test public void activeMaryStillGetsConditionFlavorBonus() {
        ServicePlatform platform = ServicePlatform.get();
        platform.setModel(mock(ModelWrite.class, (name, args) -> 4));
        InvestigatorInfo info = mock(InvestigatorInfo.class, (name, args) -> InvestigatorId.THE_NUN);
        InvestigatorWrite investigator = mock(InvestigatorWrite.class, (name, args) ->
                name.equals("getInfo") ? info : false);
        platform.setInvestigators(mock(InvestigatorsWrite.class, (name, args) -> investigator));
        platform.setTestFlavor(mock(TestFlavorWrite.class, (name, args) ->
                name.equals("getFlavorType") ? TestFlavorType.CONDITION : new BlessedCondition()));
        assertEquals(5, InvestigatorDiceModifiers.roll(null));
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

