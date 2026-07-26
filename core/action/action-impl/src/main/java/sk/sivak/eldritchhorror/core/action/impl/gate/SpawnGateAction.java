package sk.sivak.eldritchhorror.core.action.impl.gate;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

/**
 * @author msivak
 */
public class SpawnGateAction extends AbstractHookableAction<Object, GateInfo> {

    private static final Logger logger = LogManager.getLogger(SpawnGateAction.class);
    private final boolean quick;

    public SpawnGateAction(boolean quick) {
        super(BeforeAfterEvent.SPAWN_GATES);
        this.quick = quick;
    }


    @Override
    protected void onExecute(SingleSubscriber<? super GateInfo> ss) {
        if (quick) {
            GateInfo gateInfo = ServicePlatform.get().getGateStack().spawnGate();
            ServicePlatform.get().getGameController().confirmGateSpawn(gateInfo).subscribe();
            ss.onSuccess(gateInfo);
            return;
        }
        ServicePlatform servicePlatform = ServicePlatform.get();
        GateInfo gateInfo = servicePlatform.getGateStack().spawnGate();
        if (gateInfo == null) {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Can't spawn another Gate. Doom advances!");
            ServicePlatform.get().getGameController().ask(question).subscribe(ok -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getDoomOmenService().advanceDoom();
                ServicePlatform.get().getService().convertTo(GateInfo.class, () -> null);
                ServicePlatform.get().getService().release();
                ss.onSuccess(null);
            });
            return;
        }
        ServicePlatform.get().getGameController().confirmGateSpawn(gateInfo)
                .subscribe(() -> ss.onSuccess(gateInfo));

    }
}
