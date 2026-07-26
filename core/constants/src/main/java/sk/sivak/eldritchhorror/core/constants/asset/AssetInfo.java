package sk.sivak.eldritchhorror.core.constants.asset;

import sk.sivak.eldritchhorror.core.constants.card.CardInfo;

import java.util.Set;

/**
 * @author msivak
 */
public interface AssetInfo extends CardInfo {

    AssetId getId();

    Set<AssetTrait> getTraits();

    int getCost();
}
