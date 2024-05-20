package org.plugins;

import oop.if2210_tb2_sc4.GameState;

public class Main {
    public static void main(String[] args) {
        GameState gameState = GameState.getInstance();
        SaveLoadJSON saveLoadJSON = new SaveLoadJSON("state");
        saveLoadJSON.loadGameState();
        saveLoadJSON.loadPlayer(1);
        saveLoadJSON.loadPlayer(2);

        System.out.println(gameState.getCurrentPlayer());
        System.out.println(gameState.getCountItems());
        System.out.println(gameState.getShopItems());
        System.out.println(gameState.getPlayer(1));
        System.out.println(gameState.getPlayer(2));

        saveLoadJSON = new SaveLoadJSON("SAVEJSON");
        saveLoadJSON.save();
    }
}