package sk.sivak.eldritchhorror.core.eventtype.data.card;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class GainCardData {
    private boolean confirmRequired = true;
    private boolean isArtifact;
    private AssetTrait assetTrait;
    private ConditionId conditionId;
    private ConditionTrait conditionTrait;
    private SpellId spellId;
    private ArtifactId artifactId;
    private SpellTrait spellTrait;
    private boolean randomSpell;
    private AssetInfo assetInfo;

    public GainCardData(AssetTrait assetTrait, boolean isArtifact) {
        this.assetTrait = assetTrait;
        this.isArtifact = isArtifact;
    }

    public GainCardData(ConditionId conditionId) {
        this.conditionId = conditionId;
    }

    public GainCardData(ArtifactId artifactId) {
        this.artifactId = artifactId;
        this.isArtifact = true;
    }

    public GainCardData(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public GainCardData(ConditionTrait conditionTrait) {
        this.conditionTrait = conditionTrait;
    }

    public GainCardData(SpellId spellId) {
        this.spellId = spellId;
    }

    public GainCardData(SpellTrait spellTrait) {
        this.spellTrait = spellTrait;
    }

    public GainCardData(boolean randomSpell) {
        this.randomSpell = randomSpell;
    }

    public ConditionTrait getConditionTrait() {
        return conditionTrait;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public ConditionId getConditionId() {
        return conditionId;
    }

    public SpellId getSpellId() {
        return spellId;
    }

    public SpellTrait getSpellTrait() {
        return spellTrait;
    }

    public boolean isRandomSpell() {
        return randomSpell;
    }

    public boolean isArtifact() {
        return isArtifact;
    }

    public AssetTrait getAssetTrait() {
        return assetTrait;
    }

    public void setNoCardToGain() {
        conditionId = null;
        conditionTrait = null;
        spellId = null;
        spellTrait = null;
        assetInfo = null;
        artifactId = null;
        isArtifact = false;
    }

    public void setConfirmRequired(boolean confirmRequired) {
        this.confirmRequired = confirmRequired;
    }

    public boolean isConfirmRequired() {
        return confirmRequired;
    }

    public ArtifactId getArtifactId() {
        return artifactId;
    }

    @Override
    public String toString() {
        return "GainCardData{" +
                "conditionId=" + conditionId +
                ", conditionTrait=" + conditionTrait +
                ", spellId=" + spellId +
                ", spellTrait=" + spellTrait +
                ", assetInfo=" + assetInfo +
                '}';
    }
}
