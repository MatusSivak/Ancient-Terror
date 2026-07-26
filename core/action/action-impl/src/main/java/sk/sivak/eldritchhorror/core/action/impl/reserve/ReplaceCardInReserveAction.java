package sk.sivak.eldritchhorror.core.action.impl.reserve;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropOutput;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

public class ReplaceCardInReserveAction extends AbstractHookableAction<ShowReserveDragAndDropOutput, ShowReserveDragAndDropOutput> {

    private static final Logger logger = LogManager.getLogger(ReplaceCardInReserveAction.class);

    public ReplaceCardInReserveAction() {
        super(BeforeAfterEvent.REPLACE_CARD_IN_RESERVE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super ShowReserveDragAndDropOutput> ShowReserveDragAndDropOutput) {
        if (input.getSelectedAssets().size() != 0) {
            ss.onSuccess(input);
            return;
        }
        if (ServicePlatform.get().getAssetsDeck().getReserve().isEmpty()) {
            ss.onSuccess(input);
            return;
        }
        askToReplace();
    }

    private void askToReplace() {
        Question<Boolean> question = new Question<>();
        question.addOption(new Question.Option<>("No", false, 0x800000ff))
                .addOption(new Question.Option<>("Yes", true, 0x008000ff));

        question.setTitle("Replace card in reserve?");
        Question.SelectComponentsTableData<AssetInfo> tableData = new Question.SelectComponentsTableData<>();
        tableData.setType(Question.SelectComponentsTableType.CARDS);
        tableData.setAvailableKeys(ServicePlatform.get().getAssetsDeck().getReserve());
        question.setSelectComponentsTableData(tableData);
        Single<Answer<Boolean, CardInfo>> ask = ServicePlatform.get().getGameService().ask(question);
        ask.subscribe(this::onAnswer);
        ss.onSuccess(null);
    }

    private void onAnswer(Answer<Boolean, CardInfo> answer) {
        if (answer.getResponseData()) {
            onSelect(answer.getAdditionalData());
        } else {
            ServicePlatform.get().getService().convertTo(ShowReserveDragAndDropOutput.class, () -> input);
        }
    }

    private void onSelect(CardInfo cardInfo) {
        if (cardInfo != null) {
            ServicePlatform.get().getAssetsDeck().discardFromReserve(((AssetInfo) cardInfo));
            ServicePlatform.get().getGameController().enableDiscardButton();
            ServicePlatform.get().getService().convertTo(ShowReserveDragAndDropOutput.class, () -> input);
        } else {
            SelectCardData selectCardData = new SelectCardData();
            selectCardData.setTitleText("Select Asset to replace");
            selectCardData.setHideText("Display Reserve?");
            selectCardData.setAvailableCards(ServicePlatform.get().getAssetsDeck().getReserve());
            ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(this::onSelect);
        }
    }

}
