package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea21Encounter extends AbstractAzathothResearchEncounter {

    public ResearchSea21Encounter() {
        super(21, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(getTextBuilder().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            OmenColor omenColor = ServicePlatform.get().getOmenTrack().getCurrentOmen().getOmenColor();
            List<LocationId> spawnedGates = ServicePlatform.get().getGateStackRead().getSpawnedGates(omenColor.toGateColor());
            if (spawnedGates.size() > 0) {
                ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                for (LocationId spawnedGate : spawnedGates) {
                    ServicePlatform.get().getService().moveCameraToLocation(spawnedGate);
                }
                ServicePlatform.get().getService().showResearchBackground(LocationType.SEA);
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
            }
            new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, - spawnedGates.size(),
                    () -> {
                        ServicePlatform.get().getService().hold();
                        gainThisClue();
                        ServicePlatform.get().getDoomOmenService().removeTokenFromOmenTrack(OmenId.NORTH);
                        ServicePlatform.get().getService().release();
                    },
                    () -> {
                        ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED);
                    }
            ).withoutPassFlavor().withTwoPassInfos().withResearchFlavor().execute();

            ServicePlatform.get().getService().release();
        });
    }
}
