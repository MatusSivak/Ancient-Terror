package sk.sivak.eldritchhorror.core.action.impl.typewriter;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

public class FinishTypewriterPaperAction implements Action<Object, Void> {

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getTypewriterController().finishPaper().subscribe(() -> onSub.onSuccess(null));
        });
    }

    @Override
    public void setInput(Object input) {
        //
    }
}
