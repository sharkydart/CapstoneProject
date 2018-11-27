package com.homebrewforlife.sharkydart.anyonecanfish.models;

import java.util.List;

public class Fire_TackleBox {
    private List<Fire_Lure> theLures;
    private String desc;
    private String name;

    public Fire_TackleBox(){}

    public Fire_TackleBox(List<Fire_Lure> theLures, String desc, String name) {
        this.theLures = theLures;
        this.desc = desc;
        this.name = name;
    }

    public List<Fire_Lure> getTheLures() {
        return theLures;
    }

    public void setTheLures(List<Fire_Lure> theLures) {
        this.theLures = theLures;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
