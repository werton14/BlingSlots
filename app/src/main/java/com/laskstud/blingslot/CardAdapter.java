package com.laskstud.blingslot;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<CardItem> cardItems;

    public CardAdapter(List<CardItem> cardItems) {
        this.cardItems = cardItems;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int i) {
        CardItem cardItem = cardItems.get(i);

        cardViewHolder.cardImageView.setImageDrawable(cardItem.drawable);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

        if(cardItem.isUnlocked) {
            cardViewHolder.cardImageView.setColorFilter(null);
            cardViewHolder.lockImageView.setVisibility(View.INVISIBLE);
            cardViewHolder.playImageButton.setVisibility(View.VISIBLE);
            cardViewHolder.cardTextView.setText("PLAY");
        } else {
            cardViewHolder.cardImageView.setColorFilter(filter);
            cardViewHolder.lockImageView.setVisibility(View.VISIBLE);
            cardViewHolder.playImageButton.setVisibility(View.INVISIBLE);
            cardViewHolder.cardTextView.setText("LEVEL " + String.valueOf(cardItem.neededLevelToUnlock));
        }
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }


    class CardViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView cardImageView;
        ImageView lockImageView;
        TextView cardTextView;
        ImageButton playImageButton;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            cardImageView = itemView.findViewById(R.id.cardImageView);
            cardTextView = itemView.findViewById(R.id.cardTextView);
            playImageButton = itemView.findViewById(R.id.playImageButton);
            lockImageView = itemView.findViewById(R.id.lockImageView);
        }
    }
}
