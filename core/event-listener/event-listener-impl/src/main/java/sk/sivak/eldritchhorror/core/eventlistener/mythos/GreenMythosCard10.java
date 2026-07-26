package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.SelectSingleGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

public class GreenMythosCard10 implements MythosCardEventListener{

    @Override
    public void execute() {
        List<GateInfo> spawnedGates = ServicePlatform.get().getGateStackRead().getSpawnedGates();
        OmenColor omenColor = ServicePlatform.get().getOmenTrack().getCurrentOmen().getOmenColor();
        ServicePlatform.get().getGameService().selectSingleGate(new SelectSingleGateData(spawnedGates)).subscribe(selectedGate -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().discardGate(selectedGate.getLocationId());
            if (!selectedGate.getGateColor().equals(omenColor.toGateColor())) {
                ServicePlatform.get().getDoomOmenService().advanceDoom();
            }
            ServicePlatform.get().getService().release();
        });

    }

}
