package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;
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

/** Portrait selection and persistent details, matching investigator selection. */
public class SelectAncientOneTable extends Table {
    private final Map<AncientOneId, AncientOneInfo> choices = new EnumMap<>(AncientOneId.class);
    private final Map<AncientOneId, Table> cards = new EnumMap<>(AncientOneId.class);
    private final Map<AncientOneId, Table> lockBadges = new EnumMap<>(AncientOneId.class);
    private final Map<AncientOneId, Boolean> unlocked = new EnumMap<>(AncientOneId.class);
    private final SingleSubscriber<? super AncientOneInfo> subscriber;
    private final Label name = label("", 0.4f, "E8D9B0");
    private final Label subtitle = label("", 0.28f, "BEB69F");
    private final Label doom = label("", 0.48f, "D5AD89");
    private final Label mysteries = label("", 0.48f, "E8D9B0");
    private final Drawable normalCard = SelectionPanelStyle.panel("18271F", "394839");
    private final Drawable selectedCard = AncientTerrorMenuStyles.highlight(
            SelectionPanelStyle.panel("2A3725", "B99C60"));
    private final Label description = label("", 0.27f, "E5DFCC");
    private final Label status = label("", 0.25f, "BEB69F");
    private final TextButton confirm = new TextButton("", AncientTerrorMenuStyles.button());
    private final ScrollPane descriptionScroll;
    private AncientOneId selected;
    private boolean purchasePending;
    private boolean completed;

    public SelectAncientOneTable(List<AncientOneInfo> availableAncientOnes,
                                 boolean cthulhuPurchased, boolean shubPurchased, boolean yogPurchased,
                                 SingleSubscriber<? super AncientOneInfo> subscriber) {
        this.subscriber = subscriber;
        for (AncientOneInfo info : availableAncientOnes) choices.put(info.getAncientOneId(), info);
        unlocked.put(AncientOneId.AZATHOTH, true);
        unlocked.put(AncientOneId.CTHULHU, cthulhuPurchased);
        unlocked.put(AncientOneId.SHUB_NIGGURATH, shubPurchased);
        unlocked.put(AncientOneId.YOG_SOTHOTH, yogPurchased);

        Table gallery = new Table();
        gallery.setBackground(SelectionPanelStyle.panel("0C1714F5", "48503A"));
        gallery.pad(8f);
        gallery.add(label(get("init.selectAncientOne"), 0.38f, "E8D9B0")).growX().height(36f).row();
        gallery.add(divider()).growX().height(1f).pad(0f, 8f, 6f, 8f).row();
        Table grid = new Table();
        int count = 0;
        for (AncientOneId id : AncientOneId.values()) {
            if (!choices.containsKey(id)) continue;
            Table card = createCard(id);
            cards.put(id, card);
            grid.add(card).size(280f, 216f).pad(4f);
            if (++count % 2 == 0) grid.row();
        }
        gallery.add(grid).expand().top();

        Table details = new Table();
        details.setBackground(SelectionPanelStyle.panel("0C1714F5", "676044"));
        details.pad(14f);
        details.add(name).growX().height(46f).row();
        details.add(subtitle).growX().height(34f).padBottom(8f).row();
        details.add(divider()).growX().height(1f).padBottom(12f).row();
        Table stats = new Table();
        stats.add(metric(doom, "ancientOne.selection.doom")).growX().uniformX().padRight(4f);
        stats.add(metric(mysteries, "ancientOne.selection.mysteries")).growX().uniformX().padLeft(4f);
        details.add(stats).growX().height(72f).padBottom(12f).row();
        description.setAlignment(Align.topLeft);
        Table rules = new Table();
        rules.top().add(description).growX().padRight(10f);
        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScroll = panelBackground("15251D");
        scrollStyle.vScrollKnob = panelBackground("89774B");
        scrollStyle.vScroll.setMinWidth(3f);
        scrollStyle.vScrollKnob.setMinWidth(3f);
        scrollStyle.vScrollKnob.setMinHeight(24f);
        descriptionScroll = new ScrollPane(rules, scrollStyle);
        descriptionScroll.setFadeScrollBars(false);
        descriptionScroll.setScrollingDisabled(true, false);
        descriptionScroll.setOverscroll(false, false);
        details.add(descriptionScroll).grow().minHeight(100f).padBottom(10f).row();
        details.add(status).growX().height(30f).padBottom(6f).row();
        confirm.getLabel().setFontScale(0.33f);
        confirm.getLabel().setWrap(true);
        AncientTerrorMenuStyles.makeMomentary(confirm);
        AncientTerrorMenuStyles.addFocusHighlight(confirm);
        addClickListener(confirm, this::confirmSelection);
        details.add(confirm).growX().height(50f);

        add(gallery).width(600f).growY().padRight(12f);
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
        card.setBackground(normalCard);
        Texture artwork = CustomAssetManager.getTexture("ancient_one/button_" + productId(id) + ".jpg");
        // A centered landscape crop fills the card without stretching the artwork.
        int cropHeight = Math.min(artwork.getHeight(), Math.round(artwork.getWidth() * 152f / 268f));
        Image portrait = new Image(new TextureRegion(artwork, 0,
                (artwork.getHeight() - cropHeight) / 2, artwork.getWidth(), cropHeight));
        portrait.setScaling(Scaling.fit);
        Image lock = new Image(CustomAssetManager.getTexture("ancient_one/lock.png"));
        lock.setScaling(Scaling.fit);
        lock.setVisible(!unlocked.get(id));
        Table lockOverlay = new Table();
        lockOverlay.top().right();
        Table badge = new Table();
        badge.setBackground(SelectionPanelStyle.panel("0C1714EE", "89774B"));
        badge.add(lock).size(22f).pad(4f);
        badge.setVisible(!unlocked.get(id));
        lockOverlay.add(badge).pad(5f);
        lockBadges.put(id, badge);
        card.add(new Stack(portrait, lockOverlay)).growX().height(152f).row();
        card.add(label(get(prefix(id) + ".name"), 0.32f, "E8D9B0")).growX().height(26f).row();
        card.add(label(get(prefix(id) + ".alt"), 0.23f, "BEB69F")).growX().height(26f);
        addClickListener(card, () -> { if (!purchasePending && !completed) select(id); });
        return card;
    }

