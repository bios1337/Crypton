package com.example.bios.crypton.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import com.example.bios.crypton.netutils.CoinListClient;
import com.example.bios.crypton.netutils.NetworkUtils;
import com.example.bios.crypton.pojos.Currency;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BottomNavigationViewModel extends ViewModel {
    private MutableLiveData<List<Currency>> currencies;

    public LiveData<List<Currency>> getCurrencies() {
        if (currencies == null) {
            currencies = new MutableLiveData<List<Currency>>();
            loadCurrencies();
        }
        return currencies;
    }

    private void loadCurrencies() {
        try {
            URL url = new URL(NetworkUtils.getCoinListUrl());
            new MyAsyncTask().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    class MyAsyncTask extends AsyncTask<URL, Void, Void> {

        @Override
        protected Void doInBackground(URL... urls) {

            final List<Currency> temp = new ArrayList<>();

            OkHttpClient okHttpClient = CoinListClient.getClient();
            Request request = new Request.Builder()
                    .url(NetworkUtils.getCoinListUrl())
                    .build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                JSONObject responseJson = null;
                Gson gson = new GsonBuilder().create();
                if (!response.isSuccessful()) {
                    throw new IOException("IOException" + response);
                } else {
                    try {
                        responseJson = new JSONObject(response.body().string());
                        JSONObject data = responseJson.getJSONObject("Data");
                        Iterator<String> iter = data.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            JSONObject currency = data.getJSONObject(key);
                            Currency c1 = gson.fromJson(currency.toString(), Currency.class);
                            temp.add(c1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(temp);
                currencies.postValue(temp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
