package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;

import java.util.List;

public class SelectSingleGateData {
    private List<GateInfo> gates;
    private String titleText;
    private String hideText = "Display Gates?";

    public SelectSingleGateData(List<GateInfo> gates) {
        this.gates = gates;
    }

    public List<GateInfo> getGates() {
        return gates;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getHideText() {
        return hideText;
    }

    public void setHideText(String hideText) {
        this.hideText = hideText;
    }
}
