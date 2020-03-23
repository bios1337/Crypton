package com.example.bios.crypton.netutils;

import android.net.Uri;

public class NetworkUtils {

    public static final String BASE_URL = "https://min-api.cryptocompare.com/data/all/";
    public static final String BASE_IMAGE_URL = "https://www.cryptocompare.com";
    public static final String BASE_PRICE_URL = "https://min-api.cryptocompare.com/data/price?";

    public static String getCoinListUrl() {
        return BASE_URL.concat("coinlist");
    }

    public static String getPricingUrl(String fsym) {
        Uri uri = Uri.parse(BASE_PRICE_URL)
                .buildUpon().appendQueryParameter("fsym", fsym)
                .build();
        return uri.toString().concat("&").concat("tsyms=BTC,USD,EUR");
    }

    public static String getPricingUrl(String fsym, String tsyms) {
        Uri uri = Uri.parse(BASE_PRICE_URL)
                .buildUpon().appendQueryParameter("fsym", fsym)
                .appendQueryParameter("tsyms", tsyms)
                .build();
        return uri.toString();
    }

}
