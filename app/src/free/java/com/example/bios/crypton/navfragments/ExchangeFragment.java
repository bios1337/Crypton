package com.example.bios.crypton.navfragments;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bios.crypton.R;
import com.example.bios.crypton.netutils.CoinListClient;
import com.example.bios.crypton.netutils.NetworkUtils;
import com.example.bios.crypton.pojos.Currency;
import com.example.bios.crypton.viewmodels.BottomNavigationViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExchangeFragment extends Fragment {

    private static final String SPINNER_FROM = "spinner_from";
    private static final String SPINNER_TO = "spinner_to";
    List<String> symbols;

    private InterstitialAd mInterstitialAd;

    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private EditText coinValueEditText;
    private Button buttonExchangeValue;
    private TextView resultTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_exchange, container, false);

        spinnerFrom = view.findViewById(R.id.spinner_from_coin);
        spinnerTo = view.findViewById(R.id.spinner_to_coin);
        coinValueEditText = view.findViewById(R.id.coin_value);
        buttonExchangeValue = view.findViewById(R.id.button_exchange_value);
        resultTextView = view.findViewById(R.id.result_text_view);

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("B593240C3E87A7A5DCDF8C1B78C5FD9E").build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                performExchange();
            }
        });

        buttonExchangeValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });

        BottomNavigationViewModel model = ViewModelProviders.of(getActivity()).get(BottomNavigationViewModel.class);
        model.getCurrencies().observe(this, new Observer<List<Currency>>() {
            @Override
            public void onChanged(@Nullable List<Currency> currencies) {
                symbols = new ArrayList<>();
                if (currencies != null) {
                    for (Currency c : currencies) {
                        symbols.add(c.getSymbol());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getActivity(), android.R.layout.simple_spinner_item, symbols);

                spinnerFrom.setAdapter(adapter);
                spinnerTo.setAdapter(adapter);
                if (savedInstanceState != null) {
                    int selectedFrom = savedInstanceState.getInt(SPINNER_FROM);
                    int selectedTo = savedInstanceState.getInt(SPINNER_TO);
                    spinnerFrom.setSelection(selectedFrom);
                    spinnerTo.setSelection(selectedTo);
                }
            }
        });

        return view;
    }


    private void checkInternetAndShowToast() {
        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(), "No Internet", Toast.LENGTH_LONG).show();
            return;
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void performExchange() {

        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(), "No Internet", Toast.LENGTH_LONG).show();
            return;
        }


        String string = coinValueEditText.getText().toString();
        if (string.isEmpty()) {
            Toast.makeText(getActivity(), "Enter some value first!", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            Double value = Double.parseDouble(string);
            String symbol1 = spinnerFrom.getSelectedItem().toString();
            String symbol2 = spinnerTo.getSelectedItem().toString();
            loadPricingData(value, symbol1, symbol2);

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Enter numerical value!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadPricingData(final Double value, final String symbol1, final String symbol2) {
        OkHttpClient okHttpClient = CoinListClient.getClient();
        Request request = new Request.Builder()
                .url(NetworkUtils.getPricingUrl(symbol1, symbol2))
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "Failed to load pricing data", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject responseJson = new JSONObject(response.body().string());
                    final Double resultForOne = responseJson.getDouble(symbol2);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String result = String.valueOf(value * resultForOne);
                            String formattedResult = String.valueOf(value).concat(" " + symbol1)
                                    .concat(" = ").concat(result).concat(" " + symbol2);
                            flipIt(resultTextView);
                            resultTextView.setText(formattedResult);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void flipIt(final View viewToFlip) {
        ObjectAnimator flip = ObjectAnimator.ofFloat(viewToFlip, "rotationX", 180f, 360f);
        flip.setDuration(500);
        flip.start();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        int selectedFrom = spinnerFrom.getSelectedItemPosition();
        int selectedTo = spinnerTo.getSelectedItemPosition();
        outState.putInt(SPINNER_FROM, selectedFrom);
        outState.putInt(SPINNER_TO, selectedTo);
    }
}