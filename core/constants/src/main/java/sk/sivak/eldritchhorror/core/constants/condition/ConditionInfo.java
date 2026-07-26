package sk.sivak.eldritchhorror.core.constants.condition;

import sk.sivak.eldritchhorror.core.constants.card.CardInfo;

import java.util.Set;

/**
 * @author msivak
 */
public interface ConditionInfo extends CardInfo {

    ConditionId getId();

    Set<ConditionTrait> getTraits();

    ConditionBack getConditionBack();
}
