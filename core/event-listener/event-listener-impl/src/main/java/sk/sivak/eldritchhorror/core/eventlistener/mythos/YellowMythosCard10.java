package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

public class YellowMythosCard10 implements MythosCardEventListener{

    @Override
    public void execute() {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
            boolean isDelayed = investigator.isDelayed();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigator.getInfo().getInvestigatorId());
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            if (isDelayed) {
                ServicePlatform.get().getTokenService().loseHealth(3);
            } else {
                Question<Boolean> question = new Question<>();
                question.setTitle("Become Delayed?");
                question.setOptions(Question.Option.noYesOptions);
                ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
                    if (answer.getResponseData()) {
                        ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(investigatorId, false));
                    } else {
                        ServicePlatform.get().getTokenService().loseHealth(3);
                    }
                });

            }
        }
        ServicePlatform.get().getService().release();
    }
}
