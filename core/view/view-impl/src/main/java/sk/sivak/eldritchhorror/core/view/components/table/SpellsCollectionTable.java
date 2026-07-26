package sk.sivak.eldritchhorror.core.view.components.table;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class SpellsCollectionTable extends CardsCollectionTable<SpellId, SpellInfo, SpellTrait> {

    public SpellsCollectionTable() {
        setEnumValuesSupplier(SpellId::values);
        setFilterOutFunction(spellIdEnum -> false);
        setEnumToCardInfoFunction(spellIdEnum -> {
            try {
                return (SpellInfo) Class.forName(spellIdEnum.getSpellClassName()).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        });
        setCardsComparator((a1, a2) -> {
            SpellTrait spellTraitA = a1.getTraits().iterator().next();
            SpellTrait spellTraitB = a2.getTraits().iterator().next();
            if (spellTraitA != spellTraitB) {
                return spellTraitB.asString().compareTo(spellTraitA.asString());
            }
            if (getUnlocked().contains(a1.getId()) && !getUnlocked().contains(a2.getId())) {
                return -1;
            }
            if (!getUnlocked().contains(a1.getId()) && getUnlocked().contains(a2.getId())) {
                return 1;
            }
            return 0;
        });
        setPreviousInitValue(null);
        setValueProvider(spellInfo -> spellInfo.getTraits().iterator().next());
        setLabelNameFunction(SpellTrait::asString);
        setCardInfoToIdFunction(SpellInfo::getId);
    }
}
