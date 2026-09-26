package sk.sivak.eldritchhorror.core.view.firebase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;
import java.io.ByteArrayOutputStream;

public final class BugReportScreenshot {
    private BugReportScreenshot() { }

    /** Called on the render thread before the menu covers the game. */
    public static byte[] capture() {
        Pixmap source = null;
        try {
            source = ScreenUtils.getFrameBufferPixmap(0, 0,
                    Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
            for (int maxSize : new int[]{800, 640, 480, 320}) {
                float scale = Math.min(1f, maxSize / (float) Math.max(source.getWidth(), source.getHeight()));
                Pixmap thumbnail = new Pixmap(Math.max(1, (int) (source.getWidth() * scale)),
                        Math.max(1, (int) (source.getHeight() * scale)), Pixmap.Format.RGB888);
                PixmapIO.PNG encoder = new PixmapIO.PNG();
                try {
                    thumbnail.setFilter(Pixmap.Filter.BiLinear);
                    thumbnail.drawPixmap(source, 0, 0, source.getWidth(), source.getHeight(),
                            0, 0, thumbnail.getWidth(), thumbnail.getHeight());
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    encoder.setFlipY(true);
                    encoder.write(bytes, thumbnail);
                    if (bytes.size() <= BugReportPayload.MAX_SCREENSHOT_BYTES) return bytes.toByteArray();
                } finally { encoder.dispose(); thumbnail.dispose(); }
            }
        } catch (Exception e) {
            Gdx.app.log("BugReport", "Screenshot unavailable");
        } finally { if (source != null) source.dispose(); }
        return null;
    }
}
