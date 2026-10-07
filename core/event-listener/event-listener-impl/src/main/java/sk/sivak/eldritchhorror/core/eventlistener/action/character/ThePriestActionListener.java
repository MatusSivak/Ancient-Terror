package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import java.util.ArrayList;
import java.util.List;

public class ThePriestActionListener extends AbstractActionPhaseListener<ThePriestActionListener.CharacterAction> {
    public ThePriestActionListener() { name = "Share Boon"; }
    @Override protected String getAdditionalInfo() { return null; }
    @Override protected String getGeneralDescription() { return "Give one of your Boon Conditions to another investigator on any space."; }
    @Override protected CharacterAction createAction() { return new CharacterAction(); }
    @Override protected boolean isVisible() { return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_PRIEST; }
    @Override protected boolean isDisabled() {
        disabledReason = "No Boon can be given to another investigator.";
        return transferableBoons().isEmpty();
    }
    @Override protected boolean isNotRecommended() { return false; }
    @Override protected ActionButtonData.ActionButtonId getActionButtonId() { return ActionButtonData.ActionButtonId.INVESTIGATOR; }
    @Override protected String getTexturePath() { return "investigator/THE_PRIEST.png"; }
    protected class CharacterAction extends AbstractActionPhaseAction {
        @Override public void execute() {
            SelectCardData select = new SelectCardData();
            select.setTitleText("Choose a Boon to give");
            select.setHideText("Show Boon Conditions");
            select.setAvailableCards(transferableBoons());
            ServicePlatform.get().getCardService().selectSingleCard(select).subscribe(card -> {
                if (card == null) { ServicePlatform.get().getService().convertToNull(); return; }
                ConditionInfo boon = (ConditionInfo) card;
                Question<InvestigatorId> question = new Question<>();
                question.setTitle("Who receives " + boon.getName() + "?");
                List<Question.Option<InvestigatorId>> options = new ArrayList<>();
                for (InvestigatorRead investigator : getInvestigators().getOnBoardInvestigators()) {
                    InvestigatorId id = investigator.getInfo().getInvestigatorId();
                    if (canReceive(id, boon)) options.add(new Question.Option<>(investigator.getInfo().getInvestigatorName(), id));
                }
                question.setOptions(options);
                ServicePlatform.get().getGameService().ask(question).subscribe(answer ->
                    ServicePlatform.get().getService().transferCondition(InvestigatorId.THE_PRIEST, answer.getResponseData(), boon));
            });
        }
    }
    private boolean canReceive(InvestigatorId id, ConditionInfo boon) {
        return id != InvestigatorId.THE_PRIEST && !ServicePlatform.get().getConditionsDeck().hasCondition(id, boon.getId());
    }
    private List<ConditionInfo> transferableBoons() {
        List<ConditionInfo> result = new ArrayList<>();
        for (ConditionInfo boon : ServicePlatform.get().getConditionsDeck().getCondition(InvestigatorId.THE_PRIEST, ConditionTrait.BOON)) {
            for (InvestigatorRead investigator : getInvestigators().getOnBoardInvestigators()) {
                if (canReceive(investigator.getInfo().getInvestigatorId(), boon)) { result.add(boon); break; }
            }
        }
        return result;
    }
}

