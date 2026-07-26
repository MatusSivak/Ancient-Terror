package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SomethingUnlessSpendTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class OtherWorld22Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld22Encounter() {
        super(22);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(), this::gainArtifact,
                new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                        this::closeThisGateAndEnd,
                        () -> gainCondition(ConditionId.AMNESIA)
                        ).withOtherWorldFlavor());

        EncounterTemplate failTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, 0,
                () -> {
                    ServicePlatform.get().getService().hold();
                    loseHealth(1);
                    loseSanity(1);
                    ServicePlatform.get().getService().release();
                })
                .withOtherWorldFlavor().withTwoFailInfos();


        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.WILL, 0, passTemplate, failTemplate).execute();
    }
}
