package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.CrystalOfTheElderThingsArtifact;
import sk.sivak.eldritchhorror.core.constants.artifact.CursedSphereArtifact;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.card.AbstractCardReckoningListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.TokenType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CrystalOfTheElderThingsListener extends AbstractArtifactListener<CrystalOfTheElderThingsArtifact> {

    private static final Logger logger = LogManager.getLogger(CrystalOfTheElderThingsListener.class);
    private BeforeLoseHealthOrSanityListener beforeLoseHealthOrSanityListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(beforeLoseHealthOrSanityListener);

    }

    @Override
    protected void register() {
        beforeLoseHealthOrSanityListener = new BeforeLoseHealthOrSanityListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeLoseHealthOrSanityListener, BeforeAfterEvent.LOSE_SANITY);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeLoseHealthOrSanityListener, BeforeAfterEvent.LOSE_HEALTH);
    }

    private class BeforeLoseHealthOrSanityListener extends AbstractArtifactEventListener<LoseTokenData> {
        public BeforeLoseHealthOrSanityListener() {
            super(CrystalOfTheElderThingsListener.this);
        }

        @Override
        protected Runnable getEventAction(LoseTokenData input) {
            return () -> {
                if (!ServicePlatform.get().getModel().isMythosCardTextEffect()) {
                    return;
                }
                if (input.getTokenType() != TokenType.HEALTH && input.getTokenType() != TokenType.SANITY) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequestBuilder(getArtifactInfo())
                        .withAssetHideType(HideType.RETURN)
                        .withDisplayAssetResponseType(DisplayAssetResponseType.OK)
                        .withTitle("Can't lose "+(input.getTokenType() == TokenType.SANITY ? "Sanity." : "Health."))
                        .withAssetOriginType(AssetOriginType.INVENTORY).build();
                ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(ok -> {
                    input.setAmount(0);
                    ServicePlatform.get().getService().convertTo(LoseTokenData.class, () -> input);
                });

            };
        }

        @Override
        public Class<LoseTokenData> getDataClass() {
            return LoseTokenData.class;
        }
    }

}
