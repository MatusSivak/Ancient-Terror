package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.AncientTerrorMenuStyles;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/** The single storefront for every full-game entry point. */
public final class FullGamePurchaseDialog extends Dialog {
    private boolean pending;
    private final TextButton buy;
    private final TextButton cancel;
    private final TextButton restore;
    private final Label status;
    private final Runnable onPurchased;

    private FullGamePurchaseDialog(Runnable onPurchased) {
        super(get("purchase.fullGame.title"), style());
        this.onPurchased = onPurchased;
        setModal(true);
        setMovable(false);
        setTransform(true);
        setKeepWithinStage(false);
        getTitleLabel().setFontScale(0.38f);
        getTitleLabel().setAlignment(Align.center);
        getContentTable().pad(8f, 10f, 0f, 10f);
        Table offer = new Table();
        offer.add(artwork()).width(264f).top().padRight(14f);
        Table benefits = new Table();
        Label introduction = label(get("purchase.fullGame.description"), 0.26f);
        introduction.setAlignment(Align.left);
        benefits.add(introduction).width(280f).padBottom(10f).row();
        for (String feature : new String[]{"investigators", "ancientOnes", "noAds", "cards"}) {
            Label benefit = label(get("purchase.fullGame." + feature), 0.25f);
            benefit.setAlignment(Align.left);
            benefits.add(benefit).width(280f).padBottom(7f).row();
        }
        Table price = new Table();
        price.setBackground(SelectionPanelStyle.panel("18271F", "89774B"));
        price.add(label(get("purchase.fullGame.price"), 0.26f)).width(260f).pad(7f, 10f, 7f, 10f);
        benefits.add(price).width(280f).padTop(2f);
        offer.add(benefits).width(280f).top();
        getContentTable().add(offer).row();
        status = label("", 0.25f);
        getContentTable().add(status).width(558f).height(24f);
        buy = buttonText(get("purchase.fullGame.buy"));
        buy.getLabel().setFontScale(0.3f);
        buy.getStyle().up = AncientTerrorMenuStyles.highlight(buy.getStyle().up);
        cancel = buttonText(get("purchase.fullGame.cancel"));
        restore = buttonText(get("purchase.fullGame.restore"));
        getButtonTable().defaults().height(42f).pad(4f, 6f, 4f, 6f);
        button(cancel, "cancel");
        getButtonTable().getCell(cancel).width(190f);
        button(buy, "buy");
        getButtonTable().getCell(buy).width(344f);
        getButtonTable().row();
        button(restore, "restore");
        getButtonTable().getCell(restore).colspan(2).width(270f).height(32f);
        key(Input.Keys.ESCAPE, "cancel");
        key(Input.Keys.BACK, "cancel");
    }

    public static void show(Stage stage, Runnable onPurchased) {
        if (stage == null) return;
        for (com.badlogic.gdx.scenes.scene2d.Actor actor : stage.getActors()) {
            if (actor instanceof FullGamePurchaseDialog) return;
        }
        new FullGamePurchaseDialog(onPurchased).show(stage);
    }

    @Override
    public Dialog show(Stage stage, com.badlogic.gdx.scenes.scene2d.Action action) {
        super.show(stage, action);
        fitToStage();
        return this;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        fitToStage();
    }

    private void fitToStage() {
        if (getStage() == null) return;
        // Keep the complete frame and buttons inside the viewport, including after resizing.
        float scale = Math.min(1f, Math.min(getStage().getWidth() * 0.88f / getWidth(),
                getStage().getHeight() * 0.88f / getHeight()));
        setOrigin(0f, 0f);
        setScale(scale);
        setPosition((getStage().getWidth() - getWidth() * scale) / 2f,
                (getStage().getHeight() - getHeight() * scale) / 2f);
    }

    @Override protected void result(Object answer) {
        if (pending) { cancel(); return; }
        if ("cancel".equals(answer)) return;
        cancel(); // Keep this dialog open during store callbacks and unsuccessful attempts.
        setPending(true);
        boolean restoring = "restore".equals(answer);
        status.setText(get("purchase.fullGame.pending"));
        InAppPurchaseManager manager = new InAppPurchaseManager();
        (restoring ? manager.isProductPurchased(InAppPurchaseManager.FULL_GAME)
                : manager.purchaseProduct(InAppPurchaseManager.FULL_GAME)).subscribe(success -> Gdx.app.postRunnable(() -> {
            setPending(false);
            if (success) {
                hide();
                if (onPurchased != null) onPurchased.run();
            } else {
                status.setText(get(restoring ? "purchase.fullGame.notRestored" : "purchase.fullGame.cancelled"));
            }
        }), error -> Gdx.app.postRunnable(() -> {
            setPending(false);
            status.setText(get("purchase.fullGame.failed"));
        }));
    }

    private void setPending(boolean pending) {
        this.pending = pending;
        buy.setDisabled(pending);
        cancel.setDisabled(pending);
        restore.setDisabled(pending);
    }

    private static Table artwork() {
        Table artwork = new Table();
        artwork.setBackground(SelectionPanelStyle.panel("0C1714", "89774B"));
        artwork.pad(5f);
        Table ancientOnes = new Table();
        for (String id : new String[]{"cthulhu", "shub_niggurath", "yog_sothoth"}) {
            Image ancientOne = new Image(CustomAssetManager.getTexture("ancient_one/button_" + id + ".jpg"));
            ancientOne.setScaling(Scaling.fit);
            ancientOnes.add(ancientOne).size(82f).pad(1f);
        }
        artwork.add(ancientOnes).row();
        Table portraits = new Table();
        for (String id : new String[]{"THE_BOOTLEGGER", "THE_HANDYMAN", "THE_VIOLINIST", "THE_WAITRESS"}) {
            Image portrait = new Image(CustomAssetManager.getTexture("investigator/" + id + ".png"));
            portrait.setScaling(Scaling.fit);
            portraits.add(portrait).size(59f, 90f).pad(2f);
        }
        artwork.add(portraits).padTop(5f);
        return artwork;
    }

    private static WindowStyle style() {
        NinePatch patch = CustomAssetManager.createMenuDialogPatch();
        patch.scale(0.5f, 0.5f);
        WindowStyle style = new WindowStyle();
        style.background = new NinePatchDrawable(patch);
        style.stageBackground = CustomAssetManager.getTextureRegionDrawable(
                CustomAssetManager.PURE_WHITE_BACKGROUND).tint(new Color(0f, 0f, 0f, 0.88f));
        style.titleFont = CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4);
        style.titleFontColor = Color.valueOf("E8D9B0");
        return style;
    }

    private static Label label(String text, float scale) {
        Label label = new Label(text, new Label.LabelStyle(
                CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4), Color.valueOf("E8D9B0")));
        label.setWrap(true);
        label.setAlignment(Align.center);
        label.setFontScale(scale);
        return label;
    }

    private static TextButton buttonText(String text) {
        TextButton button = new TextButton(text, AncientTerrorMenuStyles.button());
        button.getLabel().setFontScale(0.3f);
        button.getStyle().checked = null;
        button.getStyle().checkedFontColor = null;
        AncientTerrorMenuStyles.addFocusHighlight(button);
        return button;
    }
}
