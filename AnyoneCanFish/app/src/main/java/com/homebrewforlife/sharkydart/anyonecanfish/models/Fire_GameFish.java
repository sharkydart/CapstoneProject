package com.homebrewforlife.sharkydart.anyonecanfish.models;

public class Fire_GameFish {
    private String image_url;
    private String information;
    private String species;
    private String wiki;

    public Fire_GameFish(){}

    public Fire_GameFish(String image_url, String information, String species, String wiki) {
        this.image_url = image_url;
        this.information = information;
        this.species = species;
        this.wiki = wiki;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getWiki() {
        return wiki;
    }

    public void setWiki(String wiki) {
        this.wiki = wiki;
    }
}
