package sk.sivak.eldritchhorror.core.view.asset;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import rx.Completable;
import rx.CompletableSubscriber;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.components.card.TornApartCardBuilder;
import sk.sivak.eldritchhorror.core.view.components.table.LabelTable;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.CardClickListener;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.Collections;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.color;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.FADING_EFFECT_DURATION;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType.CENTER;
import static sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType.INVENTORY;
import static sk.sivak.eldritchhorror.core.constants.displayasset.HideType.DISABLE_ALWAYS;
import static sk.sivak.eldritchhorror.core.constants.displayasset.HideType.DISCARD_ALWAYS;
import static sk.sivak.eldritchhorror.core.constants.displayasset.HideType.DISCARD_ON_NO;
import static sk.sivak.eldritchhorror.core.constants.displayasset.HideType.DISCARD_ON_YES;
import static sk.sivak.eldritchhorror.core.constants.displayasset.HideType.INVENTORY_ON_NO;
import static sk.sivak.eldritchhorror.core.constants.displayasset.HideType.INVENTORY_ON_YES;
import static sk.sivak.eldritchhorror.core.constants.displayasset.HideType.RETURN;
import static sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse.NO;
import static sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse.NOTHING;
import static sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse.OK;
import static sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse.YES;
import static sk.sivak.eldritchhorror.core.view.components.card.CardTemplate.CARD_HEIGHT;
import static sk.sivak.eldritchhorror.core.view.components.card.CardTemplate.CARD_WIDTH;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * @author msivak
 */
public class AssetView {

    private static final int INVENTORY_X = 36;
    private static final int INVENTORY_Y = 94;

    public static final int CARD_ORIGIN_X = CARD_WIDTH / 2;
    public static final int CARD_ORIGIN_Y = CARD_HEIGHT / 2;
    private final static float ACTION_DURATION = FADING_EFFECT_DURATION * 2;
    private Actor cardTemplate;
    private LabelTable labelTable;
    private Button[] buttons;
    private float originalBottomHeight;

    public Single<ShowCardResponse> showCard(ShowCardRequest showCardRequest) {
        return Single.create(subscriber -> {
            originalBottomHeight = InfoStage.getBottomHeight();
            displayButtons(showCardRequest.getDisplayAssetResponseType(), showCardRequest.getHideType(),
                    showCardRequest.getAssetOriginType(), subscriber);
            displayTable(showCardRequest.getTitle(), showCardRequest.getPortraitTextureName());
            if (showCardRequest.isHiddenTemplate()) {
                displayHiddentTemplate(showCardRequest.getAssetOriginType());
            } else if (showCardRequest.getAssetInfo() != null) {
                displayCard(showCardRequest.getAssetInfo(), showCardRequest.getAssetOriginType());
            } else if (showCardRequest.getArtifactInfo() != null) {
                displayCard(showCardRequest.getArtifactInfo(), showCardRequest.getAssetOriginType());
            } else if (showCardRequest.getConditionInfo() != null) {
                displayCard(showCardRequest.getConditionInfo(), showCardRequest.getAssetOriginType());
            } else if (showCardRequest.getSpellInfo() != null) {
                displayCard(showCardRequest.getSpellInfo(), showCardRequest.getAssetOriginType());
            }
        });
    }

    private void displayTable(String title, String portraitTextureName) {
        if (title == null) {
            return;
        }
        if (portraitTextureName != null) {
            labelTable = LabelTable.createAndShowTable(ACTION_DURATION * 1.5f, title, portraitTextureName, Collections.emptyList(), 310);
        } else {
            labelTable = LabelTable.createAndShowTable(ACTION_DURATION * 1.5f, title);
        }
    }

