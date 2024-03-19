package com.minebirds.models;

import com.minebirds.Database;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.Document;

public class GameDoc {

  public Document doc;
  public String mode;

  public GameDoc(String key) {
    this.mode = "existent";
    this.doc = new Document("key", key);
  }

  public GameDoc() {
    this.mode = "new";
    this.doc = new Document();
    this.doc.put("type", "bv");
    this.doc.put("status", "OPENED");
    List<String> players = new ArrayList<>();
    this.doc.put("players", players);
  }

  public void save(String key) {
    this.doc.put("key", key);
    Database.create(doc, "games");
  }

  public GameDoc setCreator(String value) {
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

  public GameDoc startGame() {
    this.doc.put("startedAt", String.valueOf(new Date().getTime()));
    return this;
  }
}
