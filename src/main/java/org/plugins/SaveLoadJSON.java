package org.plugins;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectWriter;
import oop.if2210_tb2_sc4.GameData;
import oop.if2210_tb2_sc4.GameState;
import oop.if2210_tb2_sc4.Ladang;
import oop.if2210_tb2_sc4.Player;
import oop.if2210_tb2_sc4.Shop;
import oop.if2210_tb2_sc4.card.Card;
import oop.if2210_tb2_sc4.card.EffectType;
import oop.if2210_tb2_sc4.card.FarmResourceCard;
import oop.if2210_tb2_sc4.card.ProductCard;
import oop.if2210_tb2_sc4.save_load.SaveLoad;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import oop.if2210_tb2_sc4.save_load.SaveLoadAnnotation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@SaveLoadAnnotation(type = "JSON")
public class SaveLoadJSON implements SaveLoad {
    private JsonNode JsonState;
    private String folderName;

    public SaveLoadJSON(){
        try {
            FileReader reader;
            this.folderName = "default";

            URL url = SaveLoad.class.getResource("");
            assert url != null;
            String cwd = url.getPath().startsWith("/") ? url.getPath().substring(1) : url.getPath();

            try {
                reader = new FileReader(cwd + folderName+ "/state.json");
            } catch (FileNotFoundException e){
                handleNewFile(Paths.get(cwd + folderName+ "state.json"));
                reader = new FileReader(cwd + folderName+ "state.json");
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonState = mapper.readTree(reader);

            GameData.initCards();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public SaveLoadJSON(String folderName){
        try {
            FileReader reader;
            this.folderName = folderName;

            URL url = SaveLoad.class.getResource("");
            assert url != null;
            String cwd = url.getPath().startsWith("/") ? url.getPath().substring(1) : url.getPath();

            try {
                reader = new FileReader(cwd + folderName + "/state.json");
            } catch (FileNotFoundException e){
                handleNewFile(Paths.get(cwd + folderName + "/state.json"));
                reader = new FileReader(cwd + folderName + "/state.json");
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonState = mapper.readTree(reader);
            GameData.initCards();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void loadGameState(){
        try {
            JsonNode gameStateNode = JsonState.get("gameState");

            GameState.getInstance().setCurrentPlayer(gameStateNode.get("currentPlayer").asInt());

            int countItem = gameStateNode.get("countItem").asInt();
            Shop tempShop = new Shop();

            for (int i = 0; i < countItem; i++) {
                String item = gameStateNode.get("items").get(i).get("name").asText();
                int count = gameStateNode.get("items").get(i).get("count").asInt();
                tempShop.addCard((ProductCard) GameData.getCard(item), count);
            }

            GameState.getInstance().setShop(tempShop);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void loadPlayer(int no_player){
        try {
            Player player = new Player();
            oop.if2210_tb2_sc4.Deck deck = new oop.if2210_tb2_sc4.Deck();
            Ladang ladang = new Ladang();

            JsonNode playerNode = JsonState.get("player" + no_player);

            player.setJumlahGulden(playerNode.get("gulden").asInt());
            deck.setCardsInDeckCount(playerNode.get("deckCount").asInt());
            deck.setCardsInHandCount(playerNode.get("handCount").asInt());

            JsonNode handsCard = playerNode.get("hand");
            for (int i = 0; i < deck.getActiveCardinHandCount(); i++) {
                String card = handsCard.get(i).get("name").asText();
                String location = handsCard.get(i).get("lokasi").asText();
                deck.setActiveCard(location, GameData.createCard(card));
            }
            player.setDeck(deck);

            int ladangCount = playerNode.get("ladangCount").asInt();
            JsonNode ladangNode = playerNode.get("ladang");
            for (int i = 0; i < ladangCount; i++) {
                JsonNode cardInLadangNode = ladangNode.get(i);
                String nama = cardInLadangNode.get("name").asText();
                String lokasi = cardInLadangNode.get("lokasi").asText();
                int ageOrWeight = cardInLadangNode.get("ageOrWeight").asInt();

                FarmResourceCard newCard = (FarmResourceCard) GameData.createCard(nama);

                assert newCard != null : "Card not found";

                if (newCard instanceof oop.if2210_tb2_sc4.card.PlantCard) {
                    ((oop.if2210_tb2_sc4.card.PlantCard) newCard).setAge(ageOrWeight);
                } else if (newCard instanceof oop.if2210_tb2_sc4.card.AnimalCard) {
                    ((oop.if2210_tb2_sc4.card.AnimalCard) newCard).setWeight(ageOrWeight);
                }

                int effectCount = cardInLadangNode.get("effectCount").asInt();
                JsonNode effectNode = cardInLadangNode.get("effect");
                for (int j = 0; j < effectCount; j++) {
                    String effect = effectNode.get(j).asText();

                    if (Objects.equals(effect, "ACCELERATE")){
                        newCard.addEffect(EffectType.ACCELERATE);
                    } else if (Objects.equals(effect, "DELAY")){
                        newCard.addEffect(EffectType.DELAY);
                    }  else if (Objects.equals(effect, "INSTANT_HARVEST")){
                        newCard.addEffect(EffectType.INSTANT_HARVEST);
                    } else if (Objects.equals(effect, "PROTECT")){
                        newCard.addEffect(EffectType.PROTECT);
                    } else if (Objects.equals(effect, "TRAP")){
                        newCard.addEffect(EffectType.TRAP);
                    }
                }

                ladang.setCard(lokasi, newCard);
            }

            player.setLadang(ladang);
            GameState.getInstance().setPlayer(no_player, player);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void save(String folderName){
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            Object jsonDataObject;


             /* Create gameState Map */
            Map<ProductCard, Integer> items = GameState.getInstance().getShop().getCardStock();
            items.entrySet().removeIf(entry -> entry.getValue() == 0);
            List<Object> itemsList = new ArrayList<>();
            for (Map.Entry<ProductCard, Integer> entry : items.entrySet()) {
                itemsList.add(Map.of(
                        "name", entry.getKey().getName(),
                        "count", entry.getValue()
                ));
            }
            Map<String, Object> gameState = Map.of(
                    "currentPlayer", GameState.getInstance().getCurrentPlayer(),
                    "countItem", GameState.getInstance().getShop().getCardStock().size(),
                    "items", itemsList
            );

            /* Create player1 Map */
            Player p1 = GameState.getInstance().getPlayer(1);
            Card[] hand = p1.getActiveDeck();
            List<Map<String,String>> handList = new ArrayList<>();

            // Make handList
            for (int i = 0; i < p1.getJumlahDeckActive(); i++) {
                Card card = hand[i];
                handList.add(Map.of(
                        "name", card.getName(),
                        "lokasi", (char) (i+'A') + "01"
                ));
            }

            // Make ladang
            List<Object> ladangList = new ArrayList<>();
            for (Map.Entry<String, FarmResourceCard> entry : p1.getLadang().getAllCardwithLocationinLadang().entrySet()) {
                FarmResourceCard card = entry.getValue();
                ladangList.add(Map.of(
                        "name", card.getName(),
                        "lokasi", entry.getKey(),
                        "ageOrWeight", card instanceof oop.if2210_tb2_sc4.card.PlantCard ? ((oop.if2210_tb2_sc4.card.PlantCard) card).getAge() : ((oop.if2210_tb2_sc4.card.AnimalCard) card).getWeight(),
                        "effectCount", card.getEffect().size(),
                        "effect", card.getEffect()
                ));
            }

            Map<String, Object> player1 = Map.of(
                    "gulden", p1.getJumlahGulden(),
                    "deckCount", p1.getDeck().getCardsInDeckCount(),
                    "handCount", p1.getDeck().getActiveCardinHandCount(),
                    "hand", handList,
                    "ladangCount", p1.getJumlahKartuLadang(),
                    "ladang", ladangList
            );


            /* Create player2 Map */
            Player p2 = GameState.getInstance().getPlayer(2);
            hand = p2.getActiveDeck();
            handList = new ArrayList<>();

            // Make handList
            for (int i = 0; i < p2.getJumlahDeckActive(); i++) {
                Card card = hand[i];
                handList.add(Map.of(
                        "name", card.getName(),
                        "lokasi", (char) (i+'A') + "01"
                ));
            }

            // Make ladang
            ladangList = new ArrayList<>();
            for (Map.Entry<String, FarmResourceCard> entry : p2.getLadang().getAllCardwithLocationinLadang().entrySet()) {
                FarmResourceCard card = entry.getValue();
                ladangList.add(Map.of(
                        "name", card.getName(),
                        "lokasi", entry.getKey(),
                        "ageOrWeight", card instanceof oop.if2210_tb2_sc4.card.PlantCard ? ((oop.if2210_tb2_sc4.card.PlantCard) card).getAge() : ((oop.if2210_tb2_sc4.card.AnimalCard) card).getWeight(),
                        "effectCount", card.getEffect().size(),
                        "effect", card.getEffect()
                ));
            }

            Map<String, Object> player2 = Map.of(
                    "gulden", p2.getJumlahGulden(),
                    "deckCount", p2.getDeck().getCardsInDeckCount(),
                    "handCount", p2.getDeck().getActiveCardinHandCount(),
                    "hand", handList,
                    "ladangCount", p2.getJumlahKartuLadang(),
                    "ladang", ladangList
            );


            // Create jsonDataObject
            jsonDataObject = Map.of(
                    "gameState", gameState,
                    "player1", player1,
                    "player2", player2
            );

            // Write to file
            URL url = SaveLoad.class.getResource("");
            assert url != null;
            String cwd = url.getPath().startsWith("/") ? url.getPath().substring(1) : url.getPath();
            writer.writeValue(handleNewFile(Paths.get(cwd + folderName + "/state.json")), jsonDataObject);
        } catch (Exception e) {
            System.out.println("Error: "  + e.getMessage());
        }
    }

    private File handleNewFile(Path path){
        File file = null;
        try {
            // if folder does not exist, create new file
            Files.createDirectories(path.getParent());

            file = new File(path.toString());
            if (!file.exists()) {
                // if file does not exist, create new file
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getName());
                } else {
                    System.out.println("File creating failed.");
                }
            } else {
                System.out.println("File " + file.getName() + " already exists.");
                System.out.println("Save will overwrite the file.");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            System.out.println();
        }
        return file;
    }
}