    private void displayButtons(DisplayAssetResponseType displayAssetResponseType, HideType hideType, AssetOriginType assetOriginType,
                                SingleSubscriber<? super ShowCardResponse> subscriber) {
        float bottomHeight = InfoStage.getBottomHeight();
        switch (displayAssetResponseType) {
            case YES_NO:
                buttons = ButtonUtils.buildNoYesButtons(FADING_EFFECT_DURATION * 2,
                        () -> buttonClickAction(subscriber, NO, hideType, assetOriginType),
                        () -> buttonClickAction(subscriber, YES, hideType, assetOriginType));
                break;
            case OK:
                TextButton okButton = ButtonBuilder.buildButton(get("dialog.ok"));

                buttons = new Button[1];
                buttons[0] = okButton;

                InfoStage.setBottomHeight(InfoStage.getBottomHeight() + okButton.getHeight() + 5);

                ButtonUtils.addClickListener(okButton, () -> buttonClickAction(subscriber, OK, hideType, assetOriginType));

                InfoStage.addActionToInfoStage(new FastForwardAction(new SequenceAction(Actions.delay(ACTION_DURATION/2f), Actions.run(() -> {
                    InfoStage.showButton(okButton, VIEWPORT_WIDTH / 2 - okButton.getWidth() / 2, bottomHeight);
                }))));
                break;
            case NOTHING:
                if (DISCARD_ALWAYS != hideType && DISABLE_ALWAYS != hideType) {
                    return;
                }
                TextButton discardButton = ButtonBuilder.buildButton(get("dialog.ok"));

                buttons = new Button[1];
                buttons[0] = discardButton;

                InfoStage.setBottomHeight(InfoStage.getBottomHeight() + discardButton.getHeight() + 5);

                ButtonUtils.addClickListener(discardButton, () -> buttonClickAction(subscriber, NOTHING, hideType, assetOriginType));

                InfoStage.addActionToInfoStage(new FastForwardAction(new SequenceAction(Actions.delay(ACTION_DURATION/2f), Actions.run(() -> {
                    InfoStage.showButton(discardButton, VIEWPORT_WIDTH / 2 - discardButton.getWidth() / 2, bottomHeight);
                }))));
                break;
        }
    }

    private void buttonClickAction(SingleSubscriber<? super ShowCardResponse> subscriber,
                                   ShowCardResponse showCardResponse,
                                   HideType hideType,
                                   AssetOriginType assetOriginType) {

        InfoStage.hideActor(labelTable);
        for (Button button : buttons) {
            InfoStage.hideActor(button);
        }
        InfoStage.setBottomHeight(originalBottomHeight);

        if (HideType.RETURN == hideType) {
            returnCard(subscriber, showCardResponse, assetOriginType);
        }
        if (HideType.DISCARD_ALWAYS == hideType) {
            discardCard(subscriber, showCardResponse);
        }
        if (HideType.DISABLE_ALWAYS == hideType) {
            disableCard(subscriber, showCardResponse);
        }

        if (DISCARD_ON_NO == hideType && NO == showCardResponse) {
            discardCard(subscriber, showCardResponse);
        } else if (DISCARD_ON_NO == hideType) {
            returnCard(subscriber, showCardResponse, assetOriginType);
        }


        if (HideType.DISABLE_ON_YES == hideType && YES == showCardResponse) {
            disableCard(subscriber, showCardResponse);
        } else if (HideType.DISABLE_ON_YES == hideType) {
            returnCard(subscriber, showCardResponse, assetOriginType);
        }

        if (DISCARD_ON_YES == hideType && YES == showCardResponse) {
            discardCard(subscriber, showCardResponse);
        } else if (DISCARD_ON_YES == hideType) {
            returnCard(subscriber, showCardResponse, assetOriginType);
        }

        if (INVENTORY_ON_YES == hideType && YES == showCardResponse)  {
            returnCard(subscriber, showCardResponse, INVENTORY);
        } else if (INVENTORY_ON_YES == hideType) {
            returnCard(subscriber, showCardResponse, AssetOriginType.CENTER);
        }

        if (INVENTORY_ON_NO == hideType && NO == showCardResponse)  {
            returnCard(subscriber, showCardResponse, INVENTORY);
        } else if (INVENTORY_ON_NO == hideType) {
            returnCard(subscriber, showCardResponse, AssetOriginType.CENTER);
        }
    }

