package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.List;

public class YellowMythosCard8 implements MythosCardEventListener{

    @Override
    public void execute() {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
            if (ServicePlatform.get().getLocationMap().getLocationInfo(investigator.getCurrentLocationId()).getLocationType() != LocationType.CITY) {
                continue;
            }
            List<? extends CardInfo> weapons = EncounterUtils.getPossession(investigatorId, AssetTrait.WEAPON);
            if (weapons.isEmpty()) {
                continue;
            }
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            ServicePlatform.get().getTestService().test(Stat.INFLUENCE, 0,1).subscribe(x -> {
                if (x.isSuccessful()) {
                    return;
                }
                ServicePlatform.get().getService().hold();
                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setAvailableCards(weapons);
                selectCardData.setHideText("Display Inventory?");
                selectCardData.setTitleText("Select Weapon");
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
                ServicePlatform.get().getGameService().gainCondition(ConditionId.DETAINED);
                ServicePlatform.get().getService().release();
            });
        }
        ServicePlatform.get().getService().release();
    }

}
