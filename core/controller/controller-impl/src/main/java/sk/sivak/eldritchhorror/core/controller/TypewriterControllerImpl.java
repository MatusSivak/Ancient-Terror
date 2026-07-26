package sk.sivak.eldritchhorror.core.controller;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.firebase.HallOfFameData;
import sk.sivak.eldritchhorror.core.view.TypewriterView;

public class TypewriterControllerImpl implements TypewriterController{

    private TypewriterView typewriterView;

    public void setTypewriterView(TypewriterView typewriterView) {
        this.typewriterView = typewriterView;
    }

    @Override
    public Completable showPaper(boolean newPaper) {
        return typewriterView.showPaper(newPaper);
    }

    @Override
    public Completable finishPaper() {
        return typewriterView.finishPaper();
    }

    @Override
    public Completable hidePaper() {
        return typewriterView.hidePaper();
    }

    @Override
    public Completable typeHeader(String header) {
        return typewriterView.typeHeader(header);
    }

    @Override
    public Completable typeFlavor(String flavor) {
        return typewriterView.typeFlavor(flavor);
    }

    @Override
    public Completable typeInfo(String info) {
        return typewriterView.typeInfo(info);
    }

    @Override
    public Single<Integer> displayButtons(String[] buttonTexts) {
        return typewriterView.displayButtons(buttonTexts);
    }

    @Override
    public Single<String> readInput(String label) {
        return typewriterView.readInput(label);
    }

    @Override
    public void recordHallOfFame(HallOfFameData hallOfFameData) {
        typewriterView.recordHallOfFame(hallOfFameData);
    }
}
