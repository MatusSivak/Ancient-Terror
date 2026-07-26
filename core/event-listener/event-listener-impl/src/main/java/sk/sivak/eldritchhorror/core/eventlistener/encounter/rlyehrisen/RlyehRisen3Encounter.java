package sk.sivak.eldritchhorror.core.eventlistener.encounter.rlyehrisen;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.RlyehRisenEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class RlyehRisen3Encounter extends AbstractRlyehRisenEncounter {

    public RlyehRisen3Encounter() {
        super(3);
    }

    @Override
    protected void execute() {

        TestPassFlavorInfoFailFlavorInfoTemplate passTemplate = new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1, () -> {
            ServicePlatform.get().getGameService().advanceActiveMystery();
        }).withoutPassFlavor();

        EncounterTemplate failTemplate = () -> {
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                loseSanity(3);
                ServicePlatform.get().getService().release();
            });
        };

        new RlyehRisenEncounterTemplate(getTextBuilder(), Stat.WILL, -1, passTemplate, failTemplate).execute();
    }
}
