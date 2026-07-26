package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.CardClickListener;

import java.util.LinkedList;
import java.util.List;

public class CardsTable extends VisTable {

    public static final float CARD_SCALE = 0.16f;

    private List<CardTemplate> cardTemplates = new LinkedList<>();
    private List<CardClickListener> cardClickListeners = new LinkedList<>();

    public CardsTable() {
        pad(5);
    }

    public void init(List<? extends CardInfo> cardInfoList, int maxWidth) {
        cardTemplates.clear();
        clear();
        HorizontalGroup horizontalGroup = new HorizontalGroup();
        ScrollPane scrollPane = new ScrollPane(horizontalGroup);
        float scrollWidth = Math.min(maxWidth, cardInfoList.size() * CARD_SCALE * CardTemplate.CARD_WIDTH);
        add(scrollPane).width(scrollWidth);
        for (CardInfo cardInfo : cardInfoList) {
            withCardInfo(cardInfo, horizontalGroup);
        }
        pack();
    }

    private void withCardInfo(CardInfo cardInfo, HorizontalGroup horizontalGroup) {
        CardTemplate cardTemplate = CardTemplate.buildCard(cardInfo);
        CardClickListener listener = new CardClickListener();
        cardTemplates.add(cardTemplate);
        cardClickListeners.add(listener);

        cardTemplate.addListener(listener);
        cardTemplate.setScale(CARD_SCALE);

        horizontalGroup.addActor(new Container<>(cardTemplate).width(CardTemplate.CARD_WIDTH * CARD_SCALE).height(CardTemplate.CARD_HEIGHT * CARD_SCALE));
    }

    public List<CardTemplate> getCardTemplates() {
        return cardTemplates;
    }

    public List<CardClickListener> getCardClickListeners() {
        return cardClickListeners;
    }

}
