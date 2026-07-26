package sk.sivak.eldritchhorror.core.controller;

import rx.Completable;
import rx.Observable;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.model.ModelRead;
import sk.sivak.eldritchhorror.core.view.View;

/**
 * @author msivak
 */
public class ControllerImpl implements Controller {

    private View view;
    private ModelRead model;

    public void setView(View view) {
        this.view = view;
    }

    public void setModel(ModelRead model) {
        this.model = model;
    }

    @Override
    public Observable<Boolean> getAnswer(String question) {
        return null;
    }
}