    private void displayCard(CardInfo cardInfo, AssetOriginType assetOriginType) {
        cardTemplate = CardTemplate.buildCard(cardInfo);
        cardTemplate.setOrigin(CARD_ORIGIN_X, CARD_ORIGIN_Y);
        cardTemplate.setScale(0.0f);
        if (CENTER == assetOriginType) {
            cardTemplate.setPosition(VIEWPORT_WIDTH / 2 - CARD_ORIGIN_X, VIEWPORT_HEIGHT / 2 - CARD_ORIGIN_Y);
        } else if (INVENTORY == assetOriginType) {
            cardTemplate.setPosition(INVENTORY_X - CARD_ORIGIN_X, INVENTORY_Y - CARD_ORIGIN_Y);
        }
        cardTemplate.getColor().a = 0f;

        float availableHeight = VIEWPORT_HEIGHT - InfoStage.getBottomHeight() - 5;
        float targetScale = availableHeight / CARD_HEIGHT;
        float bottomOffset = -(CARD_HEIGHT - (CARD_HEIGHT * targetScale)) / 2 + InfoStage.getBottomHeight();
        cardTemplate.addAction(
                new FastForwardAction<>(parallel(
                        color(new Color(0xffffffff), ACTION_DURATION),
                        rotateTo(360, ACTION_DURATION),
                        scaleTo(targetScale, targetScale, ACTION_DURATION),
                        moveTo(VIEWPORT_WIDTH / 2 - CARD_ORIGIN_X,
                                bottomOffset,
                                ACTION_DURATION),
                        run(() -> cardTemplate.addListener(new CardClickListener()))
                )
                )
        );

        InfoStage.addSmallActorToInfoStage(cardTemplate);
    }

    private void displayHiddentTemplate(AssetOriginType assetOriginType) {
        cardTemplate = new Image(CustomAssetManager.getTexture("card/card_template_hidden.png"));
        cardTemplate.setSize(CARD_ORIGIN_X * 2, CARD_ORIGIN_Y*2);
        cardTemplate.setOrigin(CARD_ORIGIN_X, CARD_ORIGIN_Y);
        cardTemplate.setScale(0.0f);
        if (CENTER == assetOriginType) {
            cardTemplate.setPosition(VIEWPORT_WIDTH / 2 - CARD_ORIGIN_X, VIEWPORT_HEIGHT / 2 - CARD_ORIGIN_Y);
        } else if (INVENTORY == assetOriginType) {
            cardTemplate.setPosition(INVENTORY_X - CARD_ORIGIN_X, INVENTORY_Y - CARD_ORIGIN_Y);
        }
        cardTemplate.getColor().a = 0f;

        float availableHeight = VIEWPORT_HEIGHT - InfoStage.getBottomHeight() - 5;
        float targetScale = availableHeight / CARD_HEIGHT;
        float bottomOffset = -(CARD_HEIGHT - (CARD_HEIGHT * targetScale)) / 2 + InfoStage.getBottomHeight();
        cardTemplate.addAction(
                new FastForwardAction<>(parallel(
                        color(new Color(0xffffffff), ACTION_DURATION),
                        rotateTo(360, ACTION_DURATION),
                        scaleTo(targetScale, targetScale, ACTION_DURATION),
                        moveTo(VIEWPORT_WIDTH / 2 - CARD_ORIGIN_X,
                                bottomOffset,
                                ACTION_DURATION),
                        run(() -> cardTemplate.addListener(new CardClickListener()))
                )
                )
        );

        InfoStage.addSmallActorToInfoStage(cardTemplate);
    }

    private void disableCard(SingleSubscriber<? super ShowCardResponse> onSub, ShowCardResponse showCardResponse) {
        ((CardTemplate)cardTemplate).disable().subscribe(() -> {
            returnCard(onSub, showCardResponse, AssetOriginType.INVENTORY);
        });
    }

    private void returnCard(SingleSubscriber<? super ShowCardResponse> onSub, ShowCardResponse showCardResponse, AssetOriginType assetOriginType) {
        MoveToAction moveToAction = null;
        if (CENTER == assetOriginType) {
            moveToAction = moveTo(VIEWPORT_WIDTH / 2 - CARD_ORIGIN_X, VIEWPORT_HEIGHT / 2 - CARD_ORIGIN_Y, FADING_EFFECT_DURATION);
        } else if (INVENTORY == assetOriginType) {
            moveToAction = moveTo(INVENTORY_X - CARD_ORIGIN_X, INVENTORY_Y - CARD_ORIGIN_Y, FADING_EFFECT_DURATION);
        }
        cardTemplate.addAction(
                sequence(
                        new FastForwardAction(parallel(
                                color(new Color(0xffffff00), FADING_EFFECT_DURATION),
                                rotateTo(0, FADING_EFFECT_DURATION),
                                scaleTo(0.0f, 0.0f, FADING_EFFECT_DURATION),
                                moveToAction
                        )),
                        run(cardTemplate::remove)
                )
        );
        onSub.onSuccess(showCardResponse);
    }

