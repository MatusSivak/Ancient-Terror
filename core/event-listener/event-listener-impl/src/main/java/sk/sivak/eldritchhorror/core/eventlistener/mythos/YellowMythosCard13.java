package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

/**
 * Everyone Has a Price: each investigator may gain a Debt Condition to discard 1 Condition.
 */
public class YellowMythosCard13 implements MythosCardEventListener {

    @Override
    public void execute() {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
            if (!ServicePlatform.get().getConditionsDeck().canGetConditionId(investigatorId, ConditionId.DEBT)) {
                continue;
            }
            List<ConditionInfo> conditions = getDiscardableConditions(investigatorId);
            if (conditions.isEmpty()) {
                continue;
            }
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            Question<Boolean> question = new Question<>();
            question.setTitle("Gain Debt to discard a Condition?");
            question.setOptions(Question.Option.noYesOptions);
            ServicePlatform.get().getGameService().ask(question).map(Answer::getResponseData).subscribe(accept -> {
                if (!accept) {
                    return;
                }
                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setAvailableCards(conditions);
                selectCardData.setTitleText("Select Condition to discard");
                selectCardData.setHideText("Display Conditions?");
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(cardInfo -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().discardConditionFromInvestigator(investigatorId, (ConditionInfo) cardInfo);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.DEBT);
                    ServicePlatform.get().getService().release();
                });
            });
        }
        ServicePlatform.get().getService().release();
    }

    private List<ConditionInfo> getDiscardableConditions(InvestigatorId investigatorId) {
        return Stream.collectToList(ServicePlatform.get().getConditionsDeck().getConditions(investigatorId),
                condition -> condition.getId() != ConditionId.LOST_IN_TIME_AND_SPACE);
    }
}
