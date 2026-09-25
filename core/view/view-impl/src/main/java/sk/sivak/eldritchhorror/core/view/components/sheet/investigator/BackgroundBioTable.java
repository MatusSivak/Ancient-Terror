package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;
import rx.functions.Action0;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class BackgroundBioTable extends VisTable {
    private final Label name;
    private final Label biography;
    private final ScrollPane scrollPane;
    private Action0 onBackgroundClick;

    public BackgroundBioTable() {
        top().left();
        pad(18, 22, 18, 22);

        name = new Label("", new Label.LabelStyle(
                getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), Color.valueOf("393323")));
        name.setFontScale(0.66f);
        name.setWrap(true);
        add(name).growX().padBottom(12).row();

        Image divider = new Image(getTexture(PURE_WHITE_BACKGROUND));
        divider.setColor(Color.valueOf("8E7953"));
        add(divider).growX().height(1).padBottom(16).row();

        biography = new Label("", new Label.LabelStyle(
                getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), Color.valueOf("292B23")));
        biography.setWrap(true);
        biography.setAlignment(Align.topLeft);
        biography.setFontScale(0.46f);
        Table text = new Table();
        text.top().left();
        text.add(biography).growX().padRight(14);

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        BaseDrawable track = (BaseDrawable) SelectionPanelStyle.panel("8E795326", "8E795326");
        track.setMinWidth(4);
        BaseDrawable knob = (BaseDrawable) SelectionPanelStyle.panel("8E7953", "8E7953");
        knob.setMinWidth(4);
        knob.setMinHeight(28);
        scrollStyle.vScroll = track;
        scrollStyle.vScrollKnob = knob;
        scrollPane = new ScrollPane(text, scrollStyle);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setOverscroll(false, false);
        add(scrollPane).grow().minHeight(0).minWidth(0);
    }

    public void init(String investigatorName, String bioBackground) {
        name.setText(investigatorName == null ? "" : investigatorName);
        // Reflow legacy line breaks while keeping intentional paragraph breaks.
        biography.setText(bioBackground == null ? "" : bioBackground
                .replace("\r\n", "\n").replaceAll("(?<!\\n)\\n(?!\\n)", " ").trim());
        scrollPane.setScrollY(0);
        scrollPane.updateVisualScroll();
    }

    public void setOnBackgroundClick(Action0 onBackgroundClick) {
        this.onBackgroundClick = onBackgroundClick;
        clearListeners();
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (BackgroundBioTable.this.onBackgroundClick != null) {
                    BackgroundBioTable.this.onBackgroundClick.call();
                }
            }
        });
    }
}
