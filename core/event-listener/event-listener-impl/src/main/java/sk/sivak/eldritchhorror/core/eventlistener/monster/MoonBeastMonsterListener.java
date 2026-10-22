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

public class MoonBeastMonsterListener extends AbstractComposedMonsterListener {

    @Override
    protected void registerListeners() {
        direct(DirectEvent.BEFORE_DAMAGE_TEST, CombatData.class, combatData -> {
            if (combatData.getMonsterInfo() != monsterInfo || possessions(AssetTrait.ITEM).isEmpty()) {
                return;
            }
            Question<Boolean> question = new Question<>();
            question.setTitle("Discard 1 Item instead of the Strength test?");
            question.setPortraitBeforeTitle(monsterInfo);
            question.setOptions(Question.Option.noYesOptions);
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, combatData));
        });
    }

    private void onAnswer(Answer<Boolean, Object> answer, CombatData combatData) {
        if (!answer.getResponseData()) {
            ServicePlatform.get().getService().convertTo(CombatData.class, () -> combatData);
            return;
        }
        discardOne(possessions(AssetTrait.ITEM), "Discard 1 Item",
                () -> ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.AFTER_DAMAGE_CHECK, combatData));
    }

}
