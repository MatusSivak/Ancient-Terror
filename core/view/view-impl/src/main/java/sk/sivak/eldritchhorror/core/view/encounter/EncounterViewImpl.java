package sk.sivak.eldritchhorror.core.view.encounter;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.view.EncounterView;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.encounter.SelectEncounterTable;

import java.util.Collection;

public class EncounterViewImpl implements EncounterView {


    @Override
    public Single<String> selectEncounter(Collection<EncounterButtonData> encounterButtonDataList) {
        return Single.create(onSub -> {
            BigActorsManager.initEncounterTable(onSub, encounterButtonDataList);
            BigActorsManager.displayOrHideEncounterTable();
        });
    }
}
