package com.example.bios.crypton.pojos;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Currency implements Parcelable, Comparable<Currency> {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Currency> CREATOR = new Parcelable.Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };
    @SerializedName("Id")
    private String id;
    @SerializedName("Url")
    private String url;
    @SerializedName("ImageUrl")
    private String imageUrl;
    @SerializedName("Name")
    private String name;
    @SerializedName("Symbol")
    private String symbol;
    @SerializedName("CoinName")
    private String coinName;
    @SerializedName("FullName")
    private String fullName;
    @SerializedName("Algorithm")
    private String algorithm;
    @SerializedName("ProofType")
    private String proofType;
    @SerializedName("FullyPremined")
    private String fullyPremined;
    @SerializedName("TotalCoinSupply")
    private String totalCoinSupply;
    @SerializedName("PreMinedValue")
    private String preMinedValue;
    @SerializedName("TotalCoinsFreeFloat")
    private String totalCoinsFreeFloat;
    @SerializedName("SortOrder")
    private String sortOrder;
    @SerializedName("isTrading")
    private boolean isTrading;
    @SerializedName("Sponsored")
    private boolean sponsored;

    public Currency() {

    }

    public Currency(String id, String url, String imageUrl, String name, String symbol, String coinName, String fullName, String algorithm, String proofType, String fullyPremined, String totalCoinSupply, String preMinedValue, String totalCoinsFreeFloat, String sortOrder, boolean sponsored, boolean isTrading) {
        this.id = id;
        this.url = url;
        this.imageUrl = imageUrl;
        this.name = name;
        this.symbol = symbol;
        this.coinName = coinName;
        this.fullName = fullName;
        this.algorithm = algorithm;
        this.proofType = proofType;
        this.fullyPremined = fullyPremined;
        this.totalCoinSupply = totalCoinSupply;
        this.preMinedValue = preMinedValue;
        this.totalCoinsFreeFloat = totalCoinsFreeFloat;
        this.sortOrder = sortOrder;
        this.sponsored = sponsored;
        this.isTrading = isTrading;
    }

    protected Currency(Parcel in) {
        id = in.readString();
        url = in.readString();
        imageUrl = in.readString();
        name = in.readString();
        symbol = in.readString();
        coinName = in.readString();
        fullName = in.readString();
        algorithm = in.readString();
        proofType = in.readString();
        fullyPremined = in.readString();
        totalCoinSupply = in.readString();
        preMinedValue = in.readString();
        totalCoinsFreeFloat = in.readString();
        sortOrder = in.readString();
        isTrading = in.readByte() != 0x00;
        sponsored = in.readByte() != 0x00;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getProofType() {
        return proofType;
    }

    public void setProofType(String proofType) {
        this.proofType = proofType;
    }

    public String getFullyPremined() {
        return fullyPremined;
    }

    public void setFullyPremined(String fullyPremined) {
        this.fullyPremined = fullyPremined;
    }

    public String getTotalCoinSupply() {
        return totalCoinSupply;
    }

    public void setTotalCoinSupply(String totalCoinSupply) {
        this.totalCoinSupply = totalCoinSupply;
    }

    public String getPreMinedValue() {
        return preMinedValue;
    }

    public void setPreMinedValue(String preMinedValue) {
        this.preMinedValue = preMinedValue;
    }

    public String getTotalCoinsFreeFloat() {
        return totalCoinsFreeFloat;
    }

    public void setTotalCoinsFreeFloat(String totalCoinsFreeFloat) {
        this.totalCoinsFreeFloat = totalCoinsFreeFloat;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isSponsored() {
        return sponsored;
    }

    public void setSponsored(boolean sponsored) {
        this.sponsored = sponsored;
    }

    public boolean isTrading() {
        return isTrading;
    }

    public void setTrading(boolean trading) {
        isTrading = trading;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(imageUrl);
        dest.writeString(name);
        dest.writeString(symbol);
        dest.writeString(coinName);
        dest.writeString(fullName);
        dest.writeString(algorithm);
        dest.writeString(proofType);
        dest.writeString(fullyPremined);
        dest.writeString(totalCoinSupply);
        dest.writeString(preMinedValue);
        dest.writeString(totalCoinsFreeFloat);
        dest.writeString(sortOrder);
        dest.writeByte((byte) (isTrading ? 0x01 : 0x00));
        dest.writeByte((byte) (sponsored ? 0x01 : 0x00));
    }

    @Override
    public int compareTo(@NonNull Currency o) {
        int callingSortOrder = Integer.parseInt(this.getSortOrder());
        int argumentSortOrder = Integer.parseInt(o.getSortOrder());
        if (callingSortOrder < argumentSortOrder) {
            return -1;
        } else {
            return 1;
        }
    }
}