package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardArtifactFromInvestigatorData;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardAssetFromInvestigatorData;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DISCARD_ARTIFACT_FROM_INVESTIGATOR;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DISCARD_ASSET_FROM_INVESTIGATOR;

/**
 * @author msivak
 */
public class TheBootleggerInitListener extends AbstractInvestigatorInitListener {

    private TheBootleggerPassiveAssetListener theBootleggerPassiveAssetListener;
    private TheBootleggerPassiveArtifactListener theBootleggerPassiveArtifactListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_BOOTLEGGER;
    }

    @Override
    protected void initInvestigator() {
        justRegisterListeners();
        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.CAT_BURGLAR);
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.WHISKEY);
        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        theBootleggerPassiveAssetListener = new TheBootleggerPassiveAssetListener();
        theBootleggerPassiveArtifactListener = new TheBootleggerPassiveArtifactListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(theBootleggerPassiveAssetListener, DISCARD_ASSET_FROM_INVESTIGATOR);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(theBootleggerPassiveArtifactListener, DISCARD_ARTIFACT_FROM_INVESTIGATOR);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(theBootleggerPassiveAssetListener);
        ServicePlatform.get().getEventQueue().unregisterListener(theBootleggerPassiveArtifactListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class TheBootleggerPassiveAssetListener extends EventListenerImpl<DiscardAssetFromInvestigatorData> {

        @Override
        public void onNotify(DiscardAssetFromInvestigatorData eventData) {
            if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != InvestigatorId.THE_BOOTLEGGER) {
                return;
            }
            if (!eventData.isForced()) {
                return;
            }
            if (eventData.getAssetInfo() == null) {
                return;
            }
            boolean itemAllyOrTrinketFound = Stream.anyMatch(eventData.getAssetInfo().getTraits(), assetTrait ->
                    assetTrait.equals(AssetTrait.ITEM) || assetTrait.equals(AssetTrait.ALLY) || assetTrait.equals(AssetTrait.TRINKET));
            if (!itemAllyOrTrinketFound) {
                return;
            }
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Bootlegger cannot be forced to discard this asset.");
            question.setPortraitBeforeTitle(InvestigatorId.THE_BOOTLEGGER);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getGameService().sewCard(eventData.getAssetInfo());

                ServicePlatform.get().getService().skipAfterEvent(DISCARD_ASSET_FROM_INVESTIGATOR, eventData);
                ServicePlatform.get().getService().release();
            });

        }

        @Override
        public Class<DiscardAssetFromInvestigatorData> getDataClass() {
            return DiscardAssetFromInvestigatorData.class;
        }
    }

    private class TheBootleggerPassiveArtifactListener extends EventListenerImpl<DiscardArtifactFromInvestigatorData> {

        @Override
        public void onNotify(DiscardArtifactFromInvestigatorData eventData) {
            if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != InvestigatorId.THE_BOOTLEGGER) {
                return;
            }
            if (!eventData.isForced()) {
                return;
            }
            if (eventData.getArtifactInfo() == null) {
                return;
            }
            boolean itemAllyOrTrinketFound = Stream.anyMatch(eventData.getArtifactInfo().getTraits(), trait ->
                    trait.equals(AssetTrait.ITEM) || trait.equals(AssetTrait.ALLY) || trait.equals(AssetTrait.TRINKET));
            if (!itemAllyOrTrinketFound) {
                return;
            }
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Bootlegger cannot be forced to discard this artifact.");
            question.setPortraitBeforeTitle(InvestigatorId.THE_BOOTLEGGER);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getGameService().sewCard(eventData.getArtifactInfo());

                ServicePlatform.get().getService().skipAfterEvent(DISCARD_ARTIFACT_FROM_INVESTIGATOR, eventData);
                ServicePlatform.get().getService().release();
            });

        }

        @Override
        public Class<DiscardArtifactFromInvestigatorData> getDataClass() {
            return DiscardArtifactFromInvestigatorData.class;
        }
    }
}
