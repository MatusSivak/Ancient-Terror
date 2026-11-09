package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.assetmanager.MonsterArtwork;

/** A monster waiting on the floor behind the active opponent. */
final class BackgroundMonster extends Image {
    static final float EPIC_SIZE = 134f;
    private final boolean epic;
    private final float centerX;
    private final float floorY;
    private final CombatGroundShadow shadow = new CombatGroundShadow();
    private float phase = MathUtils.random(MathUtils.PI2);

    BackgroundMonster(MonsterInfo monster, float centerX, float floorY) {
        super(CustomAssetManager.getTexture(MonsterArtwork.path(monster.getClass().getSimpleName(), monster.isEpic())));
        epic = monster.isEpic();
        this.centerX = centerX;
        this.floorY = floorY;
        float size = epic ? EPIC_SIZE : 108f;
        setSize(size, size);
        setScaling(Scaling.fit);
        validate();
        setOrigin(getWidth() / 2f, getImageY());
        setTouchable(Touchable.disabled);
        setColor(0.52f, 0.52f, 0.55f, 0f);
        setScaleX(MonsterArtwork.facesRight(monster.getClass().getSimpleName(), epic) ? -1f : 1f);
        act(0f);
    }

    boolean isEpic() {
        return epic;
    }

    CombatGroundShadow getShadow() {
        return shadow;
    }

    @Override
    public boolean remove() {
        shadow.remove();
        return super.remove();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        phase = (phase + delta * 0.8f) % MathUtils.PI2;
        float footX = centerX + 1.8f * MathUtils.sin(phase);
        setPosition(footX - getOriginX(), floorY - getOriginY());
        shadow.setVisible(isVisible());
        shadow.place(footX, floorY + getImageHeight() * 0.025f, getImageWidth() * 0.9f,
                getHeight() * 0.13f, getColor().a * 0.44f);
    }
}
