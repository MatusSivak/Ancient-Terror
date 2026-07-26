package sk.sivak.eldritchhorror.core.eventlistener.back.condition.blessed;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class BlessedConditionBackListener3 extends AbstractBlessedConditionBackListener {

    public BlessedConditionBackListener3(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos("[#GOOD]Retreat Doom[]").subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getDoomOmenService().retreatDoom();
            ServicePlatform.get().getEncounterService().release();
        });
    }
}
