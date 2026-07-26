package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

import java.util.List;

import static java.util.Arrays.asList;

public class YellowMythosCard2 implements MythosCardEventListener{

    @Override
    public void execute() {
        List<? extends InvestigatorRead> investigators = Stream.collectToList(ServicePlatform.get().getInvestigators().getOnBoardInvestigators(),
                investigator -> ServicePlatform.get().getCluePool().getClueCount(investigator.getInfo().getInvestigatorId()) > 0);

        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigator.getInfo().getInvestigatorId());
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            for (int i = 0; i < ServicePlatform.get().getCluePool().getClueCount(investigator.getInfo().getInvestigatorId()); i++) {


                Single<SpendData> canSpendHealthSingle = ServicePlatform.get().getTokenService().canSpend(0, 0, 1, 0);
                Single<SpendData> canSpendSanitySingle = ServicePlatform.get().getTokenService().canSpend(0, 0, 0, 1);

                canSpendHealthSingle.zipWith(canSpendSanitySingle, (canSpendHealth, canSpendSanity) ->
                        new Boolean[]{canSpendHealth.hasEnough(), canSpendSanity.hasEnough()}).subscribe(canSpendHealthOrSanity -> {
                    if (canSpendHealthOrSanity[0] || canSpendHealthOrSanity[1]) {
                        Question<Boolean> question = new Question<>();
                        question.setTitle("Discard Clue?");
                        question.setOptions(Question.Option.noYesOptions);
                        ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswerDiscardClue(investigator, answer, canSpendHealthOrSanity));
                    } else {
                        ServicePlatform.get().getTokenService().loseClue();
                    }
                });
            }
        }
        ServicePlatform.get().getService().release();
    }

    private void onAnswerDiscardClue(InvestigatorRead investigator, Answer<Boolean, Object> answer, Boolean[] canSpendHealthOrSanity) {
        ServicePlatform.get().getService().hold();
        if (answer.getResponseData()) {
            ServicePlatform.get().getTokenService().loseClue();
        } else {
            if (canSpendHealthOrSanity[0] && canSpendHealthOrSanity[1]) {
                Question<String> question = new Question<>();
                question.setOptions(asList(
                        new Question.Option<>("Health", "Health", 0x800000ff),
                        new Question.Option<>("Sanity", "Sanity", 0x000080ff)));
                question.setTitle("What do you want to spend?");
                ServicePlatform.get().getGameService().ask(question).subscribe(answer2 ->
                        onAnswerSpendHealthOrSanity(answer2.getResponseData().equals("Health")));
            } else if (canSpendHealthOrSanity[0]) {
                onAnswerSpendHealthOrSanity(true);
            } else if (canSpendHealthOrSanity[1]) {
                onAnswerSpendHealthOrSanity(false);
            } else {
                ServicePlatform.get().getTokenService().loseClue();
            }
        }
        ServicePlatform.get().getService().release();
    }

    private void onAnswerSpendHealthOrSanity(boolean health) {
        ServicePlatform.get().getService().hold();
        ClueFocusHealthSanity clueFocusHealthSanity;
        if (health) {
            clueFocusHealthSanity = new ClueFocusHealthSanity(0,0,1,0);
        } else {
            clueFocusHealthSanity = new ClueFocusHealthSanity(0,0,0,1);
        }
        ServicePlatform.get().getTokenService().canSpend(clueFocusHealthSanity).subscribe(canSpend -> {
            ServicePlatform.get().getService().hold();
            if (canSpend.hasEnough()) {
                ServicePlatform.get().getTokenService().spend(clueFocusHealthSanity).subscribe(spend -> {
                    ServicePlatform.get().getService().hold();
                    if (spend.hasEnough()) {
                        spend.pay();
                    } else {
                        ServicePlatform.get().getTokenService().loseClue();
                    }
                    ServicePlatform.get().getService().release();
                });
            } else {
                ServicePlatform.get().getTokenService().loseClue();
            }
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }
}
