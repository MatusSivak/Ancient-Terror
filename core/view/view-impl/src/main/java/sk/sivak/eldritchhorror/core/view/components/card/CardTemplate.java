package sk.sivak.eldritchhorror.core.view.components.card;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.TokenCardInfo;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.card.Trait;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.FAST_ACTION_DURATION;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ARTIFACT_CARD_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.CARD_CIRCLE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.CARD_TEMPLATE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.CONDITION_CARD_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.DISABLED_CARD;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_BLACK_CHANCERY;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_GOBLIN_ONE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_CARD_TEMPLATE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_CINZEL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_LIBRE_BASKERVILLE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PICTURE_FILTER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.RECKONING;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.SPELL_CARD_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.TOKEN_CARD_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureAsync;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureRegionDrawable;
import static sk.sivak.eldritchhorror.core.view.utils.MarkupText.markupWithKeywords;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * @author msivak
 */
public class CardTemplate extends WidgetGroup {

    private CardTemplate(String textureId,
                            String title,
                            Color titleBackgroundColor,
                            Color titleFontColor,
                            Color descriptionBackgroundColor,
                            Integer cost,
                            Trait[] traits,
                            Color traitsColor,
                            String description,
                            Color descriptionColor,
                            String cardTypeTexture,
                            String reckoningText,
                            boolean isDisabled) {

        long start = System.currentTimeMillis();
        loadPicture(textureId, () -> {
            loadCardDescriptionBackground(descriptionBackgroundColor, () -> {
                loadCardTitle(title, titleBackgroundColor, titleFontColor, () -> {
                    loadTemplate( () -> {
                        loadCost(cost);
                        loadCardType(cardTypeTexture);
                        loadTraits(traits, traitsColor);
                        loadDescription(description, descriptionColor);
                        loadForeground();
                    });
                });
            });
        });
    }

	private void loadForeground() {
        getTextureAsync(PURE_WHITE_BACKGROUND).subscribe(t -> {
            foreground = new Image(getTextureRegionDrawable(PURE_WHITE_BACKGROUND));
            foreground.setPosition(0, 0);
            foreground.setSize(CARD_WIDTH, CARD_HEIGHT);
            foreground.setColor(new Color(0, 0, 0, 0f));
            addActor(foreground);
        });
	}

    private void loadTemplate(Runnable callback) {
        getTextureAsync(NEW_CARD_TEMPLATE).subscribe(ct -> {
            addActor(new Image(getTextureRegionDrawable(NEW_CARD_TEMPLATE)));
            callback.run();
        });
    }

    private void loadPicture(String textureId, Runnable callback) {
        getTextureAsync(textureId).subscribe(t -> {
			Image picture = new Image(getTextureRegionDrawable(textureId));
            picture.setScale(1.62f);
            picture.setPosition(30,665);
            addActor(picture);
            callback.run();
        });
    }

    private void loadCardTitle(String title,
                               Color titleBackgroundColor,
                               Color titleFontColor,
                               Runnable callback) {
        String cardTitleBackgroundTexture = "card/card_title_background.png";
        getTextureAsync(cardTitleBackgroundTexture).subscribe(t -> {
            Image titleBackground = new Image(getTextureRegionDrawable(cardTitleBackgroundTexture));
            titleBackground.setColor(titleBackgroundColor);
            addActor(titleBackground);

            Label titleLabel = createTitleLabel(title, titleFontColor);
            titleLabel.setPosition(310,618);
            titleLabel.setFontScale(Math.min(0.7f, 13f / title.length()));
            addActor(titleLabel);
            callback.run();
        });
    }

    private void loadCardDescriptionBackground(Color descriptionBackgroundColor,
                                               Runnable callback) {
        String cardDescriptionBackgroundTexture = "card/card_description_background.png";
        getTextureAsync(cardDescriptionBackgroundTexture).subscribe(t -> {
            Image descriptionBackground = new Image(getTextureRegionDrawable(cardDescriptionBackgroundTexture));
            descriptionBackground.setColor(descriptionBackgroundColor);
            addActor(descriptionBackground);
            callback.run();
        });
    }

