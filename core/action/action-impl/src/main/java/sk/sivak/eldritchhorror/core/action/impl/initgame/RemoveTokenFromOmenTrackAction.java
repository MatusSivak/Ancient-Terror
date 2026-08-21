package sk.sivak.eldritchhorror.core.action.impl.initgame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.question.Question;

/**
 * @author msivak
 */
public class RemoveTokenFromOmenTrackAction implements Action<Object, Void> {

    private static final Logger logger = LogManager.getLogger(RemoveTokenFromOmenTrackAction.class);
    private final OmenId omenId;

    public RemoveTokenFromOmenTrackAction(OmenId omenId) {
        this.omenId = omenId;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(sub -> {
            if (ServicePlatform.get().getOmenTrack().getOmenInfo(omenId).getTokensCount() == 0) {
                Question<Object> question = new Question<>();
                question.setOptions(Question.Option.okOption);
                question.setTitle("There are no Orbs.");
                ServicePlatform.get().getGameService().ask(question).subscribe(x -> {

                });
                sub.onSuccess(null);
                return;
            }
            ServicePlatform.get().getOmenTrack().removeTokenFromOmen(omenId);
            ServicePlatform.get().getDoomOmenController().removeTokenFromOmenTrack(omenId).subscribe(() -> {
                sub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {
    }
}
