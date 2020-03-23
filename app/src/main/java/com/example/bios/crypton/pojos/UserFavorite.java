package com.example.bios.crypton.pojos;

import java.util.ArrayList;
import java.util.List;

public class UserFavorite {
    public String userName;
    public List<Currency> favCoins = new ArrayList<>();

    public UserFavorite(String userName, List<Currency> favCoins) {
        this.userName = userName;
        this.favCoins = favCoins;
    }

    public UserFavorite() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Currency> getFavCoins() {
        return favCoins;
    }

    public void setFavCoins(List<Currency> favCoins) {
        this.favCoins = favCoins;
    }

}
