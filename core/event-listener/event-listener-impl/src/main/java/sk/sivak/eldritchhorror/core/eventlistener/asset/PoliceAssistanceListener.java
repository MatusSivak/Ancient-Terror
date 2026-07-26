package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.PoliceAssistanceAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class PoliceAssistanceListener extends AbstractAssetListener<PoliceAssistanceAsset> {

    private static final Logger logger = LogManager.getLogger(PoliceAssistanceListener.class);

    @Override
    protected void register() {
        ServicePlatform.get().getService().addEventCommand((input) -> {
            askQuestion(input);
        });
    }

    @Override
    public boolean gainCard() {
        return false;
    }

    private void askQuestion(Object input) {
        List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
        if (monsters.isEmpty()) {
            onNoMonsters(input);
            return;
        }
        boolean monsterExists = Stream.anyMatch(monsters, monsterInfo -> monsterInfo.getToughness() <= 2);
        if (!monsterExists) {
            onNoMonsters(input);
            return;
        }
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
        showCardRequest.setAssetInfo(getAssetInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        showCardRequest.setTitle("Discard Monster");
        ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(result -> selectMonster(input));
    }

    private void onNoMonsters(Object input) {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
        showCardRequest.setAssetInfo(getAssetInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        showCardRequest.setTitle("There are no Monsters with Toughness 2 or less.");
        ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(result -> discard(input));
    }

    private void selectMonster(Object input) {
        List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
        monsters = Stream.collectToList(monsters, monsterInfo -> monsterInfo.getToughness() <= 2 && !monsterInfo.isEpic());

        SelectMonsterData selectMonsterData = new SelectMonsterData();
        selectMonsterData.setTitleText("Select Monster");
        selectMonsterData.setHideText("Display Monsters?");
        selectMonsterData.setAvailableMonsters(monsters);

        ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData)
                .subscribe(monsterInfo -> onMonsterSelect(monsterInfo, input));
    }

    private void onMonsterSelect(MonsterInfo monsterInfo, Object input) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getMonsterService().discardMonster(monsterInfo);
        if (ServicePlatform.get().getPerformedActions().canPerformAction(investigatorId)) {
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        }
        discard(input);
        ServicePlatform.get().getService().release();
    }


    private void discard(Object input) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
        ServicePlatform.get().getService().convertTo(Object.class, () -> input);
        ServicePlatform.get().getService().release();
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.emptyList();
    }
}
