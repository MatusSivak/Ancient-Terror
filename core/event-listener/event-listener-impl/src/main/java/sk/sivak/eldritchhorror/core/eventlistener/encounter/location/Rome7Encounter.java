package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Rome7Encounter extends AbstractLocationEncounter{

    public Rome7Encounter() {
        super(7, LocationEncounter.LocationEncounterType.ROME);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), null, this::onYes);
    }

    private void onYes() {
        ServicePlatform.get().getEncounterService().hold();
        ServicePlatform.get().getEncounterService().hideTypewriterPaper();
        ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
        ServicePlatform.get().getInvestigatorService().improveSkill(null);
        ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
        ServicePlatform.get().getEncounterService().typeFlavor(" \n" + getTextBuilder().withPass().withFlavor().build());
        new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED))
                .withoutFailFlavor().execute();
        ServicePlatform.get().getEncounterService().release();

    }

}
