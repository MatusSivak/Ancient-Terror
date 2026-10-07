package sk.sivak.eldritchhorror.core.action.impl.test;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.condition.*;
import sk.sivak.eldritchhorror.core.constants.condition.blessed.BlessedCondition;
import sk.sivak.eldritchhorror.core.constants.condition.cursed.CursedCondition;
import sk.sivak.eldritchhorror.core.constants.condition.debt.DebtCondition;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import static org.junit.Assert.*;

public class InvestigatorDiceModifiersTest {
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

