package sk.sivak.eldritchhorror.core.eventlistener.monster;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.*;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import static sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig.YigEffects.*;
public class YigCultistMonsterListener extends AbstractMonsterListener {
    private EventListenerImpl<CombatData> poisoned;
    public static void configure(MonsterInfo info, boolean awake, boolean resetHealth) {
        AbstractMonsterInfo m = (AbstractMonsterInfo) info;
        m.setHorrorTestType(Stat.WILL); m.setHorrorTestModifier(0); m.setHorror(awake ? 2 : 1);
        m.setDamageTestType(Stat.STRENGTH); m.setDamageTestModifier(-1); m.setDamage(awake ? 3 : 2);
        m.setToughness(awake ? 2 : 1); if (resetHealth) m.setCurrentHealth(m.getToughness());
        m.setReckoning(false); m.setReckoningText(null);
        m.setSpecialText("If the Strength test causes you to lose Health, gain Poisoned.");
    }
    @Override public void register(MonsterInfo info) { setup(info, true); }
    @Override public void justRegisterListeners(MonsterInfo info) {
        // Ambushes use this entry point too, but their fresh monsters are not on the board.
        setup(info, !p().getMonsterCup().getMonsters().contains(info));
    }
    private void setup(MonsterInfo info, boolean resetHealth) {
        monsterInfo = info; configure(info, p().getModel().getAncientOne().getAncientOneInfo().isAwaken(), resetHealth);
        poisoned = new EventListenerImpl<CombatData>() {
            public Class<CombatData> getDataClass() { return CombatData.class; }
            public void onNotify(CombatData data) {
                if (data.getMonsterInfo() != monsterInfo || data.getHealthLost() <= 0) return;
                sequence(() -> { p().getGameService().gainCondition(ConditionId.POISONED); p().getService().convertTo(CombatData.class, () -> data); });
            }
        };
        p().getEventQueue().addBeforeEventListener(poisoned, BeforeAfterEvent.DESTROY_MONSTER_HEALTH);
    }
    @Override public void unregister() { p().getEventQueue().unregisterListener(poisoned); }
}
