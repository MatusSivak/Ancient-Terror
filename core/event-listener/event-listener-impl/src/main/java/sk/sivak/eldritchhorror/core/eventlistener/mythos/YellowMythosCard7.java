package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.List;

public class YellowMythosCard7 implements MythosCardEventListener{

    @Override
    public void execute() {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            Question<Integer> question = new Question<>();
            question.setTitle("What do you want to gain?");
            question.setOptions(Arrays.asList(
                    new Question.Option<>("Clue", 1),
                    new Question.Option<>("Asset", 2),
                    new Question.Option<>("Spell", 3)
            ));
            ServicePlatform.get().getGameService().ask(question).map(Answer::getResponseData).subscribe(option -> {
                ServicePlatform.get().getService().hold();
                switch (option) {
                    case 1:
                        ServicePlatform.get().getTokenService().gainClueFromPool();
                        break;
                    case 2:
                        ServicePlatform.get().getGameService().gainAsset(ServicePlatform.get().getAssetDeck().findFirst());
                        break;
                    case 3:
                        ServicePlatform.get().getGameService().gainSpell();
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                ServicePlatform.get().getService().release();
            });
        }
        ServicePlatform.get().getService().release();
    }

}
