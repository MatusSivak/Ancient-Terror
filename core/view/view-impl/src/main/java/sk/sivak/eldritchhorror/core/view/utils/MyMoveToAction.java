package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import java8.features.function.Supplier;
import sk.sivak.eldritchhorror.core.view.map.helper.DistanceHelper;

public class MyMoveToAction extends MoveToAction {
    private final Supplier<Vector2> randomPointSupplier;
    private final Vector2 initActionPosition;
    private Vector2 previousLocation;
    private final float scale;
    private float velocity;

    public MyMoveToAction(Vector2 currentPosition, Supplier<Vector2> randomPointSupplier, float velocity, float scale) {
        this.randomPointSupplier = randomPointSupplier;
        this.initActionPosition = currentPosition;
        this.velocity = velocity;
        this.previousLocation = currentPosition;
        this.scale = scale;
        init();
    }

    @Override
    public void restart() {
        super.restart();
        init();
    }

    private void init() {
        if (Gdx.app.getPreferences("AncientTerror.xml").getBoolean("strobe_disabled")) {
            if (getActor() == null) {
                return;
            }
            getActor().setScale(1f);
            setPosition(initActionPosition.x, initActionPosition.y);
            float duration = (float) DistanceHelper.getDistance(previousLocation, initActionPosition) / velocity;
            setDuration(duration);
            previousLocation = initActionPosition;
            return;
        } else if (getActor() != null) {
            getActor().setScale(scale);
        }
        Vector2 nextPosition = randomPointSupplier.get();
        setPosition(nextPosition.x, nextPosition.y);
        setInterpolation(Interpolation.sine);
        float duration = (float) DistanceHelper.getDistance(previousLocation, nextPosition) / velocity;
        setDuration(duration);
        previousLocation = nextPosition;
    }
}