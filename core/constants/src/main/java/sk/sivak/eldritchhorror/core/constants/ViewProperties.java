package sk.sivak.eldritchhorror.core.constants;

import java8.features.util.Objects;

/**
 * @author msivak
 */
public class ViewProperties {

    public static final int VIEWPORT_WIDTH = 960;
    public static final int VIEWPORT_HEIGHT = 540;

    public static final int MAP_WIDTH = 3000;
    public static final int MAP_HEIGHT = 1500;
    public static final float MAP_ALLOWED_OFFSET = 9 * MAP_WIDTH / 16f - MAP_HEIGHT;
    public static final int ZOOMED_MIN_VIEWPORT_HEIGHT = (int) (VIEWPORT_HEIGHT * 0.75f);

    public static final int MONSTER_SIZE = 70;
    public static final int EPIC_MONSTER_SIZE = (int) (MONSTER_SIZE * 1.25f);
    public static final float MONSTER_SPEED = 30;
    public static final float HUD_HEIGHT_RATIO = 0.10f;
    public static final float FADING_EFFECT_DURATION = 0.5f;
    public static final boolean FPS_LOG_ENABLED = false;
    public static final String MODE_TEST = "TEST";
    public static final String MODE_PROD = "PROD";
//    public static final String MODE = MODE_TEST;
    public static final String MODE = MODE_PROD;
    public static final float FONT_FOR_HEIGHT = 1080;
    public static int CAMERA_MOVE_SPEED;
    public static float CAMERA_ZOOM_SPEED;
    public static float NORMAL_ACTION_DURATION;
    public static float FAST_ACTION_DURATION;
    public static float ZOOM_TO_WORLD_DURATION;

    static {
        if (Objects.equals(MODE_PROD, MODE)) {
            CAMERA_MOVE_SPEED = 250;
            CAMERA_ZOOM_SPEED = 0.75f;
            NORMAL_ACTION_DURATION = 1.0f;
            ZOOM_TO_WORLD_DURATION = 1f;
        } else if (Objects.equals(MODE_TEST, MODE)) {
            CAMERA_MOVE_SPEED = 10000;
            CAMERA_ZOOM_SPEED = 40f;
            NORMAL_ACTION_DURATION = 1.0f;
            ZOOM_TO_WORLD_DURATION = 0.25f;
        }
        FAST_ACTION_DURATION = NORMAL_ACTION_DURATION / 2;
    }
}
