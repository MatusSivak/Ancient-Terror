package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchWilderness12Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchWilderness12Encounter() {
        super(12, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0, this::gainThisClue, () -> {
            ServicePlatform.get().getService().hold();
            List<? extends CardInfo> investigatorAllies = findInvestigatorAllies();
            if (!investigatorAllies.isEmpty()) {
                selectAllyToDiscard(investigatorAllies);
            }
            ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.GOAT_SPAWN).subscribe();
            ServicePlatform.get().getService().release();
        }).withTwoFailInfos().withResearchFlavor().execute();
    }

    private List<? extends CardInfo> findInvestigatorAllies() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ALLY);
    }

    private void selectAllyToDiscard(List<? extends CardInfo> investigatorAllies) {
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(investigatorAllies);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Ally");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
    }
}
