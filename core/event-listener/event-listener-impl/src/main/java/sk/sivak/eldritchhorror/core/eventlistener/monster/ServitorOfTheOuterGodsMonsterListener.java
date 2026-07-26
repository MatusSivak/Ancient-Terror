package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.CONFIRM_TEST;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.SPAWN_GATES;

public class ServitorOfTheOuterGodsMonsterListener extends AbstractMonsterListener {

    private AfterSpawnGateListener afterSpawnGateListener;
    private BeforeConfirmTest beforeConfirmTest;
    private OmenChangedListener omenChangedListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);

        afterSpawnGateListener = new AfterSpawnGateListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterSpawnGateListener, SPAWN_GATES);

        beforeConfirmTest = new BeforeConfirmTest();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeConfirmTest, CONFIRM_TEST);

        omenChangedListener = new OmenChangedListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(omenChangedListener, DirectEvent.OMEN_CHANGED);

        recalculateDamage();
    }

    private class AfterSpawnGateListener extends EventListenerImpl<GateInfo> {

        @Override
        public void onNotify(GateInfo eventData) {
            recalculateDamage();
        }

        @Override
        public Class<GateInfo> getDataClass() {
            return GateInfo.class;
        }
    }

    private class OmenChangedListener extends EventListenerImpl<OmenInfo> {

        @Override
        public void onNotify(OmenInfo object) {
            recalculateDamage();
        }

        @Override
        public Class<OmenInfo> getDataClass() {
            return OmenInfo.class;
        }
    }

    private void recalculateDamage() {
        OmenColor omenColor = ServicePlatform.get().getOmenTrack().getCurrentOmen().getOmenColor();
        List<LocationId> spawnedGates = ServicePlatform.get().getGateStackRead().getSpawnedGates(omenColor.toGateColor());
        ((AbstractMonsterInfo) monsterInfo).setDamage(spawnedGates.size());
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
        ServicePlatform.get().getEventQueue().unregisterListener(afterSpawnGateListener);
        ServicePlatform.get().getEventQueue().unregisterListener(omenChangedListener);
    }
}
