package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

/** Everyone Has a Price: each investigator may gain Debt to discard one Condition. */
public class YellowMythosCard13 implements MythosCardEventListener {
    @Override
    public void execute() {
        ServicePlatform p = ServicePlatform.get();
        InvestigatorId original = p.getInvestigators().getActiveInvestigatorId();
        p.getService().hold();
        for (InvestigatorRead investigator : p.getInvestigators().getOnBoardInvestigators()) {
            InvestigatorId id = investigator.getInfo().getInvestigatorId();
            // Check at resolution time, after earlier investigators' effects have finished.
            p.getService().addEventCommand(ignored -> { offer(id); });
        }
        p.getInvestigatorService().changeActiveInvestigator(original);
        p.getInvestigatorService().showActiveInvestigator(false);
        p.getService().release();
    }

    private void offer(InvestigatorId id) {
        ServicePlatform p = ServicePlatform.get();
        if (!p.getConditionsDeck().canGetConditionId(id, ConditionId.DEBT)) return;
        List<ConditionInfo> conditions = getDiscardableConditions(id);
        if (conditions.isEmpty()) return;
        p.getService().hold();
        p.getInvestigatorService().changeActiveInvestigator(id);
        p.getInvestigatorService().showActiveInvestigator(true);
        if (conditions.size() == 1) {
            confirmDiscard(id, conditions.get(0));
        } else {
            Question<Boolean> question = new Question<>();
            question.setTitle("Gain Debt to discard a Condition of your choice?");
            question.setOptions(Question.Option.noYesOptions);
            Question.SelectComponentsTableData<ConditionInfo> cards = new Question.SelectComponentsTableData<>();
            cards.setType(Question.SelectComponentsTableType.CARDS);
            cards.setAvailableKeys(conditions);
            question.setSelectComponentsTableData(cards);
            p.getGameService().ask(question).map(Answer::getResponseData).subscribe(accept -> {
                if (!accept) return;
                SelectCardData selection = new SelectCardData();
                selection.setAvailableCards(conditions);
                selection.setTitleText("Select Condition to discard for Debt");
                selection.setHideText("Display Conditions?");
                p.getCardService().selectSingleCard(selection)
                        .subscribe(card -> confirmDiscard(id, (ConditionInfo) card));
            });
        }
        p.getService().release();
    }

    private void confirmDiscard(InvestigatorId id, ConditionInfo condition) {
        ServicePlatform p = ServicePlatform.get();
        ShowCardRequest request = new ShowCardRequest();
        request.setConditionInfo(condition);
        request.setTitle("Gain Debt to discard " + condition.getName() + "?");
        request.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
        request.setHideType(HideType.DISCARD_ON_YES);
        p.getGameService().showCard(request).subscribe(response -> {
            if (response != ShowCardResponse.YES) return;
            if (!getDiscardableConditions(id).contains(condition)
                    || !p.getConditionsDeck().canGetConditionId(id, ConditionId.DEBT)) return;
            p.getService().hold();
            p.getGameService().gainCondition(ConditionId.DEBT);
            p.getService().discardConditionFromInvestigator(id, condition);
            p.getService().release();
        });
    }

    private List<ConditionInfo> getDiscardableConditions(InvestigatorId id) {
        return Stream.collectToList(ServicePlatform.get().getConditionsDeck().getConditions(id),
                condition -> condition.getId() != ConditionId.LOST_IN_TIME_AND_SPACE);
    }
}
