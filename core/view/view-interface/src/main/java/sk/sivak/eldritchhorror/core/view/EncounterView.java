package sk.sivak.eldritchhorror.core.view;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;

import java.util.Collection;

public interface EncounterView {
    Single<String> selectEncounter(Collection<EncounterButtonData> encounterButtonDataList);
}
