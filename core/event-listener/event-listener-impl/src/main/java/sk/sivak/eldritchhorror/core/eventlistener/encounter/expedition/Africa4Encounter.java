package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ComplexEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ExpeditionEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.LoseImprovementData;

public class Africa4Encounter extends AbstractExpeditionEncounter {

    public Africa4Encounter() {
        super(4, LocationId.THE_HEART_OF_AFRICA);
    }

    @Override
    protected void execute() {
        EncounterTemplate passTemplate = new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                this::gainArtifact).withExpeditionFlavor();

        EncounterTemplate failInnerTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, 0,
                this::gainTwoClues,
                () -> loseHealth(2)).withExpeditionFlavor();
        InfoFlavorTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(),
                () -> ServicePlatform.get().getInvestigatorService().loseImprovement(new LoseImprovementData(Stat.INFLUENCE, 2)), failInnerTemplate);

        new ExpeditionEncounterTemplate(getTextBuilder(), Stat.LORE, -1, passTemplate, failTemplate).execute();
    }
}
