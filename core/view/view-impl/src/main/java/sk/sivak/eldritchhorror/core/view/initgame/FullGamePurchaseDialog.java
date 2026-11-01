package sk.sivak.eldritchhorror.core.view.initgame;

import sk.sivak.eldritchhorror.core.view.components.SoundDialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.StoreDialogStyle;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/** The single storefront for every full-game entry point. */
public final class FullGamePurchaseDialog extends SoundDialog {
    private boolean pending;
    private final TextButton buy;
    private final TextButton cancel;
    private final TextButton restore;
    private final Label status;
    private final Runnable onPurchased;

    private FullGamePurchaseDialog(Runnable onPurchased) {
        super(get("purchase.fullGame.title"), StoreDialogStyle.window());
        this.onPurchased = onPurchased;
        setModal(true);
        setMovable(false);
        setTransform(true);
        setKeepWithinStage(false);
        getTitleLabel().setFontScale(0.38f);
        getTitleLabel().setAlignment(Align.center);
        getContentTable().pad(4f, 10f, 0f, 10f);
        Table offer = new Table();
        offer.add(artwork()).width(264f).top().padRight(14f);
        Table benefits = new Table();
        Label introduction = StoreDialogStyle.label(get("purchase.fullGame.description"), 0.26f);
        introduction.setAlignment(Align.left);
        benefits.add(introduction).colspan(2).width(280f).padBottom(6f).row();
        for (String feature : new String[]{"ancientOnes", "investigators", "monsters", "cards", "noAds"}) {
            Image check = new Image(CustomAssetManager.getTexture("icon/tick.png"));
            check.setScaling(Scaling.fit);
            benefits.add(check).size(16f).top().padTop(3f).padRight(8f).padBottom(4f);
            Label benefit = StoreDialogStyle.label(get("purchase.fullGame." + feature), 0.25f);
            benefit.setAlignment(Align.left);
            benefits.add(benefit).width(256f).padBottom(4f).row();
        }
        offer.add(benefits).width(280f).top();
        getContentTable().add(offer).row();
        status = StoreDialogStyle.label("", 0.25f);
        getContentTable().add(status).width(558f).height(24f);
        buy = StoreDialogStyle.primaryButton(get("purchase.fullGame.buy"));
        cancel = StoreDialogStyle.textLink(get("purchase.fullGame.cancel"), 0.24f);
        restore = StoreDialogStyle.textLink(get("purchase.fullGame.restore"), 0.21f);
        getButtonTable().defaults().pad(1f, 6f, 1f, 6f);
        button(buy, "buy");
        getButtonTable().getCell(buy).width(470f).height(54f);
        getButtonTable().row();
        Label purchaseNote = StoreDialogStyle.label(get("purchase.fullGame.oneTime"), 0.21f);
        purchaseNote.setColor(Color.valueOf("BEB69F"));
        getButtonTable().add(purchaseNote).width(470f).padTop(0f).padBottom(1f).row();
        button(cancel, "cancel");
        getButtonTable().getCell(cancel).width(150f).height(28f);
        getButtonTable().row();
        button(restore, "restore");
        getButtonTable().getCell(restore).width(180f).height(28f).padTop(0f);
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
        StoreDialogStyle.fitToStage(this);
    }

    @Override protected void result(Object answer) {
        if (pending) { cancel(); return; }
        if ("cancel".equals(answer)) return;
        cancel(); // Keep this dialog open during store callbacks and unsuccessful attempts.
        setPending(true);
        boolean restoring = "restore".equals(answer);
        status.setText(get("purchase.fullGame.pending"));
        InAppPurchaseManager manager = new InAppPurchaseManager();
        (restoring ? manager.restoreFullGame() : manager.purchaseFullGame()).subscribe(success -> Gdx.app.postRunnable(() -> {
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
        for (String id : new String[]{"cthulhu", "shub_niggurath", "yog_sothoth", "yig"}) {
            Image ancientOne = new Image(CustomAssetManager.getTexture("ancient_one/button_" + id + ".jpg"));
            ancientOne.setScaling(Scaling.fit);
            ancientOnes.add(ancientOne).size(61f).pad(1f);
        }
        artwork.add(ancientOnes).row();
        Table portraits = new Table();
        int portraitIndex = 0;
        for (String id : new String[]{"THE_BOOTLEGGER", "THE_HANDYMAN", "THE_VIOLINIST", "THE_WAITRESS",
                "THE_BUTLER", "THE_PRIEST", "THE_NUN", "THE_EXPLORER"}) {
            Image portrait = new Image(CustomAssetManager.getTexture("investigator/" + id + ".png"));
            portrait.setScaling(Scaling.fit);
            portraits.add(portrait).size(59f, 90f).pad(2f);
            if (++portraitIndex % 4 == 0) portraits.row();
        }
        artwork.add(portraits).padTop(5f);
        return artwork;
    }
}
