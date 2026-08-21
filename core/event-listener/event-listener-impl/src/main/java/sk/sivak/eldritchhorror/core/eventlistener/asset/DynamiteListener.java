package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.CardInfoAware;
import sk.sivak.eldritchhorror.core.constants.asset.DynamiteAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class DynamiteListener extends AbstractAssetListener<DynamiteAsset> {

    private static final Logger logger = LogManager.getLogger(DynamiteListener.class);
    private ActionListener actionListener;

    @Override
    protected void register() {
        actionListener = new ActionListener();
        getEventQueue().addBeforeEventListener(actionListener, BeforeAfterEvent.FIND_ACTIONS);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(actionListener);
    }

    private class ActionListener extends AbstractActionPhaseListener<ActionListener.DynamiteAction> {

        public ActionListener() {
            name = "Dynamite";
        }

        @Override
        protected String getAdditionalInfo() {
            if (isNotRecommended()) {
                return null;
            }
            List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(getActiveInvestigator().getCurrentLocationId());
            StringBuilder sb = new StringBuilder();
            for (MonsterInfo monsterAtLocation : monstersAtLocation) {
                sb
                        .append(monsterAtLocation.getName())
                        .append(" (")
                        .append(monsterAtLocation.getCurrentHealth())
                        .append(")")
                        .append("\n");
            }
            return sb.toString();
        }

        @Override
        protected String getGeneralDescription() {
            return "You may discard this card\n" +
                    "to cause each Monster\n" +
                    "on your space\n" +
                    "to lose 3 Health.";
        }

        @Override
        protected String getTexturePath() {
            return "card/asset/DYNAMITE.jpg";
        }

        @Override
        protected float getScaleDownPercentage() {
            return 1.0f;
        }

        @Override
        protected boolean getNeedsMask() {
            return true;
        }

        @Override
        protected DynamiteAction createAction() {
            return new DynamiteAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == investigatorId;
        }

        @Override
        protected boolean isDisabled() {
            return false;
        }

        @Override
        protected boolean isNotRecommended() {
            if (!ServicePlatform.get().getMonsterCup().containsMonster(getActiveInvestigator().getCurrentLocationId())) {
                notRecommendedReason = "No Monsters on this space.";
                return true;
            }
            return false;
        }

        protected class DynamiteAction extends AbstractActionPhaseAction implements CardInfoAware<DynamiteAsset> {

            @Override
            public void execute() {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
                showCardRequest.setTitle("Use Dynamite");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    useDynamite();
                });
            }

            private void useDynamite() {

                LocationId currentLocationId = getActiveInvestigator().getCurrentLocationId();
                List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(currentLocationId);

                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
                for (MonsterInfo monsterInfo : monstersAtLocation) {
                    ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo, 3);
                }
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            }

            @Override
            public DynamiteAsset getCardInfo() {
                return getAssetInfo();
            }
        }
    }
}
