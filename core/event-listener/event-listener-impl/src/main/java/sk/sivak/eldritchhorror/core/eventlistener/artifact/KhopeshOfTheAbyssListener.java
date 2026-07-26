package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import java8.features.util.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.artifact.KhopeshOfTheAbyssArtifact;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class KhopeshOfTheAbyssListener extends AbstractArtifactListener<KhopeshOfTheAbyssArtifact> {

    private static final Logger logger = LogManager.getLogger(KhopeshOfTheAbyssListener.class);
    private RegisterUsableAssetListener registerUsableAssetListener;
    private EnableCardListener enableCardListener;
    private AfterDefeatMonsterListener afterDefeatMonsterListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(registerUsableAssetListener, enableCardListener, afterDefeatMonsterListener);
    }

    @Override
    protected void register() {
        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);

        enableCardListener = new EnableCardListener(getArtifactInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);

        afterDefeatMonsterListener = new AfterDefeatMonsterListener();
        getEventQueue().addAfterEventListener(afterDefeatMonsterListener, BeforeAfterEvent.DEFEAT_MONSTER);
    }

    private class RegisterUsableAssetListener extends AbstractArtifactEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(KhopeshOfTheAbyssListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                    return;
                }
                addUsableArtifact(input, getArtifactInfo(), 5);
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class AfterDefeatMonsterListener extends AbstractArtifactEventListener<DefeatMonsterData> {

        AfterDefeatMonsterListener() {
            super(KhopeshOfTheAbyssListener.this);
        }

        @Override
        protected Runnable getEventAction(DefeatMonsterData input) {
            return () -> {
                if (!enabled) {
                    return;
                }
                if (!input.isInCombat()) {
                    return;
                }

                if (ServicePlatform.get().getMonsterCup().getMonsters().isEmpty()) {
                    return;
                }

                Set<LocationId> closestLocations = findClosestLocations();
                LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
                if (closestLocations.size() == 1 && closestLocations.iterator().next() == currentLocationId) {
                    return;
                }

                ServicePlatform.get().getGameService().showCard(new ShowCardRequestBuilder(getArtifactInfo())
                        .withTitle("Move to the nearest Monster?")
                        .withAssetHideType(HideType.DISABLE_ON_YES)
                        .withDisplayAssetResponseType(DisplayAssetResponseType.YES_NO)
                        .build())
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input, closestLocations));
            };
        }

        private void onAnswer(ShowCardResponse response, DefeatMonsterData input, Set<LocationId> closestLocations) {
            if (response == ShowCardResponse.YES) {
                SelectLocationData selectLocationData = new SelectLocationData(new LinkedList<>(closestLocations));
                ServicePlatform.get().getGameService().selectLocation(selectLocationData).subscribe(locationId ->
                        onLocationSelected(locationId, input));

            } else {
                ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> input);
            }
        }

        private void onLocationSelected(LocationId locationId, DefeatMonsterData input) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().disableCard(getArtifactInfo());
            ServicePlatform.get().getEncounterService().disableAnotherEncounter(getActiveInvestigatorId());
            ServicePlatform.get().getInvestigatorService().removeInvestigatorFromPlace(getActiveInvestigatorId());
            ServicePlatform.get().getInvestigatorService().spawnInvestigator(getActiveInvestigatorId(), locationId);
            ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> input);
            ServicePlatform.get().getService().release();
        }


        @Override
        public Class<DefeatMonsterData> getDataClass() {
            return DefeatMonsterData.class;
        }

        private Set<LocationId> findClosestLocations() {
            TreeMap<Integer, List<MonsterInfo>> map = new TreeMap<>();

            LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
            for (MonsterInfo monster : ServicePlatform.get().getMonsterCup().getMonsters()) {
                FindNearestData findNearestData = ServicePlatform.get().getLocationMap().findNearest(currentLocationId,
                        locationInfo -> locationInfo.getLocationId() == monster.getCurrentLocation()
                );
                MapUtils.computeIfAbsent(map, findNearestData.getDistance(), key -> new LinkedList<>());
                map.get(findNearestData.getDistance()).add(monster);
            }


            List<MonsterInfo> closestMonsters = map.values().iterator().next();
            Set<LocationId> closestLocations = new HashSet<>();
            for (MonsterInfo closestMonster : closestMonsters) {
                closestLocations.add(closestMonster.getCurrentLocation());
            }
            return closestLocations;
        }
    }
}
