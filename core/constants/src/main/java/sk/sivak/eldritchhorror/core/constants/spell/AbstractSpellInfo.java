package sk.sivak.eldritchhorror.core.constants.spell;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractSpellInfo implements SpellInfo {

    protected Set<SpellTrait> traits = new LinkedHashSet<>();
    private SpellBack spellBack;
    private boolean disabled;

    public Set<SpellTrait> getTraits() {
        return traits;
    }

    @Override
    public boolean hasReckoning() {
        return false;
    }

    @Override
    public SpellBack getSpellBack() {
        return spellBack;
    }

    public void setSpellBack(SpellBack spellBack) {
        this.spellBack = spellBack;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    public void disable() {
        disabled = true;
    }

    public void enable() {
        disabled = false;
    }
}
