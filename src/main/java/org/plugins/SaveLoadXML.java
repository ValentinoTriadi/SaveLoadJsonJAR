package org.plugins;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SaveLoadXML {
    public static void main(String[] args) {
        try {
            // Create a DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Create a DocumentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML file and get the document
            Document document = builder.parse("src/main/java/org/plugins/state/state.xml");

            // Normalize the XML structure
            document.getDocumentElement().normalize();

            // Get the root element
            Element root = document.getDocumentElement();
            System.out.println("Root Element: " + root.getNodeName());

            // Parse player1
            NodeList player1List = document.getElementsByTagName("player1");
            if (player1List.getLength() > 0) {
                Node player1Node = player1List.item(0);
                if (player1Node.getNodeType() == Node.ELEMENT_NODE) {
                    Element player1Element = (Element) player1Node;
                    parsePlayer(player1Element);
                }
            }

            // Parse player2
            NodeList player2List = document.getElementsByTagName("player2");
            if (player2List.getLength() > 0) {
                Node player2Node = player2List.item(0);
                if (player2Node.getNodeType() == Node.ELEMENT_NODE) {
                    Element player2Element = (Element) player2Node;
                    parsePlayer(player2Element);
                }
            }

            // Parse gameState
            NodeList gameStateList = document.getElementsByTagName("gameState");
            if (gameStateList.getLength() > 0) {
                Node gameStateNode = gameStateList.item(0);
                if (gameStateNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element gameStateElement = (Element) gameStateNode;
                    parseGameState(gameStateElement);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parsePlayer(Element playerElement) {
        // Parse hand
        NodeList handList = playerElement.getElementsByTagName("hand");
        if (handList.getLength() > 0) {
            Element handElement = (Element) handList.item(0);
            System.out.println("Hand count: " + handElement.getAttribute("count"));
            NodeList cardList = handElement.getElementsByTagName("card");
            for (int i = 0; i < cardList.getLength(); i++) {
                Node cardNode = cardList.item(i);
                if (cardNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element cardElement = (Element) cardNode;
                    System.out.println("Card lokasi: " + cardElement.getElementsByTagName("lokasi").item(0).getTextContent());
                    System.out.println("Card name: " + cardElement.getElementsByTagName("name").item(0).getTextContent());
                }
            }
        }

        // Parse ladang
        NodeList ladangList = playerElement.getElementsByTagName("ladang");
        if (ladangList.getLength() > 0) {
            Element ladangElement = (Element) ladangList.item(0);
            System.out.println("Ladang count: " + ladangElement.getAttribute("count"));
            NodeList itemList = ladangElement.getElementsByTagName("item");
            for (int i = 0; i < itemList.getLength(); i++) {
                Node itemNode = itemList.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) itemNode;
                    System.out.println("Item lokasi: " + itemElement.getElementsByTagName("lokasi").item(0).getTextContent());
                    System.out.println("Item name: " + itemElement.getElementsByTagName("name").item(0).getTextContent());
                    System.out.println("Item ageOrWeight: " + itemElement.getElementsByTagName("ageOrWeight").item(0).getTextContent());

                    NodeList effectList = itemElement.getElementsByTagName("effect");
                    System.out.println("Effects count: " + effectList.getLength());
                    for (int j = 0; j < effectList.getLength(); j++) {
                        System.out.println("Effect: " + effectList.item(j).getTextContent());
                    }
                }
            }
        }

        // Parse gulden and deckCount
        System.out.println("Gulden: " + playerElement.getElementsByTagName("gulden").item(0).getTextContent());
        System.out.println("Deck count: " + playerElement.getElementsByTagName("deckCount").item(0).getTextContent());
    }

    private static void parseGameState(Element gameStateElement) {
        // Parse items
        NodeList itemsList = gameStateElement.getElementsByTagName("items");
        if (itemsList.getLength() > 0) {
            Element itemsElement = (Element) itemsList.item(0);
            System.out.println("Items count: " + itemsElement.getAttribute("count"));
            NodeList itemList = itemsElement.getElementsByTagName("item");
            for (int i = 0; i < itemList.getLength(); i++) {
                Node itemNode = itemList.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) itemNode;
                    System.out.println("Item count: " + itemElement.getElementsByTagName("count").item(0).getTextContent());
                    System.out.println("Item name: " + itemElement.getElementsByTagName("name").item(0).getTextContent());
                }
            }
        }

        // Parse currentPlayer
        System.out.println("Current player: " + gameStateElement.getElementsByTagName("currentPlayer").item(0).getTextContent());
    }
}

