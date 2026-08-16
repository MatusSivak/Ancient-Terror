package sk.sivak.eldritchhorror;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import sk.sivak.eldritchhorror.core.Game;
import sk.sivak.eldritchhorror.core.constants.tracker.DummyPurchaseManager;

public class GameDesktop {

    public static void main(String[] args) {
        for (String arg : args) {
            if ("--grid-test-prototype".equals(arg)) {
                System.setProperty("ancientterror.gridtest.prototype", "true");
            } else if (arg.startsWith("--grid-test-moves=")) {
                System.setProperty("ancientterror.gridtest.moves", arg.substring("--grid-test-moves=".length()));
            }
        }
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        // Simulate mobile screen on external monitor
//        config.width = 480;
//        config.height = 270;

        // Simulate mobile screen on laptop monitor
        		config.width = 640;
        		config.height = 360;

//        config.width = 1280;
//        config.height = 720;

        config.width = 960;
        config.height = 540;

//        config.width = 1340;
//        config.height = 540;

//        config.width = 1920;
//        config.height = 1080;

//        config.width = Float.valueOf(1600 * 0.95f).intValue();
//        config.height = Float.valueOf(900 * 0.95f).intValue();

//        almost fullscreen
        config.width = (int)(1920 * 0.9f);
        config.height = (int)(1080 * 0.9f);

//        Xiaomi Mi A2 Lite
//        config.width = (int) (2280 * 0.75f);
//        config.height = (int) (1080 * 0.75f);

//        some old phone
//        config.width = 800;
//		  config.height = 480;

//        config.width = 1500;
//        config.height = 600;

//		config.vSyncEnabled = false; // Setting to false disables vertical sync
//		config.foregroundFPS = 0; // Setting to 0 disables foreground fps throttling
//		config.backgroundFPS = 0; // Setting to 0 disables background fps throttling


//        config.fullscreen = true;

//		config.width = 800;
//		config.height = 480;

//		config.width = 640;
//		config.height = 480;

        Game game = new Game();
        game.setPurchaseManager(new DummyPurchaseManager());
        new LwjglApplication(game, config);
    }
}
