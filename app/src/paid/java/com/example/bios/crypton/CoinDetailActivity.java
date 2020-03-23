package com.example.bios.crypton;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bios.crypton.netutils.CoinListClient;
import com.example.bios.crypton.netutils.NetworkUtils;
import com.example.bios.crypton.pojos.Currency;
import com.example.bios.crypton.pojos.UserFavorite;
import com.example.bios.crypton.widget.CoinWidgetProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CoinDetailActivity extends AppCompatActivity {

    private static final String TAG = CoinDetailActivity.class.getSimpleName();
    private static final String USER_REFERENCE = "users";
    private static final String WALLET_REFERENCE = "wallet";
    @BindView(R.id.coin_image_view)
    ImageView mImageView;
    @BindView(R.id.coin_name)
    TextView mName;
    @BindView(R.id.coin_symbol)
    TextView mCoinSymbol;
    @BindView(R.id.coin_coinname)
    TextView mCoinName;
    @BindView(R.id.coin_fullname)
    TextView mCoinFullName;
    @BindView(R.id.coin_algorithm)
    TextView mCoinAlgorithm;
    @BindView(R.id.coin_prooftype)
    TextView mCoinProofType;
    @BindView(R.id.coin_fullpremined)
    TextView mCoinFullyPremined;
    @BindView(R.id.coin_total_supply)
    TextView mCoinTotalCoinSupply;
    @BindView(R.id.coin_premined_value)
    TextView mCoinPreMinedValue;
    @BindView(R.id.coin_total_free_float)
    TextView mTotalCoinsFreeFloat;
    @BindView(R.id.coin_istrading)
    TextView mCoinTrading;

    @BindView(R.id.coin_btc_value)
    TextView mCoinBtcTv;
    @BindView(R.id.coin_usd_value)
    TextView mCoinUsdTv;
    @BindView(R.id.coin_eur_value)
    TextView mCoinEurTv;
    private Currency currency;
    private OkHttpClient okHttpClient = null;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private String btcValue;
    private String usdValue;
    private String eurValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_detail);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(USER_REFERENCE);
        currency = getIntent().getParcelableExtra(Currency.class.getSimpleName());
        ButterKnife.bind(this);
        Log.d(TAG, currency.getFullName());
        loadCurrencyImage();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(currency.getCoinName());
        }

        if (!isNetworkAvailable()) {
            showInternetSnackBar();
        } else {
            getPricingDetails();
        }
    }

    private void loadCurrencyImage() {
        Picasso picasso = Picasso.get();
        picasso.load(NetworkUtils.BASE_IMAGE_URL.concat(currency.getImageUrl())).into(mImageView);
    }

    private void getPricingDetails() {

        String pricingUrl = NetworkUtils.getPricingUrl(currency.getSymbol());
        okHttpClient = CoinListClient.getClient();
        Request request = new Request.Builder()
                .url(pricingUrl)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject responseJson = null;
                if (!response.isSuccessful()) {
                    throw new IOException("IOException" + response);
                } else {
                    try {
                        responseJson = new JSONObject(response.body().string());
                        btcValue = String.valueOf(responseJson.getDouble("BTC"));
                        eurValue = String.valueOf(responseJson.getDouble("EUR"));
                        usdValue = String.valueOf(responseJson.getDouble("USD"));
                        Log.d("Maaz", btcValue);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bindValuesToLayout();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void bindValuesToLayout() {
        mName.setText(currency.getName());
        mCoinSymbol.setText(currency.getSymbol());
        mCoinName.setText(currency.getCoinName());
        mCoinFullName.setText(currency.getFullName());
        mCoinAlgorithm.setText(currency.getAlgorithm());
        mCoinProofType.setText(currency.getProofType());
        mCoinFullyPremined.setText(currency.getFullyPremined());
        mCoinTotalCoinSupply.setText(currency.getTotalCoinSupply());

        if (currency.getPreMinedValue().equals(getString(R.string.n_a)))
            mCoinPreMinedValue.setText(getString(R.string.hifen));
        else
            mCoinPreMinedValue.setText(getString(R.string.n_a));

        if (currency.getTotalCoinsFreeFloat().equals(getString(R.string.n_a)))
            mTotalCoinsFreeFloat.setText(getString(R.string.hifen));
        else
            mTotalCoinsFreeFloat.setText(currency.getTotalCoinsFreeFloat());

        if (String.valueOf(currency.isTrading()).equals(getString(R.string.is_trading_false)))
            mCoinTrading.setText(getResources().getString(R.string.is_not_trading));
        else
            mCoinTrading.setText(getResources().getString(R.string.is_trading));

        mCoinBtcTv.setText(btcValue);
        mCoinUsdTv.setText(usdValue);
        mCoinEurTv.setText(eurValue);
    }

    private void showInternetSnackBar() {
        Snackbar.make(mImageView, getString(R.string.no_internet), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) finish();
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void addTofavorite(View view) {
        mDatabase.child(auth.getUid()).child(WALLET_REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserFavorite userFav = dataSnapshot.getValue(UserFavorite.class);
                if (userFav == null) {
                    mDatabase.child(auth.getUid()).child(WALLET_REFERENCE).setValue(new UserFavorite(auth.getCurrentUser().getEmail(),
                            new ArrayList<Currency>() {
                            }));
                    return;
                } else {
                    List<Currency> walletFavs = userFav.getFavCoins();
                    for (Currency c : walletFavs) {
                        if (c.getCoinName().equals(currency.getCoinName())) {
                            return;
                        }
                    }
                    walletFavs.add(currency);
                    mDatabase.child(auth.getUid()).child(WALLET_REFERENCE).setValue(userFav);
                    Toast.makeText(getApplicationContext(), getString(R.string.added_to_wallet), Toast.LENGTH_SHORT).show();
                    CoinWidgetProvider.sendRefreshBroadcast(getApplicationContext());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), getString(R.string.show_toast_error_message), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openMoreInfo(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(NetworkUtils.BASE_IMAGE_URL.concat(currency.getUrl()));
        intent.setData(uri);
        startActivity(intent);
    }
}




