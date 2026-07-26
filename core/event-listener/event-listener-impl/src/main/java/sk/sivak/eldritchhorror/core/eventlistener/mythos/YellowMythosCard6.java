package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

public class YellowMythosCard6 implements MythosCardEventListener{

    @Override
    public void execute() {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            RollData rollData = new RollData();
            rollData.setMaxFailed(2);
            rollData.setMinSuccessful(3);
            ServicePlatform.get().getTestService().rollDie(rollData).subscribe(rolledValue -> {
                if (rolledValue < 3) {
                    ServicePlatform.get().getService().hold();
                    travelToBermudaTriangle();
                    becomeDelayed(investigator);
                    ServicePlatform.get().getService().release();
                }
            });
        }
        ServicePlatform.get().getService().release();
    }

    private void becomeDelayed(InvestigatorRead investigator) {
        ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(investigator.getInfo().getInvestigatorId(), true));
    }

    private void travelToBermudaTriangle() {
        ServicePlatform.get().getBasicActionService().travelToLocation(new LocationInfo.Connection() {
            @Override
            public LocationId getLocationId() {
                return LocationId.SPACE_8;
            }

            @Override
            public PathType getPathType() {
                return PathType.WALK;
            }
        });
    }
}
