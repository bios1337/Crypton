package com.example.bios.crypton.pojos;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.List;

public class CryptoCompareResponse {

    @SerializedName("Response")
    private String response;
    @SerializedName("Message")
    private String message;
    @SerializedName("BaseImageUrl")
    private String baseImageUrl;
    @SerializedName("BaseLinkUrl")
    private String baseLinkUrl;
    @SerializedName("DefaultWatchlist")
    private JSONObject defaultWatchList;
    @SerializedName("SponosoredNews")
    private List<JSONObject> sponsoredNews;
    @SerializedName("Data")
    private JSONObject data;
    @SerializedName("Type")
    private Integer type;

    public CryptoCompareResponse(String response, String message, String baseImageUrl, String baseLinkUrl, JSONObject defaultWatchList, List<JSONObject> sponsoredNews, JSONObject data, Integer type) {
        this.response = response;
        this.message = message;
        this.baseImageUrl = baseImageUrl;
        this.baseLinkUrl = baseLinkUrl;
        this.defaultWatchList = defaultWatchList;
        this.sponsoredNews = sponsoredNews;
        this.data = data;
        this.type = type;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBaseImageUrl() {
        return baseImageUrl;
    }

    public void setBaseImageUrl(String baseImageUrl) {
        this.baseImageUrl = baseImageUrl;
    }

    public String getBaseLinkUrl() {
        return baseLinkUrl;
    }

    public void setBaseLinkUrl(String baseLinkUrl) {
        this.baseLinkUrl = baseLinkUrl;
    }

    public JSONObject getDefaultWatchList() {
        return defaultWatchList;
    }

    public void setDefaultWatchList(JSONObject defaultWatchList) {
        this.defaultWatchList = defaultWatchList;
    }

    public List<JSONObject> getSponsoredNews() {
        return sponsoredNews;
    }

    public void setSponsoredNews(List<JSONObject> sponsoredNews) {
        this.sponsoredNews = sponsoredNews;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
