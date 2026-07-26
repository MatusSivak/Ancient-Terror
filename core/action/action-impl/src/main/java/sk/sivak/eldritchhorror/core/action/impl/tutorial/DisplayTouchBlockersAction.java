package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tutorial.TouchBlockerData;
import sk.sivak.eldritchhorror.core.eventtype.data.tutorial.CompositeTouchBlockerData;

public class DisplayTouchBlockersAction implements Action<Object, Object> {

    private final CompositeTouchBlockerData compositeTouchBlockerData;

    public DisplayTouchBlockersAction(CompositeTouchBlockerData compositeTouchBlockerData) {
        this.compositeTouchBlockerData = compositeTouchBlockerData;
    }

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getInitGameController().clearTouchBlocker(
                    toBlockerTarget(compositeTouchBlockerData.getBlockerTarget()));
            for (CompositeTouchBlockerData.TouchBlockerData touchBlockerData : compositeTouchBlockerData.getTouchBlockerDataList()) {
                execute(touchBlockerData, compositeTouchBlockerData.getBlockerTarget());
                onSub.onSuccess("OK");
            }
        });
    }

    private void execute(CompositeTouchBlockerData.TouchBlockerData sourceData, CompositeTouchBlockerData.BlockerTarget blockerTarget) {
        TouchBlockerData targetData = new TouchBlockerData(
                toRectangle(sourceData.getBlockerArea()), toBlockerTarget(blockerTarget));
        targetData.setClickthrough(sourceData.isClickthrough());
        targetData.setSemiTransparent(sourceData.isSemiTransparent());
        if (sourceData.getWindowArea() != null) {
            targetData.setWindowArea(toRectangle(sourceData.getWindowArea()));
        }
        ServicePlatform.get().getInitGameController().displayTouchBlocker(targetData);
    }

    private static TouchBlockerData.Rectangle toRectangle(CompositeTouchBlockerData.Rectangle rectangle) {
        return new TouchBlockerData.Rectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    private static TouchBlockerData.BlockerTarget toBlockerTarget(CompositeTouchBlockerData.BlockerTarget blockerTarget) {
        return TouchBlockerData.BlockerTarget.valueOf(blockerTarget.name());
    }

    @Override
    public void setInput(Object input) {

    }
}
