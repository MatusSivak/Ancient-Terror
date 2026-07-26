package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.token.GainClueData;

import java.util.Collections;

/**
 * @author msivak
 */
public class GainClueFromPoolAction extends AbstractHookableAction<GainClueData, GainClueData> {

    public GainClueFromPoolAction() {
        super(BeforeAfterEvent.GAIN_CLUE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super GainClueData> ss) {
        if (input.getInvestigatorId() == null) {
            input.setInvestigatorId(ServicePlatform.get().getInvestigators().getActiveInvestigator().getInfo().getInvestigatorId());
        }
        if (ServicePlatform.get().getCluePool().isCluePoolEmpty()) {
            Question<Boolean> question = new Question<>();
            question.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
            question.setTitle("There are no Clues remaining.");
            ServicePlatform.get().getGameController().ask(question).subscribe(ok -> {
                ss.onSuccess(input);
            });
            return;
        }
        ServicePlatform.get().getCluePool().gainClueFromPool(input.getInvestigatorId());
        ServicePlatform.get().getGameController().gainClueFromPool()
                .subscribe(() -> {
                    ss.onSuccess(input);
                });
    }
}
