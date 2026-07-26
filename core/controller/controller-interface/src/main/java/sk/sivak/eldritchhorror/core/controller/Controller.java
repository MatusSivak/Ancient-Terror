package sk.sivak.eldritchhorror.core.controller;

import rx.Completable;
import rx.Observable;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

/**
 * @author msivak
 */
public interface Controller {

    Observable<Boolean> getAnswer(String question);
}
