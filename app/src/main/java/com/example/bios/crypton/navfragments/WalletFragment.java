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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.bios.crypton.BottomNavigationActivity;
import com.example.bios.crypton.CoinDetailActivity;
import com.example.bios.crypton.R;
import com.example.bios.crypton.adapters.CryptocurrencyAdapter;
import com.example.bios.crypton.pojos.Currency;
import com.example.bios.crypton.pojos.UserFavorite;
import com.example.bios.crypton.viewmodels.BottomNavigationViewModel;
import com.example.bios.crypton.viewpager.BottomNavigationViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WalletFragment extends Fragment implements CryptocurrencyAdapter.CryptocurrencyAdapterClickHandler {

    private static final String SAVED_LAYOUT_MANAGER = "layout_manager";

    private static final String WALLET_REFERENCE = "wallet";
    private static final String USER_REFERENCE = "users";
    private static final int NUM_COLOMNS = 2;
    private static Bundle mBundleRecyclerViewState;
    RecyclerView mRecyclerView;
    CryptocurrencyAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;
    private List<Currency> favCurrencies;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private List<Currency> allCurrencies;
    private BottomNavigationViewPager viewPager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        mRecyclerView = view.findViewById(R.id.wallet_recycler_view);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(USER_REFERENCE);

        if (getActivity() instanceof BottomNavigationActivity) {
            progressBar = ((BottomNavigationActivity) getActivity()).getProgressBar();
            viewPager = ((BottomNavigationActivity) getActivity()).getViewPager();
        }

        BottomNavigationViewModel model = ViewModelProviders.of(getActivity()).get(BottomNavigationViewModel.class);
        model.getCurrencies().observe(this, new Observer<List<Currency>>() {
            @Override
            public void onChanged(@Nullable List<Currency> currencies) {
                allCurrencies = currencies;
                progressBar.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);

                if (isPortrait())
                    mLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
                else
                    mLayoutManager = new GridLayoutManager(view.getContext(), NUM_COLOMNS);
                mAdapter = new CryptocurrencyAdapter(loadWalletCurrencies(), WalletFragment.this);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setHasFixedSize(true);
            }
        });


        return view;
    }

    private List<Currency> loadWalletCurrencies() {
        final List<Currency> walletCurrencies = new ArrayList<>();

        mDatabase.child(auth.getUid()).child(WALLET_REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                walletCurrencies.clear();
                UserFavorite userFav = dataSnapshot.getValue(UserFavorite.class);
                if (userFav != null) {
                    List<Currency> favCurrencies = userFav.getFavCoins();
                    for (Currency s : favCurrencies) {
                        for (Currency curr : allCurrencies) {
                            if (curr.getId().equals(s.getId())) {
                                walletCurrencies.add(curr);
                            }
                        }
                    }
                    setFavCurrencies(walletCurrencies);
                    mAdapter.notifyDataSetChanged();
                    if (mBundleRecyclerViewState != null && mLayoutManager != null) {
                        Parcelable listState = mBundleRecyclerViewState.getParcelable(SAVED_LAYOUT_MANAGER);
                        mLayoutManager.onRestoreInstanceState(listState);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return walletCurrencies;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onCryptoClick(int position) {
        Intent intent = new Intent(getActivity(), CoinDetailActivity.class);
        intent.putExtra(Currency.class.getSimpleName(), favCurrencies.get(position));
        startActivity(intent);
    }


    @Override
    public void onPause() {
        super.onPause();
        mBundleRecyclerViewState = new Bundle();
        mBundleRecyclerViewState.putParcelable(SAVED_LAYOUT_MANAGER, mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    public void setFavCurrencies(List<Currency> favCurrencies) {
        this.favCurrencies = favCurrencies;
    }

    public boolean isPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public CryptocurrencyAdapter getmAdapter() {
        return mAdapter;
    }

}
