package sk.sivak.eldritchhorror.core.eventlistener.action.basic;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.MonsterCupRead;

/**
 * @author msivak
 */
public class RestActionListener extends AbstractActionPhaseListener<RestActionListener.RestAction> {

    private static final Logger logger = LogManager.getLogger(RestActionListener.class);

    public RestActionListener() {
        name = "Rest";
    }

    private String additionalInfo;
    @Override
    protected String getAdditionalInfo() {
        isNotRecommended();
        return additionalInfo;
    }

    @Override
    protected String getGeneralDescription() {
        return "If there are no monsters\n" +
                "on this space,\n" +
                "recover Health and Sanity";
    }

    @Override
    protected RestAction createAction() {
        return new RestAction();
    }

    @Override
    protected boolean isVisible() {
        return true;
    }

    @Override
    protected boolean isDisabled() {
        InvestigatorRead activeInvestigator = getActiveInvestigator();
        MonsterCupRead monsterCup = ServicePlatform.get().getMonsterCup();
        if (monsterCup.containsMonster(activeInvestigator.getCurrentLocationId())) {
            disabledReason = "Cannot rest on space containing monsters.";
            return true;
        }
        return false;
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.REST;
    }

    @Override
    protected String getTexturePath() {
        return "action_button/rest.png";
    }

    @Override
    protected boolean isNotRecommended() {
        InvestigatorRead activeInvestigator = getActiveInvestigator();
        int maxHealth = activeInvestigator.getInfo().getMaxHealth();
        int maxSanity = activeInvestigator.getInfo().getMaxSanity();

        if (maxHealth == activeInvestigator.getCurrentHealth() && maxSanity == activeInvestigator.getCurrentSanity()) {
            notRecommendedReason = "Health and Sanity already full.";
            return true;
        } else if (maxHealth == activeInvestigator.getCurrentHealth()) {
            notRecommendedReason = "Health already full.";
            additionalInfo = "+1 Sanity";
            return true;
        } else if (maxSanity == activeInvestigator.getCurrentSanity()) {
            notRecommendedReason = "Sanity already full.";
            additionalInfo = "+1 Health";
            return true;
        }
        additionalInfo = "+1 Health\n+1 Sanity";
        return false;
    }

    protected class RestAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing rest action...");
            ServicePlatform.get().getBasicActionService().rest();
        }
    }

}
