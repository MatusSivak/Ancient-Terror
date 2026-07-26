package sk.sivak.eldritchhorror.core.action.impl.card;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;
import sk.sivak.eldritchhorror.core.model.ArtifactsDeckWrite;
import sk.sivak.eldritchhorror.core.model.AssetsDeckWrite;
import sk.sivak.eldritchhorror.core.model.ConditionsDeckWrite;
import sk.sivak.eldritchhorror.core.model.SpellsDeckWrite;

import java.util.Collections;

public class SelectCardToGainAction extends AbstractHookableAction<GainCardData, GainedCardData> {

    private static final Logger logger = LogManager.getLogger(SelectCardToGainAction.class);

    public SelectCardToGainAction() {
        super(BeforeAfterEvent.SELECT_CARD_TO_GAIN);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super GainedCardData> ss) {
        if (input.getConditionId() != null) {
            gainConditionById();
            return;
        }
        if (input.getArtifactId() != null) {
            gainArtifact(input.getArtifactId());
            return;
        }
        if (input.isArtifact() && input.getAssetTrait() == null) {
            gainRandomArtifact();
            return;
        }
        if (input.isArtifact() && input.getAssetTrait() != null) {
            gainArtifact(input.getAssetTrait());
            return;
        }
        if (input.getConditionTrait() != null) {
            gainConditionByTrait();
            return;
        }
        if (input.getAssetInfo() != null) {
            gainAsset();
            return;
        }
        if (input.getSpellId() != null) {
            gainSpellById();
            return;
        }
        if (input.getSpellTrait() != null) {
            gainSpellByTrait();
            return;
        }
        if (input.isRandomSpell()) {
            gainRandomSpell();
            return;
        }
        ss.onSuccess(new GainedCardData(null, null, input.isConfirmRequired()));
    }

    private void gainConditionById() {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        ConditionsDeckWrite conditionsDeck = ServicePlatform.get().getConditionsDeck();
        if (conditionsDeck.hasCondition(activeInvestigatorId, input.getConditionId())) {
            displayCannotGainCard(activeInvestigatorId + " cannot gain another " + input.getConditionId().asString() + " Condition");
        } else if (!conditionsDeck.canGetConditionId(activeInvestigatorId, input.getConditionId())){
            displayCannotGainCard("There is no " + input.getConditionId().asString() + " Condition in the deck.");
        } else {
            ConditionInfo conditionInfo = conditionsDeck.gainCondition(input.getConditionId(), activeInvestigatorId);
            ss.onSuccess(new GainedCardData(conditionInfo, GainedCardData.CardType.CONDITION, input.isConfirmRequired()));
        }
    }

    private void gainConditionByTrait() {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        ConditionsDeckWrite conditionsDeck = ServicePlatform.get().getConditionsDeck();
        if (conditionsDeck.canGetTrait(activeInvestigatorId, input.getConditionTrait())) {
            ConditionInfo conditionInfo = conditionsDeck.gainCondition(input.getConditionTrait(), activeInvestigatorId);
            ss.onSuccess(new GainedCardData(conditionInfo, GainedCardData.CardType.CONDITION, input.isConfirmRequired()));
        } else {
            displayCannotGainCard(activeInvestigatorId + " cannot gain another " + input.getConditionTrait().asString() + " Condition");
        }
    }

    private void gainSpellById() {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        SpellsDeckWrite spellsDeck = ServicePlatform.get().getSpellsDeck();
        if (!spellsDeck.hasSpell(activeInvestigatorId, input.getSpellId())) {
            SpellInfo spellInfo = spellsDeck.gainSpell(input.getSpellId(), activeInvestigatorId);
            ss.onSuccess(new GainedCardData(spellInfo, GainedCardData.CardType.SPELL, input.isConfirmRequired()));
        } else {
            displayCannotGainCard(activeInvestigatorId + " cannot gain another " + input.getSpellId().asString() + " Spell");
        }
    }

    private void gainSpellByTrait() {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        SpellsDeckWrite spellsDeck = ServicePlatform.get().getSpellsDeck();
        if (spellsDeck.canGetTrait(activeInvestigatorId, input.getSpellTrait())) {
            SpellInfo spellInfo = spellsDeck.gainSpell(input.getSpellTrait(), activeInvestigatorId);
            ss.onSuccess(new GainedCardData(spellInfo, GainedCardData.CardType.SPELL, input.isConfirmRequired()));
        } else {
            displayCannotGainCard(activeInvestigatorId + " cannot gain another " + input.getSpellTrait().asString() + " Spell");
        }
    }

