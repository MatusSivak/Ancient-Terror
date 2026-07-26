package sk.sivak.eldritchhorror.core.action.impl.typewriter;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

public class ShowTypewriterPaperAction implements Action<Object, Void> {

    private boolean newPaper;

    public ShowTypewriterPaperAction(boolean newPaper) {
        this.newPaper = newPaper;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getTypewriterController().showPaper(newPaper).subscribe(() -> onSub.onSuccess(null));
        });
    }

    @Override
    public void setInput(Object input) {
        //
    }
}
