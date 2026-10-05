package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.AncientTerrorMenuStyles;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/** All ancient ones in one visible row, with persistent details below. */
public class SelectAncientOneTable extends Table {
    // Extend the chain ends past the 160px portrait into the dark card surround.
    private static final float LOCK_SIZE = 168f;
    private final Map<AncientOneId, AncientOneInfo> choices = new EnumMap<>(AncientOneId.class);
    private final Map<AncientOneId, Table> cards = new EnumMap<>(AncientOneId.class);
    private final Map<AncientOneId, Image> portraits = new EnumMap<>(AncientOneId.class);
    private final Map<AncientOneId, Image> locks = new EnumMap<>(AncientOneId.class);
    private final Map<AncientOneId, Boolean> unlocked = new EnumMap<>(AncientOneId.class);
    private final SingleSubscriber<? super AncientOneInfo> subscriber;
    private final Label name = label("", 0.4f, "E8D9B0");
    private final Label subtitle = label("", 0.28f, "BEB69F");
    private final Label stats = label("", 0.28f, "E8D9B0");
    private final Label description = label("", 0.27f, "E5DFCC");
    private final Label status = label("", 0.25f, "BEB69F");
    private final TextButton confirm = new TextButton("", AncientTerrorMenuStyles.button());
    private final ScrollPane descriptionScroll;
    private AncientOneId selected;
    private boolean completed;

    public SelectAncientOneTable(List<AncientOneInfo> availableAncientOnes,
                                 boolean cthulhuPurchased, boolean shubPurchased, boolean yogPurchased,
                                 SingleSubscriber<? super AncientOneInfo> subscriber) {
        this.subscriber = subscriber;
        for (AncientOneInfo info : availableAncientOnes) choices.put(info.getAncientOneId(), info);
        unlocked.put(AncientOneId.YIG, yogPurchased);
        unlocked.put(AncientOneId.AZATHOTH, true);
        unlocked.put(AncientOneId.CTHULHU, cthulhuPurchased);
        unlocked.put(AncientOneId.SHUB_NIGGURATH, shubPurchased);
        unlocked.put(AncientOneId.YOG_SOTHOTH, yogPurchased);

        Table gallery = new Table();
        gallery.setBackground(panelBackground("09130FE8"));
        gallery.pad(6f);
        gallery.add(label(get("init.selectAncientOne"), 0.42f, "E8D9B0")).growX().height(36f).row();
        Table grid = new Table();
        for (AncientOneId id : AncientOneId.values()) {
            if (!choices.containsKey(id)) continue;
            Table card = createCard(id);
            cards.put(id, card);
            grid.add(card).growX().uniformX().minWidth(0f).height(198f).padLeft(3f).padRight(3f);
        }
        gallery.add(grid).growX();

        Table details = new Table();
        details.setBackground(panelBackground("07110FEE"));
        details.pad(12f);
        Table summary = new Table();
        summary.add(name).growX().height(34f).row();
        summary.add(subtitle).growX().height(32f).row();
        summary.add(stats).growX().height(40f).row();
        summary.add(status).growX().height(24f).padBottom(6f).row();
        description.setAlignment(Align.topLeft);
        descriptionScroll = new ScrollPane(description);
        descriptionScroll.setScrollingDisabled(true, false);
        descriptionScroll.setOverscroll(false, false);
        descriptionScroll.setFadeScrollBars(false);
        confirm.getLabel().setFontScale(0.33f);
        confirm.getLabel().setWrap(true);
        AncientTerrorMenuStyles.makeMomentary(confirm);
        AncientTerrorMenuStyles.addFocusHighlight(confirm);
        addClickListener(confirm, this::confirmSelection);
        summary.add(confirm).growX().height(44f);
        details.add(summary).width(280f).growY().padRight(18f);
        details.add(descriptionScroll).grow().minWidth(0f).minHeight(0f);

        add(gallery).growX().height(246f).padBottom(10f).row();
        add(details).grow();
        setSize(940f, 510f);
        for (AncientOneId id : AncientOneId.values()) {
            if (choices.containsKey(id)) { select(id); break; }
        }
        if (selected == null) confirm.setDisabled(true);
    }

    private Table createCard(AncientOneId id) {
        Table card = new Table();
        card.pad(6f);
        card.setBackground(panelBackground("17201B"));
        Image portrait = new Image(CustomAssetManager.getTexture("ancient_one/button_" + productId(id) + ".jpg"));
        portrait.setScaling(Scaling.fit);
        boolean locked = !unlocked.get(id);
        if (locked) portrait.setColor(0.68f, 0.68f, 0.68f, 1f);
        portraits.put(id, portrait);
        Image lock = new Image(CustomAssetManager.getTexture("ancient_one/lock.png"));
        lock.setScaling(Scaling.fit);
        lock.setSize(LOCK_SIZE, LOCK_SIZE);
        lock.setOrigin(Align.center);
        lock.setVisible(locked);
        if (locked) {
            lock.setColor(0.86f, 0.82f, 0.72f, 1f);
            lock.addAction(Actions.sequence(
                    Actions.delay(id.ordinal() * 0.18f),
                    Actions.forever(Actions.sequence(
                            Actions.parallel(
                                    Actions.scaleTo(1.045f, 1.045f, 1.4f, Interpolation.sine),
                                    Actions.color(Color.WHITE, 1.4f, Interpolation.sine)),
                            Actions.parallel(
                                    Actions.scaleTo(1f, 1f, 1.4f, Interpolation.sine),
                                    Actions.color(new Color(0.86f, 0.82f, 0.72f, 1f), 1.4f, Interpolation.sine))))));
        }
        locks.put(id, lock);
        Table lockOverlay = new Table();
        lockOverlay.center();
        lockOverlay.add(lock).size(LOCK_SIZE);
        card.add(new Stack(portrait, lockOverlay)).growX().height(160f).row();
        card.add(label(get(prefix(id) + ".name"), 0.30f, "E8D9B0")).growX().height(26f);
        addClickListener(card, () -> {
            if (completed) return;
            select(id);
            if (!unlocked.get(id)) showFullGamePurchase();
        });
        return card;
    }

