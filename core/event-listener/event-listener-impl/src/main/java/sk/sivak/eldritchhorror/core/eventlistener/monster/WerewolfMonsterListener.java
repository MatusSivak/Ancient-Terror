package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.CONFIRM_TEST;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.SPAWN_GATES;

public class WerewolfMonsterListener extends AbstractMonsterListener {

    private BeforeConfirmTest beforeConfirmTest;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);

        beforeConfirmTest = new BeforeConfirmTest();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeConfirmTest, CONFIRM_TEST);
    }

    private class BeforeConfirmTest extends EventListenerImpl<TestData> {

        @Override
        public void onNotify(TestData eventData) {
            if (ServicePlatform.get().getTestFlavor().getFlavorType() != TestFlavorType.COMBAT) {
                return;
            }
            CombatData combatData = ServicePlatform.get().getTestFlavor().getFlavorData();
            if (combatData.getMonsterInfo() != monsterInfo) {
                return;
            }
            if (eventData.getStat() != Stat.STRENGTH) {
                // I don't care about rules, Physical resistance is only relevant for Damage check
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            eventData.removeAllNonMagicalUsableAssets();
            ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(beforeConfirmTest);
    }
}
