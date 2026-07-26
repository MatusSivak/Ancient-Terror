package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class OtherWorld26Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld26Encounter() {
        super(26);
    }

    @Override
    protected void execute() {

       EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1,
               this::closeThisGateAndEnd,
               () -> gainCondition(ConditionId.CURSED))
               .withOtherWorldFlavor();

        EncounterTemplate failTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, -1,
                this::gainArtifact,
                () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getTokenService().loseHealth(2);
                    gainCondition(ConditionId.HALLUCINATIONS);
                    ServicePlatform.get().getService().release();
                })
                .withOtherWorldFlavor().withTwoFailInfos();


        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.OBSERVATION, 0, passTemplate, failTemplate).execute();
    }
}