    private void loadCost(Integer cost) {
        if (cost == null || cost < 0) {
            return;
        }
        String costTexture = "card/asset_value_"+cost+".png";
        getTextureAsync(costTexture).subscribe(t -> {
            Image costImage = new Image(getTextureRegionDrawable(costTexture));
            costImage.setOrigin(Align.center);
            costImage.setScale(1.25f);
            costImage.setPosition(85 - costImage.getWidth()/2f, 1175-costImage.getHeight()/2f);
            if (foreground != null) {
                addActorBefore(foreground, costImage);
            } else {
                addActor(costImage);
            }
        });
    }

    private void loadCardType(String cardTypeTexture) {
        if (cardTypeTexture == null) {
            return;
        }
        getTextureAsync(cardTypeTexture).subscribe(t -> {
            Image cardTypeImage = new Image(getTextureRegionDrawable(cardTypeTexture));
            cardTypeImage.setOrigin(Align.center);
            cardTypeImage.setScale(0.6f);
            cardTypeImage.setPosition(1110 - cardTypeImage.getWidth()/2f,
                    1175-cardTypeImage.getHeight()/2f);
            if (foreground != null) {
                addActorBefore(foreground, cardTypeImage);
            } else {
                addActor(cardTypeImage);
            }
        });
    }

    private void loadTraits(Trait[] traits, Color traitsColor) {
        Label traitsLabel = createTraitsLabel(traits, traitsColor);
        traitsLabel.setPosition(195,515);
        traitsLabel.setWidth(800);
        traitsLabel.setFontScale(0.8f);
        if (foreground != null) {
            addActorBefore(foreground, traitsLabel);
        } else {
            addActor(traitsLabel);
        }
    }

    private void loadDescription(String description, Color descriptionTextColor) {
        String hex = String.format(
                "%02X%02X%02X",
                Math.round(descriptionTextColor.r * 255),
                Math.round(descriptionTextColor.g * 255),
                Math.round(descriptionTextColor.b * 255)
        );
        Label descriptionLabel = createDescriptionLabel(description, hex);
        descriptionLabel.setWidth(1050);
        descriptionLabel.setHeight(400);
        descriptionLabel.setFontScale(0.9f);
        descriptionLabel.setPosition(70,120);
        if (foreground != null) {
            addActorBefore(foreground, descriptionLabel);
        } else {
            addActor(descriptionLabel);
        }
    }

    public static final int CARD_WIDTH = 1191;
    public static final int CARD_HEIGHT = 1254;
    private static final int IMAGE_AREA_LEFT = 20;
    private static final int IMAGE_AREA_BOTTOM = 602;
    private static final int LABEL_AREA_LEFT = 370;
    private static final int LABEL_AREA_BOTTOM = 550;
    private static final int CARD_TYPE_LABEL_AREA_LEFT = 370;
    private static final int CARD_TYPE_LABEL_AREA_BOTTOM = 20;
//    private static final int RIBBON_AREA_LEFT = 10;
//    private static final int RIBBON_AREA_BOTTOM = 310;
    private static final int BACKGROUND_AREA_LEFT = 20;
    private static final int BACKGROUND_AREA_BOTTOM = 75;
    private static final int CIRCLE_AREA_LEFT = 313;
    private static final int CIRCLE_AREA_BOTTOM = 510;
    private static final int RECKONING_AREA_LEFT = 615;
    private static final int RECKONING_AREA_BOTTOM = 80;
    private static final int DISABLED_AREA_LEFT = 215;
    private static final int DISABLED_AREA_BOTTOM = 75;
    private static final int COST_AREA_LEFT = 370;
    private static final int COST_AREA_BOTTOM = 530;
    private static final int TRAIT_AREA_WIDTH = 661;
    private static final int TRAIT_AREA_LEFT = 40;
    private static final int TRAIT_AREA_BOTTOM = 395;
    private static final int DESCRIPTION_AREA_WIDTH = 641;
    private static final int DESCRIPTION_AREA_HEIGHT = 265;
    private static final int DESCRIPTION_AREA_LEFT = 50;
    private static final int DESCRIPTION_AREA_BOTTOM = 120; // dont touch

