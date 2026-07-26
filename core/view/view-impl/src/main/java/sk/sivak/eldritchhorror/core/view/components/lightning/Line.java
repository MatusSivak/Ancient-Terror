package sk.sivak.eldritchhorror.core.view.components.lightning;

import com.badlogic.gdx.math.Vector2;

public class Line {
    private final Vector2 pointA;
    private final Vector2 pointB;

    public Line(Vector2 pointA, Vector2 pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
    }

    public Vector2 getPointA() {
        return new Vector2(pointA);
    }

    public Vector2 getPointB() {
        return new Vector2(pointB);
    }
}
