package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.model.AssetDeckRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class GreenMythosCard6 implements MythosCardEventListener{

    @Override
    public void execute() {
        AssetDeckRead assetDeck = ServicePlatform.get().getAssetDeck();
        List<? extends InvestigatorRead> relevantInvestigators = Stream.collectToList(
                ServicePlatform.get().getInvestigators().getOnBoardInvestigators(),
                investigator -> assetDeck.hasAsset(investigator.getInfo().getInvestigatorId(), AssetTrait.ALLY));
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead relevantInvestigator : relevantInvestigators) {
            InvestigatorId investigatorId = relevantInvestigator.getInfo().getInvestigatorId();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            ServicePlatform.get().getTokenService().loseHealth(3);

            List<? extends CardInfo> investigatorAllies = findInvestigatorAllies(investigatorId);
            SelectCardData selectCardData = new SelectCardData();
            selectCardData.setAvailableCards(investigatorAllies);
            selectCardData.setHideText("Display Allies?");
            selectCardData.setTitleText("Select Ally");
            ServicePlatform.get().getCardService().selectSingleCard(selectCardData)
                    .subscribe(EncounterUtils::onSelectCardToDiscard);
        }
        ServicePlatform.get().getService().release();

    }

    private List<? extends CardInfo> findInvestigatorAllies(InvestigatorId investigatorId) {
        return EncounterUtils.getPossession(investigatorId, AssetTrait.ALLY);
    }

}