    private ArtifactInfo artifactInfo;
    private ConditionInfo conditionInfo;
    private SpellInfo spellInfo;
    private AssetInfo assetInfo;
    private TokenCardInfo tokenCardInfo;

//    private Image ribbon;
    private Label titleLabel;
    private Label cardTypeLabel;
    private Image circle;
    private Image reckoning;
    private Image disabled;
    private Label costLabel;
    private Label traitsLabel;
    private Label descriptionLabel;
    private Image foreground;
    private Image cardTemplate;
    private Image pictureFilter;

    private CardTemplate(String textureId,
                         String title,
                         Color traitsColor,
                         Color ribbonColor,
                         Color titleColor,
                         String descriptionColor,
                         Integer cost,
                         Trait[] traits,
                         String description,
                         String cardType,
                         boolean hasReckoning,
                         String backgroundId,
                         Color cardTemplateColor,
                         boolean isDisabled) {
        cardTemplate = new Image(getTextureRegionDrawable(CARD_TEMPLATE));

        CustomAssetManager.getTextureAsync(backgroundId).subscribe(b -> {
            Image background = new Image(getTextureRegionDrawable(backgroundId));
            background.setPosition(BACKGROUND_AREA_LEFT, BACKGROUND_AREA_BOTTOM);
            addActorAt(0, background);

            CustomAssetManager.getTextureAsync(textureId).subscribe(t -> {
                Image picture = new Image(getTextureRegionDrawable(textureId));
                picture.setPosition(IMAGE_AREA_LEFT, IMAGE_AREA_BOTTOM);
                addActorAt(1, picture);

                CustomAssetManager.getTextureAsync(PICTURE_FILTER).subscribe(f -> {
                    pictureFilter = new Image(getTextureRegionDrawable(PICTURE_FILTER));
                    pictureFilter.setPosition(IMAGE_AREA_LEFT, IMAGE_AREA_BOTTOM);
                    pictureFilter.getColor().a = 0.75f;
                    addActorAt(2, pictureFilter);
                });
            });
        });


        foreground = new Image(getTextureRegionDrawable(PURE_WHITE_BACKGROUND));

        titleLabel = createTitleLabel(title, titleColor);
        cardTypeLabel = createCardTypeLabel(cardType);
        if (traits != null) {
            traitsLabel = createTraitsLabel(traits, traitsColor);
        }

        if (cost != null && cost < 0) {
            cost = 0;
        }
        if (cost != null) {
            costLabel = createCostLabel(cost);
            circle = new Image(getTextureRegionDrawable(CARD_CIRCLE));
        }

        if (hasReckoning) {
            reckoning = new Image(getTextureRegionDrawable(RECKONING));
        }
        disabled = new Image(getTextureRegionDrawable(DISABLED_CARD));

        descriptionLabel = createDescriptionLabel(description, descriptionColor);


        foreground.setPosition(0, 0);
        foreground.setSize(CARD_WIDTH, CARD_HEIGHT);
        foreground.setColor(new Color(0, 0, 0, 0f));
        titleLabel.setPosition(LABEL_AREA_LEFT, LABEL_AREA_BOTTOM);
        cardTypeLabel.setPosition(CARD_TYPE_LABEL_AREA_LEFT, CARD_TYPE_LABEL_AREA_BOTTOM);
        if (circle != null) {
            circle.setPosition(CIRCLE_AREA_LEFT, CIRCLE_AREA_BOTTOM);
        }
        if (reckoning != null) {
            reckoning.setPosition(RECKONING_AREA_LEFT, RECKONING_AREA_BOTTOM);
        }

        if (costLabel != null) {
            costLabel.setPosition(COST_AREA_LEFT - costLabel.getWidth() / 2, COST_AREA_BOTTOM);
        }
        if (traitsLabel != null) {
            traitsLabel.setPosition(TRAIT_AREA_LEFT, TRAIT_AREA_BOTTOM);
        }
        descriptionLabel.setPosition(DESCRIPTION_AREA_LEFT, DESCRIPTION_AREA_BOTTOM);

        cardTemplate.setColor(cardTemplateColor);
        addActor(cardTemplate);
        addActor(titleLabel);
        addActor(cardTypeLabel);
        if (circle != null) {
            addActor(circle);
        }
        if (costLabel != null) {
            addActor(costLabel);
        }
        if (traitsLabel != null) {
            addActor(traitsLabel);
        }
        if (reckoning != null) {
            addActor(reckoning);
        }
        addActor(descriptionLabel);
        addActor(disabled);

        addActor(foreground);

        if (traitsLabel != null) {
            traitsLabel.setFontScale(1.2f);
        }
        titleLabel.setFontScale(0.75f);
        cardTypeLabel.setFontScale(1.1f);
        if (circle != null) {
            circle.setScale(0.45f);
        }
        if (reckoning != null) {
            reckoning.setScale(0.2f);
        }
        disabled.getColor().a = 0.75f;
        if (!isDisabled) {
            disabled.getColor().a = 0f;
        }
        descriptionLabel.setFontScale(0.86f);
    }

