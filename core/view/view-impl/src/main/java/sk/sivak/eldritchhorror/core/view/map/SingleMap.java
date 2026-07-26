package sk.sivak.eldritchhorror.core.view.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import rx.Emitter;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;

/**
 * @author msivak
 */
public class SingleMap extends Image {

    public SingleMap(Texture texture) {
        super(texture);
    }
}
