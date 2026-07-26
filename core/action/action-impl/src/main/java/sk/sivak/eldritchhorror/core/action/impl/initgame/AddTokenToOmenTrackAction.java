package sk.sivak.eldritchhorror.core.action.impl.initgame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

/**
 * @author msivak
 */
public class AddTokenToOmenTrackAction implements Action<OmenId, Void> {

    private static final Logger logger = LogManager.getLogger(AddTokenToOmenTrackAction.class);

    private OmenId input;

    @Override
    public Single<Void> execute() {
        return Single.create(sub -> {
            ServicePlatform.get().getOmenTrack().addTokenToOmen(input);
            if (!GoogleServicesHolder.isTutorialPassed()) {
                sub.onSuccess(null);
                return;
            }
            ServicePlatform.get().getDoomOmenController().addTokenToOmenTrack(input).subscribe(() -> {
                sub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(OmenId input) {
        this.input = input;
    }
}
