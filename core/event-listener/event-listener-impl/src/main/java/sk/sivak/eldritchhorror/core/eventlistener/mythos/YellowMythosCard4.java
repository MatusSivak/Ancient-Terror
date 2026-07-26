package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

public class YellowMythosCard4 implements MythosCardEventListener{

    @Override
    public void execute() {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);

            ServicePlatform.get().getTokenService().canSpend(1,0,0,0).subscribe(this::onCanSpend);
        }
        ServicePlatform.get().getService().release();
    }

    private void onCanSpend(SpendData canSpend) {
        ServicePlatform.get().getService().hold();
        if (canSpend.hasEnough()) {
            Question<Boolean> question = new Question<>();
            question.setOptions(Question.Option.noYesOptions);
            question.setTitle("Spend one Clue?");
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, canSpend));
        } else {
            loseSanityGainMadness();
        }
        ServicePlatform.get().getService().release();
    }

    private void onAnswer(Answer<Boolean, Object> answer, SpendData canSpend) {
        ServicePlatform.get().getService().hold();
        if (answer.getResponseData()) {
            ServicePlatform.get().getTokenService().spend(1,0,0,0).subscribe(spend -> {
                ServicePlatform.get().getService().hold();
                if (spend.hasEnough()) {
                    spend.pay();
                } else {
                    loseSanityGainMadness();
                }
                ServicePlatform.get().getService().release();
            });
        } else {
            loseSanityGainMadness();
        }
        ServicePlatform.get().getService().release();
    }

    private void loseSanityGainMadness() {
        ServicePlatform.get().getTokenService().loseSanity(2);
        ServicePlatform.get().getGameService().gainCondition(ConditionTrait.MADNESS);
    }
}
