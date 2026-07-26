package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

/**
 * @author msivak
 */
public class TheExpeditionLeaderActionListener extends AbstractActionPhaseListener<TheExpeditionLeaderActionListener.TheExpeditionLeaderAction> {

    private static final Logger logger = LogManager.getLogger(TheExpeditionLeaderActionListener.class);

    public TheExpeditionLeaderActionListener() {
        name = "Gain\nAlly";
    }

    @Override
    protected String getAdditionalInfo() {
        if (isDisabled()) {
            return null;
        }
        List<AssetInfo> allies = findAllies();
        StringBuilder sb = new StringBuilder();
        for (AssetInfo ally : allies) {
            sb.append(ally.getName()).append("\n");
        }
        return sb.toString();
    }

    private List<AssetInfo> findAllies() {
        List<AssetInfo> composite = new ArrayList<>();
        composite.addAll(ServicePlatform.get().getAssetDeck().getReserve());
        composite.addAll(ServicePlatform.get().getAssetDeck().getDiscardPile());

        Predicate<AssetInfo> predicate = assetInfo ->
                assetInfo.getTraits().contains(AssetTrait.ALLY);

        return Stream.collectToList(composite, predicate);
    }

    @Override
    protected String getGeneralDescription() {
        return "Test Influence. " +
                "If you pass, gain 1 Ally Asset " +
                "of your choice from the reserve " +
                "or the discard pile";
    }

    @Override
    protected TheExpeditionLeaderAction createAction() {
        return new TheExpeditionLeaderAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_EXPEDITION_LEADER;
    }

    @Override
    protected boolean isDisabled() {
        if (isAllyNotPresent(ServicePlatform.get().getAssetDeck().getReserve()) && isAllyNotPresent(ServicePlatform.get().getAssetDeck().getDiscardPile())) {
            disabledReason = "No Allies in Reserve or Discard pile";
            return true;
        }
        return false;
    }

    private boolean isAllyNotPresent(List<AssetInfo> assets) {
        return !Stream.anyMatch(assets, assetInfo ->
                assetInfo.getTraits().contains(AssetTrait.ALLY));
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.INVESTIGATOR;
    }

    @Override
    protected String getTexturePath() {
        return "investigator/THE_EXPEDITION_LEADER.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheExpeditionLeaderAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing the expedition leader action...");

            ServicePlatform.get().getTestService().test(Stat.INFLUENCE,0,JUST_ONE).subscribe(this::onTestResult);


        }

        private void onTestResult(TestData testData) {
            if (testData.isSuccessful()) {
                afterSuccessfulTest();
            } else {
                ServicePlatform.get().getService().convertToNull();
            }
        }

        private void afterSuccessfulTest() {
            SelectCardData selectCardData = new SelectCardData();
            selectCardData.setHideText("Display allies?");
            selectCardData.setTitleText("Select Ally");
            selectCardData.setAvailableCards(findAllies());

            ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(this::onSelectAlly);
        }

        private void onSelectAlly(CardInfo cardToGain) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getGameService().gainAsset((AssetInfo) cardToGain);
            ServicePlatform.get().getCardService().refillReserve();
            ServicePlatform.get().getGameService().convertToNull();
            ServicePlatform.get().getService().release();
        }
    }
}
