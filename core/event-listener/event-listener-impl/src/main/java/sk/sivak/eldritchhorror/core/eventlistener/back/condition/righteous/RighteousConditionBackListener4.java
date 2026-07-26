package sk.sivak.eldritchhorror.core.eventlistener.back.condition.righteous;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.SelectSingleGateData;

import java.util.List;

public class RighteousConditionBackListener4 extends AbstractConditionBackListener {

    public RighteousConditionBackListener4(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    public void executeWhole() {
        ShowCardRequest request = new ShowCardRequest();
        request.setHideType(HideType.RETURN);
        request.setConditionInfo(getConditionInfo());
        request.setTitle("Flip " + getConditionInfo().getName() +" Condition?");
        request.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
        ServicePlatform.get().getGameService().showCard(request).subscribe(this::onAnswer);
    }

    private void onAnswer(ShowCardResponse showCardResponse) {
        if (showCardResponse != ShowCardResponse.YES) {
            return;
        }
        onFlip(showCardResponse);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos("[#GOOD]One Monster of your choice loses five Health[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            selectSingleMonster();
            ServicePlatform.get().getService().release();
        });
    }

    private void selectSingleMonster() {
        SelectMonsterData selectMonsterData = new SelectMonsterData();
        List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
        selectMonsterData.setAvailableMonsters(monsters);
        ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(this::onMonsterSelect);
    }

    private void onMonsterSelect(MonsterInfo monsterInfo) {
        if (monsterInfo == null) {
            return;
        }
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
        ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo, 3);
        ServicePlatform.get().getService().moveCameraToLocation(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());
        ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
        TypewriterUtils.confirmInfos("[#BAD]Discard this card[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            discardThisCard();
            ServicePlatform.get().getService().release();
        });

        ServicePlatform.get().getService().release();
    }

    private void discardThisCard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(getConditionInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard Righteous Condition");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), getConditionInfo());
        });
    }
}
