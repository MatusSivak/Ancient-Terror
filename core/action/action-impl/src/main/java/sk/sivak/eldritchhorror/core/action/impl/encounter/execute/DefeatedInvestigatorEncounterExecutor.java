package sk.sivak.eldritchhorror.core.action.impl.encounter.execute;

import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.DefeatedInvestigatorEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DefeatedInvestigatorEncounterData;
import sk.sivak.eldritchhorror.core.model.BackgroundData;
import sk.sivak.eldritchhorror.core.model.BackgroundModelRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId.THE_ROOKIE_COP;
import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.ENCOUNTER;

public class DefeatedInvestigatorEncounterExecutor {
    public static void execute(DefeatedInvestigatorEncounter input) {
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(ENCOUNTER, "Defeated", "The " +input.getInvestigatorId().toString());
        ServicePlatform.get().getService().hold();

        InvestigatorRead defeatedInvestigator = Stream.findFirstOrException(ServicePlatform.get().getInvestigators().getDefeatedInvestigators(),
                defeated -> defeated.getInfo().getInvestigatorId() == input.getInvestigatorId());
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();


        ServicePlatform.get().getService().hideSelectEncounterTable();
        ServicePlatform.get().getService().addEventCommand(in -> {
            return Single.create(onSub -> {
                ServicePlatform.get().getGameController().showCustomBackground(input.isHealth()?"crippled" : "insane").subscribe(() -> {
                    BackgroundData backgroundData = new BackgroundData(BackgroundModelRead.BackgroundType.CUSTOM,
                            input.isHealth() ? "crippled" : "insane",
                            input.isHealth() ? "encounter/background/crippled.jpg" : "encounter/background/insane.jpg");
                    ServicePlatform.get().getModel().getBackgroundModel().pushBackground(activeInvestigatorId, backgroundData);
                    onSub.onSuccess(in);
                });
            });
        });
        DefeatedInvestigatorEncounterData data = new DefeatedInvestigatorEncounterData();
        data.setInvestigatorId(input.getInvestigatorId());
        data.setHealth(input.isHealth());
        if (!data.isHealth() && data.getInvestigatorId() == THE_ROOKIE_COP) {
            data.setTransferItemsAction(() -> {
                ServicePlatform.get().getService().hold();
                discardAssets(THE_ROOKIE_COP);
                discardArtifacts(THE_ROOKIE_COP);
                discardConditions(THE_ROOKIE_COP);
                discardSpells(THE_ROOKIE_COP);
                discardClues(THE_ROOKIE_COP);
                ServicePlatform.get().getDoomOmenService().retreatDoom();
                ServicePlatform.get().getService().release();
            });
        } else {
            data.setTransferItemsAction(() -> {
                ServicePlatform.get().getService().hold();
                transferAssets(input, activeInvestigatorId);
                transferArtifacts(input, activeInvestigatorId);
                transferSpells(input, activeInvestigatorId);
                transferClues(input);
                transferTickets(defeatedInvestigator);
                ServicePlatform.get().getService().release();
            });
        }
        ServicePlatform.get().getEventListenerProvider().executeDefeatedInvestigatorEncounter(data);
        ServicePlatform.get().getEncounterService().endOfEncounter(input);
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getEventListenerProvider().unregisterDefeatedListener(input.getInvestigatorId());
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
        ServicePlatform.get().getInvestigatorService().removeDefeatedInvestigator(input.getInvestigatorId());
        ServicePlatform.get().getEncounterService().insertDefeatSequencePoint();
        ServicePlatform.get().getService().release();
    }

