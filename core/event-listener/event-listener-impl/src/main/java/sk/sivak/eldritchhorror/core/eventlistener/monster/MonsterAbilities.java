package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Shared building blocks for monster abilities (movement, area damage, discards, resistances).
 */
final class MonsterAbilities {

    private MonsterAbilities() {
    }

    static List<? extends InvestigatorRead> onBoardInvestigators() {
        return ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
    }

    /** Nearest space (from the given one) containing an investigator, or null if nobody is on the board. */
    static FindNearestData findNearestInvestigator(LocationId from) {
        List<? extends InvestigatorRead> investigators = onBoardInvestigators();
        if (investigators.isEmpty() || from == null) {
            return null;
        }
        return ServicePlatform.get().getLocationMap().findNearest(from, locationInfo -> {
            for (InvestigatorRead investigator : investigators) {
                if (investigator.getCurrentLocationId() == locationInfo.getLocationId()) {
                    return true;
                }
            }
            return false;
        });
    }

    /** Space reached after moving at most {@code steps} spaces along the path to {@code nearest}. */
    static LocationId stepsToward(FindNearestData nearest, int steps) {
        if (nearest.getDistance() <= steps) {
            return nearest.getLocationId();
        }
        return nearest.getPreviousLocations().get(steps);
    }

    static InvestigatorId firstInvestigatorAt(LocationId locationId) {
        for (InvestigatorRead investigator : onBoardInvestigators()) {
            if (investigator.getCurrentLocationId() == locationId) {
                return investigator.getInfo().getInvestigatorId();
            }
        }
        return null;
    }

    static List<InvestigatorId> investigatorsAt(Collection<LocationId> locations) {
        List<InvestigatorId> result = new LinkedList<>();
        for (InvestigatorRead investigator : onBoardInvestigators()) {
            if (locations.contains(investigator.getCurrentLocationId())) {
                result.add(investigator.getInfo().getInvestigatorId());
            }
        }
        return result;
    }

    static List<LocationId> withAdjacent(LocationId locationId) {
        List<LocationId> locations = new LinkedList<>();
        locations.add(locationId);
        for (LocationInfo.Connection connection : ServicePlatform.get().getLocationMap().getLocationInfo(locationId).getConnections()) {
            locations.add(connection.getLocationId());
        }
        return locations;
    }

    static void announceReckoning(MonsterInfo monsterInfo) {
        ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
        ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
        ServicePlatform.get().getMonsterService().highlightReckoningText(monsterInfo);
    }

    static void moveMonster(MonsterInfo monsterInfo, LocationId target) {
        if (target != null && target != monsterInfo.getCurrentLocation()) {
            ServicePlatform.get().getMonsterService().moveMonster(monsterInfo, target);
        }
    }

    static void makeActive(InvestigatorId investigatorId) {
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
    }

    /** Each listed investigator loses the given Health and Sanity. */
    static void damageEach(List<InvestigatorId> investigators, int health, int sanity) {
        for (InvestigatorId investigatorId : investigators) {
            makeActive(investigatorId);
            if (health > 0) {
                ServicePlatform.get().getTokenService().loseHealth(health);
            }
            if (sanity > 0) {
                ServicePlatform.get().getTokenService().loseSanity(sanity);
            }
        }
    }

    /** Restores the investigator that was active before a spawn effect changed it. */
    static void restoreActive(InvestigatorId previouslyActive) {
        if (previouslyActive == null) {
            ServicePlatform.get().getInvestigatorService().unsetActiveInvestigator();
        } else {
            makeActive(previouslyActive);
        }
    }

    static boolean isCombatTest() {
        return ServicePlatform.get().getTestFlavor() != null
                && ServicePlatform.get().getTestFlavor().getFlavorType() == TestFlavorType.COMBAT;
    }

    static int currentStat(InvestigatorRead investigator, Stat stat) {
        return investigator.getInfo().getBaseStat(stat) + investigator.getStatBonus(stat);
    }

