package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class GainConditionTemplate extends AbstractEncounterTemplate {
    private final ConditionId conditionId;
    private final Runnable onGainAction;

    public GainConditionTemplate(EncounterTextBuilder textBuilder, ConditionId conditionId, Runnable onGainAction) {
        super(textBuilder);
        this.conditionId = conditionId;
        this.onGainAction = onGainAction;
    }

    @Override
    public void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), null, this::onYes);
    }

    private void onYes() {
        boolean hasCondition = ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), conditionId);
        if (hasCondition) {
            TypewriterUtils.confirmInfos("Cannot gain another " + conditionId.asString() + " Condition").subscribe(() ->{
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            });
        } else {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainCondition(conditionId);
            onGainAction.run();
        }

    }
}
