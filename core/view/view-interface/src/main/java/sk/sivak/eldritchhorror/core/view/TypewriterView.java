package sk.sivak.eldritchhorror.core.view;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.firebase.HallOfFameData;

public interface TypewriterView {
    Completable showPaper(boolean newPaper);

    Completable finishPaper();

    Completable hidePaper();

    Completable typeHeader(String header);

    Completable typeFlavor(String flavor);

    Completable typeInfo(String info);

    Single<Integer> displayButtons(String... buttonTexts);

    Single<String> readInput(String label);

    void recordHallOfFame(HallOfFameData hallOfFameData);
}
