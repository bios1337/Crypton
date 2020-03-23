package com.example.bios.crypton.navfragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bios.crypton.CoinDetailActivity;
import com.example.bios.crypton.R;
import com.example.bios.crypton.adapters.CryptocurrencyAdapter;
import com.example.bios.crypton.pojos.Currency;
import com.example.bios.crypton.viewmodels.BottomNavigationViewModel;

import java.util.List;

public class CryptoFragment extends Fragment implements CryptocurrencyAdapter.CryptocurrencyAdapterClickHandler {

    private static final String SAVED_LAYOUT_MANAGER = "layout_manager";
    private static final int NUM_COLOMNS = 2;
    private static Bundle mBundleRecyclerViewState;
    RecyclerView mRecyclerView;
    SearchView searchView;
    CryptocurrencyAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private List<Currency> allCurrencies;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_cryptocurrency, container, false);

        mRecyclerView = view.findViewById(R.id.crypto_recycler_view);

        BottomNavigationViewModel model = ViewModelProviders.of(getActivity()).get(BottomNavigationViewModel.class);
        model.getCurrencies().observe(this, new Observer<List<Currency>>() {
            @Override
            public void onChanged(@Nullable List<Currency> currencies) {
                allCurrencies = currencies;
                mAdapter = new CryptocurrencyAdapter(allCurrencies, CryptoFragment.this);
                if (isPortrait())
                    mLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
                else
                    mLayoutManager = new GridLayoutManager(view.getContext(), NUM_COLOMNS);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setHasFixedSize(true);
            }
        });

        return view;
    }

    @Override
    public void onCryptoClick(int position) {
        Intent intent = new Intent(getActivity(), CoinDetailActivity.class);
        intent.putExtra(Currency.class.getSimpleName(), getmAdapter().getmFilteredList().get(position));
        startActivity(intent);
    }

    public CryptocurrencyAdapter getmAdapter() {
        return mAdapter;
    }


    @Override
    public void onPause() {
        super.onPause();
        mBundleRecyclerViewState = new Bundle();
        mBundleRecyclerViewState.putParcelable(SAVED_LAYOUT_MANAGER, mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBundleRecyclerViewState != null && mRecyclerView.getLayoutManager() != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(SAVED_LAYOUT_MANAGER);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }


    public boolean isPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
}
