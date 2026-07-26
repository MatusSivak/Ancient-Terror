package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.gate.Gate;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.List;

public interface GateStackRead {
    boolean isGateAtLocation(LocationId locationId);

    GateColor getGateColor(LocationId locationId);

    List<LocationId> getSpawnedGatesLocations();

    List<GateInfo> getSpawnedGates();

    List<LocationId> getSpawnedGates(GateColor gateColor);

    List<GateInfo> getTopTwoGates();

    GateInfo getTopGate();
}
