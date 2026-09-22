import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterViewImpl;

/** Exercises the reported crash site without opening or modifying a saved game. */
public class TypewriterLoadCheck extends ApplicationAdapter {
    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960;
        config.height = 540;
        config.x = -10000;
        config.y = -10000;
        config.forceExit = false;
        new LwjglApplication(new TypewriterLoadCheck(), config);
    }

    @Override public void create() {
        try {
            TypewriterViewImpl view = new TypewriterViewImpl();
            java.lang.reflect.Method createTable = TypewriterViewImpl.class.getDeclaredMethod("createTable");
            createTable.setAccessible(true);
            createTable.invoke(view);
            Table first = view.getTable();
            if (first == null || first.getBackground() == null) throw new AssertionError("Missing first paper");
            createTable.invoke(view);
            if (view.getTable() == first) throw new AssertionError("New paper was not pushed");
            Class<?> data = Class.forName(
                    "sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterTableStack$TableData");
            System.out.println("PASS: TypewriterViewImpl.createTable created and stacked two papers");
            System.out.println("TableData loaded from " + data.getProtectionDomain().getCodeSource().getLocation());
        } catch (Throwable failure) {
            failure.printStackTrace();
            System.exit(1);
        } finally {
            Gdx.app.exit();
        }
    }
}
