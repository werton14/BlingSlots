package com.laskstud.blingslot.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {

    public enum Element {
        CRYSTAL, STRAWBERRY, SEVEN, PLUM, MANGO;

        private static final int size = Element.values().length;

        public static int size() {
            return size;
        }
    }

    private static final int ROW_COUNT = 3;
    private static final int COLUMN_COUNT = 5;
    private static final int MIN_BET = 1;
    private static final int BET_CHANGE = 10;
    private static final int EXPERIENCE_INCREASE = 10;
    private static final int ADDITIONAL_EXPERIENCE = 15;

    private int coins = 500;
    private int bet = 10;
    private Player player;
    private boolean autoPlay = false;
    private boolean spinEnded = true;
    private GameListener gameListener;
    private Random random;

    public Game() {
        player = new Player();
        random = new Random(System.currentTimeMillis());
    }

    public List<List<Element>> getStartElements() {
        List<List<Element>> resElements = new ArrayList<>();

        for(int i = 0; i < COLUMN_COUNT; i++) {
            List<Element> elements = getRandomElements(ROW_COUNT);
            resElements.add(elements);
        }
        return resElements;
    }


    public void startSpin(){
        if(spinEnded && coins > 0) {
            spinEnded = false;
            List<List<Element>> resElements = new ArrayList<>();
            int newElementCount = ROW_COUNT * 3;
            for (int i = 0; i < COLUMN_COUNT; i++) {
                List<Element> elements = getRandomElements(newElementCount);
                resElements.add(elements);
                newElementCount += ROW_COUNT;
            }
            gameListener.onSpin(resElements);
            gameListener.onResult(getWinElementsPositions(resElements));
        }
    }

    private List<Element> getRandomElements(int elementCount) {

        List<Element> elements = new ArrayList<>();
        for(int j = 0; j < elementCount; j++)  {
            Element element = Element.values()[random.nextInt(Element.size())];
            elements.add(element);
        }

        return elements;
    }

    private List<Integer> getWinElementsPositions(List<List<Element>> resElements) {
        // Getting middle line elements
        List<Element> line = new ArrayList<>();
        for(List<Element> elements : resElements) {
            line.add(elements.get(elements.size() - 2));
        }

        int maxElementRepeats = 0;
        Element mostRepeatedElement = Element.CRYSTAL;
        for(int i = 0; i < Element.size(); i++) {
            Element currentElement = Element.values()[i];
            int frequency = Collections.frequency(line, currentElement);
            if(frequency > maxElementRepeats) {
                maxElementRepeats = frequency;
                mostRepeatedElement = currentElement;
            }
        }
        boolean win = updateCoins(maxElementRepeats);

        /// Getting win elements positions
        List<Integer> winElementsPosition = null;
        if(win) {
            winElementsPosition = new ArrayList<>();
            int index;
            while ((index = line.lastIndexOf(mostRepeatedElement)) >= 0) {
                line.remove(index);
                winElementsPosition.add(index);
            }
        }
        return winElementsPosition;
    }

    private boolean updateCoins(int elementRepeats) {
        boolean win = false;
        if(elementRepeats > 1) {
            // WIN
            win = true;
            coins += bet * elementRepeats;
            player.addExperience(ADDITIONAL_EXPERIENCE);
        } else {
            // LOSE
            win = false;
            coins -= bet;
            if(bet > coins) {
                bet = coins;
            }
        }
        player.addExperience(EXPERIENCE_INCREASE);
        return win;
    }

    public void endSpin() {
        spinEnded = true;

        gameListener.onBetChanged(bet, betIsReducible(), betIsIncreasable());
        gameListener.onExperienceAdded(player);
        gameListener.onCoinsChanged(coins);

        if(autoPlay) {
            startSpin();
        }
    }

    public void autoPlay() {
        autoPlay = !autoPlay;
        if(autoPlay) startSpin();
    }

    public void setMaxBet(){
        bet = coins;
        gameListener.onBetChanged(bet, betIsReducible(), betIsIncreasable());
    }

    public void upBet() {
        if(bet + BET_CHANGE <= coins) {
            bet += BET_CHANGE;
        } else {
            bet = coins;
        }
        gameListener.onBetChanged(bet, betIsReducible(), betIsIncreasable());
    }

    public void downBet() {
        if(bet - BET_CHANGE >= MIN_BET) {
            bet -= BET_CHANGE;
        } else  {
            bet = MIN_BET;
        }
        gameListener.onBetChanged(bet, betIsReducible(), betIsIncreasable());
    }

    private boolean betIsReducible() {
        return bet - MIN_BET > 0;
    }

    private boolean betIsIncreasable() {
        return coins - bet > 0;
    }

    public int getCoins() {
        return coins;
    }

    public int getBet() {
        return bet;
    }

    public Player getPlayer() {
        return player;
    }

    public void setGameListener(GameListener gameListener) {
        this.gameListener = gameListener;
    }

    public void gameInterapted() {
        spinEnded = true;
        autoPlay = false;
    }

    public interface GameListener {
        void onSpin(List<List<Element>> newResElements);
        void onResult(List<Integer> winElementsPosition);
        void onBetChanged(int bet,  boolean reducible, boolean increasable);
        void onCoinsChanged(int coins);
        void onExperienceAdded(Player player);
    }
}
