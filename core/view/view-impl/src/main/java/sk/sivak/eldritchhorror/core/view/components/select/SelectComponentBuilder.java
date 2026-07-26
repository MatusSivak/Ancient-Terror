package sk.sivak.eldritchhorror.core.view.components.select;

import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.view.components.investigator.InvestigatorSketch;

public class SelectComponentBuilder {

    public static <Key> SelectComponent<Key> build(Key key) {
        SelectComponent selectComponent;
        if (key instanceof InvestigatorId) {
            selectComponent = new InvestigatorSketch(((InvestigatorId) key));
        } else if (key instanceof CardInfo) {
            selectComponent = new SelectCardComponent(((CardInfo) key));
        } else if (key instanceof MonsterInfo) {
            selectComponent = new SelectMonsterComponent(((MonsterInfo) key));
        } else if (key instanceof GateInfo) {
            selectComponent = new SelectGateComponent(((GateInfo) key));
        } else {
            throw new UnsupportedOperationException();
        }
        return selectComponent;

    }
}
