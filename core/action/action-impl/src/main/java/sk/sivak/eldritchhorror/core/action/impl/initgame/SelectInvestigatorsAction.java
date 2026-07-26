package sk.sivak.eldritchhorror.core.action.impl.initgame;

import java8.features.function.Supplier;
import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

import java.util.Arrays;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.SELECT_INVESTIGATORS;

/**
 * @author msivak
 */
public class SelectInvestigatorsAction implements Action<Integer, InvestigatorInfo[]> {

    private static final Logger logger = LogManager.getLogger(SelectInvestigatorsAction.class);

    private Integer input;

    @Override
    public Single<InvestigatorInfo[]> execute() {
        if (!GoogleServicesHolder.isTutorialPassed()) {
            return Single.create(onSub -> {
                InvestigatorInfo spy = Stream.findFirstOrException(ServicePlatform.get().getInvestigators().getAvailableInvestigators(),
                        investigatorInfo -> investigatorInfo.getInvestigatorId() == InvestigatorId.THE_SPY);
                initSelectedInvestigators(onSub, new InvestigatorInfo[]{spy});
            });
        }
        Single<InvestigatorInfo[]> single = Single.create(onSub());
        return single.cache();
    }

    private Single.OnSubscribe<InvestigatorInfo[]> onSub() {
        return sub -> {
            long start = System.currentTimeMillis();
            Supplier<List<InvestigatorInfo>> initInvestigatorsAction = () -> {
                ServicePlatform.get().getInvestigators().initAvailableInvestigators(true);
                ServicePlatform.get().getInvestigators().initWithPlayers(input);
                return ServicePlatform.get().getInvestigators().getAvailableInvestigators();
            };
            ServicePlatform.get().getInitGameController().selectInvestigators(input, initInvestigatorsAction).subscribe(res -> {
                GoogleServicesHolder.getAnalyticsTracker().trackTiming(SELECT_INVESTIGATORS, "duration", System.currentTimeMillis()-start);
                initSelectedInvestigators(sub, res);
            });
        };
    }

    private void initSelectedInvestigators(SingleSubscriber<? super InvestigatorInfo[]> sub, InvestigatorInfo[] res) {
        logger.debug("Initializing selected investigators");
        ServicePlatform.get().getInvestigators().initSelectedInvestigators(res);
        InvestigatorId[] investigatorIds = Stream.map(Arrays.asList(res), InvestigatorInfo::getInvestigatorId)
                .toArray(new InvestigatorId[res.length]);
        ServicePlatform.get().getPerformedActions().init(investigatorIds);
        for (InvestigatorInfo investigator : res) {
            ServicePlatform.get().getEventListenerProvider().registerInvestigatorListeners(investigator.getInvestigatorId());
        }
        for (InvestigatorId investigatorId : investigatorIds) {
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(SELECT_INVESTIGATORS, "name", investigatorId.name());
        }

        sub.onSuccess(res);
    }

    @Override
    public void setInput(Integer input) {
        this.input = input;
    }
}
