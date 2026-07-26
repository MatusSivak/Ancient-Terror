package sk.sivak.eldritchhorror.core.eventlistener.provider;

import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.spell.AbstractSpellListener;

/**
 * @author msivak
 */
public interface SpellListenerProvider {

    AbstractSpellListener<? extends SpellInfo> getSpellListener(SpellInfo spellInfo);

    void unregister(SpellInfo spellInfo);

    void init();
}
