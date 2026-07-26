package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchWilderness5Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchWilderness5Encounter() {
        super(5, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
            new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1, () -> {
                ServicePlatform.get().getService().hold();
                gainThisClue();
                ServicePlatform.get().getTokenService().gainClueFromPool();
                ServicePlatform.get().getService().release();
            }, () -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().hideBackground();
                ServicePlatform.get().getGameService().spawnGates(1,1).subscribe();
                ServicePlatform.get().getService().release();
            }).withoutPassFlavor().withTwoPassInfos().withResearchFlavor().execute();
    }
}
