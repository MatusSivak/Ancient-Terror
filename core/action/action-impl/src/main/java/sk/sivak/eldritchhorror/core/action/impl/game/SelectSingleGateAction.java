package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.SelectSingleGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;

import java.util.Arrays;
import java.util.List;

public class SelectSingleGateAction extends AbstractHookableAction<SelectSingleGateData, GateInfo> {

    public SelectSingleGateAction() {
        super(BeforeAfterEvent.SELECT_SINGLE_GATE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super GateInfo> ss) {
        ServicePlatform.get().getGameController().selectSingleGate(
                input.getGates(),
                input.getTitleText(),
                input.getHideText()).subscribe(ss::onSuccess);
    }
}
