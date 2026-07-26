package sk.sivak.eldritchhorror.core.action.impl.action;

import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.AfterActionPerformedData;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

import java.util.Collections;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.AFTER_ACTION_PERFORMED;

public class VerifyNotDelayedAction implements Action<Object, Void> {

    private SingleSubscriber<? super Void> singleSubscriber;
    private InvestigatorWrite activeInvestigator;

    @Override
    public Single<Void> execute() {
        return Single.<Void>create(this::onSub);
    }

    private void onSub(SingleSubscriber<? super Void> singleSubscriber) {
        this.singleSubscriber = singleSubscriber;
        activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        boolean isDelayed = activeInvestigator.isDelayed();
        if (isDelayed) {
            Question<Boolean> question = new Question<>();
            question.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
            question.setTitle("Delayed Investigators can't perform actions.");
            ServicePlatform.get().getGameService().ask(question).subscribe(this::onConfirm);
        }
        singleSubscriber.onSuccess(null);
    }

    private void onConfirm(Answer<Boolean, Object> booleanObjectAnswer) {
        ServicePlatform.get().getPerformedActions().clearFreeActions(activeInvestigator.getInfo().getInvestigatorId());

        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getInvestigatorService().endDelayed(activeInvestigator.getInfo().getInvestigatorId());

        AfterActionPerformedData afterActionPerformedData = new AfterActionPerformedData();
        afterActionPerformedData.setCanPerformAction(false);
        afterActionPerformedData.setLast(ServicePlatform.get().getInvestigators().isActiveLast());
        ServicePlatform.get().getService().skipAfterEvent(AFTER_ACTION_PERFORMED, afterActionPerformedData);

        ServicePlatform.get().getService().release();
    }


    @Override
    public void setInput(Object input) {
        // ignored
    }
}
