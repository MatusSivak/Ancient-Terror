package sk.sivak.eldritchhorror.core.view.map.vortex;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.utils.Array;
import sk.sivak.eldritchhorror.core.view.animation.AnimatedImage;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import java.util.LinkedList;
import java.util.List;

public class VortexImage extends AnimatedImage {

    public static final int SIZE = 225;

    private VortexImage(Array<TextureRegion> keyFrames) {
        super(new Animation<>(0.04f, keyFrames, Animation.PlayMode.LOOP));
        setOrigin(SIZE/2f, SIZE/2f);

        setColor(new Color(0x46a0aaff));
        addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                Actions.color(new Color(0x68a2ffff), 0.75f, Interpolation.sine),
                Actions.color(new Color(0x46a0aaff), 0.75f, Interpolation.sine)
        )));
    }

    public static VortexImage build() {
        Texture texture = CustomAssetManager.getTexture("map/vortex_spritesheet.png");
        List<TextureRegion> textureRegions = new LinkedList<>();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 6; x++) {
                textureRegions.add(new TextureRegion(texture, SIZE * x, SIZE*y, SIZE, SIZE));
            }
        }
        return new VortexImage(new Array<>(textureRegions.toArray(new TextureRegion[0])));
    }
}
