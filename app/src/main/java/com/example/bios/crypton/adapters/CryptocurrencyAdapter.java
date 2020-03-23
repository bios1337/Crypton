package com.example.bios.crypton.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bios.crypton.R;
import com.example.bios.crypton.netutils.NetworkUtils;
import com.example.bios.crypton.pojos.Currency;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CryptocurrencyAdapter extends RecyclerView.Adapter<CryptocurrencyAdapter.CryptocurrencyViewHolder> implements Filterable {

    private final CryptocurrencyAdapterClickHandler mHandler;
    private List<Currency> currencies = new ArrayList<>();
    private List<Currency> mFilteredList;

    public CryptocurrencyAdapter(List<Currency> currencies, CryptocurrencyAdapterClickHandler handler) {
        this.currencies = currencies;
        this.mHandler = handler;
        mFilteredList = currencies;
    }

    public List<Currency> getmFilteredList() {
        return mFilteredList;
    }

    @NonNull
    @Override
    public CryptocurrencyAdapter.CryptocurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.viewholder_cryptocurrency, parent, false);
        return new CryptocurrencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CryptocurrencyAdapter.CryptocurrencyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    mFilteredList = currencies;
                } else {
                    List<Currency> filteredList = new ArrayList<>();
                    for (Currency currency : currencies) {
                        if (currency.getFullName().toLowerCase().contains(charString) || currency.getCoinName().toLowerCase().contains(charString)
                                || currency.getSymbol().toLowerCase().contains(charString)) {
                            filteredList.add(currency);
                        }
                    }
                    mFilteredList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredList = (ArrayList<Currency>) results.values;
                Log.d("Maaz", String.valueOf(mFilteredList.size()));
                notifyDataSetChanged();
            }
        };
    }

    public interface CryptocurrencyAdapterClickHandler {
        public void onCryptoClick(int position);
    }

    class CryptocurrencyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImageView;
        TextView mCoinName;
        TextView mSymbol;
        TextView mAlgorithm;

        public CryptocurrencyViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.viewholder_image_view);
            mCoinName = itemView.findViewById(R.id.viewholder_coin_name);
            mSymbol = itemView.findViewById(R.id.viewholder_symbol);
            mAlgorithm = itemView.findViewById(R.id.viewholder_algorithm);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            Currency currency = mFilteredList.get(position);
            String imageUrl = currency.getImageUrl();
            String coin_name = currency.getCoinName();
            String symbol = currency.getSymbol();
            String algorithm = currency.getAlgorithm();
            mCoinName.setText(coin_name);
            mSymbol.setText(symbol);
            mAlgorithm.setText(algorithm);
            Picasso picasso = Picasso.get();
            picasso.load(NetworkUtils.BASE_IMAGE_URL.concat(imageUrl))
                    .into(mImageView);
        }

        @Override
        public void onClick(View v) {
            mHandler.onCryptoClick(getAdapterPosition());
        }
    }
}
