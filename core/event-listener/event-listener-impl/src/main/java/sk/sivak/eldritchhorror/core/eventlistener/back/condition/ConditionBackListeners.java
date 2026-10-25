package sk.sivak.eldritchhorror.core.eventlistener.back.condition;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.amnesia.AmnesiaConditionBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.amnesia.AmnesiaConditionBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.amnesia.AmnesiaConditionBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.amnesia.AmnesiaConditionBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.backinjury.BackInjuryConditionBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.backinjury.BackInjuryConditionBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.backinjury.BackInjuryConditionBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.backinjury.BackInjuryConditionBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.blessed.BlessedConditionBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.blessed.BlessedConditionBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.blessed.BlessedConditionBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.blessed.BlessedConditionBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.blessed.BlessedConditionBackListener5;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.blessed.BlessedConditionBackListener6;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.blessed.BlessedConditionBackListener7;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.cursed.CursedConditionBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.cursed.CursedConditionBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.cursed.CursedConditionBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.cursed.CursedConditionBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.cursed.CursedConditionBackListener5;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.cursed.CursedConditionBackListener6;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.cursed.CursedConditionBackListener7;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.darkpact.DarkPactConditionBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.darkpact.DarkPactConditionBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.darkpact.DarkPactConditionBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.darkpact.DarkPactConditionBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.darkpact.DarkPactConditionBackListener5;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.debt.DebtConditionBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.debt.DebtConditionBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.debt.DebtConditionBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.debt.DebtConditionBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.debt.DebtConditionBackListener5;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.debt.DebtConditionBackListener6;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.debt.DebtConditionBackListener7;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.debt.DebtConditionBackListener8;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.detained.DetainedConditionBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.detained.DetainedConditionBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.detained.DetainedConditionBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.detained.DetainedConditionBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.hallucinations.HallucinationsConditionBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.hallucinations.HallucinationsConditionBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.hallucinations.HallucinationsConditionBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.hallucinations.HallucinationsConditionBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.headinjury.HeadInjuryConditionBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.headinjury.HeadInjuryConditionBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.headinjury.HeadInjuryConditionBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.headinjury.HeadInjuryConditionBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.internalinjury.InternalInjuryConditionBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.internalinjury.InternalInjuryConditionBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.internalinjury.InternalInjuryConditionBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.internalinjury.InternalInjuryConditionBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.leginjury.LegInjuryConditionBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.leginjury.LegInjuryConditionBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.leginjury.LegInjuryConditionBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.leginjury.LegInjuryConditionBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.lostintimeandspace.LostInTimeAndSpaceBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.lostintimeandspace.LostInTimeAndSpaceBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.lostintimeandspace.LostInTimeAndSpaceBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.lostintimeandspace.LostInTimeAndSpaceBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.paranoia.ParanoiaConditionBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.paranoia.ParanoiaConditionBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.paranoia.ParanoiaConditionBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.paranoia.ParanoiaConditionBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.poisoned.PoisonedConditionBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.poisoned.PoisonedConditionBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.poisoned.PoisonedConditionBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.poisoned.PoisonedConditionBackListener4;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.righteous.RighteousConditionBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.righteous.RighteousConditionBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.righteous.RighteousConditionBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.righteous.RighteousConditionBackListener4;

/** Explicit references keep every condition handler in compiled and packaged builds. */
public final class ConditionBackListeners {
    private ConditionBackListeners() {}

