package sk.sivak.eldritchhorror.core.eventlistener.provider;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.action.ActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.action.basic.*;
import sk.sivak.eldritchhorror.core.eventlistener.action.character.*;

/**
 * @author msivak
 */
public class ActionListenerProviderImpl implements ActionListenerProvider {

    private static final Logger logger = LogManager.getLogger(ActionListenerProviderImpl.class);

    private ThePoliticianActionListener thePoliticianActionListener;
    private TheSailorActionListener theSailorActionListener;
    private TheBountyHunterActionListener theBountyHunterActionListener;
    private TheRedeemedCultistActionListener theRedeemedCultistActionListener;
    private TheSpyActionListener theSpyActionListener;
    private TheRookieCopActionListener theRookieCopActionListener;
    private TheAstronomerActionListener theAstronomerActionListener;
    private TheSoldierActionListener theSoldierActionListener;
    private ThePsychicActionListener thePsychicActionListener;
    private TheMusicianActionListener theMusicianActionListener;
    private TheShamanActionListener theShamanActionListener;
    private TheActressActionListener theActressActionListener;
    private TheExConvictActionListener theExConvictActionListener;
    private TheExpeditionLeaderActionListener theExpeditionLeaderActionListener;
    private TheMartialArtistActionListener theMartialArtistActionListener;
    private TheEntertainerActionListener theEntertainerActionListener;
    private TheBootleggerActionListener theBootleggerActionListener;
    private TheHandymanActionListener theHandymanActionListener;
    private TheViolinistActionListener theViolinistActionListener;
    private TheWaitressActionListener theWaitressActionListener;


    private FocusActionListener focusActionListener;
    private TicketActionListener ticketActionListener;
    private TradeActionListener tradeActionListener;
    private TravelActionListener travelActionListener;
    private RestActionListener restActionListener;
    private AcquireAssetsActionListener acquireAssetsActionListener;
    private SkipActionListener skipActionListener;


    @Override
    public void init() {
        restActionListener = new RestActionListener();
        travelActionListener = new TravelActionListener();
        tradeActionListener = new TradeActionListener();
        ticketActionListener = new TicketActionListener();
        focusActionListener = new FocusActionListener();
        acquireAssetsActionListener = new AcquireAssetsActionListener();
        skipActionListener = new SkipActionListener();
        theSpyActionListener = new TheSpyActionListener();
        theRookieCopActionListener = new TheRookieCopActionListener();
        theAstronomerActionListener = new TheAstronomerActionListener();
        theActressActionListener = new TheActressActionListener();
        theSoldierActionListener = new TheSoldierActionListener();
        thePsychicActionListener = new ThePsychicActionListener();
        theMusicianActionListener = new TheMusicianActionListener();
        theShamanActionListener = new TheShamanActionListener();
        theExConvictActionListener = new TheExConvictActionListener();
        theExpeditionLeaderActionListener = new TheExpeditionLeaderActionListener();
        theMartialArtistActionListener = new TheMartialArtistActionListener();
        theEntertainerActionListener = new TheEntertainerActionListener();
        theBountyHunterActionListener = new TheBountyHunterActionListener();
        theRedeemedCultistActionListener = new TheRedeemedCultistActionListener();
        thePoliticianActionListener = new ThePoliticianActionListener();
        theSailorActionListener = new TheSailorActionListener();
        theBootleggerActionListener = new TheBootleggerActionListener();
        theHandymanActionListener = new TheHandymanActionListener();
        theViolinistActionListener = new TheViolinistActionListener();
        theWaitressActionListener = new TheWaitressActionListener();
    }

    @Override
    public ActionPhaseListener getRestActionListener() {
        return restActionListener;
    }

    @Override
    public TravelActionListener getTravelActionListener() {
        return travelActionListener;
    }

    @Override
    public ActionPhaseListener getTradeActionListener() {
        return tradeActionListener;
    }

    @Override
    public ActionPhaseListener getTicketActionListener() {
        return ticketActionListener;
    }

    @Override
    public ActionPhaseListener getFocusActionListener() {
        return focusActionListener;
    }

    @Override
    public ActionPhaseListener getAcquireAssetsActionListener() {
        return acquireAssetsActionListener;
    }

    @Override
    public ActionPhaseListener getSkipActionListener() {
        return skipActionListener;
    }

    @Override
    public ActionPhaseListener getInvestigatorActionListener(InvestigatorId investigatorId) {
        switch (investigatorId) {
            case THE_BOUNTY_HUNTER:
                return theBountyHunterActionListener;
            case THE_REDEEMED_CULTIST:
                return theRedeemedCultistActionListener;
            case THE_SPY:
                return theSpyActionListener;
            case THE_ASTRONOMER:
                theAstronomerActionListener.registerInitActionButtonListener();
                return theAstronomerActionListener;
            case THE_SOLDIER:
                return theSoldierActionListener;
            case THE_PSYCHIC:
                return thePsychicActionListener;
            case THE_EX_CONVICT:
                return theExConvictActionListener;
            case THE_MUSICIAN:
                return theMusicianActionListener;
            case THE_EXPEDITION_LEADER:
                return theExpeditionLeaderActionListener;
            case THE_ACTRESS:
                return theActressActionListener;
            case THE_MARTIAL_ARTIST:
                return theMartialArtistActionListener;
            case THE_ENTERTAINER:
                return theEntertainerActionListener;
            case THE_POLITICIAN:
                return thePoliticianActionListener;
            case THE_ROOKIE_COP:
                return theRookieCopActionListener;
            case THE_SAILOR:
                return theSailorActionListener;
            case THE_SHAMAN:
                return theShamanActionListener;
            case THE_BOOTLEGGER:
                return theBootleggerActionListener;
            case THE_HANDYMAN:
                return theHandymanActionListener;
            case THE_VIOLINIST:
                theViolinistActionListener.registerInitActionButtonListener();
                return theViolinistActionListener;
            case THE_WAITRESS:
                return theWaitressActionListener;
        }
        logger.warn("Action listener not found for " + investigatorId);
        return null;
    }
}