    private static void transferAssets(DefeatedInvestigatorEncounter input, InvestigatorId activeInvestigatorId) {
        for (AssetInfo asset : ServicePlatform.get().getAssetsDeck().getAssets(input.getInvestigatorId())) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                return Single.create(onSub -> {
                    ServicePlatform.get().getGameController().gainCard(asset).subscribe(() -> {
                        ServicePlatform.get().getAssetsDeck().changeOwner(asset, activeInvestigatorId);
                        ServicePlatform.get().getAssetListenerProvider().getAssetListener(asset.getId()).registerWithOwner(activeInvestigatorId, asset);
                        onSub.onSuccess(null);
                    });
                });
            });
        }
    }

    private static void transferArtifacts(DefeatedInvestigatorEncounter input, InvestigatorId activeInvestigatorId) {
        for (ArtifactInfo artifactInfo : ServicePlatform.get().getArtifactsDeck().getArtifacts(input.getInvestigatorId())) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                return Single.create(onSub -> {
                    ServicePlatform.get().getGameController().gainCard(artifactInfo).subscribe(() -> {
                        ServicePlatform.get().getArtifactsDeck().changeOwner(artifactInfo, activeInvestigatorId);
                        ServicePlatform.get().getArtifactListenerProvider().getArtifactListener(artifactInfo.getId()).justLoadWithOwner(activeInvestigatorId, artifactInfo);
                        onSub.onSuccess(null);
                    });
                });
            });
        }
    }

    private static void transferSpells(DefeatedInvestigatorEncounter input, InvestigatorId activeInvestigatorId) {
        for (SpellInfo spellInfo : ServicePlatform.get().getSpellsDeck().getSpells(input.getInvestigatorId())) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                return Single.create(onSub -> {
                    ServicePlatform.get().getGameController().gainCard(spellInfo).subscribe(() -> {
                        ServicePlatform.get().getSpellsDeck().changeOwner(spellInfo, activeInvestigatorId);
                        ServicePlatform.get().getSpellListenerProvider().getSpellListener(spellInfo).register(spellInfo, activeInvestigatorId);
                        onSub.onSuccess(null);
                    });
                });
            });
        }
    }

    private static void transferClues(DefeatedInvestigatorEncounter input) {
        int clueCount = ServicePlatform.get().getCluePool().getClueCount(input.getInvestigatorId());
        for (int i = 0; i < clueCount; i++) {
            ServicePlatform.get().getCluePool().loseClue(input.getInvestigatorId());
            ServicePlatform.get().getTokenService().gainClueFromPool();
        }
    }

    private static void transferTickets(InvestigatorRead defeatedInvestigator) {
        for (int i = 0; i < defeatedInvestigator.getShipTickets(); i++) {
            ServicePlatform.get().getService().convertTo(LocationInfo.Connection.class, () -> new LocationInfo.Connection() {
                @Override
                public LocationId getLocationId() {
                    return null;
                }

                @Override
                public PathType getPathType() {
                    return PathType.SHIP;
                }
            });
            ServicePlatform.get().getBasicActionService().gainTravelTicket(PathType.SHIP);
        }
        ;

        for (int i = 0; i < defeatedInvestigator.getTrainTickets(); i++) {
            ServicePlatform.get().getService().convertTo(LocationInfo.Connection.class, () -> new LocationInfo.Connection() {
                @Override
                public LocationId getLocationId() {
                    return null;
                }

                @Override
                public PathType getPathType() {
                    return PathType.TRAIN;
                }
            });
            ServicePlatform.get().getBasicActionService().gainTravelTicket(PathType.TRAIN);
        }
        ;
    }

    private static void discardClues(InvestigatorId activeInvestigatorId) {
        int clueCount = ServicePlatform.get().getCluePool().getClueCount(activeInvestigatorId);
        for (int i = 0; i < clueCount; i++) {
            ServicePlatform.get().getCluePool().loseClue(activeInvestigatorId);
        }
    }

    private static void discardAssets(InvestigatorId activeInvestigatorId) {
        List<AssetInfo> assets = ServicePlatform.get().getAssetsDeck().getAssets(activeInvestigatorId);
        for (AssetInfo asset : assets) {
            ServicePlatform.get().getService().discardAssetFromInvestigator(activeInvestigatorId, asset, false);
        }
    }

    private static void discardArtifacts(InvestigatorId activeInvestigatorId) {
        List<ArtifactInfo> artifacts= ServicePlatform.get().getArtifactsDeck().getArtifacts(activeInvestigatorId);
        for (ArtifactInfo artifact : artifacts) {
            ServicePlatform.get().getService().discardArtifactFromInvestigator(activeInvestigatorId, artifact, true);
        }
    }

    private static void discardSpells(InvestigatorId activeInvestigatorId) {
        List<SpellInfo> spells = ServicePlatform.get().getSpellsDeck().getSpells(activeInvestigatorId);
        for (SpellInfo spell : spells) {
            ServicePlatform.get().getService().discardSpellFromInvestigator(activeInvestigatorId, spell);
        }
    }

    private static void discardConditions(InvestigatorId activeInvestigatorId) {
        List<ConditionInfo> conditions = ServicePlatform.get().getConditionsDeck().getConditions(activeInvestigatorId);
        for (ConditionInfo condition : conditions) {
            ServicePlatform.get().getService().discardConditionFromInvestigator(activeInvestigatorId, condition);
        }
    }
}