    public static CardTemplate buildCard(CardInfo cardInfo) {
        if (cardInfo instanceof AssetInfo) {
            return buildCard(((AssetInfo) cardInfo));
        } else if (cardInfo instanceof ConditionInfo) {
            return buildCard(((ConditionInfo) cardInfo));
        } else if (cardInfo instanceof SpellInfo) {
            return buildCard(((SpellInfo) cardInfo));
        } else if (cardInfo instanceof ArtifactInfo) {
            return buildCard(((ArtifactInfo) cardInfo));
        } else if (cardInfo instanceof TokenCardInfo) {
            if (((TokenCardInfo) cardInfo).isShipTicket()) {
                return buildShipTicketCard();
            } else if (((TokenCardInfo) cardInfo).isTrainTicket()) {
                return buildTrainTicketCard();
            } else if (((TokenCardInfo) cardInfo).isClue()) {
                return buildClueCard();
            } else {
                throw new IllegalArgumentException("No idea what this token represents");
            }
        } else {
            throw new IllegalArgumentException("Not supported card template :" + cardInfo.getClass());
        }
    }

    public static CardTemplate buildCard(AssetInfo assetInfo) {
        Trait[] traits;
        if (assetInfo.getTraits() == null || assetInfo.getTraits().size() == 0) {
            traits = null;
        } else {
            traits = assetInfo.getTraits().toArray(new Trait[assetInfo.getTraits().size()]);
        }
        CardTemplate cardTemplate = new CardTemplate("card/asset/" + assetInfo.getId().name() + ".jpg",
                assetInfo.getName(),
                Color.valueOf("8C6935"),
                Color.valueOf("FFEEDD"),
                Color.valueOf("DDD0AF"),
                assetInfo.getCost(),
                traits,
                Color.valueOf("3A3124"),
                assetInfo.getDescription(),
                Color.valueOf("000000"),
                "card/card_type_asset.png",
                null,
                assetInfo.isDisabled());
		cardTemplate.setAssetInfo(assetInfo);
//		cardTemplate.updateDisabledPositionAndScale();
        return cardTemplate;
    }

    public static CardTemplate buildCard(ConditionInfo conditionInfo) {
        Trait[] traits;
        if (conditionInfo.getTraits() == null || conditionInfo.getTraits().size() == 0) {
            traits = null;
        } else {
            traits = conditionInfo.getTraits().toArray(new Trait[conditionInfo.getTraits().size()]);
        }
        CardTemplate cardTemplate = new CardTemplate("card/condition/" + conditionInfo.getId().name() + ".jpg",
                conditionInfo.getName(),
                Color.valueOf("000cb3"),
                Color.valueOf("43544E"),
                Color.WHITE,
                "000000",
                null,
                traits,
                conditionInfo.getDescription(),
                get("card.type.condition"),
                conditionInfo.hasReckoning(),
                CONDITION_CARD_BACKGROUND,
                Color.DARK_GRAY, conditionInfo.isDisabled());
        cardTemplate.setConditionInfo(conditionInfo);
        cardTemplate.updateDisabledPositionAndScale();
        return cardTemplate;
    }

