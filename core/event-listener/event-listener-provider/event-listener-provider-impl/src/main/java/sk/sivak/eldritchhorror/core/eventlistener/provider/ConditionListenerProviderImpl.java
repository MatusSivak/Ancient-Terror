package sk.sivak.eldritchhorror.core.eventlistener.provider;

import java8.features.util.MapUtils;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.eventlistener.condition.*;

import java.util.HashMap;
import java.util.Map;

public class ConditionListenerProviderImpl implements ConditionListenerProvider {

    private Map<ConditionInfo, AbstractConditionListener<? extends ConditionInfo>> registeredListenersMap;

    @Override
    public void init() {
        registeredListenersMap = new HashMap<>();
    }

    @Override
    public AbstractConditionListener<? extends ConditionInfo> getConditionListener(ConditionInfo conditionInfo) {
        AbstractConditionListener<? extends ConditionInfo> registeredListener = MapUtils.getOrDefault(registeredListenersMap, conditionInfo, null);
        if (registeredListener != null) {
            return registeredListener;
        }
        switch (conditionInfo.getId()) {
            case DEBT:
                registeredListenersMap.put(conditionInfo, new DebtListener());
                break;
            case BLESSED:
                registeredListenersMap.put(conditionInfo, new BlessedListener());
                break;
            case RIGHTEOUS:
                registeredListenersMap.put(conditionInfo, new RighteousListener());
                break;
            case CURSED:
                registeredListenersMap.put(conditionInfo, new CursedListener());
                break;
            case LEG_INJURY:
                registeredListenersMap.put(conditionInfo, new LegInjuryListener());
                break;
            case INTERNAL_INJURY:
                registeredListenersMap.put(conditionInfo, new InternalInjuryListener());
                break;
            case POISONED:
                registeredListenersMap.put(conditionInfo, new PoisonedListener());
                break;
            case BACK_INJURY:
                registeredListenersMap.put(conditionInfo, new BackInjuryListener());
                break;
            case AMNESIA:
                registeredListenersMap.put(conditionInfo, new AmnesiaListener());
                break;
            case PARANOIA:
                registeredListenersMap.put(conditionInfo, new ParanoiaListener());
                break;
            case LOST_IN_TIME_AND_SPACE:
                registeredListenersMap.put(conditionInfo, new LostInTimeAndSpaceListener());
                break;
            case HEAD_INJURY:
                registeredListenersMap.put(conditionInfo, new HeadInjuryListener());
                break;
            case HALLUCINATIONS:
                registeredListenersMap.put(conditionInfo, new HallucinationsListener());
                break;
            case DETAINED:
                registeredListenersMap.put(conditionInfo, new DetainedListener());
                break;
            case DARK_PACT:
                registeredListenersMap.put(conditionInfo, new DarkPactListener());
                break;
        }
        if (!registeredListenersMap.containsKey(conditionInfo)) {
            throw new IllegalArgumentException("No key found for " + conditionInfo.getId());
        }
        return registeredListenersMap.get(conditionInfo);
    }

    @Override
    public void unregister(ConditionInfo conditionInfo) {
        AbstractConditionListener<? extends ConditionInfo> abstractConditionListener = registeredListenersMap.get(conditionInfo);
        if (abstractConditionListener == null) {
            return;
        }
        abstractConditionListener.unregister();
    }
}