    private void gainRandomSpell() {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        SpellsDeckWrite spellsDeck = ServicePlatform.get().getSpellsDeck();
        if (spellsDeck.canGetRandomSpell(activeInvestigatorId)) {
            SpellInfo spellInfo = spellsDeck.gainSpell(activeInvestigatorId);
            ss.onSuccess(new GainedCardData(spellInfo, GainedCardData.CardType.SPELL, input.isConfirmRequired()));
        } else {
            displayCannotGainCard(activeInvestigatorId + " cannot get another random Spell");
        }
    }

    private void gainRandomArtifact() {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        ArtifactsDeckWrite artifactsDeck = ServicePlatform.get().getArtifactsDeck();

        ArtifactInfo artifactInfo = artifactsDeck.gainRandomArtifact();
        artifactsDeck.addToInvestigator(activeInvestigatorId, artifactInfo);
        ss.onSuccess(new GainedCardData(artifactInfo, GainedCardData.CardType.ARTIFACT, input.isConfirmRequired()));
    }

    private void gainArtifact(AssetTrait assetTrait) {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        ArtifactsDeckWrite artifactsDeck = ServicePlatform.get().getArtifactsDeck();

        ArtifactInfo artifactInfo = artifactsDeck.findFirst(assetTrait);

        if (artifactsDeck.removeFromDeck(artifactInfo.getId()) != null) {
            artifactsDeck.addToInvestigator(activeInvestigatorId, artifactInfo);
            ss.onSuccess(new GainedCardData(artifactInfo, GainedCardData.CardType.ARTIFACT, input.isConfirmRequired()));
            return;
        }
        artifactsDeck.removeFromDiscardPile(artifactInfo);
        artifactsDeck.addToInvestigator(activeInvestigatorId, artifactInfo);
        ss.onSuccess(new GainedCardData(artifactInfo, GainedCardData.CardType.ARTIFACT, input.isConfirmRequired()));
    }

    private void gainArtifact(ArtifactId artifactId) {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        ArtifactsDeckWrite artifactsDeck = ServicePlatform.get().getArtifactsDeck();

        if (!artifactsDeck.isInDeckOrDiscardPile(artifactId)) {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle(artifactId.asString()+" cannot be gained again.");
            ServicePlatform.get().getGameService().ask(question).subscribe(x-> {
                ServicePlatform.get().getService().convertTo(GainedCardData.class,
                        () -> new GainedCardData(null, null, input.isConfirmRequired()));
            });
            ss.onSuccess(null);
            return;
        }
        ArtifactInfo artifactInfo = artifactsDeck.findFirst(artifactId);

        if (artifactsDeck.removeFromDeck(artifactInfo.getId()) != null) {
            artifactsDeck.addToInvestigator(activeInvestigatorId, artifactInfo);
            ss.onSuccess(new GainedCardData(artifactInfo, GainedCardData.CardType.ARTIFACT, input.isConfirmRequired()));
            return;
        }
        artifactsDeck.removeFromDiscardPile(artifactInfo);
        artifactsDeck.addToInvestigator(activeInvestigatorId, artifactInfo);
        ss.onSuccess(new GainedCardData(artifactInfo, GainedCardData.CardType.ARTIFACT, input.isConfirmRequired()));
    }

    private void gainAsset() {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        AssetsDeckWrite assetsDeck = ServicePlatform.get().getAssetsDeck();

        boolean isInReserve = assetsDeck.removeFromReserve(input.getAssetInfo());
        if (isInReserve) {
            assetsDeck.addToInvestigator(activeInvestigatorId, input.getAssetInfo());
            assetsDeck.initReserve();
            ss.onSuccess(new GainedCardData(input.getAssetInfo(), GainedCardData.CardType.ASSET, input.isConfirmRequired()));
            return;
        }
        boolean isInDiscardPile = assetsDeck.removeFromDiscardPile(input.getAssetInfo());
        if (isInDiscardPile) {
            assetsDeck.addToInvestigator(activeInvestigatorId, input.getAssetInfo());
            ss.onSuccess(new GainedCardData(input.getAssetInfo(), GainedCardData.CardType.ASSET, input.isConfirmRequired()));
            return;
        }
        if (assetsDeck.removeFromDeck(input.getAssetInfo().getId()) != null) {
            assetsDeck.addToInvestigator(activeInvestigatorId, input.getAssetInfo());
            ss.onSuccess(new GainedCardData(input.getAssetInfo(), GainedCardData.CardType.ASSET, input.isConfirmRequired()));
            return;
        }
        throw new IllegalArgumentException("This asset was not found anywhere!");

    }

    private void displayCannotGainCard(String message) {
        Question<Boolean> question = new Question<>();
        question.setTitle(message);
        question.setOptions(Collections.singletonList(new Question.Option<>("OK", true)));
        ServicePlatform.get().getGameController().ask(question).subscribe(response -> {
            ss.onSuccess(new GainedCardData(null, null, input.isConfirmRequired()));
        });
    }
}
