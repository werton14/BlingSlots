package com.laskstud.blingslot;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.laskstud.blingslot.game.Game;
import com.laskstud.blingslot.game.Player;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class GameActivity extends AppCompatActivity {

    @BindViews({R.id.column1RecyclerView, R.id.column2RecyclerView, R.id.column3RecyclerView,
            R.id.column4RecyclerView, R.id.column5RecyclerView, })
    List<RecyclerView> columnsRecyclerViews;
    List<ElementAdapter> elementAdapters = new ArrayList<>();
    List<List<ElementItem>> resElements = new ArrayList<>();

    @BindView(R.id.coinsTextView) TextView coinsTextView;
    @BindView(R.id.betTextView) TextView betTextView;
    @BindView(R.id.levelTextView) TextView levelTextView;
    @BindView(R.id.experienceTextView) TextView experienceTextView;
    @BindView(R.id.experienceProgressBar) ProgressBar experienceProgressBar;

    @BindDrawable(R.drawable.ic_crystal) Drawable crystal;
    @BindDrawable(R.drawable.ic_strawberry) Drawable strawberry;
    @BindDrawable(R.drawable.ic_seven) Drawable seven;
    @BindDrawable(R.drawable.ic_plum) Drawable plum;
    @BindDrawable(R.drawable.ic_mango) Drawable mango;

    private Drawable[] elementsDrawables;

    private Game game;
    private List<Integer> elementsForWinAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        elementsDrawables = new Drawable[Game.Element.size()];
        elementsDrawables[0] = crystal;
        elementsDrawables[1] = strawberry;
        elementsDrawables[2] = seven;
        elementsDrawables[3] = plum;
        elementsDrawables[4] = mango;

        for(RecyclerView recyclerView : columnsRecyclerViews) {

            SpeedyLinearLayoutManager linearLayoutManager = new SpeedyLinearLayoutManager(this);
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(linearLayoutManager);

            List<ElementItem> elements = new ArrayList<>();
            ElementAdapter elementAdapter = new ElementAdapter(elements);
            resElements.add(elements);
            elementAdapters.add(elementAdapter);
            recyclerView.setAdapter(elementAdapter);
        }

        RecyclerView lastColumnRecyclerView = columnsRecyclerViews.get(columnsRecyclerViews.size() - 1);

        lastColumnRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(-1)) {
                    // scroll ended
                    animateWinElements();
                }
            }
        });

        game = App.getGame();

        addElementsToRecyclerViews(game.getStartElements());
        updateExperienceAndLevelViews(game.getPlayer());
        updateBetView(game.getBet());
        updateCoinsView(game.getCoins());

        game.setGameListener(new Game.GameListener() {

            @Override
            public void onSpin(List<List<Game.Element>> newResElements) {
                addElementsToRecyclerViews(newResElements);
            }

            @Override
            public void onBetChanged(int bet, boolean reducible, boolean increasable) {
                updateBetView(bet);
            }

            @Override
            public void onCoinsChanged(int coins) {
                updateCoinsView(coins);
            }

            @Override
            public void onExperienceAdded(Player player) {
                updateExperienceAndLevelViews(player);
            }

            @Override
            public void onResult(List<Integer> winElementsPosition) {
                elementsForWinAnimation = winElementsPosition;
            }
        });
    }

    private void addElementsToRecyclerViews(List<List<Game.Element>> newResElements) {
        for(int i = 0; i < newResElements.size(); i++) {
            List<ElementItem> elements = GameActivity.this.resElements.get(i);
            int startPosition = elements.size();
            for(Game.Element element : newResElements.get(i)) {
                ElementItem elementItem = new ElementItem();
                elementItem.drawable = elementsDrawables[element.ordinal()];
                elements.add(elementItem);
            }
            elementAdapters.get(i).notifyItemRangeInserted(startPosition, elements.size());
            columnsRecyclerViews.get(i).smoothScrollToPosition(elements.size() - 1);
        }
    }

    private void updateBetView(int bet) {
        betTextView.setText(String.valueOf(bet));
    }

    private void  updateCoinsView(int coins) {
        coinsTextView.setText(String.valueOf(coins));
    }

    private void updateExperienceAndLevelViews(Player player) {
        String level = "LEVEL " + String.valueOf(player.getLevel());
        String experience = String.valueOf(player.getExperience()) + '/' +
                String.valueOf(player.getNextLevelExperience());

        levelTextView.setText(level);
        experienceTextView.setText(experience);

        experienceProgressBar.setMax(player.getNextLevelExperience());
        experienceProgressBar.setProgress(player.getExperience());
    }

    private void animateWinElements() {
        if(elementsForWinAnimation != null) {
            //WIN
            Animation anim = null;
            for (int i : elementsForWinAnimation) {
                anim = new ScaleAnimation(
                        1f, 1.2f,
                        1f, 1.2f);
                anim.setRepeatCount(1);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setDuration(300);

                List<ElementItem> elements = resElements.get(i);
                int winElementPosition = elements.size() - 2;
                elements.get(winElementPosition).anim = anim;
                elementAdapters.get(i).notifyItemChanged(winElementPosition);
            }
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    game.endSpin();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            //LOSE
            game.endSpin();
        }
    }

    public void onSpinButtonClick(View view) {
        game.startSpin();
    }

    public void onMaxButtonClick(View view) {
        game.setMaxBet();
    }

    public void onAutoPlayButtonClick(View view) {
        game.autoPlay();
    }

    public void onBetMinusButtonClick(View view) {
        game.downBet();
    }

    public void onBetPlusButtonClick(View view) {
        game.upBet();
    }

    public void onBackButtonClick(View view) {
        onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        game.gameInterapted();
    }
}
