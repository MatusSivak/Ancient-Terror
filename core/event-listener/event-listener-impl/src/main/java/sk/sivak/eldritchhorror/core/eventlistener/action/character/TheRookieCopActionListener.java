package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class TheRookieCopActionListener extends AbstractActionPhaseListener<TheRookieCopActionListener.TheRookieCopAction> {

    private static final Logger logger = LogManager.getLogger(TheRookieCopActionListener.class);

    public TheRookieCopActionListener() {
        name = "Move\nMonster";
    }

    @Override
    protected String getAdditionalInfo() {
        if (isDisabled()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (MonsterInfo monsterAtLocation : getNearbyMonsters()) {
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
        return "Move 1 Monster\n" +
                "of your choice\n" +
                "with toughness 3 or less\n" +
                "from an adjacent space\n" +
                "to your space.";
    }

    @Override
    protected TheRookieCopActionListener.TheRookieCopAction createAction() {
        return new TheRookieCopActionListener.TheRookieCopAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_ROOKIE_COP;
    }

    @Override
    protected boolean isDisabled() {
        if (getNearbyMonsters().isEmpty()) {
            disabledReason = "No Monsters nearby.";
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
        return "investigator/ICON_THE_ROOKIE_COP.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheRookieCopAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing the rookie cop action...");
            SelectMonsterData selectMonsterData = new SelectMonsterData();
            selectMonsterData.setAvailableMonsters(getNearbyMonsters());
            ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(this::moveSelectedMonster);
        }

        private void moveSelectedMonster(MonsterInfo monsterInfo) {
            LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().moveMonster(monsterInfo, currentLocationId);
            ServicePlatform.get().getService().convertToNull();
            ServicePlatform.get().getService().release();
        }
    }

    private List<MonsterInfo> getNearbyMonsters() {
        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        List<LocationInfo.Connection> currentLocationConnections = ServicePlatform.get().getLocationMap().getLocationInfo(currentLocationId).getConnections();
        Collection<LocationId> nearbyLocations = Stream.map(currentLocationConnections, LocationInfo.Connection::getLocationId);
        List<MonsterInfo> nearbyMonsters = new LinkedList<>();
        for (LocationId nearbyLocation : nearbyLocations) {
            nearbyMonsters.addAll(ServicePlatform.get().getMonsterCup().getMonstersAtLocation(nearbyLocation));
        }

        IterableUtils.removeIf(nearbyMonsters, MonsterInfo::isEpic);
        IterableUtils.removeIf(nearbyMonsters, monsterInfo -> monsterInfo.getToughness() > 3);
        return nearbyMonsters;

    }
}