    private void select(AncientOneId id) {
        selected = id;
        for (Map.Entry<AncientOneId, Table> entry : cards.entrySet()) {
            Drawable normal = panelBackground("17201B");
            entry.getValue().setBackground(entry.getKey() == id ? AncientTerrorMenuStyles.highlight(normal) : normal);
        }
        AncientOneInfo info = choices.get(id);
        name.setText(get(prefix(id) + ".name"));
        subtitle.setText(get(prefix(id) + ".alt"));
        stats.setText(get("ancientOne.selection.stats", info.getStartingDoom(), info.getMysteriesRequired()));
        StringBuilder text = new StringBuilder();
        if (id != AncientOneId.YIG) section(text, get("ancientOne.setup"), info.getSetupText());
        section(text, get("ancientOne.selection.special"), id == AncientOneId.YIG
                ? "ancientOne.yig.selection.special" : info.getSpecialText());
        section(text, get("ancientOne.selection.reckoning"), info.getReckoningText());
        section(text, get("ancientOne.victory"), info.getWinText());
        section(text, "", info.getFlavorText());
        if (!unlocked.get(id)) {
            text.append("[#E8D9B0]").append(get("ancientOne.selection.includes")).append("[]\n");
            for (String feature : features(id)) text.append(get("ancientOne.features." + feature)).append('\n');
        }
        description.setText(text.toString());
        descriptionScroll.setScrollY(0f);
        status.setText(get(unlocked.get(id) ? "ancientOne.selection.available" : "ancientOne.selection.locked"));
        confirm.setText(unlocked.get(id)
                ? get("ancientOne.selection.confirm", get(prefix(id) + ".name"))
                : get("purchase.fullGame.buy"));
        confirm.setDisabled(false);
    }

    private void confirmSelection() {
        if (selected == null || confirm.isDisabled() || completed) return;
        final AncientOneId id = selected;
        if (unlocked.get(id)) {
            completed = true;
            confirm.setDisabled(true);
            remove();
            if (!subscriber.isUnsubscribed()) subscriber.onSuccess(choices.get(id));
            return;
        }
        showFullGamePurchase();
    }

    private void showFullGamePurchase() {
        FullGamePurchaseDialog.show(getStage(), () -> {
            for (AncientOneId id : unlocked.keySet()) {
                unlocked.put(id, true);
                Image lock = locks.get(id);
                if (lock != null) {
                    lock.clearActions();
                    lock.setVisible(false);
                }
                Image portrait = portraits.get(id);
                if (portrait != null) portrait.setColor(Color.WHITE);
            }
            if (selected != null) select(selected);
        });
    }

    private static void section(StringBuilder result, String heading, String value) {
        if (value == null || value.trim().isEmpty()) return;
        String resolved = value.startsWith("ancientOne.") ? get(value) : value;
        if (!heading.isEmpty()) result.append("[#E8D9B0]").append(heading).append("[]\n");
        result.append(resolved).append("\n\n");
    }

    private static String[] features(AncientOneId id) {
        if (id == AncientOneId.YIG) return new String[]{"sixMysteries", "threeEpicMonsters", "yigEncounters"};
        if (id == AncientOneId.CTHULHU) return new String[]{"sixMysteries", "threeEpicMonsters", "eightyEncounters", "exploreRlyeh", "endingRisenFromSea"};
        if (id == AncientOneId.SHUB_NIGGURATH) return new String[]{"sixMysteries", "threeEpicMonsters", "seventyEncounters", "combatOriented", "endingBattleInWoods"};
        return new String[]{"sixMysteries", "dunwichHorror", "eightyEncounters", "visitVoidBetweenWorlds", "endingKeyAndGate"};
    }

    private static String prefix(AncientOneId id) {
        switch (id) {
            case SHUB_NIGGURATH: return "ancientOne.shubNiggurath";
            case YOG_SOTHOTH: return "ancientOne.yogSothoth";
            default: return "ancientOne." + productId(id);
        }
    }

    private static String productId(AncientOneId id) { return id.name().toLowerCase(Locale.ROOT); }

    private static Label label(String text, float scale, String color) {
        Label label = new Label(text, new Label.LabelStyle(CustomAssetManager.getBitmapFontNew(
                CustomAssetManager.NEW_FONT_SOURCE_SERIF_4), Color.valueOf(color)));
        label.getStyle().font.getData().markupEnabled = true;
        label.setFontScale(scale);
        label.setAlignment(Align.center);
        label.setWrap(true);
        return label;
    }

    private static Drawable panelBackground(String color) {
        Drawable drawable = CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.PURE_WHITE_BACKGROUND).tint(Color.valueOf(color));
        drawable.setMinWidth(0f);
        drawable.setMinHeight(0f);
        return drawable;
    }
}
