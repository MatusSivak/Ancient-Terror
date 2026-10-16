package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.question.Question;

public class AncientOneAwakensAction implements Action<Object, Void> {



    @Override
    public Single<Void> execute() {
        return Single.defer(() -> {
            AncientOneInfo info = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo();
            if (info.isAwaken()) return Single.just(null);
            if (info.getAncientOneId() == AncientOneId.YIG) {
                Question<Object> notice = new Question<>();
                notice.setTitle("ancientOne.yig.awakening");
                notice.setPortraitBeforeTitle(AncientOneId.YIG);
                notice.setOptions(Question.Option.okOption);
                return ServicePlatform.get().getGameController().ask(notice).flatMap(answer -> awaken());
            }
            return awaken();
        });
    }

    private Single<Void> awaken() {
        return Single.create(onSub -> {
            ServicePlatform.get().getModel().getAncientOne().awaken();
            AncientOneInfo ancientOneInfo = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo();
            if (ancientOneInfo.getAncientOneId() == AncientOneId.YIG) {
                ServicePlatform.get().getDoomTrack().initDoomTrack(ancientOneInfo.getStartingDoom());
                ServicePlatform.get().getGameController().updateDoom();
            }
            ServicePlatform.get().getEventListenerProvider().registerAncientOneListeners(ancientOneInfo);
            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