    private void discardCard(SingleSubscriber<? super ShowCardResponse> onSub, ShowCardResponse showCardResponse) {
        Vector2 originalPosition = cardTemplate.localToStageCoordinates(new Vector2());
        float originalX = originalPosition.x;
        float originalY = originalPosition.y;
        float originalScaleX = cardTemplate.getScaleX();
        float originalScaleY = cardTemplate.getScaleY();

        cardTemplate.setPosition(0, 0);
        cardTemplate.setScale(1);
        Image leftPart = new Image(TornApartCardBuilder.buildLeftTextureRegion((CardTemplate)cardTemplate));
        Image rightPart = new Image(TornApartCardBuilder.buildRightTextureRegion((CardTemplate)cardTemplate));

        leftPart.setPosition(originalX, originalY);
        rightPart.setPosition(originalX, originalY);
        leftPart.setScale(originalScaleX, originalScaleY);
        rightPart.setScale(originalScaleX, originalScaleY);
        if (cardTemplate != null && cardTemplate.getStage() != null && leftPart != null) {
            cardTemplate.getStage().addActor(leftPart);
        }
        if (cardTemplate != null && cardTemplate.getStage() != null && rightPart != null) {
            cardTemplate.getStage().addActor(rightPart);
        }
        if (cardTemplate != null) {
            cardTemplate.remove();
        }

        leftPart.setOrigin(leftPart.getWidth() / 2, 0);
        rightPart.setOrigin(rightPart.getWidth() / 2, 0);
        leftPart.setX(leftPart.getX() - (1 - leftPart.getScaleX()) * leftPart.getWidth() * 0.5f);
        rightPart.setX(rightPart.getX() - (1 - rightPart.getScaleX()) * rightPart.getWidth() * 0.5f);

        float actionDuration = ViewProperties.NORMAL_ACTION_DURATION;
        MoveByAction moveByLeftAction = new MoveByAction();
        moveByLeftAction.setAmount(-VIEWPORT_WIDTH * 0.2f, -VIEWPORT_HEIGHT * 0.1f);
        moveByLeftAction.setDuration(actionDuration);
        moveByLeftAction.setInterpolation(Interpolation.sineIn);
        moveByLeftAction.setActor(leftPart);

        MoveByAction moveByRightAction = new MoveByAction();
        moveByRightAction.setAmount(VIEWPORT_WIDTH * 0.2f, -VIEWPORT_HEIGHT * 0.1f);
        moveByRightAction.setDuration(actionDuration);
        moveByRightAction.setInterpolation(Interpolation.sineIn);
        moveByRightAction.setActor(rightPart);

        RotateByAction rotateByLeftAction = new RotateByAction();
        rotateByLeftAction.setAmount(45);
        rotateByLeftAction.setDuration(actionDuration);
        rotateByLeftAction.setInterpolation(Interpolation.sineIn);
        rotateByLeftAction.setActor(leftPart);

        RotateByAction rotateByRightAction = new RotateByAction();
        rotateByRightAction.setAmount(-45);
        rotateByRightAction.setDuration(actionDuration);
        rotateByRightAction.setInterpolation(Interpolation.sineIn);
        rotateByRightAction.setActor(rightPart);

        AlphaAction fadeOutLeftAction = new AlphaAction();
        fadeOutLeftAction.setAlpha(0f);
        fadeOutLeftAction.setDuration(actionDuration);
        fadeOutLeftAction.setInterpolation(Interpolation.sineIn);
        fadeOutLeftAction.setActor(leftPart);

        AlphaAction fadeOutRightAction = new AlphaAction();
        fadeOutRightAction.setAlpha(0f);
        fadeOutRightAction.setDuration(actionDuration);
        fadeOutRightAction.setInterpolation(Interpolation.sineIn);
        fadeOutRightAction.setActor(rightPart);

        leftPart.addAction(
                Actions.sequence(
                        new FastForwardAction(Actions.parallel(fadeOutLeftAction, moveByLeftAction, rotateByLeftAction)),
                        Actions.run(leftPart::remove)
                ));
        rightPart.addAction(
                Actions.sequence(
                        new FastForwardAction(Actions.parallel(fadeOutRightAction, moveByRightAction, rotateByRightAction)),
                        Actions.run(rightPart::remove)
                ));

        onSub.onSuccess(showCardResponse);
    }

