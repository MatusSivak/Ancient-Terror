package sk.sivak.eldritchhorror.core.action.impl.typewriter;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

public class TypeFlavorAction implements Action<Object, Void> {

    private final String flavor;

    public TypeFlavorAction(String flavor) {
        this.flavor = flavor;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getTypewriterController().typeFlavor(flavor).subscribe(() -> onSub.onSuccess(null));
        });
    }

    @Override
    public void setInput(Object input) {
        //
    }
}
