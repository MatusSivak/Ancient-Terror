package sk.sivak.eldritchhorror.core.action.impl.investigator;

import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

import java.util.List;

public class DefeatInvestigatorAction extends AbstractHookableAction<Object, Void> {

    private final InvestigatorId investigatorId;
    private final boolean health;

    public DefeatInvestigatorAction(InvestigatorId investigatorId, boolean health) {
        super(BeforeAfterEvent.DEFEAT_INVESTIGATOR);
        this.investigatorId = investigatorId;
        this.health = health;
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {

        ServicePlatform.get().getService().hold();


        Question<Object> question = new Question<>();
        question.setOptions(Question.Option.okOption);
        question.setTitle("The " + investigatorId.toString() + " is defeated!");
        question.setPortraitBeforeTitle(investigatorId);
        ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
            confirmDefeated();
        });
        ServicePlatform.get().getService().release();
        ss.onSuccess(null);
    }

    private void confirmDefeated() {

        ServicePlatform.get().getService().hold();
        discardConditions(investigatorId);
        disableAssets();
        disableSpells();
        disableArtifacts();
        ServicePlatform.get().getDoomOmenService().advanceDoom();
        travelToNearestCity();


        ServicePlatform.get().getEventListenerProvider().unregisterInvestigatorListeners(investigatorId);
        ServicePlatform.get().getEventListenerProvider().registerDefeatedListener(investigatorId, health);

        ServicePlatform.get().getService().addEventCommand(in -> {
            return Single.create(onSubInternal -> {
                ServicePlatform.get().getGameController().defeatInvestigator(investigatorId, health).subscribe(() -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getInvestigators().defeatInvestigator(investigatorId);
                    ServicePlatform.get().getPerformedActions().clearFreeActions(investigatorId);
                    ServicePlatform.get().getPerformedEncounters().disableAnotherEncounter(investigatorId);
                    if (ServicePlatform.get().getInvestigators().getLeadInvestigator() == null) {
                        ServicePlatform.get().getInvestigatorService().selectNewLeadInvestigator(false);
                    }
                    ServicePlatform.get().getService().convertToNull();
                    ServicePlatform.get().getService().release();
                    onSubInternal.onSuccess(in);
                });
            });
        });

        ServicePlatform.get().getService().release();
    }

    private void travelToNearestCity() {
        InvestigatorWrite investigator = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId);
        FindNearestData nearestCity = ServicePlatform.get().getLocationMap().findNearest(
                investigator.getCurrentLocationId(), locationInfo -> locationInfo.getLocationType() == LocationType.CITY);
        if (ServicePlatform.get().getLocationMap().getLocationInfo(investigator.getCurrentLocationId()).getLocationType() == LocationType.CITY) {
            return;
        }
        ServicePlatform.get().getBasicActionService().travelToLocation(new LocationInfo.Connection() {
            @Override
            public LocationId getLocationId() {
                return nearestCity.getLocationId();
            }

            @Override
            public PathType getPathType() {
                return null;
            }
        });
    }

    private void discardConditions(InvestigatorId activeInvestigatorId) {
        List<ConditionInfo> conditions = ServicePlatform.get().getConditionsDeck().getConditions(activeInvestigatorId);
        for (ConditionInfo condition : conditions) {
            ServicePlatform.get().getService().discardConditionFromInvestigator(activeInvestigatorId, condition);
        }
    }

    private void disableAssets() {
        for (AssetInfo asset : ServicePlatform.get().getAssetsDeck().getAssets(investigatorId)) {
            ServicePlatform.get().getAssetListenerProvider().getAssetListener(asset.getId()).unregister();
        }
    }

    private void disableArtifacts() {
        for (ArtifactInfo artifact : ServicePlatform.get().getArtifactsDeck().getArtifacts(investigatorId)) {
            ServicePlatform.get().getArtifactListenerProvider().getArtifactListener(artifact.getId()).unregister();
        }
    }

    private void disableSpells() {
        for (SpellInfo spellInfo : ServicePlatform.get().getSpellsDeck().getSpells(investigatorId)) {
            ServicePlatform.get().getSpellListenerProvider().getSpellListener(spellInfo).unregister();
        }
    }

}
