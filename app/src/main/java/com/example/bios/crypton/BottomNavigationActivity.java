package com.example.bios.crypton;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bios.crypton.navfragments.CryptoFragment;
import com.example.bios.crypton.navfragments.ExchangeFragment;
import com.example.bios.crypton.navfragments.WalletFragment;
import com.example.bios.crypton.pojos.Currency;
import com.example.bios.crypton.viewmodels.BottomNavigationViewModel;
import com.example.bios.crypton.viewpager.BottomNavigationViewPager;
import com.example.bios.crypton.viewpager.CustomViewPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class BottomNavigationActivity extends AppCompatActivity {
    private static final String TAG = BottomNavigationActivity.class.getSimpleName();
    private static final String NAV_POSITION = "navigation_position";
    private static CryptoFragment cryptoFragment;
    private static ExchangeFragment exchangeFragment;
    private static WalletFragment walletFragment;
    private Toolbar mTopToolbar;
    private ProgressBar progressBar;
    private SearchView searchView;
    private BottomNavigationViewPager viewPager;
    private boolean isCurrencyLoaded;
    private int bottomNavigationPosition;
    private List<Currency> currencies = null;
    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            checkInternetAndShowToast();
            bottomNavigationPosition = getNavPositionFromMenuItem(item);
            invalidateOptionsMenu();
            viewPager.setCurrentItem(bottomNavigationPosition);
            return true;
        }
    };

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public BottomNavigationViewPager getViewPager() {
        return viewPager;
    }

    private void checkInternetAndShowToast() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        progressBar = findViewById(R.id.progressBar);
        viewPager = findViewById(R.id.viewpager_container);

        progressBar.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mNavigationListener);

        mTopToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);

        isCurrencyLoaded = false;

        if (savedInstanceState == null) {
            checkInternetAndShowToast();
            CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(cryptoFragment = new CryptoFragment());
            adapter.addFragment(exchangeFragment = new ExchangeFragment());
            adapter.addFragment(walletFragment = new WalletFragment());
            //adapter.addFragment(profileFragment = new ProfileFragment());
            viewPager.setAdapter(adapter);
        } else {
            bottomNavigationPosition = savedInstanceState.getInt(NAV_POSITION);
            CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(cryptoFragment);
            adapter.addFragment(exchangeFragment);
            adapter.addFragment(walletFragment);
            //adapter.addFragment(profileFragment);
            viewPager.setAdapter(adapter);
        }
        viewPager.setOffscreenPageLimit(4);
        loadCurrencyThroughModel();
    }

    private void loadCurrencyThroughModel() {
        BottomNavigationViewModel model = ViewModelProviders.of(this).get(BottomNavigationViewModel.class);
        model.getCurrencies().observe(this, new Observer<List<Currency>>() {
            @Override
            public void onChanged(@Nullable List<Currency> currencies) {
                setCurrencies(currencies);
                progressBar.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
                isCurrencyLoaded = true;
                invalidateOptionsMenu();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        MenuItem search = menu.findItem(R.id.search);
        if (bottomNavigationPosition == 1) {
            search.setVisible(false);
        }
        if (isCurrencyLoaded) {
            searchView = (SearchView) search.getActionView();
            search(searchView);
        }
        return true;
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (bottomNavigationPosition == 0 && cryptoFragment.getmAdapter()!=null)
                    cryptoFragment.getmAdapter().getFilter().filter(query);
                if (bottomNavigationPosition == 2 && walletFragment.getmAdapter()!=null)
                    walletFragment.getmAdapter().getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (bottomNavigationPosition == 0)
                    cryptoFragment.getmAdapter().getFilter().filter(newText);
                if (bottomNavigationPosition == 2)
                    walletFragment.getmAdapter().getFilter().filter(newText);
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    int getNavPositionFromMenuItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_currency:
                return 0;
            case R.id.navigation_exchange:
                return 1;
            case R.id.navigation_wallet:
                return 2;
            default:
                return -1;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_POSITION, bottomNavigationPosition);
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }
}
