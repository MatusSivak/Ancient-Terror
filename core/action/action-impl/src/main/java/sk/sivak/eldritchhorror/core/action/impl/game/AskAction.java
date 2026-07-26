package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

import java.util.concurrent.TimeUnit;

public class AskAction<RD, AD> extends AbstractHookableAction<Question<RD>, Answer<RD, AD>> {

    private Answer<RD, AD> answer;

    public AskAction() {
        super(BeforeAfterEvent.ASK);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Answer<RD, AD>> ss) {
        if (input.isDisplayCurrentMysteryCard()) {
            displayMysteryCardAndAskQuestion(ss);
        } else if (input.getDisplayOngoingRumorCard() != null) {
            displayOngoingRumorCardAndAskQuestion(input.getDisplayOngoingRumorCard(), ss);
        } else {
            justAskQuestion(ss);
        }

    }

    private void justAskQuestion(SingleSubscriber<? super Answer<RD, AD>> ss) {
        Single<Answer<RD, AD>> ask = ServicePlatform.get().getGameController().ask(input);
        ask.subscribe(ss::onSuccess);
    }

    private void displayMysteryCardAndAskQuestion(SingleSubscriber<? super Answer<RD, AD>> ss) {
        ServicePlatform.get().getGameController().showCurrentMysteryCard(false)
                .subscribe(() -> ss.onSuccess(answer));
        Single<Answer<RD, AD>> ask = ServicePlatform.get().getGameController().ask(input);
        ask.subscribe(answer -> this.answer = answer);
    }

    private void displayOngoingRumorCardAndAskQuestion(String rumorId, SingleSubscriber<? super Answer<RD, AD>> ss) {
        RumorCardInfo activeRumor = ServicePlatform.get().getRumors().getActiveRumor(rumorId);
        Completable showRumorCard = ServicePlatform.get().getGameController().justShowRumorCard(activeRumor);
        Single<Answer<RD, AD>> askQuestion = ServicePlatform.get().getGameController().ask(input);
        showRumorCard.andThen(askQuestion).subscribe(answer -> {
            ServicePlatform.get().getGameController().justHideRumorCard().subscribe(() -> {
                ss.onSuccess(answer);
            });
        });
    }

}
