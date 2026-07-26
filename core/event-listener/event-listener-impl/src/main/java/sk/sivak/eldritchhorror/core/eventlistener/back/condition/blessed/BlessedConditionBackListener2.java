package sk.sivak.eldritchhorror.core.eventlistener.back.condition.blessed;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class BlessedConditionBackListener2 extends AbstractBlessedConditionBackListener {

    public BlessedConditionBackListener2(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos("[#GOOD]Select new Omen, Doom stays[]").subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getDoomOmenService().selectNewOmen();
            ServicePlatform.get().getEncounterService().release();
        });
    }
}
