package sk.sivak.eldritchhorror.core.constants.spell;

public class SpellEffectImpl implements SpellEffect {
    private String flavorText;
    private int spellEffectId;

    public SpellEffectImpl() {
    }

    public SpellEffectImpl(String flavorText) {
        this.flavorText = flavorText;
    }

    @Override
    public String getFlavorText() {
        return flavorText;
    }

    @Override
    public int getSpellEffectId() {
        return spellEffectId;
    }

    public void setSpellEffectId(int spellEffectId) {
        this.spellEffectId = spellEffectId;
    }

    public void setFlavorText(String flavorText) {
        this.flavorText = flavorText;
    }
}