    public static CardTemplate buildCard(ArtifactInfo artifactInfo) {
        Trait[] traits;
        if (artifactInfo.getTraits() == null || artifactInfo.getTraits().size() == 0) {
            traits = null;
        } else {
            traits = artifactInfo.getTraits().toArray(new Trait[artifactInfo.getTraits().size()]);
        }
        CardTemplate cardTemplate = new CardTemplate("card/artifact/" + artifactInfo.getId().name() + ".jpg",
                artifactInfo.getName(),
                Color.valueOf("000cb3"),
                Color.valueOf("186200"),
                Color.valueOf("e4ab1f"),
                "000000",
                null,
                traits,
                artifactInfo.getDescription(),
                get("card.type.artifact"),
                artifactInfo.hasReckoning(),
                ARTIFACT_CARD_BACKGROUND,
                Color.YELLOW, artifactInfo.isDisabled());
        cardTemplate.setArtifactInfo(artifactInfo);
        cardTemplate.updateDisabledPositionAndScale();
        return cardTemplate;
    }

    public static CardTemplate buildCard(SpellInfo spellInfo) {
        Trait[] traits;
        if (spellInfo.getTraits() == null || spellInfo.getTraits().size() == 0) {
            traits = null;
        } else {
            traits = spellInfo.getTraits().toArray(new Trait[spellInfo.getTraits().size()]);
        }
        CardTemplate cardTemplate = new CardTemplate("card/spell/" + spellInfo.getId().name() + ".jpg",
                spellInfo.getName(),
                Color.valueOf("000cb3"),
                Color.valueOf("6F00B5"),
                Color.WHITE,
                "000000",
                null,
                traits,
                spellInfo.getDescription(),
                get("card.type.spell"),
                spellInfo.hasReckoning(),
                SPELL_CARD_BACKGROUND,
                Color.PURPLE, spellInfo.isDisabled());
        cardTemplate.setSpellInfo(spellInfo);
        cardTemplate.updateDisabledPositionAndScale();
        return cardTemplate;
    }

    public static CardTemplate buildClueCard() {

        CardTemplate cardTemplate = new CardTemplate("card/special/clue.jpg",
                get("card.clue.title"),
                Color.valueOf("000cb3"),
                Color.valueOf("2B7529"),
                Color.WHITE,
                "000000",
                null,
                null,
                get("card.clue.description"),
                get("card.type.token"),
                false,
                TOKEN_CARD_BACKGROUND,
                Color.GREEN,
                false);
        cardTemplate.tokenCardInfo = TokenCardInfo.buildClueTokenCardInfo();
        cardTemplate.updateDisabledPositionAndScale();
        return cardTemplate;
    }

    public static CardTemplate buildTrainTicketCard() {

        CardTemplate cardTemplate = new CardTemplate("card/special/train.jpg",
                get("card.trainTicket.title"),
                Color.valueOf("000cb3"),
                Color.valueOf("2B7529"),
                Color.WHITE,
                "000000",
                null,
                null,
                get("card.trainTicket.description"),
                get("card.type.token"),
                false,
                TOKEN_CARD_BACKGROUND,
                Color.GREEN, false);
        cardTemplate.tokenCardInfo = TokenCardInfo.buildTrainTicketTokenCardInfo();
        cardTemplate.updateDisabledPositionAndScale();
        return cardTemplate;
    }

    public static CardTemplate buildShipTicketCard() {

        CardTemplate cardTemplate = new CardTemplate("card/special/ship.jpg",
                get("card.shipTicket.title"),
                Color.valueOf("000cb3"),
                Color.valueOf("2B7529"),
                Color.WHITE,
                "000000",
                null,
                null,
                get("card.shipTicket.description"),
                get("card.type.token"),
                false,
                TOKEN_CARD_BACKGROUND,
                Color.GREEN, false);
        cardTemplate.tokenCardInfo = TokenCardInfo.buildShipTicketTokenCardInfo();
        cardTemplate.updateDisabledPositionAndScale();
        return cardTemplate;
    }


