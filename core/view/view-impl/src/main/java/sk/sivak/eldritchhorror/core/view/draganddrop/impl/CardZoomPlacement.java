package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/** Places a centered stage-level copy over a card inside scaled or scrolled groups. */
final class CardZoomPlacement {
    final float x, y, scaleX, scaleY;

    CardZoomPlacement(Actor original, float width, float height, float originX, float originY) {
        Vector2 corner = original.localToStageCoordinates(new Vector2());
        Vector2 right = original.localToStageCoordinates(new Vector2(width, 0));
        Vector2 top = original.localToStageCoordinates(new Vector2(0, height));
        Vector2 origin = original.localToStageCoordinates(new Vector2(originX, originY));
        x = origin.x - originX;
        y = origin.y - originY;
        scaleX = right.dst(corner) / width;
        scaleY = top.dst(corner) / height;
    }
}
