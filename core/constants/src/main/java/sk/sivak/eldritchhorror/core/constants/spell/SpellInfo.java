package sk.sivak.eldritchhorror.core.constants.spell;

import sk.sivak.eldritchhorror.core.constants.card.CardInfo;

import java.util.Set;

/**
 * @author msivak
 */
public interface SpellInfo extends CardInfo {

    SpellId getId();

    Set<SpellTrait> getTraits();

    SpellBack getSpellBack();
}
