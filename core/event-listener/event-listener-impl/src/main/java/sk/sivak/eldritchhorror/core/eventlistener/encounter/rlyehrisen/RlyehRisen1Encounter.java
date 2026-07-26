package sk.sivak.eldritchhorror.core.eventlistener.encounter.rlyehrisen;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.RlyehRisenEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class RlyehRisen1Encounter extends AbstractRlyehRisenEncounter {

    public RlyehRisen1Encounter() {
        super(1);
    }

    @Override
    protected void execute() {

        TestPassFlavorInfoTemplate passTemplate = new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1, () -> {
            ServicePlatform.get().getGameService().advanceActiveMystery();
        });

        EncounterTemplate failTemplate = () -> {
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                gainCondition(ConditionId.DETAINED);
                ServicePlatform.get().getService().release();
            });
        };

        new RlyehRisenEncounterTemplate(getTextBuilder(), Stat.STRENGTH, -1, passTemplate, failTemplate).execute();
    }
}
