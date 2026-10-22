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

public class PoltergeistMonsterListener extends AbstractComposedMonsterListener {

    @Override
    protected void registerListeners() {
        before(BeforeAfterEvent.DEFEAT_MONSTER, DefeatMonsterData.class, data -> {
            if (data.getMonsterInfo() != monsterInfo) {
                return;
            }
            InvestigatorId active = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            InvestigatorRead lead = ServicePlatform.get().getInvestigators().getLeadInvestigator();
            if (active == null && lead == null) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            if (active == null) {
                makeActive(lead.getInfo().getInvestigatorId());
            }
            ServicePlatform.get().getGameService().gainCondition(ConditionTrait.BOON);
            if (active == null) {
                restoreActive(null);
            }
            ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> data);
            ServicePlatform.get().getService().release();
        });
    }

    @Override
    protected void onSpawn() {
        if (ServicePlatform.get().getInvestigators().getLeadInvestigator() == null) {
            return;
        }
        spawnEffect(true, () -> {
            InvestigatorRead lead = ServicePlatform.get().getInvestigators().getLeadInvestigator();
            makeActive(lead.getInfo().getInvestigatorId());
            ServicePlatform.get().getGameService().gainCondition(ConditionTrait.BANE);
        });
    }

}
