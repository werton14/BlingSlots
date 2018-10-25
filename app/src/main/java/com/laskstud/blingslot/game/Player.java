package com.laskstud.blingslot.game;

public class Player {

    private static final int levelFactor = 2;

    private int experience = 0;
    private int nextLevelExperience = 100;
    private int level = 0;

    public Player() {}

    public Player(int experience, int nextLevelExperience, int level) {
        this.experience = experience;
        this.nextLevelExperience = nextLevelExperience;
        this.level = level;
    }

    void addExperience(int e) {
        experience += e;
        if(experience >= nextLevelExperience) {
            experience = nextLevelExperience - experience;
            nextLevelExperience *= levelFactor;
            level++;
        }
    }

    public int getExperience() {
        return experience;
    }

    public int getNextLevelExperience() {
        return nextLevelExperience;
    }

    public int getLevel() {
        return level;
    }
}
