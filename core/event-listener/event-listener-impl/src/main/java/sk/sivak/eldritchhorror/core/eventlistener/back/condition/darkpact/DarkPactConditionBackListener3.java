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

public class DarkPactConditionBackListener3 extends AbstractConditionBackListener {

    public DarkPactConditionBackListener3(ConditionInfo conditionInfo) {
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
        TypewriterUtils.confirmInfos(
                "[#BAD]One Gate is spawned for each Spell you have[]",
                "[#BAD]Omen advances[]",
                "[#GOOD]Discard this card[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            List<SpellInfo> spells = ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId());

            if (spells.isEmpty()) {
                ServicePlatform.get().getDoomOmenService().advanceOmen();
                discardThisCard();
                ServicePlatform.get().getService().release();
                return;
            }

            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            Question.SelectComponentsTableData<SpellInfo> selectComponentsTableData = new Question.SelectComponentsTableData<>();
            selectComponentsTableData.setAvailableKeys(spells);
            selectComponentsTableData.setType(Question.SelectComponentsTableType.CARDS);
            question.setTitle("These are your Spells.");
            question.setSelectComponentsTableData(selectComponentsTableData);

            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getService().hold();
                for (int i = 0; i < spells.size(); i++) {
                    ServicePlatform.get().getGameService().spawnGates(1, 1).subscribe();
                }
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().moveCameraToLocation(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());

            ServicePlatform.get().getDoomOmenService().advanceOmen();
            discardThisCard();
            ServicePlatform.get().getService().release();
        });
    }

    private void discardThisCard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(getConditionInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard Dark Pact Condition");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), getConditionInfo());
        });
    }
}
