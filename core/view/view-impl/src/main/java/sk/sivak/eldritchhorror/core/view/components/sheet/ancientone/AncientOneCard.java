package sk.sivak.eldritchhorror.core.view.components.sheet.ancientone;

import com.badlogic.gdx.scenes.scene2d.Actor;
import rx.Completable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;

public interface AncientOneCard {
    void init(AncientOneInfo ancientOneInfo);
    Actor getActor();
    void setAfterHideAction(Action0 afterHideAction);
    void displayOrHide();
    void setBeforeDisplayAction(Action0 beforeDisplayAction);
    Completable increaseAncientOnePower(int increment);

    void setAfterDisplayAction(Action0 input);
}
