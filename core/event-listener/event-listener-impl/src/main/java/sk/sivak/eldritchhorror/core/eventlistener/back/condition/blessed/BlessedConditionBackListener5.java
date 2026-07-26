package sk.sivak.eldritchhorror.core.eventlistener.back.condition.blessed;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class BlessedConditionBackListener5 extends AbstractBlessedConditionBackListener {

    public BlessedConditionBackListener5(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(
                "[#GOOD]Recover two Health[]",
                "[#GOOD]Recover two Sanity[]").subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().gainHealth(2);
            ServicePlatform.get().getTokenService().gainSanity(2);
            ServicePlatform.get().getEncounterService().release();
        });
    }
}
