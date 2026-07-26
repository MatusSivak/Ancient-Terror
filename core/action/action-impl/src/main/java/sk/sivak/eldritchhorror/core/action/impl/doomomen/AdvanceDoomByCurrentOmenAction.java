package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

import java.util.Collections;
import java.util.List;

public class AdvanceDoomByCurrentOmenAction extends AbstractHookableAction<OmenInfo, Void> {

    public AdvanceDoomByCurrentOmenAction() {
        super(BeforeAfterEvent.ADVANCE_DOOM_BY_CURRENT_OMEN);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        OmenColor omenColor = input.getOmenColor();
        List<LocationId> gatesRepresentingCurrentOmen = ServicePlatform.get().getGateStack()
                .getSpawnedGates(omenColor.toGateColor());
        ServicePlatform.get().getService().hold();
        for (LocationId gate : gatesRepresentingCurrentOmen) {
            ServicePlatform.get().getService().moveCameraToLocation(gate);
            Question<Object> question = new Question<>();
            question.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
            question.setTitle(omenColor.getPrettyString() + " Gate matches Omen. Doom advances!");
            ServicePlatform.get().getGameService().ask(question).subscribe(x -> {
                ServicePlatform.get().getDoomOmenService().advanceDoom();
            });
        }
        ServicePlatform.get().getService().convertTo(Void.class, () -> null);
        ServicePlatform.get().getService().release();
        ss.onSuccess(null);

    }
}
