package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
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

public class TheNunActionListener extends AbstractActionPhaseListener<TheNunActionListener.CharacterAction> {
    public TheNunActionListener() { name = "Comfort"; }
    @Override protected String getAdditionalInfo() { return null; }
    @Override protected String getGeneralDescription() { return "Test Will. For each success, you or another investigator on any space may discard 1 Madness Condition."; }
    @Override protected CharacterAction createAction() { return new CharacterAction(); }
    @Override protected boolean isVisible() { return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_NUN; }
    @Override protected boolean isDisabled() {
        disabledReason = "No investigator has a Madness Condition.";
        return madnessOwners().isEmpty();
    }
    @Override protected boolean isNotRecommended() { return false; }
    @Override protected ActionButtonData.ActionButtonId getActionButtonId() { return ActionButtonData.ActionButtonId.INVESTIGATOR; }
    @Override protected String getTexturePath() { return "investigator/THE_NUN.png"; }
    protected class CharacterAction extends AbstractActionPhaseAction {
        @Override public void execute() {
            ServicePlatform.get().getTestService().test(Stat.WILL, 0, Integer.MAX_VALUE).subscribe(result -> discardMadness(result.getScore()));
        }
    }
    private List<InvestigatorRead> madnessOwners() {
        List<InvestigatorRead> result = new ArrayList<>();
        for (InvestigatorRead investigator : getInvestigators().getOnBoardInvestigators())
            if (ServicePlatform.get().getConditionsDeck().hasTrait(investigator.getInfo().getInvestigatorId(), ConditionTrait.MADNESS)) result.add(investigator);
        return result;
    }
    private void discardMadness(int remaining) {
        if (remaining <= 0 || madnessOwners().isEmpty()) { ServicePlatform.get().getService().convertToNull(); return; }
        Question<InvestigatorId> question = new Question<>();
        question.setTitle("Discard a Madness Condition (" + remaining + " remaining)");
        List<Question.Option<InvestigatorId>> options = new ArrayList<>();
        for (InvestigatorRead investigator : madnessOwners()) options.add(new Question.Option<>(investigator.getInfo().getInvestigatorName(), investigator.getInfo().getInvestigatorId()));
        options.add(new Question.Option<>("Done", null));
        question.setOptions(options);
        ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
            InvestigatorId owner = answer.getResponseData();
            if (owner == null) { ServicePlatform.get().getService().convertToNull(); return; }
            SelectCardData select = new SelectCardData();
            select.setTitleText("Choose a Madness Condition to discard");
            select.setHideText("Show Madness Conditions");
            select.setAvailableCards(ServicePlatform.get().getConditionsDeck().getCondition(owner, ConditionTrait.MADNESS));
            ServicePlatform.get().getCardService().selectSingleCard(select).subscribe(card -> {
                if (card == null) { ServicePlatform.get().getService().convertToNull(); return; }
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().discardConditionFromInvestigator(owner, (ConditionInfo) card);
                ServicePlatform.get().getService().addEventCommand(in -> { discardMadness(remaining - 1); });
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            });
        });
    }
}

