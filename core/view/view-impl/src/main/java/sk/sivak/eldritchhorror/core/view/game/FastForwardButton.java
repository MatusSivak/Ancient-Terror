package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.AD_MOB;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FAST_FORWARD_DOWN;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FAST_FORWARD_DiSABLED;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FAST_FORWARD_UP;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureRegionDrawable;

public class FastForwardButton extends ImageButton {

    private boolean typewriterTyping;

    public FastForwardButton() {
        super(getTextureRegionDrawable(FAST_FORWARD_UP),
                getTextureRegionDrawable(FAST_FORWARD_DOWN),
                getTextureRegionDrawable(FAST_FORWARD_DOWN));
        setSize(VIEWPORT_HEIGHT * 0.1f,VIEWPORT_HEIGHT * 0.1f);
        setPosition(VIEWPORT_WIDTH - 5 - getWidth(), 5);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!isAnythingToFastForward()) {
            return;
        }
        onEnoughEnergy(delta);
    }

    private void onEnoughEnergy(float delta) {
        if (isPressedOrButtonIsDown()) {
            setChecked(true);
            FastForwardAction.turnOn();
        } else {
            setChecked(false);
            FastForwardAction.turnOff();
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    private boolean isPressedOrButtonIsDown() {
        return isPressed() || Gdx.input.isKeyPressed(Input.Keys.F);
    }


    private Map<FastForwardAction<? extends Action>, Long> fastForwardActions = new HashMap<>();

    public <T extends Action> void registerNewAction(FastForwardAction<T> fastForwardAction) {
        fastForwardActions.put(fastForwardAction, System.currentTimeMillis());
        clearActions();
        getColor().set(1f,1f,1f,1f);
    }

    public <T extends Action> void updateActionActivity(FastForwardAction<T> fastForwardAction) {
        fastForwardActions.put(fastForwardAction, System.currentTimeMillis());
    }

    public <T extends Action> void unregisterAction(FastForwardAction<T> fastForwardAction) {
        fastForwardActions.remove(fastForwardAction);

        if (!isAnythingToFastForward()) {
            addAction(Actions.alpha(0.0f, 0.25f));
        }
    }

    private boolean isAnythingToFastForward() {
        // action actor == null ???
        return !fastForwardActions.isEmpty() || typewriterTyping;
    }

    public void setTypewriterTyping(boolean typewriterTyping) {
        this.typewriterTyping = typewriterTyping;
    }
}
