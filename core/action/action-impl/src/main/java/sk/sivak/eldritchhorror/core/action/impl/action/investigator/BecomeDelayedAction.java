package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import java.util.Collections;

/**
 * @author msivak
 */
public class BecomeDelayedAction extends AbstractHookableAction<DelayedData, DelayedData> {

    public BecomeDelayedAction() {
        super(BeforeAfterEvent.BECOME_DELAYED);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super DelayedData> ss) {
        InvestigatorId investigatorId;
        if (input.getInvestigatorId() == null) {
            investigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        } else {
            investigatorId = input.getInvestigatorId();
        }
        boolean isDelayed = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).isDelayed();
        if (isDelayed) {
            Question<Boolean> question = new Question<>();

            question.setTitle("Already Delayed.");
            question.setOptions(Collections.singletonList(
                    new Question.Option<>("OK", true)

            ));
            ServicePlatform.get().getGameService().ask(question).subscribe(onSuccess -> {
                ServicePlatform.get().getService().convertTo(DelayedData.class, () -> input);
            });
            ss.onSuccess(null);
        } else {
            ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).startDelayed();
            ServicePlatform.get().getGameController().showDelayedAnimation(investigatorId).subscribe(() ->
                    ss.onSuccess(input)
            );
        }

    }
}
