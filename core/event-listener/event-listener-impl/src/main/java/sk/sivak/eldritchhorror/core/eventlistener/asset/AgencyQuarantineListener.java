package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.AgencyQuarantineAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class AgencyQuarantineListener extends AbstractAssetListener<AgencyQuarantineAsset> {

    private static final Logger logger = LogManager.getLogger(AgencyQuarantineListener.class);

    @Override
    protected void register() {
        ServicePlatform.get().getService().addEventCommand(this::askQuestion);
    }



    @Override
    public boolean gainCard() {
        return false;
    }

    private void askQuestion(Object input) {
        if (ServicePlatform.get().getMonsterCup().getMonsters().isEmpty()) {
            onNoMonsters(input);
            return;
        }
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
        showCardRequest.setAssetInfo(getAssetInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        showCardRequest.setTitle("Choose a space");
        ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(result -> selectLocation(input));
    }

    private void onNoMonsters(Object input) {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
        showCardRequest.setAssetInfo(getAssetInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        showCardRequest.setTitle("There are no Monsters");
        ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(result -> discard(input));
    }

    private void selectLocation(Object input) {
        List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
        List<LocationId> locations = new LinkedList<>(Stream.map(monsters, MonsterInfo::getCurrentLocation));
        ServicePlatform.get().getGameService().selectLocation(new SelectLocationData(locations))
                .subscribe(locationId -> attackMonsters(locationId, input));
    }

    private void attackMonsters(LocationId locationId, Object input) {
        List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(locationId);
        ServicePlatform.get().getService().hold();
        for (MonsterInfo monsterInfo : monstersAtLocation) {
            ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo, 4);
        }
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
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
