package com.example.bios.crypton.netutils;

import okhttp3.OkHttpClient;

public class CoinListClient {
    private static OkHttpClient okHttpClient = null;

    public static OkHttpClient getClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }
}
