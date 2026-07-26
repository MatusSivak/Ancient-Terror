package sk.sivak.eldritchhorror.core.view.components.table;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;

public class ConditionsCollectionTable extends CardsCollectionTable<ConditionId, ConditionInfo, Object> {

    public ConditionsCollectionTable() {
        setEnumValuesSupplier(ConditionId::values);
        setFilterOutFunction(conditionIdEnum -> false);
        setEnumToCardInfoFunction(conditionIdEnum -> {
            try {
                return (ConditionInfo) Class.forName(conditionIdEnum.getConditionClassName()).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        });
        setCardsComparator((a1, a2) -> {
            if (getUnlocked().contains(a1.getId()) && !getUnlocked().contains(a2.getId())) {
                return -1;
            }
            if (!getUnlocked().contains(a1.getId()) && getUnlocked().contains(a2.getId())) {
                return 1;
            }
            return 0;
        });
        setPreviousInitValue("X");
        setValueProvider(conditionInfo -> "X");
        setLabelNameFunction(x -> "asd");
        setCardInfoToIdFunction(ConditionInfo::getId);
    }
}
