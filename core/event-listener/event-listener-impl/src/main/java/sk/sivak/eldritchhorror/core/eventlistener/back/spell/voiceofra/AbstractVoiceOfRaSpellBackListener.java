package sk.sivak.eldritchhorror.core.eventlistener.back.spell.voiceofra;

import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import java.util.List;

public abstract class AbstractVoiceOfRaSpellBackListener extends AbstractSpellBackListener {

    public AbstractVoiceOfRaSpellBackListener(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    public void executeWhole() {
        ShowCardRequest request = new ShowCardRequest();
        request.setHideType(HideType.RETURN);
        request.setSpellInfo(getSpellInfo());
        request.setTitle("Test Lore and flip this card.");
        request.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        ServicePlatform.get().getGameService().showCard(request).subscribe(this::onTest);
    }

    private void onTest(ShowCardResponse showCardResponse) {
        ServicePlatform.get().getTestService().test(Stat.LORE, 0, 1,
                new TestFlavorRequest(TestFlavorType.SPELL, getSpellInfo())).subscribe(testResult -> {
                    setTestData(testResult);
                    super.executeWhole();
        });
    }
}
