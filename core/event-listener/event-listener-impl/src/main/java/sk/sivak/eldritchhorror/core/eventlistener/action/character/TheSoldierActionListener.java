package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;

import java.util.List;

/**
 * @author msivak
 */
public class TheSoldierActionListener extends AbstractActionPhaseListener<TheSoldierActionListener.TheSoldierAction> {

    private static final Logger logger = LogManager.getLogger(TheSoldierActionListener.class);

    public TheSoldierActionListener() {
        name = "Attack\nMonster";
    }

    @Override
    protected String getAdditionalInfo() {
        if (isDisabled()) {
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
        return "You and 1 Monster on your space each lose 1 Health";
    }

    @Override
    protected TheSoldierAction createAction() {
        return new TheSoldierAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_SOLDIER;
    }

    @Override
    protected boolean isDisabled() {
        List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(getActiveInvestigator().getCurrentLocationId());
        if (monstersAtLocation == null || monstersAtLocation.isEmpty()) {
            disabledReason = "There are no Monsters here.";
            return true;
        }
        return false;
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.INVESTIGATOR;
    }

    @Override
    protected String getTexturePath() {
        return "investigator/THE_SOLDIER.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheSoldierAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing soldier action...");
            LocationId currentLocationId = getActiveInvestigator().getCurrentLocationId();
            List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(currentLocationId);
            SelectMonsterData selectMonsterData = new SelectMonsterData();
            selectMonsterData.setAvailableMonsters(monstersAtLocation);
            selectMonsterData.setHideText("Display Monsters?");
            selectMonsterData.setTitleText("Select Monster");
            ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(this::onMonsterSelect);
        }

        private void onMonsterSelect(MonsterInfo monsterInfo) {
            ServicePlatform.get().getService().hold();
            if (monsterInfo == null) {
                ServicePlatform.get().getTokenService().loseHealth(1);
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
                return;
            }
            ServicePlatform.get().getTokenService().loseHealth(1);
            ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo,1);
            ServicePlatform.get().getService().convertToNull();
            ServicePlatform.get().getService().release();
        }

    }
}
