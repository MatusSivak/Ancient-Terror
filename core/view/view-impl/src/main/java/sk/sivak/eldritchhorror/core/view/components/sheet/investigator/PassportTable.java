package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


import com.badlogic.gdx.utils.Align;
import java8.features.stream.Stream;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.CardClickListener;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class PassportTable extends Table {
    private static final float PASSPORT_SCALE = 0.9f;
    private static final float BACKGROUND_WIDTH = 800f;
    private static final float BACKGROUND_HEIGHT = BACKGROUND_WIDTH / 1.5f;
    private static final float BACKGROUND_X = -15f;
    private static final float BACKGROUND_Y = -12f;

    private boolean displayed;
    private InvestigatorSheetButtons buttons;
    private TextureRegion selectedTabHeader;

    private BioTableData bioTableData;
    private StatsTableData statsTableData;
    private TokensTableData tokensTableData;
    private SpecialTableData specialTableData;
    private List<CardInfo> cards;
    private List<CardClickListener> activeCardListeners = Collections.emptyList();
    private Image hitImage;
    private final DisplayHide displayHide;
    private Action0 beforeDisplayAction;
    private Action0 beforeHideAction;

    public PassportTable() {
        align(Align.topLeft);
        displayHide = new DisplayHide(this, BigActorsManager.BigActorKey.PASSPORT);
        displayHide.setActorKey(OnScreenActors.ActorKey.PASSPORT);
        displayHide.setDisplayedScale(PASSPORT_SCALE);
    }

    public void init(BioTableData bioTableData,
                     StatsTableData statsTableData,
                     TokensTableData tokensTableData,
                     SpecialTableData specialTableData,
                     List<CardInfo> cards) {

        this.bioTableData = bioTableData;
        this.statsTableData = statsTableData;
        this.tokensTableData = tokensTableData;
        this.specialTableData = specialTableData;
        this.cards = cards;

    }

    public void prepareBasicInfoTab() {
        clear();
        activeCardListeners = Collections.emptyList();
        padLeft(35);
        padBottom(50);

        SpecialTable specialTable = new SpecialTable();
        specialTable.init(specialTableData);

        BioTable bioTable = new BioTable();
        bioTable.init(bioTableData);

        TokensTable tokensTable = new TokensTable();
        tokensTable.init(tokensTableData);

        CharacterSkillsTable statsTable = new CharacterSkillsTable();
        statsTable.init(statsTableData);


        Table skillsAndVitals = new Table();
        skillsAndVitals.add(CharacterSheetWidgets.section(statsTable))
                .grow().minWidth(0).padBottom(8).row();
        skillsAndVitals.add(CharacterSheetWidgets.section(CharacterSheetWidgets.createVitals(tokensTableData))).grow().minWidth(0);
        Table topPanels = new Table();
        topPanels.defaults().growY().minWidth(0);
        topPanels.add(CharacterSheetWidgets.section(bioTable))
                .width(CharacterSheetWidgets.panelWidth(topPanels, 0.32f)).padRight(8);
        topPanels.add(skillsAndVitals)
                .width(CharacterSheetWidgets.panelWidth(topPanels, 0.45f)).padRight(8);
        topPanels.add(CharacterSheetWidgets.section(tokensTable))
                .width(CharacterSheetWidgets.panelWidth(topPanels, 0.23f));

        Table sheet = new Table();
        sheet.top();
        sheet.add(topPanels).growX().minHeight(220).padBottom(8).row();
        sheet.add(CharacterSheetWidgets.section(specialTable)).growX();
        add(new FittedCharacterSheet(sheet)).width(700).height(400);

        buttons = new InvestigatorSheetButtons() {
            @Override
            protected void onCardsTabClick() {
                prepareCardsTab();
            }

            @Override
            protected void onBackgroundTabClick() {
                prepareBackgroundTab();
            }
        };

        addHitImage();

        buttons.initButtons();
        buttons.highlightBasicInfo();

        prepareCommon();
    }

    public void prepareCardsTab() {

        List<AssetInfo> assets = Stream.collectToList(cards, cardInfo -> cardInfo instanceof AssetInfo);
        List<AssetInfo> uniqueAssets = Collections.emptyList();
        List<AssetInfo> artifacts = Stream.collectToList(cards, cardInfo -> cardInfo instanceof ArtifactInfo);
        List<SpellInfo> spells = Stream.collectToList(cards, cardInfo -> cardInfo instanceof SpellInfo);
        List<CardInfo> conditions = Stream.collectToList(cards, cardInfo -> cardInfo instanceof ConditionInfo);
        clear();

        addHitImage();
        pad(0, 35, 50, 0);

        List<CardClickListener> cardClickListeners = new LinkedList<>();
        List<CardTemplate> cardTemplates = new LinkedList<>();
        Table sheet = new Table();
        sheet.top().left();

        Table firstRow = createCardRow(
                new String[]{"investigator.section.assets", "investigator.section.uniqueAssets", "investigator.section.artifacts"},
                java.util.Arrays.asList(assets, uniqueAssets, artifacts), cardClickListeners, cardTemplates);
        Table secondRow = createCardRow(
                new String[]{"investigator.section.spells", "investigator.section.conditions"},
                java.util.Arrays.asList(spells, conditions), cardClickListeners, cardTemplates);
        if (firstRow.hasChildren()) {
            sheet.add(firstRow).width(700).left().padBottom(secondRow.hasChildren() ? 12 : 0).row();
        }
        if (secondRow.hasChildren()) {
            sheet.add(secondRow).width(700).left().row();
        }
        for (CardClickListener listener : cardClickListeners) {
            listener.setOtherListeners(cardClickListeners);
            listener.setAllTemplates(cardTemplates);
        }
        FittedCharacterSheet fittedSheet = new FittedCharacterSheet(sheet);
        add(fittedSheet).width(700).height(400);
        activeCardListeners = cardClickListeners;
        // Clicks on titles, panels and empty scroll space bubble here; only the cards themselves keep it open.
        fittedSheet.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isInsideCard(event.getTarget())) {
                    onClickOutsideCard();
                }
            }
        });
        buttons = new InvestigatorSheetButtons() {
            @Override
            protected void onBasicInfoTabClick() {
                prepareBasicInfoTab();
            }

            @Override
            protected void onBackgroundTabClick() {
                prepareBackgroundTab();
            }
        };

        buttons.initButtons();
        buttons.highlightCards();

        prepareCommon();
    }

    private static boolean isInsideCard(Actor actor) {
        for (Actor current = actor; current != null; current = current.getParent()) {
            if (current instanceof CardTemplate) return true;
        }
        return false;
    }

    private void onClickOutsideCard() {
        if (!displayHide.isDisplayed() || BigActorsManager.isLocked()) return;
        for (CardClickListener listener : activeCardListeners) {
            if (listener.isZoomed()) {
                // First outside click shrinks a zoomed card back; the next one closes the sheet.
                listener.scaleDownIfZoomed();
                return;
            }
        }
        BigActorsManager.displayOrHidePassport();
    }

    /** Section backgrounds and scroll viewports stay within their allocated page width. */
    private Table createCardRow(String[] titles, List<? extends List<? extends CardInfo>> groups,
                                List<CardClickListener> listeners, List<CardTemplate> templates) {
        Table row = new Table();
        row.top().left();
        int populated = 0;
        for (List<? extends CardInfo> group : groups) {
            if (!group.isEmpty()) populated++;
        }
        if (populated == 0) return row;
        float sectionWidth = (700f - (populated - 1) * 12f) / populated;
        int added = 0;
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).isEmpty()) continue;
            CardsTable cardsTable = new CardsTable();
            cardsTable.init(groups.get(i), (int) (sectionWidth - 30));
            listeners.addAll(cardsTable.getCardClickListeners());
            templates.addAll(cardsTable.getCardTemplates());

            Table panel = new Table();
            panel.setBackground(new RoundedSheetPanel());
            panel.pad(10);
            Label title = new Label(
                    get(titles[i]), new Label.LabelStyle(
                    CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SPECIAL_ELITE, 42),
                    Color.valueOf("E8D6AD")));
            title.setFontScale(0.35f);
            title.setWrap(true);
            title.setAlignment(Align.left);
            panel.add(title).width(sectionWidth - 20).padBottom(8).row();
            panel.add(cardsTable).width(sectionWidth - 20).left();
            row.add(panel).width(sectionWidth).top().padRight(++added < populated ? 12 : 0);
        }
        return row;
    }
    private void prepareBackgroundTab() {
        clear();
        activeCardListeners = Collections.emptyList();
        addHitImage();
        pad(0, 35, 50, 0);

        BackgroundBioTable backgroundBioTable = new BackgroundBioTable();
        backgroundBioTable.init(bioTableData.getName(), bioTableData.getBackgroundBio());
        backgroundBioTable.setOnBackgroundClick(this::onClickOutsideCard);
        add(backgroundBioTable).width(700).height(400);


        buttons = new InvestigatorSheetButtons() {

            @Override
            protected void onBasicInfoTabClick() {
                prepareBasicInfoTab();
            }

            @Override
            protected void onCardsTabClick() {
                prepareCardsTab();
            }
        };

        buttons.initButtons();
        buttons.highlightBio();

        prepareCommon();

    }

    private void prepareCommon() {
        // Only swap the header strip so the page and frame never change between tabs.
        selectedTabHeader = new TextureRegion(CustomAssetManager.getTexture(buttons.getHeaderPath()));
        buttons.setPosition(0, 0);
        addActor(buttons);
        pack();

        setTransform(true);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setScale(PASSPORT_SCALE);
        // Center the visible artwork, which extends beyond this table's layout bounds.
        float x = (VIEWPORT_WIDTH - BACKGROUND_WIDTH * PASSPORT_SCALE) / 2
                - BACKGROUND_X * PASSPORT_SCALE - (1 - PASSPORT_SCALE) * getOriginX();
        float y = (VIEWPORT_HEIGHT - BACKGROUND_HEIGHT * PASSPORT_SCALE) / 2
                - BACKGROUND_Y * PASSPORT_SCALE - (1 - PASSPORT_SCALE) * getOriginY();
        displayHide.setDisplayedX(x);
        displayHide.setDisplayedY(y);
        setPosition(x, y);
    }

    private void addHitImage() {
        hitImage = new Image(CustomAssetManager.getTextureRegion(CustomAssetManager.PURE_WHITE_BACKGROUND));
        addActor(hitImage);
        hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        hitImage.getColor().a = 0;
        addClickListener(hitImage, this::onClickOutsideCard);
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        float height = BACKGROUND_HEIGHT * getScaleY();
        float headerHeight = height * 96f / 1024f;
        batch.setColor(Color.WHITE);
        batch.draw(CustomAssetManager.getTexture("passport-dossier-center.png"),
                BACKGROUND_X * getScaleX() + getX() + (1 - getScaleX()) * getWidth() * 0.5f,
                BACKGROUND_Y * getScaleY() + getY() + (1 - getScaleY()) * getHeight() * 0.5f,
                BACKGROUND_WIDTH * getScaleX(), height - headerHeight);
        batch.draw(selectedTabHeader,
                BACKGROUND_X * getScaleX() + getX() + (1 - getScaleX()) * getWidth() * 0.5f,
                BACKGROUND_Y * getScaleY() + getY() + (1 - getScaleY()) * getHeight() * 0.5f
                        + height - headerHeight,
                BACKGROUND_WIDTH * getScaleX(), headerHeight);
        super.draw(batch, parentAlpha);
    }


    public void displayOrHide() {

        displayHide.setBeforeDisplayAction(() -> {
            prepareBasicInfoTab();
            InfoStage.getInvestigatorHud().hide().subscribe();
            HudButtons.getTrackHud().hide().subscribe();
            if (beforeDisplayAction != null) {
                beforeDisplayAction.call();
            }
        });

        displayHide.setBeforeHideAction(() -> {
            InfoStage.getInvestigatorHud().show().subscribe();
            HudButtons.getTrackHud().show().subscribe();
            if (beforeHideAction != null) {
                beforeHideAction.call();
            }
        });

        displayHide.displayOrHide().subscribe();
    }

    public void setBeforeDisplayAction(Action0 beforeDisplayAction) {
        this.beforeDisplayAction = beforeDisplayAction;
    }

    public void setBeforeHideAction(Action0 beforeHideAction) {
        this.beforeHideAction = beforeHideAction;
    }
}
