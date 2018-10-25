package com.laskstud.blingslot;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LobbyActivity extends AppCompatActivity {

    private static final int CARD_COUNT = 4;

    @BindView(R.id.cardRecyclerView) RecyclerView recyclerView;

    @BindDrawable(R.drawable.card1) Drawable card1;
    @BindDrawable(R.drawable.card2) Drawable card2;
    @BindDrawable(R.drawable.card3) Drawable card3;
    @BindDrawable(R.drawable.card1) Drawable card4;

    Drawable[] cardDrawables;

    private List<CardItem> cardItems;
    private CardAdapter cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        ButterKnife.bind(this);

        cardDrawables = new Drawable[CARD_COUNT];
        cardDrawables[0] = card1;
        cardDrawables[1] = card2;
        cardDrawables[2] = card3;
        cardDrawables[3] = card4;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        cardItems = createCardItems();

        cardAdapter = new CardAdapter(cardItems);
        recyclerView.setAdapter(cardAdapter);
    }

    private List<CardItem> createCardItems(){
        int[] levels = { 0, 1, 5, 7 };
        int level = App.getGame().getPlayer().getLevel();
        List<CardItem> cardItems = new ArrayList<>();
        for(int i = 0; i < CARD_COUNT; i++) {
            CardItem cardItem = new CardItem();
            cardItem.drawable = cardDrawables[i];
            cardItem.neededLevelToUnlock = levels[i];
            if(levels[i] <= level) {
                cardItem.isUnlocked = true;
            } else {
                cardItem.isUnlocked = false;
            }
            cardItems.add(cardItem);
        }

        return cardItems;
    }

    @Override
    protected void onResume() {
        super.onResume();
        cardItems.clear();
        cardItems.addAll(createCardItems());
        cardAdapter.notifyDataSetChanged();
    }

    public void onPlayImageButtonClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
