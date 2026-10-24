package sk.sivak.eldritchhorror.core.view.components.track;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.LinkedList;
import java.util.List;

public class OmenTrackTokens extends Group {

    public final static int TOKEN_SIZE = 125;
    public final static int TOKEN_DISTANCE = 100;
    private static final float ROTATION_SPEED = 20f;
    private List<OmenTrackToken> tokens = new LinkedList<>();
    private float rotation;

    public void init(int tokensCount) {
        init(tokensCount, null);
    }

    public void init(int tokensCount, Runnable onAppear) {
        if (tokensCount == getTokensCount()) return;
        clearActions();
        if (!tokens.isEmpty()) {
            for (OmenTrackToken token : tokens) {
                token.clearActions();
                token.addAction(new FastForwardAction<>(Actions.alpha(0f, 0.5f, Interpolation.linear)));
            }
            // Rebuild and announce once for the group, not once for every fading token.
            addAction(Actions.sequence(new FastForwardAction<>(Actions.delay(0.5f)),
                    Actions.run(() -> replaceTokens(tokensCount, onAppear))));
        } else {
            replaceTokens(tokensCount, onAppear);
        }
    }

    private void replaceTokens(int count, Runnable onAppear) {
        clearChildren();
        tokens.clear();
        justInit(count);
        if (onAppear != null) onAppear.run();
    }

    public void justInit(int tokensCount) {
        for (int i = 0; i < tokensCount; i++) {
            tokens.add(createOmenTrackToken(i, tokensCount));
        }

        for (OmenTrackToken token : tokens) {
            addActor(token);
        }
    }

    private OmenTrackToken createOmenTrackToken(int index, int tokensCount) {
        OmenTrackToken omenTrackToken = new OmenTrackToken();
        omenTrackToken.setWidth(TOKEN_SIZE);
        omenTrackToken.setHeight(TOKEN_SIZE);

        float x = MathUtils.cosDeg((360 / tokensCount) * index);
        float y = MathUtils.sinDeg((360 / tokensCount) * index);

        omenTrackToken.setPosition(x * TOKEN_DISTANCE, y * TOKEN_DISTANCE);

        omenTrackToken.setScale(3f);
        omenTrackToken.getColor().a = 0f;
        omenTrackToken.setOrigin(omenTrackToken.getWidth() / 2, omenTrackToken.getHeight() / 2);
        omenTrackToken.addAction(Actions.parallel(
                Actions.scaleTo(1f, 1f, 1f, Interpolation.swingOut),
                Actions.alpha(1f, 1f, Interpolation.linear)
        ));
        return omenTrackToken;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        rotation += ROTATION_SPEED * delta;
        if (rotation > 360) {
            rotation -= 360;
        }
        for (int i = 0; i < tokens.size(); i++) {
            actTokenIndex(i, tokens.size());
        }

    }

    private void actTokenIndex(int index, int tokensCount) {
        OmenTrackToken omenTrackToken = tokens.get(index);
        float x = MathUtils.cosDeg((360 / tokensCount) * index + rotation);
        float y = MathUtils.sinDeg((360 / tokensCount) * index + rotation);

        omenTrackToken.setPosition(x * TOKEN_DISTANCE, y * TOKEN_DISTANCE);

    }

    public int getTokensCount() {
        if (tokens == null) {
            return 0;
        }
        return tokens.size();
    }

    private static class OmenTrackToken extends Image {
        public OmenTrackToken() {
            super(CustomAssetManager.getTexture(CustomAssetManager.GATE));
        }
    }
}
