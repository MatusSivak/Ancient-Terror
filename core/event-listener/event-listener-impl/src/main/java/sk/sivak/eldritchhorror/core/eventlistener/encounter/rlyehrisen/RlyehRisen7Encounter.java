package sk.sivak.eldritchhorror.core.eventlistener.encounter.rlyehrisen;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.RlyehRisenEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class RlyehRisen7Encounter extends AbstractRlyehRisenEncounter {

    public RlyehRisen7Encounter() {
        super(7);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1, () -> {
            ServicePlatform.get().getGameService().advanceActiveMystery();
        }, () -> {
            ServicePlatform.get().getService().hold();
            gainCondition(ConditionId.BACK_INJURY);
            gainCondition(ConditionId.LEG_INJURY);
            gainCondition(ConditionId.INTERNAL_INJURY);
            ServicePlatform.get().getService().release();
        }).withThreeFailInfos();

        EncounterTemplate failTemplate = () -> {
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo(1).build(),
                    getTextBuilder().withInfo(2).build(),
                    getTextBuilder().withInfo(3).build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                gainCondition(ConditionId.BACK_INJURY);
                gainCondition(ConditionId.LEG_INJURY);
                gainCondition(ConditionId.INTERNAL_INJURY);
                ServicePlatform.get().getService().release();
            });
        };

        new RlyehRisenEncounterTemplate(getTextBuilder(), Stat.STRENGTH, -1, passTemplate, failTemplate).execute();
    }
}
