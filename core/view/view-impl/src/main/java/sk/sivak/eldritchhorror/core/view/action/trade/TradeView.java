package sk.sivak.eldritchhorror.core.view.action.trade;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import java8.features.function.Function;
import rx.Single;
import rx.SingleSubscriber;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.TokenCardInfo;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;
import sk.sivak.eldritchhorror.core.view.components.button.HideOkButtons;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java8.features.stream.Stream.collectToList;
import static java8.features.stream.Stream.map;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class TradeView {
    public static final float SCALE_XY = 0.133f;
    private TradeDragAndDropBinder dragAndDrop;
    private HideOkButtons hideOkButtons;
    private TradeData input;
    private Table table;

    public Single<TradeData> init(TradeData input) {
        this.input = input;
        return Single.create(onSub -> {
            InfoStage.displayText(get("trade.title"));
            dragAndDrop = createDragAndDrop(input.getSourceCards(), input.getTargetCards(),
                    input.getSourceInvestigatorId(), input.getTargetInvestigatorId(),
                    input.getSourceTokenTrading(), input.getTargetTokenTrading());
            dragAndDrop.getSourceTargetGroup().setY(InfoStage.getInvestigatorHud().getPrefHeight() + VIEWPORT_HEIGHT * 0.02f);
            hideOkButtons = new HideOkButtons();
            hideOkButtons.init(get("trade.displayPossessions"), getOnConfirmAction(onSub), dragAndDrop.getSourceTargetGroup());
            hideOkButtons.showButtons();
            table = new Table();
            table.setPosition((ViewProperties.VIEWPORT_WIDTH+75)/2,ViewProperties.VIEWPORT_HEIGHT/2 - 10);
            ScrollPane scrollPane = new ScrollPane(dragAndDrop.getSourceTargetGroup());
            scrollPane.setScrollingDisabled(false, true);
            table.add(scrollPane)
                    .width(dragAndDrop.getSourceTargetGroup().getWidth())
                    .maxWidth(ViewProperties.VIEWPORT_WIDTH-100)
                    .height(dragAndDrop.getSourceTargetGroup().getHeight());
            InfoStage.showActor(table);
        });
    }

    private Action0 getOnConfirmAction(SingleSubscriber<? super TradeData> onSub) {
        return () -> {
            input.setSourceCards(dragAndDrop.getSourceCards());
            input.setTargetCards(dragAndDrop.getTargetCards());
            TradeData tradeData = new TradeData();
            tradeData.setSourceInvestigatorId(input.getSourceInvestigatorId());
            tradeData.setTargetInvestigatorId(input.getTargetInvestigatorId());
            tradeData.setSourceCards(dragAndDrop.getSourceCards());
            tradeData.setTargetCards(dragAndDrop.getTargetCards());
            tradeData.setSourceTokenTrading(new TradeData.TokenTrading());
            tradeData.setTargetTokenTrading(new TradeData.TokenTrading());
            tradeData.getSourceTokenTrading().setBeforeTrade(input.getSourceTokenTrading().getBeforeTrade());
            tradeData.getTargetTokenTrading().setBeforeTrade(input.getTargetTokenTrading().getBeforeTrade());
            tradeData.getSourceTokenTrading().setAfterTrade(createTokenData(tradeData.getSourceCards().iterator()));
            tradeData.getTargetTokenTrading().setAfterTrade(createTokenData(tradeData.getTargetCards().iterator()));

            table.addAction(new FastForwardAction<>(Actions.sequence(
                    Actions.alpha(0, 1f),
                    Actions.run(() -> table.remove()))));
            onSub.onSuccess(tradeData);
        };
    }

    private TradeData.Tokens createTokenData(Iterator<CardInfo> cardsIterator) {
        TradeData.Tokens tokens = new TradeData.Tokens();
        while (cardsIterator.hasNext()) {
            CardInfo next = cardsIterator.next();
            if (!(next instanceof TokenCardInfo)) {
                continue;
            }
            TokenCardInfo tokenCardInfo = (TokenCardInfo) next;
            if (tokenCardInfo.isClue()) {
                tokens.setClues(tokens.getClues() + 1);
            } else if (tokenCardInfo.isShipTicket()) {
                tokens.setShipTickets(tokens.getShipTickets() + 1);
            } else if (tokenCardInfo.isTrainTicket()) {
                tokens.setTrainTickets(tokens.getTrainTickets() + 1);
            }
            cardsIterator.remove();
        }
        return tokens;
    }

    private TradeDragAndDropBinder createDragAndDrop(List<CardInfo> sourceCards, List<CardInfo> targetCards,
                                                     InvestigatorId sourceInvestigatorId, InvestigatorId targetInvestigatorId,
                                                     TradeData.TokenTrading sourceTokenTrading, TradeData.TokenTrading targetTokenTrading) {

        Function<CardInfo, CardTemplate> cardInfoToTemplate = it -> {
            CardTemplate cardTemplate = CardTemplate.buildCard(it);
            cardTemplate.setScale(SCALE_XY);
            return cardTemplate;
        };
        CardTemplate[] sourceCardTemplates = collectToList(map(sourceCards, cardInfoToTemplate)).toArray(new CardTemplate[sourceCards.size()]);
        CardTemplate[] targetCardTemplates = collectToList(map(targetCards, cardInfoToTemplate)).toArray(new CardTemplate[targetCards.size()]);

        sourceCardTemplates = addTokenCards(sourceCards, sourceTokenTrading, sourceCardTemplates);
        targetCardTemplates = addTokenCards(targetCards, targetTokenTrading, targetCardTemplates);

        TradeDragAndDropBinder dragAndDropBinder = new TradeDragAndDropBinder(InfoStage.getStageSafe());
        dragAndDropBinder.init(sourceCardTemplates, targetCardTemplates, sourceInvestigatorId, targetInvestigatorId);
        InfoStage.showActor(dragAndDropBinder.getSourceTargetGroup());
        return dragAndDropBinder;
    }

    private CardTemplate[] addTokenCards(List<CardInfo> cards, TradeData.TokenTrading tokenTrading, CardTemplate[] cardTemplates) {
        int clues = tokenTrading.getBeforeTrade().getClues();
        int shipTickets = tokenTrading.getBeforeTrade().getShipTickets();
        int trainTickets = tokenTrading.getBeforeTrade().getTrainTickets();
        cardTemplates = Arrays.copyOf(cardTemplates, cards.size() + clues + shipTickets + trainTickets);
        for (int i = 0; i < clues; i++) {
            CardTemplate clueCard = CardTemplate.buildClueCard();
            clueCard.setScale(SCALE_XY);
            cardTemplates[i + cards.size()] = clueCard;
        }
        for (int i = clues; i < clues + shipTickets; i++) {
            CardTemplate shipCard = CardTemplate.buildShipTicketCard();
            shipCard.setScale(SCALE_XY);
            cardTemplates[i + cards.size()] = shipCard;
        }
        for (int i = clues + shipTickets; i < clues + shipTickets + trainTickets; i++) {
            CardTemplate trainCard = CardTemplate.buildTrainTicketCard();
            trainCard.setScale(SCALE_XY);
            cardTemplates[i + cards.size()] = trainCard;
        }
        return cardTemplates;
    }

}
