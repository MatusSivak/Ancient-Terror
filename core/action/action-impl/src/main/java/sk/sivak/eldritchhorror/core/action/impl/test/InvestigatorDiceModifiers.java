package sk.sivak.eldritchhorror.core.action.impl.test;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import rx.Completable;
import java.util.ArrayList;
import java.util.List;

/** Presents Nun's bonus after the raw roll, then commits it before resolution. */
public final class InvestigatorDiceModifiers {
    private InvestigatorDiceModifiers() {}

    public static Completable applyAfterRoll(List<DiceRoll> dice, ConditionInfo condition, int minSuccess) {
        final ConditionInfo explicitCondition = condition;
        return Completable.defer(() -> {
            InvestigatorRead investigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
            if (investigator == null || dice.isEmpty()) return Completable.complete();
            ConditionInfo source = explicitCondition;
            if (source == null && ServicePlatform.get().getTestFlavor() != null
                    && ServicePlatform.get().getTestFlavor().getFlavorType() == TestFlavorType.CONDITION)
                source = ServicePlatform.get().getTestFlavor().getFlavorData();
            if (adjust(1, investigator.getInfo().getInvestigatorId(), investigator.isLostInTimeAndSpace(), source) == 1)
                return Completable.complete();
            List<DiceRoll> adjusted = new ArrayList<>();
            for (DiceRoll raw : dice) {
                DiceRoll result = new DiceRoll();
                result.setDiceNr(raw.getDiceNr());
                result.setDiceValue(Math.min(6, raw.getDiceValue() + 1));
                result.setScore(result.getDiceValue() >= minSuccess ? DiceRoll.Score.GOOD : DiceRoll.Score.BAD);
                adjusted.add(result);
            }
            return ServicePlatform.get().getTestController().showNunDiceBonus(adjusted).doOnCompleted(() -> {
                for (int i = 0; i < dice.size(); i++) {
                    dice.get(i).setDiceValue(adjusted.get(i).getDiceValue());
                    dice.get(i).setScore(adjusted.get(i).getScore());
                }
            });
        });
    }

    public static int adjust(int value, InvestigatorId investigator, boolean offBoard, ConditionInfo condition) {
        if (investigator == InvestigatorId.THE_NUN && !offBoard && condition != null
                && (condition.getTraits().contains(ConditionTrait.BOON) || condition.getTraits().contains(ConditionTrait.BANE)))
            return Math.min(6, value + 1);
        return value;
    }
}

