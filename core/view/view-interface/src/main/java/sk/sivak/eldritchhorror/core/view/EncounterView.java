package sk.sivak.eldritchhorror.core.view;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;

import java.util.Collection;

public interface EncounterView {
    Single<String> selectEncounter(Collection<EncounterButtonData> encounterButtonDataList);

    /** Completes once the map camera has fully stopped moving (immediately when it is still). */
    Completable waitForCameraToStop();
}
