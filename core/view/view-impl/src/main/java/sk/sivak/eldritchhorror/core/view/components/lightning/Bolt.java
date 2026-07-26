package sk.sivak.eldritchhorror.core.view.components.lightning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.viewport.Viewport;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

import java.util.List;
import java.util.UUID;

public class Bolt extends Group {

    private final List<BoltSegment> boltSegments;
    private final UUID uuid;

    public Bolt(List<BoltSegment> boltSegments) {
        this.boltSegments = boltSegments;
        for (BoltSegment boltSegment : boltSegments) {
            addActor(boltSegment);
        }
        uuid = UUID.randomUUID();
    }

    public Vector2 getStart() {
        return boltSegments.get(0).getStart();
    }

    public Vector2 getEnd() {
        return boltSegments.get(boltSegments.size()-1).getEnd();
    }

    /*
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (textureRegion == null) {
            long start1 = System.currentTimeMillis();
            Viewport viewport;
            if (getStage() == null) {
                viewport = InfoStage.getStageSafe().getViewport();
            } else {
                viewport = getStage().getViewport();
            }

            FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,
                    viewport.getScreenWidth(),
                    viewport.getScreenHeight(), true);
            frameBuffer.begin();
            Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            super.draw(batch, parentAlpha);
            frameBuffer.end(
                    viewport.getLeftGutterWidth(),
                    viewport.getBottomGutterHeight(),
                    viewport.getScreenWidth(),
                    viewport.getScreenHeight());
            textureRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
            textureRegion.flip(false, true);
            System.out.println("Duration1: " + (System.currentTimeMillis() - start1));
        }
        batch.draw(textureRegion,
                0,
                0,
                ViewProperties.VIEWPORT_WIDTH,
                ViewProperties.VIEWPORT_HEIGHT);

    }
    */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bolt bolt = (Bolt) o;

        return uuid != null ? uuid.equals(bolt.uuid) : bolt.uuid == null;
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }

    public UUID getUuid() {
        return uuid;
    }
}
