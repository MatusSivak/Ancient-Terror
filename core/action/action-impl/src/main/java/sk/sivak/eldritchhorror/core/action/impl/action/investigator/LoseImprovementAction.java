package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.LoseImprovementData;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

/**
 * @author msivak
 */
public class LoseImprovementAction extends AbstractHookableAction<LoseImprovementData, LoseImprovementData> {

    public LoseImprovementAction() {
        super(BeforeAfterEvent.LOSE_IMPROVEMENT);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super LoseImprovementData> ss) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();

        if (input.getStat() == null || activeInvestigator.getStatBonus(input.getStat()) != 0) { // choose which stat
            ServicePlatform.get().getGameController().loseImprovement(input.getStat(), input.getAmount()).subscribe(this::onImprovementRemoved);
        } else { // cannot remove improvement
            onMissingImprovement(input.getStat().prettyString() + " is not improved, cannot lose improvement.");
        }
    }

    private void onImprovementRemoved(Stat selectedStat) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        activeInvestigator.loseImprovement(selectedStat, input.getAmount());
        input.setStat(selectedStat);
        ss.onSuccess(input);
    }

    private void onMissingImprovement(String text) {
        Question<Object> question = new Question<>();
        question.setTitle(text);
        question.setOptions(Question.Option.okOption);
        ServicePlatform.get().getGameController().showImproveSkillTable().subscribe();
        ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
            ServicePlatform.get().getGameController().hideImproveSkillTable().subscribe();
            ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
            ss.onSuccess(null);
        });
        ss.onSuccess(null);
    }
}
