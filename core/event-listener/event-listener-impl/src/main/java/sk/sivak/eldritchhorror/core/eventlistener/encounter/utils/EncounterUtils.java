package sk.sivak.eldritchhorror.core.eventlistener.encounter.utils;

import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class EncounterUtils {

    private EncounterUtils() {
    }

    public static List<? extends CardInfo> getPossession(InvestigatorId investigatorId, AssetTrait... assetTraits) {
        List<CardInfo> cards = new LinkedList<>();
        cards.addAll(ServicePlatform.get().getAssetDeck().getAssets(investigatorId));
        cards.addAll(ServicePlatform.get().getArtifactsDeck().getArtifacts(investigatorId));
        if (assetTraits.length == 0) {
            return cards;
        }
        return Stream.collectToList(cards, cardInfo -> {
            for (AssetTrait assetTrait : assetTraits) {
                if (cardInfo.getTraits().contains(assetTrait)) {
                    return true;
                }
            }
            return false;
        });
    }

    public static void improveSkillOrGainClue(EncounterTextBuilder textBuilder, Stat skill) {
        if (canImproveSkill(skill)) {
            ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.SELECT_REWARD);
            ServicePlatform.get().getEncounterService().displayButtons(
                    textBuilder.withPass().withOption(1).build(),
                    textBuilder.withPass().withOption(2).build()
            ).subscribe(x -> {
                ServicePlatform.get().getEncounterService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                if (x == 0) {
                    ServicePlatform.get().getTokenService().gainClueFromPool();
                } else {
                    ServicePlatform.get().getInvestigatorService().improveSkill(skill);
                }
                ServicePlatform.get().getEncounterService().release();
            });
        } else {
            TypewriterUtils.confirmInfos(textBuilder.withPass().withOption(1).build()).subscribe(() -> {
                ServicePlatform.get().getEncounterService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getTokenService().gainClueFromPool();
                ServicePlatform.get().getEncounterService().release();
            });
        }
    }

    private static boolean canImproveSkill(Stat skill) {
        return ServicePlatform.get().getInvestigators().getActiveInvestigator().getStatBonus(skill) < 2;
    }


    public static void onSelectCardToDiscardByChoice(CardInfo cardInfo) {
        onSelectCardToDiscardPrivate(cardInfo, false);
    }

    public static void onSelectCardToDiscard(CardInfo cardInfo) {
        onSelectCardToDiscardPrivate(cardInfo, true);
    }

    private static void onSelectCardToDiscardPrivate(CardInfo cardInfo, boolean forced) {
        if (cardInfo == null) {
            Question<Object> question = new Question<>();
            question.setTitle("Nothing to discard.");
            question.setOptions(Collections.singletonList(new Question.Option<>("OK", true)));
            ServicePlatform.get().getGameService().ask(question).subscribe();
            return;
        }
        ShowCardRequest showCardRequest = new ShowCardRequest();
        if (cardInfo instanceof AssetInfo) {
            showCardRequest.setAssetInfo(((AssetInfo) cardInfo));
        } else if (cardInfo instanceof ArtifactInfo) {
            showCardRequest.setArtifactInfo(((ArtifactInfo) cardInfo));
        } else if (cardInfo instanceof SpellInfo) {
            showCardRequest.setSpellInfo(((SpellInfo) cardInfo));
        } else if (cardInfo instanceof ConditionInfo) {
            showCardRequest.setConditionInfo(((ConditionInfo) cardInfo));
        }
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard " + cardInfo.getName());
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayAsset.subscribe(response -> {
            if (cardInfo instanceof AssetInfo) {
                ServicePlatform.get().getService().discardAssetFromInvestigator(getActiveInvestigatorId(), ((AssetInfo) cardInfo), forced);
            } else if (cardInfo instanceof ArtifactInfo) {
                ServicePlatform.get().getService().discardArtifactFromInvestigator(getActiveInvestigatorId(), ((ArtifactInfo) cardInfo), forced);
            } else if (cardInfo instanceof SpellInfo) {
                ServicePlatform.get().getService().discardSpellFromInvestigator(getActiveInvestigatorId(), ((SpellInfo) cardInfo));
            } else if (cardInfo instanceof ConditionInfo) {
                ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), ((ConditionInfo) cardInfo));
            }

        });
    }

    public static InvestigatorId getActiveInvestigatorId() {
        return ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
    }
}
