package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;
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
import sk.sivak.eldritchhorror.core.view.utils.UiText;
import sk.sivak.eldritchhorror.core.view.utils.GameplaySounds;

import static sk.sivak.eldritchhorror.core.view.utils.GameplaySounds.Cue.*;
import static sk.sivak.eldritchhorror.core.view.utils.GameplaySounds.withCue;

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
            InfoStage.displayText(UiText.get("prepare.activeExpedition"));
            MapUtils.moveCameraToLocation(locationId).subscribe(() -> {
                withCue(EXPEDITION_SPAWN, ExpeditionUtils.spawnExpedition(locationId)).subscribe(onSub::onCompleted);
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
                    InfoStage.displayText(UiText.get("prepare.spawningClues"));
                    MapUtils.moveCameraToLocation(currentLocationId).subscribe(() -> {
                        ClueUtils.spawnClue(spawnLocationId, currentLocationId,
                                () -> GameplaySounds.play(CLUE_SPAWN)).subscribe(onSub::onCompleted);
                    });
                })
        );
    }

    public void justSpawnClue(LocationId spawnLocationId, LocationId currentLocationId) {
        ClueUtils.spawnClue(spawnLocationId, currentLocationId).subscribe();
    }

    public Completable confirmGateSpawn(GateInfo gateInfo, OmenColor omenColor) {
        return Completable.create(onSub -> {
            InfoStage.displayText(UiText.get("prepare.spawningGates"));
            MapUtils.moveCameraToLocation(gateInfo.getLocationId()).subscribe(() -> {
                GateUtils.spawnGate(gateInfo, omenColor,
                        () -> GameplaySounds.play(GATE_OPEN)).subscribe(onSub::onCompleted);
            });
        });
    }

    public void justPlaceGate(GateInfo gateInfo, OmenColor omenColor) {
        GateUtils.spawnGate(gateInfo, omenColor).subscribe();
    }

    public Completable confirmMonsterSpawn(MonsterInfo monsterInfo) {
        return Completable.create(onSub -> {
            MapUtils.moveCameraToLocation(monsterInfo.getCurrentLocation()).subscribe(() -> {
                MonsterUtils.spawnMonster(monsterInfo, gameController,
                        () -> GameplaySounds.play(monsterInfo.isEpic() ? EPIC_MONSTER_SPAWN : MONSTER_SPAWN))
                        .subscribe(onSub::onCompleted);
            });
        });
    }

    public void justPlaceMonster(MonsterInfo monsterInfo) {
        MonsterUtils.spawnMonster(monsterInfo, gameController).subscribe();
    }

    public Completable initInvestigator(InvestigatorBasics selectedInvestigators) {
        return Completable.create(onSub -> {
            InfoStage.displayText(UiText.get("prepare.spawningInvestigators"));
            MapUtils.moveCameraToLocation(selectedInvestigators.getLocationId()).subscribe(() -> {
                InvestigatorUtils.spawnInvestigator(selectedInvestigators.getLocationId(),
                        selectedInvestigators.getInvestigatorId(), gameController,
                        () -> GameplaySounds.play(INVESTIGATOR_SPAWN))
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
            InfoStage.displayText(UiText.get("prepare.discardingClue"));
            MapUtils.moveCameraToLocation(currentLocationId).subscribe(() -> {
                withCue(RESOURCE_SPEND, ClueUtils.discardClue(spawnLocationId)).subscribe(onSub::onCompleted);
            });
        });
    }

    public Completable showRedPinsLocation(List<LocationId> pinLocations) {
        Completable completable = Completable.complete();
        if (pinLocations == null || pinLocations.size() == 0) {
            return completable;
        }

        InfoStage.displayTextForce(UiText.get("prepare.mysteryLocation"));
        for (LocationId pinLocation : pinLocations) {
            completable = completable.andThen(MapUtils.moveCameraToLocation(pinLocation));
            completable = completable.andThen(RedPinUtils.spawnRedPin(pinLocation));
        }
        return completable;
    }

    public Completable showStormLocation(String stormId, LocationId locationId) {
        InfoStage.displayTextForce(UiText.get("prepare.rumorLocation"));
        Completable completable = MapUtils.moveCameraToLocation(locationId);
        completable = completable.andThen(withCue(HAZARD_SPAWN, StormImageUtils.spawnStorm(stormId, locationId)));
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
        Completable moveCameraToLocation = Completable.create(onSub -> {
            // Frame the whole halo before the closing effect brings it forward.
            MoveCameraToLocationHelper.moveCameraToPosition(LocationPositionResolver.resolve(location), onSub, 1.25f);
        });
        Completable closeGate = Completable.defer(() -> {
            MapStage.getCamera().update();
            List<Actor> copies = MapStage.getActor(idLayerResolver);
            Actor visibleGate = null;
            float nearest = Float.MAX_VALUE;
            for (Actor copy : copies) {
                if (copy.getStage() == null) continue;
                Vector2 center = copy.localToStageCoordinates(new Vector2(copy.getWidth() / 2f, copy.getHeight() / 2f));
                float distance = center.dst2(MapStage.getCamera().position.x, MapStage.getCamera().position.y);
                if (distance < nearest) {
                    nearest = distance;
                    visibleGate = copy;
                }
            }
            for (Actor copy : copies) {
                if (copy != visibleGate) copy.remove();
            }
            return visibleGate == null ? Completable.complete() : ((NewGateAnimatedImage) visibleGate).closeGate();
        });
        Completable removeActorAndSwapLayers = Completable.create(onSub -> {
            MapStage.removeActor(idLayerResolver);
            onSub.onCompleted();
        });
        return moveCameraToLocation.andThen(withCue(GATE_CLOSE, closeGate)).andThen(removeActorAndSwapLayers);
    }

    public Completable spawnVortex(LocationId locationId) {
        InfoStage.displayTextForce(UiText.get("prepare.vortexLocation"));
        Completable completable = MapUtils.moveCameraToLocation(locationId);
        completable = completable.andThen(withCue(HAZARD_SPAWN, VortexImageUtils.spawnVortex(locationId)));
        return completable;
    }

    public void justAddVortex(LocationId locationId) {
        VortexImageUtils.spawnVortex(locationId).subscribe();
    }
}
