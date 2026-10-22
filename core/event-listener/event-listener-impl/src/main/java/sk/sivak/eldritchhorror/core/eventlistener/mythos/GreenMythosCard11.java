package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

/**
 * Righteous Calling: each investigator may spend 1 Clue to gain 1 Boon Condition.
 */
public class GreenMythosCard11 implements MythosCardEventListener {

    @Override
    public void execute() {
        List<? extends InvestigatorRead> investigators = Stream.collectToList(
                ServicePlatform.get().getInvestigators().getOnBoardInvestigators(),
                investigator -> {
                    InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
                    return ServicePlatform.get().getCluePool().getClueCount(investigatorId) > 0
                            && ServicePlatform.get().getConditionsDeck().canGetTrait(investigatorId, ConditionTrait.BOON);
                });
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigator.getInfo().getInvestigatorId());
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            Question<Boolean> question = new Question<>();
            question.setTitle("Spend 1 Clue to gain a Boon?");
            question.setOptions(Question.Option.noYesOptions);
            ServicePlatform.get().getGameService().ask(question).map(Answer::getResponseData).subscribe(spend -> {
                if (!spend) {
                    return;
                }
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getTokenService().loseClue();
                ServicePlatform.get().getGameService().gainCondition(ConditionTrait.BOON);
                ServicePlatform.get().getService().release();
            });
        }
        ServicePlatform.get().getService().release();
    }
}
