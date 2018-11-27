package com.homebrewforlife.sharkydart.anyonecanfish.models;

import java.util.List;

public class MySolunar {
/*
The computed 'phen' are identified by the following abbreviations or symbols:

BC = Begin civil twilight
R = Rise
U = Upper Transit
S = Set
EC = End civil twilight
L = Lower Transit (above the horizon)
** = object continuously above the horizon
-- = object continuously below the horizon
^^ = object continuously above the twilight limit
~~ = object continuously below the twilight limit

    sundata - array(5) of objects
            { phen - phenomenon code, time - local time of the phase}
    moondata - array(3) of objects
            { phen - phenomenon code, time - local time of the phase}
    fracillum - fraction of the Moon's apparent surface which is lit
                (if "closestphase" occurs on date requested, "fracillum" will not be in JSON)
    curphase - phase of the Moon on the date requested (if "closestphase" occurs on date requested, "curphase" will not be in JSON)

    //not including this
    "closestphase," which contains information on the nearest primary phase to the date requested:
        "phase" — phase of the moon at the nearest primary phase to the date requested
        "date" — the date of the primary phase
        "time" — the time of the primary phase
*/
    private List<solunar_phenomena> sundata;
    private List<solunar_phenomena> moondata;
    private String fracillum;
    private String curphase;

    public MySolunar(){}

    public MySolunar(List<solunar_phenomena> sundata, List<solunar_phenomena> moondata, String fracillum, String curphase) {
        this.sundata = sundata;
        this.moondata = moondata;
        this.fracillum = fracillum;
        this.curphase = curphase;
    }

    public List<solunar_phenomena> getSundata() {
        return sundata;
    }

    public void setSundata(List<solunar_phenomena> sundata) {
        this.sundata = sundata;
    }

    public List<solunar_phenomena> getMoondata() {
        return moondata;
    }

    public void setMoondata(List<solunar_phenomena> moondata) {
        this.moondata = moondata;
    }

    public String getFracillum() {
        return fracillum;
    }

    public void setFracillum(String fracillum) {
        this.fracillum = fracillum;
    }

    public String getCurphase() {
        return curphase;
    }

    public void setCurphase(String curphase) {
        this.curphase = curphase;
    }
}
