package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.assetmanager.MonsterArtwork;
import sk.sivak.eldritchhorror.core.view.map.monster.MonsterFacing;

/** A bystander walks a short loop across the far part of the combat scene. */
final class BackgroundMonster extends Image {
    private final boolean epic;
    private final MonsterFacing facing;
    private final float centerX;
    private final float floorY = MathUtils.random(100f, 118f);
    private static final float PATROL_DEPTH = 5f;
    private final float radius = MathUtils.random(18f, 27f);
    private final float pace = MathUtils.random(0.32f, 0.46f);
    private final CombatGroundShadow shadow = new CombatGroundShadow();
    private float phase = MathUtils.random(MathUtils.PI2);
    private float step;

    BackgroundMonster(MonsterInfo monster, int index, int count) {
        super(CustomAssetManager.getTexture(MonsterArtwork.path(monster.getClass().getSimpleName(), monster.isEpic())));
        epic = monster.isEpic();
        facing = new MonsterFacing(MonsterArtwork.facesRight(monster.getClass().getSimpleName(), epic));
        facing.face(MathUtils.cos(phase) >= 0f, false);
        float size = epic ? 124f : 96f;
        setSize(size, size);
        setScaling(Scaling.fit);
        validate();
        setOrigin(getWidth() / 2f, getImageY());
        setTouchable(Touchable.disabled);
        setColor(0.52f, 0.52f, 0.55f, 0f);
        // Space out patrols so a crowd does not emerge from a single point.
        float margin = size / 2f + radius + 8f;
        float slot = count == 1 ? 0.5f : index / (count - 1f);
        centerX = MathUtils.lerp(MonsterCombatTable.MONSTER_SIDE_LEFT + margin,
                MonsterCombatTable.SAFE_RIGHT - margin, slot);
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
        float previousX = MathUtils.sin(phase) * radius;
        float previousY = MathUtils.cos(phase) * PATROL_DEPTH;
        phase = (phase + delta * pace) % MathUtils.PI2;
        float x = MathUtils.sin(phase) * radius;
        float y = MathUtils.cos(phase) * PATROL_DEPTH;
        float dx = x - previousX;
        float dy = y - previousY;
        step += (float) Math.sqrt(dx * dx + dy * dy) * MathUtils.PI / 18f;
        facing.face(MathUtils.cos(phase) >= 0f, true);
        facing.update(delta);
        float depthScale = 1f - y * 0.003f;
        setScale(facing.scaleX() * depthScale, depthScale);
        setRotation(MathUtils.sin(step) * 2.5f);
        setPosition(centerX + x - getOriginX(), floorY + y - getOriginY());
        shadow.setVisible(isVisible());
        shadow.place(centerX + x, floorY + y, getImageWidth() * depthScale * 0.85f,
                getHeight() * depthScale * 0.16f, getColor().a * 0.55f);
    }
}
