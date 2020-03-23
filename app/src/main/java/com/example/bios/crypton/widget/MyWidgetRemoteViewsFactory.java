package com.example.bios.crypton.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.bios.crypton.R;
import com.example.bios.crypton.netutils.NetworkUtils;
import com.example.bios.crypton.pojos.Currency;
import com.example.bios.crypton.pojos.UserFavorite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.widget.AdapterView.INVALID_POSITION;

public class MyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    public static final String TAG = MyWidgetRemoteViewsFactory.class.getSimpleName();
    private final String USER_REFERENCE = "users";
    private final String WALLET_REFERENCE = "wallet";
    private Context mContext;
    private List<Currency> favCurrencies = new ArrayList<>();
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private int mAppWidgetId;

    public MyWidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        //initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    private void setFavCurrencies(List<Currency> walletCurrencies) {
        Log.d(TAG, "setFavCurrencies:" + String.valueOf(walletCurrencies.size()));
        this.favCurrencies = walletCurrencies;
    }

    @Override
    public void onDestroy() {
        if (favCurrencies != null) {
            favCurrencies.clear();
        }
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount:= " + String.valueOf(favCurrencies.size()));
        return favCurrencies == null ? 0 : favCurrencies.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == INVALID_POSITION || favCurrencies == null || favCurrencies.get(position) == null) {
            return null;
        }
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.coin_widget_list_item);
        rv.setTextViewText(R.id.widget_item_name, favCurrencies.get(position).getFullName());

        String url = NetworkUtils.BASE_IMAGE_URL.concat(favCurrencies.get(position).getImageUrl());

        try {
            URL imageUrl = new URL(url);
            Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            rv.setImageViewBitmap(R.id.widget_coin_image, bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(Currency.class.getSimpleName(), favCurrencies.get(position));
        rv.setOnClickFillInIntent(R.id.widget_item_name, fillInIntent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(USER_REFERENCE);
        if (auth == null) {
            setFavCurrencies(new ArrayList<Currency>());
            CoinWidgetProvider.sendRefreshBroadcast(mContext);
        } else {
            Log.d(TAG, "initData: authId:" + auth.getUid());
            mDatabase.child(auth.getUid()).child(WALLET_REFERENCE).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserFavorite userFav = dataSnapshot.getValue(UserFavorite.class);
                    if (userFav != null) {
                        List<Currency> walletCurrencies = userFav.getFavCoins();
                        Log.d(TAG, "initData.onDataChange");
                        if (!compareCollections(walletCurrencies, favCurrencies)) {
                            Log.d(TAG, "compareCollections:" + String.valueOf(compareCollections(walletCurrencies, favCurrencies)));
                            setFavCurrencies(walletCurrencies);
                            CoinWidgetProvider.sendRefreshBroadcast(mContext);
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public boolean compareCollections(List<Currency> a, List<Currency> b) {
        if (a != null && b != null && a.size() == b.size()) {
            for (int i = 0; i < a.size(); i++) {
                if (!a.get(i).getId().equals(b.get(i).getId())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


}
