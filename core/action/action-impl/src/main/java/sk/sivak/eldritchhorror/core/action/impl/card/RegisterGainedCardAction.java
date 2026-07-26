package sk.sivak.eldritchhorror.core.action.impl.card;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.artifact.AbstractArtifactListener;
import sk.sivak.eldritchhorror.core.eventlistener.asset.AbstractAssetListener;
import sk.sivak.eldritchhorror.core.eventlistener.condition.AbstractConditionListener;
import sk.sivak.eldritchhorror.core.eventlistener.provider.ArtifactListenerProvider;
import sk.sivak.eldritchhorror.core.eventlistener.provider.AssetListenerProvider;
import sk.sivak.eldritchhorror.core.eventlistener.spell.AbstractSpellListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;

public class RegisterGainedCardAction extends AbstractHookableAction<GainedCardData, GainedCardData> {

    private static final Logger logger = LogManager.getLogger(RegisterGainedCardAction.class);

    public RegisterGainedCardAction() {
        super(BeforeAfterEvent.REGISTER_GAINED_CARD);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super GainedCardData> ss) {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        if (input.getCardToGain() == null) {
            ss.onSuccess(input);
        } else if (input.getCardType() == GainedCardData.CardType.CONDITION) {
            registerAndGainCondition(activeInvestigatorId, input.getCardToGain());
        } else if (input.getCardType() == GainedCardData.CardType.ASSET) {
            registerAndGainAsset(activeInvestigatorId, input.getCardToGain());
        } else if (input.getCardType() == GainedCardData.CardType.SPELL) {
            registerAndGainSpell(activeInvestigatorId, input.getCardToGain());
        } else if (input.getCardType() == GainedCardData.CardType.ARTIFACT) {
            registerAndGainArtifact(activeInvestigatorId, input.getCardToGain());
        }
    }

    private void registerAndGainCondition(InvestigatorId activeInvestigatorId, ConditionInfo conditionInfo) {
        AbstractConditionListener<? extends ConditionInfo> conditionListener = ServicePlatform.get().getConditionListenerProvider()
                .getConditionListener(conditionInfo);
        conditionListener.register(conditionInfo, activeInvestigatorId);
        conditionListener.setEventData(input);
        if (input.isConfirmRequired()) {
            ServicePlatform.get().getGameController().gainCard(conditionInfo).subscribe(() -> ss.onSuccess(input));
        } else {
            ss.onSuccess(input);
        }
    }

    private void registerAndGainSpell(InvestigatorId activeInvestigatorId, SpellInfo spellInfo) {
        AbstractSpellListener<? extends SpellInfo> spellListener = ServicePlatform.get().getSpellListenerProvider()
                .getSpellListener(spellInfo);
        spellListener.register(spellInfo, activeInvestigatorId);
        if (input.isConfirmRequired()) {
            ServicePlatform.get().getGameController().gainCard(spellInfo).subscribe(() -> ss.onSuccess(input));
        } else {
            ss.onSuccess(input);
        }
    }

    private void registerAndGainAsset(InvestigatorId activeInvestigatorId, AssetInfo assetInfo) {
        AssetListenerProvider assetListenerProvider = ServicePlatform.get().getAssetListenerProvider();
        try {
            AbstractAssetListener<? extends AssetInfo> assetListener = assetListenerProvider.getAssetListener(assetInfo.getId());
            if (assetListener.gainCard() && input.isConfirmRequired()) {
                ServicePlatform.get().getGameController().gainCard(assetInfo).subscribe(() -> {
                    registerAndDone(activeInvestigatorId, assetListener, assetInfo);
                });
            } else {
                registerAndDone(activeInvestigatorId, assetListener, assetInfo);
            }
        } catch (IllegalArgumentException iae) {
            logger.error("Asset listener for asset '" + assetInfo.getName() + "' not found");
            ss.onSuccess(input);
        }
    }

    private void registerAndGainArtifact(InvestigatorId activeInvestigatorId, ArtifactInfo artifactInfo) {
        ArtifactListenerProvider artifactListenerProvider = ServicePlatform.get().getArtifactListenerProvider();
        try {
            AbstractArtifactListener<? extends ArtifactInfo> artifactListener = artifactListenerProvider.getArtifactListener(artifactInfo.getId());
            if (artifactListener.gainCard() && input.isConfirmRequired()) {
                ServicePlatform.get().getGameController().gainCard(artifactInfo).subscribe(() -> {
                    registerAndDone(activeInvestigatorId, artifactListener, artifactInfo);
                });
            } else {
                registerAndDone(activeInvestigatorId, artifactListener, artifactInfo);
            }
        } catch (IllegalArgumentException iae) {
            logger.error("Artifact listener for asset '" + artifactInfo.getName() + "' not found");
            ss.onSuccess(input);
        }
    }

    private void registerAndDone(InvestigatorId activeInvestigatorId,
                                 AbstractAssetListener<? extends AssetInfo> assetListener,
                                 AssetInfo assetInfo) {
        assetListener.registerWithOwner(activeInvestigatorId, assetInfo);
        ss.onSuccess(input);
    }

    private void registerAndDone(InvestigatorId activeInvestigatorId,
                                 AbstractArtifactListener<? extends ArtifactInfo> artifactListener,
                                 ArtifactInfo artifactInfo) {
        artifactListener.registerWithOwner(activeInvestigatorId, artifactInfo);
        ss.onSuccess(input);
    }
}
