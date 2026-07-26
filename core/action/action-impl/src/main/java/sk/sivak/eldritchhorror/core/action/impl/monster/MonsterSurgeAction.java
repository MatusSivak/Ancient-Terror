package sk.sivak.eldritchhorror.core.action.impl.monster;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;

import java.util.Collections;
import java.util.List;

public class MonsterSurgeAction extends AbstractHookableAction<Object, Void> {

    private final int monstersCount;

    public MonsterSurgeAction(int monstersCount) {
        super(BeforeAfterEvent.MONSTER_SURGE);
        this.monstersCount = monstersCount;
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        OmenInfo currentOmen = ServicePlatform.get().getOmenTrack().getCurrentOmen();
        List<LocationId> gateLocations = ServicePlatform.get().getGateStack()
                .getSpawnedGates(currentOmen.getOmenColor().toGateColor());
        Collections.reverse(gateLocations);

        if (gateLocations.isEmpty()) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getGameService().spawnGates(1, 1).subscribe();
            ServicePlatform.get().getService().convertTo(Void.class, () -> null);
            ServicePlatform.get().getService().release();
        } else {
            ServicePlatform.get().getService().hold();
            for (LocationId gateLocation : gateLocations) {
                ServicePlatform.get().getService().moveCameraToLocation(gateLocation);
                ServicePlatform.get().getGameService().spawnMonsters(monstersCount, gateLocation, false).subscribe();
            }
            ServicePlatform.get().getService().convertTo(Void.class, () -> null);
            ServicePlatform.get().getService().release();
        }
        ss.onSuccess(null);

    }
}
