package sk.sivak.eldritchhorror.core.constants.spell;

import sk.sivak.eldritchhorror.core.constants.Interval;

import java.util.HashMap;
import java.util.Map;

public class AbstractSpellBack implements SpellBack {
    private Map<Interval, SpellEffect> intervalSpellEffectMap = new HashMap<>();

    private int spellEffectId = 0;
    @Override
    public SpellEffect getSpellEffect(int rolledValue) {
        for (Map.Entry<Interval, SpellEffect> entry : intervalSpellEffectMap.entrySet()) {
            if (!entry.getKey().test(rolledValue)) {
                continue;
            }
            return entry.getValue();
        }
        throw new IllegalArgumentException("Spell effect not found for rolledValue = " + rolledValue);
    }

    protected void addSpellEffect(String intervalExpression, SpellEffectImpl spellEffect) {
        intervalSpellEffectMap.put(Interval.build(intervalExpression), spellEffect);
        spellEffect.setSpellEffectId(spellEffectId++);
    }

    @Override
    public int getSpellBackId() {
        return Integer.valueOf(getClass().getSimpleName().split("SpellBack")[1]);
    }

}
