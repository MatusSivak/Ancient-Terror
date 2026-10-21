package sk.sivak.eldritchhorror.core.action.impl.typewriter;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

public class ShowTypewriterPaperAction implements Action<Object, Void> {

    private boolean newPaper;
    private sk.sivak.eldritchhorror.core.constants.card.CardInfo card;

    public ShowTypewriterPaperAction(sk.sivak.eldritchhorror.core.constants.card.CardInfo card) {
        this.newPaper = true;
        this.card = card;
    }

    public ShowTypewriterPaperAction(boolean newPaper) {
        this.newPaper = newPaper;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            rx.Completable presentation = card == null
                    ? ServicePlatform.get().getTypewriterController().showPaper(newPaper)
                    : ServicePlatform.get().getTypewriterController().showCardResolutionPaper(card);
            presentation.subscribe(() -> onSub.onSuccess(null), onSub::onError);
        });
    }

    @Override
    public void setInput(Object input) {
        //
    }
}
