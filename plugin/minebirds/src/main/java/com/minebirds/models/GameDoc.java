package com.minebirds.models;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import com.minebirds.Database;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

public class GameDoc {

  public Document doc;
  public String mode;
  public String creator;

  public GameDoc(String key) {
    this.mode = "existent";
    this.doc = new Document("key", key);
  }

  public GameDoc() {
    this.mode = "new";
    this.doc = new Document();
    this.doc.put("type", "bv");
    this.doc.put("status", "OPENED");
  }

  public void save(String key) {
    this.doc.put("key", key);
    List<String> players = new ArrayList<>();
    players.add(this.creator);
    this.doc.put("players", players);
    Database.create(doc, "games");
  }

  public static boolean playerIsInGame(String playerUsername, String key) {
    MongoCollection<Document> collection = Database.database.getCollection(
      "games"
    );
    // Query to check if the username exists in the players array
    Document foundDocument = collection
      .find(and(eq("players", playerUsername), eq("key", key)))
      .first();
    // Check and output result
    if (foundDocument != null) {
      return true;
    } else {
      return false;
    }
  }

  public GameDoc setCreator(String value) {
    this.creator = value;
    this.doc.put("creator", value);
    return this;
  }

  public GameDoc setType(String value) {
    this.doc.put("type", value);
    return this;
  }

  public String getCreator() {
    return (String) this.doc.get("creator");
  }

  public Document get() {
    return this.doc;
  }
}
