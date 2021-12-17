package com.lq.plugin.init;

import com.google.gson.Gson;

public class DeepLinkClassInfo {

    private String fullName;

    private String path;

    public DeepLinkClassInfo() {
        clear();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPath() {
        return path;
    }

    public void clear() {
        this.fullName = "";
        this.path = "/";
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
