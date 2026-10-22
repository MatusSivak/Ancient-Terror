package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.ConditionsDeckRead;
import sk.sivak.eldritchhorror.core.model.CluePoolRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorsRead;
import sk.sivak.eldritchhorror.core.service.InvestigatorService;
import sk.sivak.eldritchhorror.core.service.Service;
import sk.sivak.eldritchhorror.core.service.TokenService;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;

public class YellowMythosCard14Test {
    private final List<String> effects = new ArrayList<>();
    private final Map<InvestigatorId, List<ConditionInfo>> conditions = new HashMap<>();
    private final Map<InvestigatorId, Integer> clues = new HashMap<>();

    @Before public void setup() {
        ServicePlatform.nullifyInstance();
        ServicePlatform platform = ServicePlatform.get();
        List<InvestigatorRead> investigators = Arrays.asList(investigator(InvestigatorId.THE_SAILOR), investigator(InvestigatorId.THE_SOLDIER));
        platform.setInvestigators(mock(InvestigatorsRead.class, (name, args) -> investigators));
        platform.setConditionsDeck(mock(ConditionsDeckRead.class, (name, args) ->
                conditions.getOrDefault((InvestigatorId) args[0], Collections.emptyList())));
        platform.setCluePoolRead(mock(CluePoolRead.class, (name, args) -> clues.getOrDefault((InvestigatorId) args[0], 0)));
        platform.setService(mock(Service.class, (name, args) -> { effects.add(name); return null; }));
        platform.setInvestigatorService(mock(InvestigatorService.class, (name, args) -> {
            effects.add(name + (args != null && args[0] instanceof InvestigatorId ? ":" + ((InvestigatorId) args[0]).name() : ""));
            return null;
        }));
        platform.setTokenService(mock(TokenService.class, (name, args) -> {
            effects.add(name + (args != null ? ":" + args[0] : ""));
            return null;
        }));
    }

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void losesPerConditionTraitAndCapsCluesAtHeld() {
        conditions.put(InvestigatorId.THE_SAILOR, Arrays.asList(
                condition(ConditionTrait.INJURY), condition(ConditionTrait.INJURY),
                condition(ConditionTrait.MADNESS), condition(ConditionTrait.DEAL), condition(ConditionTrait.DEAL)));
        clues.put(InvestigatorId.THE_SAILOR, 1);
        new YellowMythosCard14().execute();
        assertEquals(Arrays.asList("hold",
                "changeActiveInvestigator:THE_SAILOR", "showActiveInvestigator",
                "loseHealth:2", "loseSanity:1", "loseClue",
                "release"), effects);
    }

    @Test public void investigatorWithoutRelevantConditionsIsSkipped() {
        conditions.put(InvestigatorId.THE_SOLDIER, Collections.singletonList(condition(ConditionTrait.COMMON)));
        new YellowMythosCard14().execute();
        assertEquals(Arrays.asList("hold", "release"), effects);
    }

    private InvestigatorRead investigator(InvestigatorId id) {
        InvestigatorInfo info = mock(InvestigatorInfo.class, (name, args) -> id);
        return mock(InvestigatorRead.class, (name, args) -> name.equals("getInfo") ? info : null);
    }

    private ConditionInfo condition(ConditionTrait trait) {
        return mock(ConditionInfo.class, (name, args) -> name.equals("getTraits") ? EnumSet.of(trait) : null);
    }

    private <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (proxy, method, args) -> handler.apply(method.getName(), args)));
    }
}
