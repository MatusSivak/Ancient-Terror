package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.monster.MonsterAbilities.*;

public class GhastMonsterListener extends AbstractComposedMonsterListener {

    @Override
    protected void registerListeners() {

    }

    @Override
    protected void onSpawn() {
        sk.sivak.eldritchhorror.core.constants.test.TestFlavorType flavor = ServicePlatform.get().getTestFlavor().getFlavorType();
        if (flavor != null && flavor != sk.sivak.eldritchhorror.core.constants.test.TestFlavorType.EMPTY) {
            return; // never start a nested combat
        }
        spawnEffect(true, () -> {
            InvestigatorId investigator = firstInvestigatorAt(monsterInfo.getCurrentLocation());
            if (investigator == null) {
                return;
            }
            makeActive(investigator);
            ServicePlatform.get().getTestService().combat(monsterInfo);
        });
    }

}