    public static AbstractConditionBackListener create(ConditionInfo condition) {
        int back = condition.getConditionBack().getConditionBackId();
        switch (condition.getId()) {
            case AMNESIA:
                switch (back) {
                    case 1: return new AmnesiaConditionBackListener1(condition);
                    case 2: return new AmnesiaConditionBackListener2(condition);
                    case 3: return new AmnesiaConditionBackListener3(condition);
                    case 4: return new AmnesiaConditionBackListener4(condition);
                }
                break;
            case BACK_INJURY:
                switch (back) {
                    case 1: return new BackInjuryConditionBackListener1(condition);
                    case 2: return new BackInjuryConditionBackListener2(condition);
                    case 3: return new BackInjuryConditionBackListener3(condition);
                    case 4: return new BackInjuryConditionBackListener4(condition);
                }
                break;
            case BLESSED:
                switch (back) {
                    case 1: return new BlessedConditionBackListener1(condition);
                    case 2: return new BlessedConditionBackListener2(condition);
                    case 3: return new BlessedConditionBackListener3(condition);
                    case 4: return new BlessedConditionBackListener4(condition);
                    case 5: return new BlessedConditionBackListener5(condition);
                    case 6: return new BlessedConditionBackListener6(condition);
                    case 7: return new BlessedConditionBackListener7(condition);
                }
                break;
            case CURSED:
                switch (back) {
                    case 1: return new CursedConditionBackListener1(condition);
                    case 2: return new CursedConditionBackListener2(condition);
                    case 3: return new CursedConditionBackListener3(condition);
                    case 4: return new CursedConditionBackListener4(condition);
                    case 5: return new CursedConditionBackListener5(condition);
                    case 6: return new CursedConditionBackListener6(condition);
                    case 7: return new CursedConditionBackListener7(condition);
                }
                break;
            case DARK_PACT:
                switch (back) {
                    case 1: return new DarkPactConditionBackListener1(condition);
                    case 2: return new DarkPactConditionBackListener2(condition);
                    case 3: return new DarkPactConditionBackListener3(condition);
                    case 4: return new DarkPactConditionBackListener4(condition);
                    case 5: return new DarkPactConditionBackListener5(condition);
                }
                break;
            case DEBT:
                switch (back) {
                    case 1: return new DebtConditionBackListener1(condition);
                    case 2: return new DebtConditionBackListener2(condition);
                    case 3: return new DebtConditionBackListener3(condition);
                    case 4: return new DebtConditionBackListener4(condition);
                    case 5: return new DebtConditionBackListener5(condition);
                    case 6: return new DebtConditionBackListener6(condition);
                    case 7: return new DebtConditionBackListener7(condition);
                    case 8: return new DebtConditionBackListener8(condition);
                }
                break;
            case DETAINED:
                switch (back) {
                    case 1: return new DetainedConditionBackListener1(condition);
                    case 2: return new DetainedConditionBackListener2(condition);
                    case 3: return new DetainedConditionBackListener3(condition);
                    case 4: return new DetainedConditionBackListener4(condition);
                }
                break;
            case HALLUCINATIONS:
                switch (back) {
                    case 1: return new HallucinationsConditionBackListener1(condition);
                    case 2: return new HallucinationsConditionBackListener2(condition);
                    case 3: return new HallucinationsConditionBackListener3(condition);
                    case 4: return new HallucinationsConditionBackListener4(condition);
                }
                break;
            case HEAD_INJURY:
                switch (back) {
                    case 1: return new HeadInjuryConditionBackListener1(condition);
                    case 2: return new HeadInjuryConditionBackListener2(condition);
                    case 3: return new HeadInjuryConditionBackListener3(condition);
                    case 4: return new HeadInjuryConditionBackListener4(condition);
                }
                break;
            case INTERNAL_INJURY:
                switch (back) {
                    case 1: return new InternalInjuryConditionBackListener1(condition);
                    case 2: return new InternalInjuryConditionBackListener2(condition);
                    case 3: return new InternalInjuryConditionBackListener3(condition);
                    case 4: return new InternalInjuryConditionBackListener4(condition);
                }
                break;
            case LEG_INJURY:
                switch (back) {
                    case 1: return new LegInjuryConditionBackListener1(condition);
                    case 2: return new LegInjuryConditionBackListener2(condition);
                    case 3: return new LegInjuryConditionBackListener3(condition);
                    case 4: return new LegInjuryConditionBackListener4(condition);
                }
                break;
            case LOST_IN_TIME_AND_SPACE:
                switch (back) {
                    case 1: return new LostInTimeAndSpaceBackListener1(condition);
                    case 2: return new LostInTimeAndSpaceBackListener2(condition);
                    case 3: return new LostInTimeAndSpaceBackListener3(condition);
                    case 4: return new LostInTimeAndSpaceBackListener4(condition);
                }
                break;
            case PARANOIA:
                switch (back) {
                    case 1: return new ParanoiaConditionBackListener1(condition);
                    case 2: return new ParanoiaConditionBackListener2(condition);
                    case 3: return new ParanoiaConditionBackListener3(condition);
                    case 4: return new ParanoiaConditionBackListener4(condition);
                }
                break;
            case POISONED:
                switch (back) {
                    case 1: return new PoisonedConditionBackListener1(condition);
                    case 2: return new PoisonedConditionBackListener2(condition);
                    case 3: return new PoisonedConditionBackListener3(condition);
                    case 4: return new PoisonedConditionBackListener4(condition);
                }
                break;
            case RIGHTEOUS:
                switch (back) {
                    case 1: return new RighteousConditionBackListener1(condition);
                    case 2: return new RighteousConditionBackListener2(condition);
                    case 3: return new RighteousConditionBackListener3(condition);
                    case 4: return new RighteousConditionBackListener4(condition);
                }
                break;
        }
        throw new IllegalArgumentException("Unknown condition back: " + condition.getId() + "/" + back);
    }
}