    private void select(AncientOneId id) {
        selected = id;
        for (Map.Entry<AncientOneId, Table> entry : cards.entrySet()) {
            entry.getValue().setBackground(entry.getKey() == id ? selectedCard : normalCard);
        }
        AncientOneInfo info = choices.get(id);
        name.setText(get(prefix(id) + ".name"));
        subtitle.setText(get(prefix(id) + ".alt"));
        doom.setText(Integer.toString(info.getStartingDoom()));
        mysteries.setText(Integer.toString(info.getMysteriesRequired()));
        StringBuilder text = new StringBuilder();
        section(text, get("ancientOne.setup"), info.getSetupText());
        section(text, get("ancientOne.selection.special"), info.getSpecialText());
        section(text, get("ancientOne.selection.reckoning"), info.getReckoningText());
        section(text, get("ancientOne.victory"), info.getWinText());
        section(text, "", info.getFlavorText());
        if (!unlocked.get(id)) {
            text.append("[#E8D9B0]").append(get("ancientOne.selection.includes")).append("[]\n");
            for (String feature : features(id)) text.append(get("ancientOne.features." + feature)).append('\n');
        }
        description.setText(text.toString());
        descriptionScroll.setScrollY(0f);
        status.setColor(Color.valueOf(unlocked.get(id) ? "BDD0A6" : "D5AD89"));
        status.setText(get(unlocked.get(id) ? "ancientOne.selection.available" : "ancientOne.selection.locked"));
        confirm.setText(get(unlocked.get(id) ? "ancientOne.selection.confirm" : "ancientOne.selection.unlock", get(prefix(id) + ".name")));
        confirm.setDisabled(false);
    }

    private void confirmSelection() {
        if (selected == null || confirm.isDisabled() || completed || purchasePending) return;
        final AncientOneId id = selected;
        if (unlocked.get(id)) {
            completed = true;
            confirm.setDisabled(true);
            remove();
            if (!subscriber.isUnsubscribed()) subscriber.onSuccess(choices.get(id));
            return;
        }
        purchasePending = true;
        confirm.setDisabled(true);
        status.setText(get("ancientOne.selection.purchasing"));
        new InAppPurchaseManager().purchaseProduct(productId(id)).subscribe(success -> Gdx.app.postRunnable(() -> {
            purchasePending = false;
            if (success) {
                unlocked.put(id, true);
                lockBadges.get(id).setVisible(false);
            }
            select(id);
            if (!success) status.setText(get("ancientOne.selection.purchaseCancelled"));
        }), error -> Gdx.app.postRunnable(() -> {
            purchasePending = false;
            select(id);
            status.setText(get("ancientOne.selection.purchaseFailed"));
        }));
    }

    private static void section(StringBuilder result, String heading, String value) {
        if (value == null || value.trim().isEmpty()) return;
        String resolved = value.startsWith("ancientOne.") ? get(value) : value;
        if (!heading.isEmpty()) result.append("[#E8D9B0]").append(heading).append("[]\n");
        result.append(resolved).append("\n\n");
    }

    private static String[] features(AncientOneId id) {
        if (id == AncientOneId.CTHULHU) return new String[]{"sixMysteries", "threeEpicMonsters", "eightyEncounters", "exploreRlyeh", "endingRisenFromSea", "priceCoffee"};
        if (id == AncientOneId.SHUB_NIGGURATH) return new String[]{"sixMysteries", "threeEpicMonsters", "seventyEncounters", "combatOriented", "endingBattleInWoods", "priceCoffee"};
        return new String[]{"sixMysteries", "dunwichHorror", "eightyEncounters", "visitVoidBetweenWorlds", "endingKeyAndGate", "priceCoffee"};
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

    private static Image divider() {
        return new Image(panelBackground("75633E"));
    }

    private static Table metric(Label value, String captionKey) {
        Table metric = new Table();
        metric.setBackground(SelectionPanelStyle.panel("14231D", "394839"));
        metric.pad(4f);
        metric.add(value).growX().height(34f).row();
        metric.add(label(get(captionKey), 0.23f, "BEB69F")).growX().height(26f);
        return metric;
    }

    private static Drawable panelBackground(String color) {
        Drawable drawable = CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.PURE_WHITE_BACKGROUND).tint(Color.valueOf(color));
        drawable.setMinWidth(0f);
        drawable.setMinHeight(0f);
        return drawable;
    }
}
