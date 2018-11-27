package com.homebrewforlife.sharkydart.anyonecanfish.models;

public class Fire_Lure {
    private String name;
    private String size;
    private String type;
    private String desc;
    private String image_url;
    private String hook_type;
    private int hook_count;

    public Fire_Lure(){}

    public Fire_Lure(String name, String size, String type, String desc, String image_url, String hook_type, int hook_count) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.desc = desc;
        this.image_url = image_url;
        this.hook_type = hook_type;
        this.hook_count = hook_count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getHook_type() {
        return hook_type;
    }

    public void setHook_type(String hook_type) {
        this.hook_type = hook_type;
    }

    public int getHook_count() {
        return hook_count;
    }

    public void setHook_count(int hook_count) {
        this.hook_count = hook_count;
    }
}
