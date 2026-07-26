package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.WirelessReportAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class WirelessReportListener extends AbstractAssetListener<WirelessReportAsset> {

    private static final Logger logger = LogManager.getLogger(WirelessReportListener.class);

    @Override
    protected void register() {
        ServicePlatform.get().getService().<GainedCardData>addEventCommand((input) -> {
            if (ServicePlatform.get().getCluePool().getClueCount(investigatorId) > 0) {
                showCardEnoughClues(input);
            } else {
                showCardNotEnoughClues(input);
            }
        });
    }

    private void showCardNotEnoughClues(GainedCardData input) {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
        showCardRequest.setAssetInfo(getAssetInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
        showCardRequest.setTitle("You don't have any Clues.");
        ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe((response) -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
            ServicePlatform.get().getService().convertTo(Object.class, () -> input);
            ServicePlatform.get().getService().release();
        });
    }

    private void showCardEnoughClues(GainedCardData input) {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
        showCardRequest.setAssetInfo(getAssetInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
        showCardRequest.setTitle("Give Clues to another investigator.");
        ServicePlatform.get().getGameService().showCard(showCardRequest)
                .subscribe(showCardResponse -> onEnoughClues(showCardResponse, input));
    }

    private void onEnoughClues(ShowCardResponse showCardResponse, GainedCardData input) {
        int clueCount = ServicePlatform.get().getCluePool().getClueCount(investigatorId);
        if (clueCount == 1) {
            Answer<Integer, Object> answer = new Answer<>();
            answer.setResponseData(1);
            onSelectedAmount(answer, input);
        } else {
            Question<Integer> question = new Question<>();
            question.setTitle("How many Clues do you want to give?");
            question.setOptions(new LinkedList<>());
            for (int i = 1; i <= clueCount; i++) {
                question.getOptions().add(new Question.Option<>(String.valueOf(i), i));
            }
            ServicePlatform.get().getGameService().ask(question)
                    .subscribe(integerObjectAnswer -> onSelectedAmount(integerObjectAnswer, input));
        }
    }

    private void onSelectedAmount(Answer<Integer, Object> answer, GainedCardData input) {
        ServicePlatform.get().getInvestigatorService()
                .selectInvestigator(new InvestigatorRestriction(true).addDisabledInvestigator(investigatorId))
                .subscribe((selectedInvestigator) ->
                        withSelectedInvestigator(answer.getResponseData(), selectedInvestigator, input));
    }

    private void withSelectedInvestigator(Integer amount, InvestigatorId selectedInvestigator, GainedCardData input) {
        ServicePlatform.get().getService().hold();
        for (Integer i = 0; i < amount; i++) {
            ServicePlatform.get().getTokenService().loseClue();
        }
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(selectedInvestigator);
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        for (Integer i = 0; i < amount; i++) {
            ServicePlatform.get().getTokenService().gainClueFromPool();
        }
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
        if (ServicePlatform.get().getPerformedActions().canPerformAction(investigatorId)) {
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        }
        ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
        ServicePlatform.get().getService().convertTo(GainedCardData.class,() -> input);
        ServicePlatform.get().getService().release();
    }

    @Override
    public boolean gainCard() {
        return false;
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.emptyList();
    }
}
