package sk.sivak.eldritchhorror.core.view.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.view.EncounterView;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.CameraActor;
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

    @Override
    public Completable waitForCameraToStop() {
        return Completable.create(onSub -> Gdx.app.postRunnable(() -> {
            if (MapStage.getStage() == null) {
                onSub.onCompleted();
                return;
            }
            for (Actor actor : MapStage.getStage().getActors()) {
                if (actor instanceof CameraActor) {
                    ((CameraActor) actor).afterMovement(onSub::onCompleted);
                    return;
                }
            }
            onSub.onCompleted();
        }));
    }
}
