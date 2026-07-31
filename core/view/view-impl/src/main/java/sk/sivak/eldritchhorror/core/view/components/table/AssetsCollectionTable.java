package sk.sivak.eldritchhorror.core.view.components.table;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;

import java.util.List;

import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class AssetsCollectionTable extends CardsCollectionTable<AssetId, AssetInfo, Integer> {

    public AssetsCollectionTable() {
        setEnumValuesSupplier(AssetId::values);
        setFilterOutFunction(assetIdEnum -> assetIdEnum.equals(AssetId.BANK_LOAN));
        setEnumToCardInfoFunction(assetIdEnum -> {
            try {
                return (AssetInfo) Class.forName(assetIdEnum.getAssetClassName()).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        });
        setCardsComparator((a1, a2) -> {
            if (a1.getCost() != a2.getCost()) {
                return a1.getCost() - a2.getCost();
            }
            if (getUnlocked().contains(a1.getId()) && !getUnlocked().contains(a2.getId())) {
                return -1;
            }
            if (!getUnlocked().contains(a1.getId()) && getUnlocked().contains(a2.getId())) {
                return 1;
            }
            return 0;
        });
        setPreviousInitValue(-1);
        setValueProvider(AssetInfo::getCost);
        setLabelNameFunction(cost -> get("cardsCollection.costLabel", cost));
        setCardInfoToIdFunction(AssetInfo::getId);
    }
}
