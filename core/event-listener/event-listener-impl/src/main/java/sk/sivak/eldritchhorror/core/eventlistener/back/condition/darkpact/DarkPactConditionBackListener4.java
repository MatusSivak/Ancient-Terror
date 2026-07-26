package sk.sivak.eldritchhorror.core.eventlistener.back.condition.darkpact;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;

import java.util.List;

public class DarkPactConditionBackListener4 extends AbstractConditionBackListener {

    public DarkPactConditionBackListener4(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    public void executeWhole() {
        ShowCardRequest showCardRequest = new ShowCardRequestBuilder(getConditionInfo())
                .withTitle("Roll 1 die. On a 1, flip this card.")
                .withDisplayAssetResponseType(DisplayAssetResponseType.OK)
                .build();
        ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(ok -> onConfirm());
    }

    @Override
    public void justFlip() {
        ShowCardRequest showCardRequest = new ShowCardRequestBuilder(getConditionInfo())
                .withTitle("Flip this card.")
                .withDisplayAssetResponseType(DisplayAssetResponseType.OK)
                .build();
        ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(ok -> onFlip(null));
    }

    private void onConfirm() {
        RollData rollData = new RollData();
        rollData.setMinSuccessful(2);
        rollData.setMaxFailed(1);
        Single<Integer> singleDieResult = ServicePlatform.get().getTestService().rollDie(rollData);
        singleDieResult.subscribe(this::resolveDiceRoll);
    }

    private void resolveDiceRoll(Integer rolledValue) {
        if (rolledValue <= 1) {
            onFlip(null);
        }
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos("[#BAD]You are Devoured[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getInvestigatorService().devourInvestigator(getActiveInvestigatorId());
            ServicePlatform.get().getService().release();
        });
    }
}
