package sk.sivak.eldritchhorror.core.view.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;

import java.util.LinkedList;
import java.util.List;

import static java8.features.util.IterableUtils.forEach;

/**
 * @author msivak
 */
public class FpsLogger extends Actor {

    private List<Integer> fpsList;

    public FpsLogger() {
        fpsList = new LinkedList<>();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (ViewProperties.FPS_LOG_ENABLED) {
            fpsList.add(Gdx.graphics.getFramesPerSecond());
        }

    }

    public void print() {
        FileHandle fpsLog = Gdx.files.local("fps.log");
        fpsLog.writeString("", false);
        forEach(fpsList, el -> fpsLog.writeString("" + el + "\n", true));
    }
}
