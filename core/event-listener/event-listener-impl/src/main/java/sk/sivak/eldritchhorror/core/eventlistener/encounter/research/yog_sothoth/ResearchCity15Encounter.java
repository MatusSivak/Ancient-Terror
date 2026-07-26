package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchCity15Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchCity15Encounter() {
        super(15, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -1,
                this::gainThisClue
        ).withResearchFlavor().execute();
    }

}
