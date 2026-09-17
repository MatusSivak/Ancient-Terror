package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import sk.sivak.eldritchhorror.core.view.shader.GrayscaleShader;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;

import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** Presentation helpers in the passport's logical viewport units. */
final class CharacterSheetWidgets {
    static final float SKILL_GLYPH_SIZE = 36;
    static final float RESOURCE_ICON_SIZE = 32;
    static final float TOKEN_ICON_SIZE = 24;
    static final float INVENTORY_ICON_SIZE = 44;
    static final float PANEL_PADDING = 10;
    static final Color INK = new Color(0xf2e5caff);
    static final Color SECONDARY_INK = new Color(0xc9bda5ff);

    private CharacterSheetWidgets() { }

    static Label text(String text, float size, Color color) {
        Label label = new Label(text, new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), color));
        label.setFontScale(size / 40f);
        label.setAlignment(Align.center);
        return label;
    }

    static Image icon(String asset) {
        Image image = new Image(getTextureRegionDrawable(asset));
        image.setScaling(Scaling.fit);
        return image;
    }

    static Value panelWidth(final Table row, final float proportion) {
        return new Value() {
            @Override
            public float get(Actor context) {
                return Math.max(0, row.getWidth() - 16) * proportion;
            }
        };
    }

    static Table createVitals(TokensTableData data) {
        Table vitals = new Table();
        vitals.add(createHealthWidget(SANITY_ICON, data.getCurrentSanity(), data.getMaxSanity(),
                new Color(0x7eafffff))).growX().padBottom(10).row();
        vitals.add(createHealthWidget(HEALTH_ICON, data.getCurrentHealth(), data.getMaxHealth(),
                new Color(0xf16b60ff))).growX();
        return vitals;
    }

    static Table createHealthWidget(String asset, int current, int max, Color color) {
        Table widget = new Table();
        HorizontalGroup icons = new HorizontalGroup();
        icons.wrap().space(-2).wrapSpace(3).align(Align.left).rowAlign(Align.left);
        int slots = Math.min(20, Math.max(0, Math.max(current, max)));
        for (int i = 0; i < slots; i++) {
            boolean empty = i >= current;
            float pointScale = empty ? 0.7f : 1f;
            Image point = new Image(getTextureRegionDrawable(asset)) {
                @Override
                public float getPrefWidth() { return 24 * pointScale; }

                @Override
                public float getPrefHeight() { return RESOURCE_ICON_SIZE * pointScale; }

                @Override
                public void draw(Batch batch, float parentAlpha) {
                    ShaderProgram previous = batch.getShader();
                    if (empty) batch.setShader(GrayscaleShader.get());
                    try {
                        super.draw(batch, parentAlpha);
                    } finally {
                        if (empty) batch.setShader(previous);
                    }
                }
            };
            point.setScaling(Scaling.fit);
            point.setOrigin(12, RESOURCE_ICON_SIZE / 2f);
            point.setScale(pointScale);
            point.setName(empty ? "empty-point" : "current-point");
            icons.addActor(new Container<Image>(point).size(24, RESOURCE_ICON_SIZE));
        }
        widget.add(icons).growX().minWidth(0).minHeight(RESOURCE_ICON_SIZE).padRight(8);
        Label total = text("(" + current + "/" + max + ")", 18, color);
        total.setAlignment(Align.right);
        widget.add(total).minWidth(48);
        return widget;
    }

    static HorizontalGroup createTokenCollection(String asset, int count) {
        HorizontalGroup tokens = new HorizontalGroup();
        tokens.wrap().space(3).wrapSpace(3).align(Align.left).rowAlign(Align.left);
        appendTokens(tokens, asset, count);
        return tokens;
    }

    static HorizontalGroup createTicketCollection(int ship, int train) {
        HorizontalGroup tickets = createTokenCollection(TICKET_SHIP_DOWN, ship);
        appendTokens(tickets, TICKET_TRAIN_DOWN, train);
        return tickets;
    }

    static Table createInventory(TokensTableData data) {
        Table inventory = new Table();
        inventory.defaults().uniformX().expandX().size(INVENTORY_ICON_SIZE).pad(3);
        String[] assets = {CLUE_TOKEN, FOCUS_TOKEN, TICKET_SHIP_DOWN, TICKET_TRAIN_DOWN};
        int[] counts = {data.getClue(), data.getFocus(), data.getShip(), data.getTrain()};
        int cells = 0;
        for (int type = 0; type < assets.length; type++) {
            int visible = counts[type] > 10 ? 5 : Math.max(0, counts[type]);
            for (int i = 0; i < visible; i++) {
                inventory.add(icon(assets[type]));
                if (++cells % 2 == 0) inventory.row();
            }
            if (counts[type] > visible) {
                inventory.add(text("+" + (counts[type] - visible), 16, INK));
                if (++cells % 2 == 0) inventory.row();
            }
        }
        // Keep an odd final token in the first column, with no phantom token beside it.
        if (cells % 2 != 0) inventory.add();
        return inventory;
    }
    private static void appendTokens(HorizontalGroup tokens, String asset, int count) {
        // Bound actor creation for exceptional counts; +N means additional tokens.
        int visible = count > 10 ? 5 : Math.max(0, count);
        for (int i = 0; i < visible; i++) {
            Image token = icon(asset);
            float aspect = token.getDrawable().getMinWidth() / token.getDrawable().getMinHeight();
            tokens.addActor(new Container<Image>(token)
                    .width(TOKEN_ICON_SIZE * Math.max(1, aspect)).height(TOKEN_ICON_SIZE));
        }
        if (count > visible) {
            tokens.addActor(text("+" + (count - visible), 16, INK));
        }
    }

    static Table section(Table content) {
        Table panel = new Table();
        panel.setBackground(new RoundedSheetPanel());
        panel.pad(PANEL_PADDING);
        panel.add(content).grow();
        return panel;
    }

    static Container<Label> effectText(Label label) {
        BaseDrawable background = (BaseDrawable) getTextureRegionDrawable(WHITE_BACKGROUND)
                .tint(new Color(0.60f, 0.50f, 0.34f, 0.14f));
        background.setMinWidth(0);
        background.setMinHeight(0);
        return new Container<Label>(label).background(background).fillX().pad(6, 14, 8, 14);
    }

}
