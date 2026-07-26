package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class OtherWorld23Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld23Encounter() {
        super(23);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(),
                () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.WILL),
                new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, -1,
                        this::closeThisGateAndEnd,
                        () -> gainCondition(ConditionId.BACK_INJURY)
                        ).withoutFailFlavor().withOtherWorldFlavor());

        EncounterTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(),
                () -> gainCondition(ConditionId.LEG_INJURY),
                new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                        () -> {
                            ServicePlatform.get().getService().hold();
                            loseHealth(1);
                            loseSanity(1);
                            ServicePlatform.get().getService().release();
                        }
                ).withoutFailFlavor().withTwoFailInfos().withOtherWorldFlavor());


        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.OBSERVATION, 0, passTemplate, failTemplate).execute();
    }
}
