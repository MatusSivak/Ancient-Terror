package sk.sivak.eldritchhorror.core.view.components.card;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.TokenCardInfo;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.card.Trait;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;

import java.util.Set;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_CARD_TEMPLATE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_CINZEL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_LIBRE_BASKERVILLE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureAsync;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureRegionDrawable;
import static sk.sivak.eldritchhorror.core.view.utils.MarkupText.markupWithKeywords;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * @author msivak
 */
public class CardTemplate extends WidgetGroup {

    public static final int CARD_WIDTH = 1191;
    public static final int CARD_HEIGHT = 1254;
    private static final int DISABLED_AREA_LEFT = 215;
    private static final int DISABLED_AREA_BOTTOM = 75;

    private ArtifactInfo artifactInfo;
    private ConditionInfo conditionInfo;
    private SpellInfo spellInfo;
    private AssetInfo assetInfo;
    private TokenCardInfo tokenCardInfo;

    private Image foreground;

    private CardTemplate(String textureId,
                         String title,
                         Color titleBackgroundColor,
                         Color titleFontColor,
                         Color descriptionBackgroundColor,
                         Integer cost,
                         Set<? extends Trait> traitSet,
                         Color traitsColor,
                         String description,
                         Color descriptionColor,
                         String cardTypeTexture,
                         boolean isDisabled) {

        loadPicture(textureId, () -> {
            loadCardDescriptionBackground(descriptionBackgroundColor, () -> {
                loadCardTitle(title, titleBackgroundColor, titleFontColor, () -> {
                    loadTemplate( () -> {
                        loadCost(cost);
                        loadCardType(cardTypeTexture);
                        loadTraits(traitSet, traitsColor);
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

    private void loadTraits(Set<? extends Trait> traitSet, Color traitsColor) {
        Trait[] traits;
        if (traitSet == null || traitSet.isEmpty()) {
            return;
        } else {
            traits = traitSet.toArray(new Trait[0]);
        }
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
        CardTemplate cardTemplate = new CardTemplate("card/asset/" + assetInfo.getId().name() + ".jpg",
                assetInfo.getName(),
                Color.valueOf("8C6935"),
                Color.valueOf("FFEEDD"),
                Color.valueOf("DDD0AF"),
                assetInfo.getCost(),
                assetInfo.getTraits(),
                Color.valueOf("3A3124"),
                assetInfo.getDescription(),
                Color.valueOf("000000"),
                "card/card_type_asset.png",
                assetInfo.isDisabled());
		cardTemplate.setAssetInfo(assetInfo);
//		cardTemplate.updateDisabledPositionAndScale();
        return cardTemplate;
    }

    public static CardTemplate buildCard(ConditionInfo conditionInfo) {

        CardTemplate cardTemplate = new CardTemplate("card/condition/" + conditionInfo.getId().name() + ".jpg",
                conditionInfo.getName(),
                Color.valueOf("24272A"),
                Color.valueOf("D9D9D5"),
                Color.valueOf("B9BAB7"),
                null,
                conditionInfo.getTraits(),
                Color.valueOf("24272A"),
                conditionInfo.getDescription(),
                Color.valueOf("18191A"),
                "card/card_type_condition.png",
                conditionInfo.isDisabled());
        cardTemplate.setConditionInfo(conditionInfo);
//		cardTemplate.updateDisabledPositionAndScale();
        return cardTemplate;
    }


    public static CardTemplate buildCard(ArtifactInfo artifactInfo) {

        CardTemplate cardTemplate = new CardTemplate("card/artifact/" + artifactInfo.getId().name() + ".jpg",
                artifactInfo.getName(),
                Color.valueOf("2B6B68"),
                Color.valueOf("E8E5DA"),
                Color.valueOf("C6D2CE"),
                null,
                artifactInfo.getTraits(),
                Color.valueOf("3D7264"),
                artifactInfo.getDescription(),
                Color.valueOf("1A201E"),
                "card/card_type_artifact.png",
                artifactInfo.isDisabled());
        cardTemplate.setArtifactInfo(artifactInfo);
//		cardTemplate.updateDisabledPositionAndScale();
        return cardTemplate;
    }

    public static CardTemplate buildCard(SpellInfo spellInfo) {

        CardTemplate cardTemplate = new CardTemplate("card/spell/" + spellInfo.getId().name() + ".jpg",
                spellInfo.getName(),
                Color.valueOf("583878"),
                Color.valueOf("F0E8F4"),
                Color.valueOf("C9C1D0"),
                null,
                spellInfo.getTraits(),
                Color.valueOf("684183"),
                spellInfo.getDescription(),
                Color.valueOf("211B24"),
                "card/card_type_spell.png",
                spellInfo.isDisabled());
        cardTemplate.setSpellInfo(spellInfo);
//		cardTemplate.updateDisabledPositionAndScale();
        return cardTemplate;
    }

    public static CardTemplate buildClueCard() {
        CardTemplate cardTemplate = new CardTemplate("card/special/clue.jpg",
                get("card.clue.title"),
                Color.valueOf("583878"),
                Color.valueOf("F0E8F4"),
                Color.valueOf("C9C1D0"),
                null,
                null,
                Color.valueOf("684183"),
                get("card.clue.description"),
                Color.valueOf("211B24"),
                null,
                false);

        cardTemplate.tokenCardInfo = TokenCardInfo.buildClueTokenCardInfo();
//        cardTemplate.updateDisabledPositionAndScale();
        return cardTemplate;
    }

    public static CardTemplate buildTrainTicketCard() {

        CardTemplate cardTemplate = new CardTemplate("card/special/train.jpg",
                get("card.trainTicket.title"),
                Color.valueOf("583878"),
                Color.valueOf("F0E8F4"),
                Color.valueOf("C9C1D0"),
                null,
                null,
                Color.valueOf("684183"),
                get("card.trainTicket.description"),
                Color.valueOf("211B24"),
                null,
                false);

        cardTemplate.tokenCardInfo = TokenCardInfo.buildTrainTicketTokenCardInfo();
//        cardTemplate.updateDisabledPositionAndScale();
        return cardTemplate;
    }

    public static CardTemplate buildShipTicketCard() {

        CardTemplate cardTemplate = new CardTemplate("card/special/ship.jpg",
                get("card.shipTicket.title"),
                Color.valueOf("583878"),
                Color.valueOf("F0E8F4"),
                Color.valueOf("C9C1D0"),
                null,
                null,
                Color.valueOf("684183"),
                get("card.shipTicket.description"),
                Color.valueOf("211B24"),
                null,
                false);

        cardTemplate.tokenCardInfo = TokenCardInfo.buildShipTicketTokenCardInfo();
//        cardTemplate.updateDisabledPositionAndScale();
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
            /*
            disabled.addAction(new FastForwardAction<>(sequence(
                    alpha(0.75f, ViewProperties.FADING_EFFECT_DURATION),
                    delay(FAST_ACTION_DURATION),
                    Actions.run(onSub::onCompleted))
            ));

             */
        });

    }

    private void updateDisabledPositionAndScale() {
        /*
        DisabledCardConfigProvider.Config disabledCardConfig = DisabledCardConfigProvider.getConfig(getCardInfo().getId());
        float scale = 0.45f;
        disabled.setScale(scale);
        disabled.setPosition(DISABLED_AREA_LEFT, DISABLED_AREA_BOTTOM);
        disabled.setScale(disabledCardConfig.getScaleMultiplier() * disabled.getScaleY());
        disabled.setY(disabled.getY() + disabledCardConfig.getOffsetY());
        float width = scale * disabled.getWidth();
        float newWidth = scale * disabledCardConfig.getScaleMultiplier() * disabled.getWidth();
        disabled.setX(disabled.getX() + (width-newWidth) /2f);

         */
    }
}