    public Completable gainCard(CardInfo cardInfo) {
        return Completable.create(onSub -> {
            originalBottomHeight = InfoStage.getBottomHeight();
            displayGainButton(onSub);
            displayTable(get("asset.gained", cardInfo.getName()), null);
            displayCard(cardInfo, CENTER);
        });
    }

    private void displayGainButton(CompletableSubscriber completableSubscriber) {
        TextButton discardButton = ButtonBuilder.buildButton(get("dialog.ok"));

        buttons = new Button[1];
        buttons[0] = discardButton;

        InfoStage.setBottomHeight(InfoStage.getBottomHeight() + discardButton.getHeight() + 5);

        ButtonUtils.addClickListener(discardButton, () -> onGainButtonClick(completableSubscriber));


        InfoStage.addActionToInfoStage(new FastForwardAction(new SequenceAction(Actions.delay(0.5f), Actions.run(() -> {
            InfoStage.showButton(discardButton, VIEWPORT_WIDTH / 2 - discardButton.getWidth() / 2, originalBottomHeight);
        }))));
    }

    private void onGainButtonClick(CompletableSubscriber completableSubscriber) {
        InfoStage.hideActor(labelTable);
        for (Button button : buttons) {
            InfoStage.hideActor(button);
        }
        InfoStage.setBottomHeight(originalBottomHeight);

        cardTemplate.addAction(
                sequence(
                        new FastForwardAction(parallel(
                                color(new Color(0xffffff00), FADING_EFFECT_DURATION),
                                rotateTo(0, FADING_EFFECT_DURATION),
                                scaleTo(0.0f, 0.0f, FADING_EFFECT_DURATION),
                                moveTo(INVENTORY_X - CARD_ORIGIN_X, INVENTORY_Y - CARD_ORIGIN_Y, FADING_EFFECT_DURATION)
                        )),
                        run(cardTemplate::remove)
                )
        );
        completableSubscriber.onCompleted();
    }

    public Completable sewCard(CardInfo cardInfo) {
        return Completable.create(onSub -> {
            sewCard(onSub, cardInfo);
        });
    }

