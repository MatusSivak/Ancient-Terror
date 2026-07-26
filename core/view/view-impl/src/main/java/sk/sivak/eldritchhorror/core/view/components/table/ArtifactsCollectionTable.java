package sk.sivak.eldritchhorror.core.view.components.table;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class ArtifactsCollectionTable extends CardsCollectionTable<ArtifactId, ArtifactInfo, Object> {

    public ArtifactsCollectionTable() {
        setEnumValuesSupplier(ArtifactId::values);
        setFilterOutFunction(artifactIdEnum -> false);
        setEnumToCardInfoFunction(artifactIdEnum -> {
            try {
                return (ArtifactInfo) Class.forName(artifactIdEnum.getArtifactClassName()).newInstance();
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
        setValueProvider(artifactInfo -> "X");
        setLabelNameFunction(x -> "asd");
        setCardInfoToIdFunction(ArtifactInfo::getId);
    }
}
