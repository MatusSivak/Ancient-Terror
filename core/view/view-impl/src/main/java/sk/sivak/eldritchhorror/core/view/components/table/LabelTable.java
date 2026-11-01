package sk.sivak.eldritchhorror.core.view.components.table;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.PromptStyle;

import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;

public class LabelTable extends VisTable {
    private static final float PORTRAIT_SIZE = 54f;
    private static final float ICON_SIZE = 26f;
    private static final float GAP = 12f;

    private LabelTable() { }

    public static LabelTable createAndShowTable(float delay, String text, Texture texture) {
        return show(delay, text, null, Collections.singletonList(texture), 0, false);
    }

    public static LabelTable createAndShowTable(float delay, String text, String portrait,
                                               List<Texture> textures, int minWidth) {
        return show(delay, text, portrait, textures, minWidth, false);
    }

    public static LabelTable createAndShowTable(float delay, String text) {
        return show(delay, text, null, Collections.emptyList(), 0, false);
    }

    public static LabelTable createAndShowHint(String text) {
        return show(0, text, null, Collections.emptyList(), 0, true);
    }

    public static LabelTable createAndShowNotice(String text, String portrait) {
        return show(0, text, portrait, Collections.emptyList(), 0, false);
    }

    private static LabelTable show(float delay, String text, String portrait,
                                    List<Texture> textures, int minWidth, boolean hint) {
        if (!VisUI.isLoaded()) VisUI.load();
        LabelTable table = new LabelTable();
        table.setTouchable(Touchable.disabled);
        table.setBackground(PromptStyle.panel());
        float artworkWidth = 0;
        if (portrait != null) {
            table.add(image(getTexture(portrait))).size(PORTRAIT_SIZE).padRight(GAP);
            artworkWidth += PORTRAIT_SIZE + GAP;
        }
        for (Texture texture : textures) {
            if (texture == null) continue;
            table.add(image(texture)).size(ICON_SIZE).padRight(GAP);
            artworkWidth += ICON_SIZE + GAP;
        }

        Label label = PromptStyle.label(text, hint);
        float available = PromptStyle.MAX_WIDTH - 2 * PromptStyle.PAD_X - artworkWidth;
        // Leave a little slack for scaled glyph bearings and Scene2D's pixel rounding.
        float textWidth = Math.min(available, Math.max(Math.max(minWidth, label.getPrefWidth() + 8f),
                240f - 2 * PromptStyle.PAD_X - artworkWidth));
        boolean multiline = label.getPrefWidth() > textWidth || (text != null && text.contains("\n"));
        label.setAlignment(multiline || portrait != null ? Align.left : Align.center);
        label.setWrap(true);
        table.add(label).width(textWidth);
        // Use the real wrapped/portrait height so cards and buttons reserve the correct space.
        table.pack();
        table.setPosition((VIEWPORT_WIDTH - table.getWidth()) / 2f, InfoStage.getBottomHeight());
        InfoStage.addSmallActorToInfoStage(table);
        table.getColor().a = 0;
        table.addAction(Actions.alpha(1, delay));
        InfoStage.setBottomHeight(table.getTop() + 5);
        return table;
    }

    private static Image image(Texture texture) {
        Image image = new Image(texture);
        image.setScaling(Scaling.fit);
        return image;
    }
}