    private void sewCard(CompletableSubscriber onSub, CardInfo cardInfo) {
        cardTemplate = CardTemplate.buildCard(cardInfo);
        cardTemplate.setOrigin(CARD_ORIGIN_X, CARD_ORIGIN_Y);

        float availableHeight = VIEWPORT_HEIGHT - 95.6f - 5;
        float targetScale = availableHeight / CARD_HEIGHT;
        float bottomOffset = -(CARD_HEIGHT - (CARD_HEIGHT * targetScale)) / 2 + 95.6f;
        cardTemplate.setPosition(VIEWPORT_WIDTH / 2 - CARD_ORIGIN_X, bottomOffset);
        cardTemplate.setScale(targetScale);
        InfoStage.addSmallActorToInfoStage(cardTemplate);
        Vector2 originalPosition = cardTemplate.localToStageCoordinates(new Vector2());
        float originalX = originalPosition.x;
        float originalY = originalPosition.y;
        float originalScaleX = cardTemplate.getScaleX();
        float originalScaleY = cardTemplate.getScaleY();

        cardTemplate.setPosition(0, 0);
        cardTemplate.setScale(1);
        Image leftPart = new Image(TornApartCardBuilder.buildLeftTextureRegion((CardTemplate)cardTemplate));
        Image rightPart = new Image(TornApartCardBuilder.buildRightTextureRegion((CardTemplate)cardTemplate));

        leftPart.setRotation(45);
        rightPart.setRotation(-45);
        leftPart.getColor().a = 0f;
        rightPart.getColor().a = 0f;
        leftPart.setPosition(originalX - VIEWPORT_WIDTH * 0.2f, originalY - VIEWPORT_HEIGHT * 0.1f);
        rightPart.setPosition(originalX + VIEWPORT_WIDTH * 0.2f, originalY - VIEWPORT_HEIGHT * 0.1f);
        leftPart.setScale(originalScaleX, originalScaleY);
        rightPart.setScale(originalScaleX, originalScaleY);
        cardTemplate.getStage().addActor(leftPart);
        cardTemplate.getStage().addActor(rightPart);
        cardTemplate.remove();

        leftPart.setOrigin(leftPart.getWidth() / 2, 0);
        rightPart.setOrigin(rightPart.getWidth() / 2, 0);
        leftPart.setX(leftPart.getX() - (1 - leftPart.getScaleX()) * leftPart.getWidth() * 0.5f);
        rightPart.setX(rightPart.getX() - (1 - rightPart.getScaleX()) * rightPart.getWidth() * 0.5f);

        float actionDuration = ViewProperties.NORMAL_ACTION_DURATION;
        MoveByAction moveByLeftAction = new MoveByAction();
        moveByLeftAction.setAmount(VIEWPORT_WIDTH * 0.2f, VIEWPORT_HEIGHT * 0.1f);
        moveByLeftAction.setDuration(actionDuration);
        moveByLeftAction.setInterpolation(Interpolation.sineIn);
        moveByLeftAction.setActor(leftPart);

        MoveByAction moveByRightAction = new MoveByAction();
        moveByRightAction.setAmount(-VIEWPORT_WIDTH * 0.2f, VIEWPORT_HEIGHT * 0.1f);
        moveByRightAction.setDuration(actionDuration);
        moveByRightAction.setInterpolation(Interpolation.sineIn);
        moveByRightAction.setActor(rightPart);

        RotateByAction rotateByLeftAction = new RotateByAction();
        rotateByLeftAction.setAmount(-45);
        rotateByLeftAction.setDuration(actionDuration);
        rotateByLeftAction.setInterpolation(Interpolation.sineIn);
        rotateByLeftAction.setActor(leftPart);

        RotateByAction rotateByRightAction = new RotateByAction();
        rotateByRightAction.setAmount(45);
        rotateByRightAction.setDuration(actionDuration);
        rotateByRightAction.setInterpolation(Interpolation.sineIn);
        rotateByRightAction.setActor(rightPart);

        AlphaAction fadeInLeftAction = new AlphaAction();
        fadeInLeftAction.setAlpha(1f);
        fadeInLeftAction.setDuration(actionDuration);
        fadeInLeftAction.setInterpolation(Interpolation.sineIn);
        fadeInLeftAction.setActor(leftPart);

        AlphaAction fadeInRightAction = new AlphaAction();
        fadeInRightAction.setAlpha(1f);
        fadeInRightAction.setDuration(actionDuration);
        fadeInRightAction.setInterpolation(Interpolation.sineIn);
        fadeInRightAction.setActor(rightPart);

        MoveToAction moveToAction = moveTo(INVENTORY_X - CARD_ORIGIN_X, INVENTORY_Y - CARD_ORIGIN_Y, ACTION_DURATION);

        SequenceAction moveToInventoryAction = sequence(
                new FastForwardAction<>(parallel(
                        color(new Color(0xffffff00), ACTION_DURATION),
                        rotateTo(0, ACTION_DURATION),
                        scaleTo(0.0f, 0.0f, ACTION_DURATION),
                        moveToAction
                )),
                run(cardTemplate::remove),
                run(onSub::onCompleted)
        );
        moveToInventoryAction.setActor(cardTemplate);


        leftPart.addAction(
                Actions.sequence(
                        new FastForwardAction<>(Actions.parallel(fadeInLeftAction, moveByLeftAction, rotateByLeftAction)),
                        Actions.run(() -> {
                            leftPart.remove();
                            InfoStage.addSmallActorToInfoStage(cardTemplate);
                            cardTemplate.getColor().a = 1f;
                            cardTemplate.setRotation(360);
                            cardTemplate.setScale(originalScaleX, originalScaleY);
                            cardTemplate.setPosition(VIEWPORT_WIDTH / 2f - CARD_ORIGIN_X, bottomOffset);
                            InfoStage.addActionToInfoStage(moveToInventoryAction);

                        })
                ));
        rightPart.addAction(
                Actions.sequence(
                        new FastForwardAction<>(Actions.parallel(fadeInRightAction, moveByRightAction, rotateByRightAction)),
                        Actions.run(rightPart::remove)
                ));



    }

}
