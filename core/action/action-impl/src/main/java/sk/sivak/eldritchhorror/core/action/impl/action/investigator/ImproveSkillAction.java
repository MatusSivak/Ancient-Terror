package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

import java.util.Collections;

/**
 * @author msivak
 */
public class ImproveSkillAction extends AbstractHookableAction<Stat, Stat> {

    public ImproveSkillAction() {
        super(BeforeAfterEvent.IMPROVE_SKILL);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Stat> ss) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        if (input != null && activeInvestigator.getStatBonus(input) == 2) {
            onStatMaximized(input.prettyString() + " cannot be improved.");
        } else if (input == null) {
            int totalBonus = 0;
            totalBonus += activeInvestigator.getStatBonus(Stat.WILL);
            totalBonus += activeInvestigator.getStatBonus(Stat.OBSERVATION);
            totalBonus += activeInvestigator.getStatBonus(Stat.LORE);
            totalBonus += activeInvestigator.getStatBonus(Stat.STRENGTH);
            totalBonus += activeInvestigator.getStatBonus(Stat.INFLUENCE);
            if (totalBonus == 10) {
                onStatMaximized("No skill can be improved.");
            } else {
                ServicePlatform.get().getGameController().improveSkill(null).subscribe(this::onImproveSkill);
            }
        } else {
            ServicePlatform.get().getGameController().improveSkill(input).subscribe(this::onImproveSkill);
        }
    }

    private void onStatMaximized(String text) {
        Question<Object> question = new Question<>();
        question.setTitle(text);
        question.setOptions(Collections.singletonList(
                new Question.Option<>("OK", true)

        ));
        ServicePlatform.get().getGameController().showImproveSkillTable().subscribe();
        ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
            ServicePlatform.get().getGameController().hideImproveSkillTable().subscribe();
            ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
            ss.onSuccess(null);
        });
        ss.onSuccess(null);
    }

    private void onImproveSkill(Stat stat) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        activeInvestigator.improveStat(stat);
        ss.onSuccess(stat);

    }
}
