package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver;
import sk.sivak.eldritchhorror.core.view.map.MapUtils;
import sk.sivak.eldritchhorror.core.view.map.clue.ClueUtils;
import sk.sivak.eldritchhorror.core.view.map.expedition.ExpeditionUtils;
import sk.sivak.eldritchhorror.core.view.map.gate.GateUtils;
import sk.sivak.eldritchhorror.core.view.map.gate.NewGateAnimatedImage;
import sk.sivak.eldritchhorror.core.view.map.helper.MoveCameraToLocationHelper;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorUtils;
import sk.sivak.eldritchhorror.core.view.map.monster.MonsterUtils;
import sk.sivak.eldritchhorror.core.view.map.redpin.RedPinUtils;
import sk.sivak.eldritchhorror.core.view.map.storm.StormImageUtils;
import sk.sivak.eldritchhorror.core.view.map.vortex.VortexImageUtils;
import sk.sivak.eldritchhorror.core.view.utils.BackgroundUtils;

import java.util.List;

/**
 * @author msivak
 */
public class PrepareBoardView {

    private GameController gameController;
    private BackgroundUtils backgroundUtils;

    public PrepareBoardView(GameController gameController) {
        this.gameController = gameController;
    }

    public Completable confirmExpeditionLocation(LocationId locationId) {
        return Completable.create(onSub -> {
            InfoStage.displayText("Active Expedition");
            MapUtils.moveCameraToLocation(locationId).subscribe(() -> {
                ExpeditionUtils.spawnExpedition(locationId).subscribe(onSub::onCompleted);
            });
        });
    }

    void justPlaceExpeditionToken(LocationId locationId) {
        ExpeditionUtils.spawnExpedition(locationId).subscribe();
    }


    public Completable discardExpeditionToken() {
        return ExpeditionUtils.discardExpeditionToken();
    }

    public Completable confirmClueLocation(LocationId spawnLocationId, LocationId currentLocationId) {
        return backgroundUtils.hideBackground().andThen(
                Completable.create(onSub -> {
                    InfoStage.displayText("Spawning Clues");
                    MapUtils.moveCameraToLocation(currentLocationId).subscribe(() -> {
                        ClueUtils.spawnClue(spawnLocationId, currentLocationId).subscribe(onSub::onCompleted);
                    });
                })
        );
    }

    public void justSpawnClue(LocationId spawnLocationId, LocationId currentLocationId) {
        ClueUtils.spawnClue(spawnLocationId, currentLocationId).subscribe();
    }

    public Completable confirmGateSpawn(GateInfo gateInfo, OmenColor omenColor) {
        return Completable.create(onSub -> {
            InfoStage.displayText("Spawning Gates");
            MapUtils.moveCameraToLocation(gateInfo.getLocationId()).subscribe(() -> {
                GateUtils.spawnGate(gateInfo, omenColor).subscribe(onSub::onCompleted);
            });
        });
    }

    public void justPlaceGate(GateInfo gateInfo, OmenColor omenColor) {
        GateUtils.spawnGate(gateInfo, omenColor).subscribe();
    }

    public Completable confirmMonsterSpawn(MonsterInfo monsterInfo) {
        return Completable.create(onSub -> {
            MapUtils.moveCameraToLocation(monsterInfo.getCurrentLocation()).subscribe(() -> {
                MonsterUtils.spawnMonster(monsterInfo, gameController).subscribe(onSub::onCompleted);
            });
        });
    }

    public void justPlaceMonster(MonsterInfo monsterInfo) {
        MonsterUtils.spawnMonster(monsterInfo, gameController).subscribe();
    }

    public Completable initInvestigator(InvestigatorBasics selectedInvestigators) {
        return Completable.create(onSub -> {
            InfoStage.displayText("Spawning Investigators");
            MapUtils.moveCameraToLocation(selectedInvestigators.getLocationId()).subscribe(() -> {
                InvestigatorUtils.spawnInvestigator(selectedInvestigators.getLocationId(),
                        selectedInvestigators.getInvestigatorId(), gameController)
                        .subscribe(onSub::onCompleted);
            });
        });
    }


