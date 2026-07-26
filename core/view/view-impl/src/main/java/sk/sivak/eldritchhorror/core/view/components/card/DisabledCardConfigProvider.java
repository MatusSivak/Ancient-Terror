package sk.sivak.eldritchhorror.core.view.components.card;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.card.CardId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;

final class DisabledCardConfigProvider {

    private DisabledCardConfigProvider() {

    }

    static Config getConfig(CardId cardId) {
        if (cardId == AssetId.TOME_OF_SECRETS) {
            return new Config(170,0.35f);
        }
        if (cardId == SpellId.VOICE_OF_RA) {
            return new Config(115,0.6f);
        }
        if (cardId == ArtifactId.THE_SILVER_KEY) {
            return new Config(170,0.35f);
        }
        if (cardId == ArtifactId.KHOPESH_OF_THE_ABYSS) {
            return new Config(45,0.6f);
        }
        if (cardId == ArtifactId.GROTESQUE_STATUE) {
            return new Config(85,0.35f);
        }
        if (cardId == ArtifactId.REQUIEM_PER_SHUGGAY) {
            return new Config(155,0.45f);
        }
        if (cardId == ArtifactId.LIGHTNING_GUN) {
            return new Config(70,0.35f);
        }
        if (cardId == ArtifactId.DRAGON_IDOL) {
            return new Config(180,0.40f);
        }
        if (cardId == ArtifactId.SERPENT_CROWN) {
            return new Config(140,0.45f);
        }
        if (cardId == ArtifactId.ZANTHU_TABLETS) {
            return new Config(155,0.45f);
        }
        if (cardId == ArtifactId.SATCHEL_OF_THE_VOID) {
            return new Config(170,0.45f);
        }
        if (cardId == AssetId.MONSTER_HUNTER) {
            return new Config(70,0.35f);
        }
        if (cardId == ArtifactId.PNAKOTIC_MANUSCRIPTS) {
            return new Config(70,0.45f);
        }
        return new Config(0,1f);
    }

    static class Config {

        private Config(float offsetY, float scaleMultiplier) {
            this.offsetY = offsetY;
            this.scaleMultiplier = scaleMultiplier;
        }

        private float offsetY;
        private float scaleMultiplier;

        float getOffsetY() {
            return offsetY;
        }

        float getScaleMultiplier() {
            return scaleMultiplier;
        }
    }
}