    /** Rolls one die; on a 1 or 2 runs {@code onLowRoll}. */
    static void rollDieOneOrTwo(Runnable onLowRoll) {
        RollData rollData = new RollData();
        rollData.setMinSuccessful(3);
        rollData.setMaxFailed(2);
        ServicePlatform.get().getTestService().rollDie(rollData).subscribe(value -> {
            if (value <= 2) {
                ServicePlatform.get().getService().hold();
                onLowRoll.run();
                ServicePlatform.get().getService().release();
            }
        });
    }

    /**
     * Lets the active investigator choose one of {@code cards} and discards it; {@code afterDiscard}
     * is queued once the discard is resolved (also when there is nothing to discard).
     */
    static void discardOne(List<? extends CardInfo> cards, String title, Runnable afterDiscard) {
        if (cards.isEmpty()) {
            afterDiscard.run();
            return;
        }
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(cards);
        selectCardData.setTitleText(title);
        selectCardData.setHideText("Display Inventory?");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(cardInfo -> {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            if (cardInfo instanceof AssetInfo) {
                showCardRequest.setAssetInfo((AssetInfo) cardInfo);
            } else if (cardInfo instanceof ArtifactInfo) {
                showCardRequest.setArtifactInfo((ArtifactInfo) cardInfo);
            } else if (cardInfo instanceof SpellInfo) {
                showCardRequest.setSpellInfo((SpellInfo) cardInfo);
            }
            showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
            showCardRequest.setTitle("Discard " + cardInfo.getName());
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(response -> {
                InvestigatorId investigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
                ServicePlatform.get().getService().hold();
                if (cardInfo instanceof AssetInfo) {
                    ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, (AssetInfo) cardInfo, true);
                } else if (cardInfo instanceof ArtifactInfo) {
                    ServicePlatform.get().getService().discardArtifactFromInvestigator(investigatorId, (ArtifactInfo) cardInfo, true);
                } else if (cardInfo instanceof SpellInfo) {
                    ServicePlatform.get().getService().discardSpellFromInvestigator(investigatorId, (SpellInfo) cardInfo);
                }
                afterDiscard.run();
                ServicePlatform.get().getService().release();
            });
        });
    }

    static List<? extends CardInfo> possessions(AssetTrait trait) {
        return EncounterUtils.getPossession(ServicePlatform.get().getInvestigators().getActiveInvestigatorId(), trait);
    }

    /** Defeats the monster mid-combat and skips the rest of the combat (same flow as Riot). */
    static void defeatDuringCombat(MonsterInfo monsterInfo, CombatData combatData) {
        ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.AFTER_DAMAGE_CHECK, combatData);
        ServicePlatform.get().getService().startInsertingAfterCommand(BeforeAfterEvent.HIDE_COMBAT_TABLE);
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getMonsterService().defeatMonster(monsterInfo, true, false);
        ServicePlatform.get().getService().convertTo(Void.class, () -> null);
        ServicePlatform.get().getService().skipBeforeEvent(BeforeAfterEvent.END_OF_COMBAT_EVENT, null);
        ServicePlatform.get().getService().release();
        ServicePlatform.get().getService().endInsertingAtCommand();
    }

    /** Physical or Magical Resistance applied to the Strength test against this monster. */
    static class ResistanceListener extends EventListenerImpl<TestData> {
        private final MonsterInfo monsterInfo;
        private final boolean magical;

        ResistanceListener(MonsterInfo monsterInfo, boolean magical) {
            this.monsterInfo = monsterInfo;
            this.magical = magical;
        }

        @Override
        public void onNotify(TestData eventData) {
            if (!isCombatTest()) {
                return;
            }
            CombatData combatData = ServicePlatform.get().getTestFlavor().getFlavorData();
            if (combatData.getMonsterInfo() != monsterInfo || eventData.getStat() != Stat.STRENGTH) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            if (magical) {
                eventData.removeAllMagicalUsableAssets();
            } else {
                eventData.removeAllNonMagicalUsableAssets();
            }
            ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
