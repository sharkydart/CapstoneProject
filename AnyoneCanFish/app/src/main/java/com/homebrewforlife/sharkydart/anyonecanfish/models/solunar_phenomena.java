package com.homebrewforlife.sharkydart.anyonecanfish.models;

public class solunar_phenomena {
    private String phen;
    private String time;

    public solunar_phenomena(){}

    public solunar_phenomena(String phen, String time) {
        this.phen = phen;
        this.time = time;
    }

    public String getPhen() {
        return phen;
    }

    public void setPhen(String phen) {
        this.phen = phen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
