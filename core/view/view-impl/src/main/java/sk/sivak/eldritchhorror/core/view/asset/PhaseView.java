package sk.sivak.eldritchhorror.core.view.asset;

import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.view.components.phase.PhaseLabel;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.map.MapUtils;
import sk.sivak.eldritchhorror.core.view.map.helper.MoveCameraToLocationHelper;
import sk.sivak.eldritchhorror.core.view.music.NewMusicBox;
import sk.sivak.eldritchhorror.core.view.utils.GameplaySounds;

/**
 * @author msivak
 */
public class PhaseView {

    public Completable showPhase(PhaseType input) {
        if (!GoogleServicesHolder.isTutorialPassed()) {
            MoveCameraToLocationHelper.setEnabled(true);
        }
        PhaseLabel phaseLabel = null;
        switch (input) {
            case ACTION:
                phaseLabel = PhaseLabel.buildActionPhaseLabel();
                NewMusicBox.getInstance().changeMusic(NewMusicBox.TRACK_CITY_OF_DREAD);
                break;
            case ENCOUNTER:
                phaseLabel = PhaseLabel.buildEncounterPhaseLabel();
                NewMusicBox.getInstance().changeMusic(NewMusicBox.TRACK_HORROR_GAME_INTRO);
                break;
            case MYTHOS:
                phaseLabel = PhaseLabel.buildMythosPhaseLabel();
                break;
            default:
                throw new IllegalArgumentException();
        }
        InfoStage.addSmallActorToInfoStage(phaseLabel);
        Completable display = phaseLabel.display();
        if (input == PhaseType.MYTHOS) {
            display = GameplaySounds.withCue(GameplaySounds.Cue.MYTHOS_PHASE, display);
        }
        return MapUtils.showWholeWorld()
                .andThen(display)
                .andThen(HudButtons.getInstance().show());
    }
}
