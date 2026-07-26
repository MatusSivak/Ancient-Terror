package sk.sivak.eldritchhorror.core.eventlistener.back.condition.blessed;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class BlessedConditionBackListener6 extends AbstractBlessedConditionBackListener {

    public BlessedConditionBackListener6(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(
                "[#GOOD]Gain two Clues[]").subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().gainClueFromPool();
            ServicePlatform.get().getTokenService().gainClueFromPool();
            ServicePlatform.get().getEncounterService().release();
        });
    }
}