    public CardInfo getCardInfo() {
        if (getAssetInfo() != null) {
            return getAssetInfo();
        } else if (getSpellInfo() != null) {
            return getSpellInfo();
        } else if (getConditionInfo() != null) {
            return getConditionInfo();
        } else if (getTokenCardInfo() != null) {
            return getTokenCardInfo();
        } else if (getArtifactInfo() != null) {
            return getArtifactInfo();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    private void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    private Label createTitleLabel(String title, Color titleColor) {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = getBitmapFontNew(NEW_FONT_LIBRE_BASKERVILLE);
        style.fontColor = titleColor;
        Label label = new Label(title, style);
        label.setWidth(580);
        label.setAlignment(Align.center, Align.center);
        return label;
    }

    private Label createCardTypeLabel(String title) {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = getBitmapFont(FONT_BLACK_CHANCERY);
        style.fontColor = Color.WHITE;
        Label label = new Label(title, style);
        label.setAlignment(Align.center, Align.center);
        return label;
    }

    private Label createTraitsLabel(Trait[] traits, Color traitsColor) {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = getBitmapFontNew(NEW_FONT_CINZEL);
        style.fontColor = traitsColor;
        String value = "";
        for (int i = 0; i < traits.length; i++) {
            value += traits[i].asString();
            if (i != traits.length - 1) {
                value += " • ";
            }
        }
        Label label = new Label(value, style);
        label.setAlignment(Align.center, Align.center);
        return label;
    }

    private Label createCostLabel(Integer cost) {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = getBitmapFont(FONT_GOBLIN_ONE);
        style.fontColor = new Color(0x00ff00ff);
        return new Label("" + cost, style);
    }

    private Label createDescriptionLabel(String description, String descriptionColor) {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4);

        Label label = new Label(markupWithKeywords(description, descriptionColor), style);
        style.font.getData().markupEnabled = true;
        label.setWrap(true);
        label.setAlignment(Align.center, Align.center);
        return label;
    }

    public void setForegroundColor(Color color) {
        foreground.setColor(color);
    }

    public Image getForeground() {
        return foreground;
    }

    public ConditionInfo getConditionInfo() {
        return conditionInfo;
    }

    public TokenCardInfo getTokenCardInfo() {
        return tokenCardInfo;
    }

    public void setConditionInfo(ConditionInfo conditionInfo) {
        this.conditionInfo = conditionInfo;
    }

    public void setArtifactInfo(ArtifactInfo artifactInfo) {
        this.artifactInfo = artifactInfo;
    }

    public void setSpellInfo(SpellInfo spellInfo) {
        this.spellInfo = spellInfo;
    }

    public SpellInfo getSpellInfo() {
        return spellInfo;
    }

    public ArtifactInfo getArtifactInfo() {
        return artifactInfo;
    }

    public Completable disable() {
        return Completable.create(onSub -> {
            updateDisabledPositionAndScale();
            disabled.addAction(new FastForwardAction<>(sequence(
                    alpha(0.75f, ViewProperties.FADING_EFFECT_DURATION),
                    delay(FAST_ACTION_DURATION),
                    Actions.run(onSub::onCompleted))
            ));
        });

    }

    private void updateDisabledPositionAndScale() {
        DisabledCardConfigProvider.Config disabledCardConfig = DisabledCardConfigProvider.getConfig(getCardInfo().getId());
        float scale = 0.45f;
        disabled.setScale(scale);
        disabled.setPosition(DISABLED_AREA_LEFT, DISABLED_AREA_BOTTOM);
        disabled.setScale(disabledCardConfig.getScaleMultiplier() * disabled.getScaleY());
        disabled.setY(disabled.getY() + disabledCardConfig.getOffsetY());
        float width = scale * disabled.getWidth();
        float newWidth = scale * disabledCardConfig.getScaleMultiplier() * disabled.getWidth();
        disabled.setX(disabled.getX() + (width-newWidth) /2f);
    }
}
