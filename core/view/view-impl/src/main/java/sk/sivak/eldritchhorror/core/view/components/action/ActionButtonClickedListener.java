package sk.sivak.eldritchhorror.core.view.components.action;

import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;

interface ActionButtonClickedListener {
    ActionButtonClickedListener empty = new ActionButtonClickedListener() {

        @Override
        public void onEnabledButtonClickedEvent(ActionButtonData actionButtonData) {

        }

        @Override
        public void onDisabledButtonClickedEvent(ActionButtonData actionButtonData) {

        }
    };

    void onEnabledButtonClickedEvent(ActionButtonData actionButtonData);

    void onDisabledButtonClickedEvent(ActionButtonData actionButtonData);
}