    public void justPlaceInvestigator(InvestigatorBasics selectedInvestigators) {
        InvestigatorUtils.spawnInvestigator(
                selectedInvestigators.getLocationId(), selectedInvestigators.getInvestigatorId(), gameController).subscribe();
    }

    public Completable discardClue(LocationId spawnLocationId, LocationId currentLocationId) {
        return Completable.create(onSub -> {
            InfoStage.displayText("Discarding Clue");
            MapUtils.moveCameraToLocation(currentLocationId).subscribe(() -> {
                ClueUtils.discardClue(spawnLocationId).subscribe(onSub::onCompleted);
            });
        });
    }

    public Completable showRedPinsLocation(List<LocationId> pinLocations) {
        Completable completable = Completable.complete();
        if (pinLocations == null || pinLocations.size() == 0) {
            return completable;
        }

        InfoStage.displayTextForce("Mystery Location");
        for (LocationId pinLocation : pinLocations) {
            completable = completable.andThen(MapUtils.moveCameraToLocation(pinLocation));
            completable = completable.andThen(RedPinUtils.spawnRedPin(pinLocation));
        }
        return completable;
    }

    public Completable showStormLocation(String stormId, LocationId locationId) {
        InfoStage.displayTextForce("Rumor Location");
        Completable completable = MapUtils.moveCameraToLocation(locationId);
        completable = completable.andThen(StormImageUtils.spawnStorm(stormId, locationId));
        return completable;
    }

    public void justAddRedPins(List<LocationId> pinLocations) {
        if (pinLocations == null || pinLocations.size() == 0) {
            return;
        }
        for (LocationId pinLocation : pinLocations) {
            RedPinUtils.spawnRedPin(pinLocation).subscribe();
        }
    }

    public void justSpawnStorm(String stormId, LocationId locationId) {
        StormImageUtils.spawnStorm(stormId, locationId).subscribe();
    }

    void clearRedPins(List<LocationId> pinLocations) {
        for (LocationId pinLocation : pinLocations) {
            RedPinUtils.discardRedPin(pinLocation).subscribe();
        }
    }

    public void clearStorm(String rumorId) {
        StormImageUtils.discardStorm(rumorId).subscribe();
    }

    public void setBackgroundUtils(BackgroundUtils backgroundUtils) {
        this.backgroundUtils = backgroundUtils;
    }

    public Completable closeGate(LocationId location) {
        GateUtils.GateIdLayerResolver idLayerResolver = new GateUtils.GateIdLayerResolver(location);
        List<Actor> actor = MapStage.getActor(idLayerResolver);
        ((NewGateAnimatedImage) actor.get(0)).closeGate().subscribe();
        ((NewGateAnimatedImage) actor.get(2)).closeGate().subscribe();
        Completable moveCameraToLocation = Completable.create(onSub -> {
            MoveCameraToLocationHelper.moveCameraToPosition(LocationPositionResolver.resolve(location), onSub, 0.75f);
        });
        Completable closeGate = ((NewGateAnimatedImage) actor.get(1)).closeGate();
        Completable removeActorAndSwapLayers = Completable.create(onSub -> {
            MapStage.removeActor(idLayerResolver);
            onSub.onCompleted();
        });
        return moveCameraToLocation.andThen(closeGate).andThen(removeActorAndSwapLayers);
    }

    public Completable spawnVortex(LocationId locationId) {
        InfoStage.displayTextForce("Vortex Location");
        Completable completable = MapUtils.moveCameraToLocation(locationId);
        completable = completable.andThen(VortexImageUtils.spawnVortex(locationId));
        return completable;
    }

    public void justAddVortex(LocationId locationId) {
        VortexImageUtils.spawnVortex(locationId).subscribe();
    }
}
