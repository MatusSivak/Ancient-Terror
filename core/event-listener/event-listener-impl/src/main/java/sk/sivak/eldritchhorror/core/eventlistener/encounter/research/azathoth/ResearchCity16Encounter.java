package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchCity16Encounter extends AbstractAzathothResearchEncounter {

    public ResearchCity16Encounter() {
        super(16, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new InfoFlavorTemplate(getTextBuilder(),
                () -> ServicePlatform.get().getGameService().gainArtifact(ArtifactId.REQUIEM_PER_SHUGGAY),
                new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -2,
                        this::gainThisClue,
                        () -> ServicePlatform.get().getTokenService().loseSanity(2)
                ).withoutPassFlavor().withResearchFlavor()
        ).execute();
    }
}
