package sk.sivak.eldritchhorror.core.action.impl.typewriter;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

public class HideTypewriterPaperAction implements Action<Object, Void> {
    private final boolean forDiceCheck;

    public HideTypewriterPaperAction() { this(false); }

    public HideTypewriterPaperAction(boolean forDiceCheck) { this.forDiceCheck = forDiceCheck; }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            (forDiceCheck ? ServicePlatform.get().getTypewriterController().hidePaper(true)
                    : ServicePlatform.get().getTypewriterController().hidePaper())
                    .subscribe(() -> onSub.onSuccess(null), onSub::onError);
        });
    }

    @Override
    public void setInput(Object input) {
        //
    }
}
