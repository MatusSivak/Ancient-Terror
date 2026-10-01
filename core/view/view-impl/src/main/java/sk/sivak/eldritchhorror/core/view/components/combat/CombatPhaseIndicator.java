package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/** Small "Horror › Damage" pill in the top-left corner showing which part of the fight is running. */
public class CombatPhaseIndicator extends Table {

    public enum Phase { NONE, HORROR, DAMAGE }

    private static final Color ACTIVE_TEXT = Color.valueOf("F2DFAE");
    private static final Color DONE_TEXT = Color.valueOf("7F8A84");
    private static final Color PENDING_TEXT = Color.valueOf("A4ADA7");
    private static final float ICON_SIZE = 18f;

    private final Table horrorStep;
    private final Table damageStep;
    private Phase phase = Phase.NONE;

    public CombatPhaseIndicator(boolean epic) {
        setBackground(SelectionPanelStyle.panel(epic ? "1A1408DD" : "121B1DDD", epic ? "D4AF37" : "87734E"));
        pad(3, 4, 3, 4);
        setTouchable(Touchable.disabled);
        horrorStep = createStep("combat/horror.png", get("combat.phase.horror"));
        damageStep = createStep("combat/damage.png", get("combat.phase.damage"));
        add(horrorStep);
        add(createLabel("\u2192", PENDING_TEXT, 0.26f)).padLeft(3).padRight(3);
        add(damageStep);
        setPhase(Phase.NONE);
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
        styleStep(horrorStep, phase == Phase.HORROR, phase == Phase.DAMAGE);
        styleStep(damageStep, phase == Phase.DAMAGE, false);
        pack();
    }

    public boolean isActive(Phase step) {
        return phase == step;
    }

    private Table createStep(String texture, String text) {
        Image icon = new Image(CustomAssetManager.getTexture(texture));
        icon.setScaling(Scaling.fit);
        Table step = new Table();
        step.pad(1, 4, 1, 6);
        step.add(icon).size(ICON_SIZE).padRight(3);
        step.add(createLabel(text, PENDING_TEXT, 0.28f));
        return step;
    }

    private void styleStep(Table step, boolean active, boolean done) {
        step.setBackground(active ? SelectionPanelStyle.panel("293530", "A99260") : null);
        Image icon = (Image) step.getChildren().get(0);
        Label label = (Label) step.getChildren().get(1);
        icon.getColor().a = active ? 1f : done ? 0.45f : 0.7f;
        label.setColor(active ? ACTIVE_TEXT : done ? DONE_TEXT : PENDING_TEXT);
    }

    private static Label createLabel(String text, Color color, float scale) {
        Label label = new Label(text, new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), Color.WHITE));
        label.setColor(color);
        label.setFontScale(scale);
        label.setAlignment(Align.center);
        return label;
    }
}
