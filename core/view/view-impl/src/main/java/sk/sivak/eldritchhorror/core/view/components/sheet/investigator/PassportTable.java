package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
import sk.sivak.eldritchhorror.core.view.components.sheet.SectionWrapper;
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

    private boolean displayed;
    private InvestigatorSheetButtons buttons;

    private BioTableData bioTableData;
    private StatsTableData statsTableData;
    private TokensTableData tokensTableData;
    private SpecialTableData specialTableData;
    private List<CardInfo> cards;
    private Image hitImage;
    private final DisplayHide displayHide;
    private Action0 beforeDisplayAction;
    private Action0 beforeHideAction;

    public PassportTable() {
        align(Align.topLeft);
        displayHide = new DisplayHide(this, BigActorsManager.BigActorKey.PASSPORT);
        displayHide.setActorKey(OnScreenActors.ActorKey.PASSPORT);
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
        padLeft(35);
        padBottom(20);

        SpecialTable specialTable = new SpecialTable();
        specialTable.init(specialTableData);

        BioTable bioTable = new BioTable();
        bioTable.init(bioTableData);

        TokensTable tokensTable = new TokensTable();
        tokensTable.init(tokensTableData);

        StatsTable statsTable = new StatsTable();
        statsTable.init(statsTableData);


        SectionWrapper bioSection = new SectionWrapper().init(get("investigator.section.bio"), bioTable);
        SectionWrapper tokenSection = new SectionWrapper().init(get("investigator.section.tokens"), tokensTable);
        SectionWrapper statsSection = new SectionWrapper().init(get("investigator.section.skills"), statsTable);
        SectionWrapper specialSection = new SectionWrapper().init(get("investigator.section.special"), specialTable);

        add(bioSection).align(Align.topLeft).width(bioSection.getWidth()).height(bioSection.getHeight());
        add(statsSection).align(Align.topRight).width(statsSection.getWidth()).height(statsSection.getHeight());
        add(tokenSection).align(Align.top).width(tokenSection.getWidth()).height(tokenSection.getHeight());
        row();
        add(specialSection).colspan(3).align(Align.topLeft).width(specialSection.getWidth()).height(specialSection.getHeight());

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
        padLeft(35);
        padBottom(20);

        HorizontalGroup firstRow = new HorizontalGroup();
        HorizontalGroup secondRow = new HorizontalGroup();

        List<CardClickListener> cardClickListeners = new LinkedList<>();
        List<CardTemplate> cardTemplates = new LinkedList<>();
        if (!assets.isEmpty()) {
            CardsTable assetsTable = new CardsTable();
            int width = calculateWidthFactor(assets.size(), uniqueAssets.size(), artifacts.size());
            assetsTable.init(assets, width);
            cardClickListeners.addAll(assetsTable.getCardClickListeners());
            cardTemplates.addAll(assetsTable.getCardTemplates());
            SectionWrapper assetsSection = new SectionWrapper().init(get("investigator.section.assets"), assetsTable);
            firstRow.addActor(new Container<>(assetsSection).width(assetsSection.getWidth()).height(assetsSection.getHeight()));
        }
        if (!uniqueAssets.isEmpty()) {
            CardsTable uniqueAssetsTable = new CardsTable();
            int width = calculateWidthFactor(uniqueAssets.size(), assets.size(), artifacts.size());
            uniqueAssetsTable.init(uniqueAssets, width);
            cardClickListeners.addAll(uniqueAssetsTable.getCardClickListeners());
            cardTemplates.addAll(uniqueAssetsTable.getCardTemplates());
            SectionWrapper uniqueAssetsSection = new SectionWrapper().init(get("investigator.section.uniqueAssets"), uniqueAssetsTable);
            firstRow.addActor(new Container<>(uniqueAssetsSection).width(uniqueAssetsSection.getWidth()).height(uniqueAssetsSection.getHeight()));
        }
        if (!artifacts.isEmpty()) {
            CardsTable artifactsTable = new CardsTable();
            int width = calculateWidthFactor(artifacts.size(), assets.size(), uniqueAssets.size());
            artifactsTable.init(artifacts, width);
            cardClickListeners.addAll(artifactsTable.getCardClickListeners());
            cardTemplates.addAll(artifactsTable.getCardTemplates());
            SectionWrapper artifactsSection = new SectionWrapper().init(get("investigator.section.artifacts"), artifactsTable);
            firstRow.addActor(new Container<>(artifactsSection).width(artifactsSection.getWidth()).height(artifactsSection.getHeight()));
        }
        add(firstRow).width(726).align(Align.topLeft).row();


        if (!spells.isEmpty()) {
            CardsTable spellsTable = new CardsTable();
            int width = calculateWidthFactor(spells.size(), conditions.size());
            spellsTable.init(spells, width);
            cardClickListeners.addAll(spellsTable.getCardClickListeners());
            cardTemplates.addAll(spellsTable.getCardTemplates());
            SectionWrapper spellsSection = new SectionWrapper().init(get("investigator.section.spells"), spellsTable);
            secondRow.addActor(new Container<>(spellsSection).width(spellsSection.getWidth()).height(spellsSection.getHeight()));
        }
        if (!conditions.isEmpty()) {
            CardsTable conditionsTable = new CardsTable();
            int width = calculateWidthFactor(conditions.size(), spells.size());
            conditionsTable.init(conditions, width);
            cardClickListeners.addAll(conditionsTable.getCardClickListeners());
            cardTemplates.addAll(conditionsTable.getCardTemplates());
            SectionWrapper conditionsSection = new SectionWrapper().init(get("investigator.section.conditions"), conditionsTable);
            secondRow.addActor(new Container<>(conditionsSection).width(conditionsSection.getWidth()).height(conditionsSection.getHeight()));
        }

        for (CardClickListener cardClickListener : cardClickListeners) {
            cardClickListener.setOtherListeners(cardClickListeners);
            cardClickListener.setAllTemplates(cardTemplates);
        }
        add(secondRow).width(726).height(223).align(Align.topLeft);

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

    private void prepareBackgroundTab() {
        clear();
        padLeft(35);
        padBottom(20);

        BackgroundBioTable backgroundBioTable = new BackgroundBioTable();
        backgroundBioTable.init(bioTableData.getBackgroundBio());
        SectionWrapper backgroundSection = new SectionWrapper().init(get("investigator.section.background"), backgroundBioTable);
        add(backgroundSection).align(Align.topLeft)
                .width(backgroundSection.getWidth())
                .height(backgroundSection.getHeight())
                .padBottom(135);


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

        addHitImage();
        buttons.initButtons();
        buttons.highlightBio();

        prepareCommon();

    }

    private void prepareCommon() {
        buttons.setPosition(0, 0);
        addActor(buttons);
        pack();

        setTransform(true);
    }

    private void addHitImage() {
        hitImage = new Image(CustomAssetManager.getTextureRegion(CustomAssetManager.PURE_WHITE_BACKGROUND));
        addActor(hitImage);
        hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        hitImage.getColor().a = 0;
        addClickListener(hitImage, BigActorsManager::displayOrHidePassport);
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }

    private int calculateWidthFactor(int currentListSize, int... listSizes) {
        if (currentListSize == 1) {
            return 195;
        }

        int widthAvailable = 700;
        int undecided = 0;
        for (int listSize : listSizes) {
            Integer finalWidth = getFinalWidth(listSize);
            if (finalWidth != null) {
                widthAvailable -= finalWidth;
            } else {
                widthAvailable -= 28;
                undecided++;
            }
        }

        int fairWidth = (int) (widthAvailable / (float) (undecided + 1));

        widthAvailable = 700;
        for (int listSize : listSizes) {
            widthAvailable -= Math.max(listSize * CardsTable.CARD_SCALE * CardTemplate.CARD_WIDTH, 195);
        }
        if (widthAvailable > fairWidth) {
            return widthAvailable;
        }
        return fairWidth;
    }

    private Integer getFinalWidth(int listSize) {
        if (listSize == 0) {
            return 0;
        } else if (listSize == 1) {
            return 195;
        } else {
            return null;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float widthHeightRatio = 1100 / 808f;
        float height = (VIEWPORT_HEIGHT + 60) * getScaleY();
        batch.setColor(Color.WHITE);
        batch.draw(CustomAssetManager.getTexture("passport.png"),
                -35 * getScaleX() + getX() + (1 - getScaleX()) * getWidth() * 0.5f,
                -75 * getScaleY() + getY() + (1 - getScaleY()) * getHeight() * 0.5f,
                height * widthHeightRatio, height);
        super.draw(batch, parentAlpha);
    }


    public void displayOrHide() {
        displayHide.setDisplayedY(8);
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
