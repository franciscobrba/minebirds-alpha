package com.minebirds;

import com.google.common.base.Objects;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class Database {

  static final String DATABASE_NAME = "minebirds_db";
  static final String SERVER = "host.docker.internal";
  static final String PORT = "33333";
  static final String DB_USER = "minebirds_user";
  static final String DB_PASS = "minebirds_pass";
  static final String DATABASE_CONNECTION =
    "mongodb://" + DB_USER + ":" + DB_PASS + "@" + SERVER + ":" + PORT;

  static MongoClient client;
  public static MongoDatabase database;

  public static boolean connect() {
    try {
      client = MongoClients.create(DATABASE_CONNECTION);
      database = client.getDatabase(DATABASE_NAME);
      System.out.println("Database connected");
      return true;
    } catch (NoClassDefFoundError e) {
      System.out.println("Database not connected");
      e.printStackTrace();
      return false;
    }
  }

  public static String create(Document doc, String collectionName) {
    MongoCollection<Document> collection = database.getCollection(
      collectionName
    );
    InsertOneResult inserted = collection.insertOne(doc);
    return inserted.getInsertedId().asObjectId().getValue().toHexString();
  }

  public static void createGame(Document doc) {
    MongoCollection<Document> collection = database.getCollection("games");
    collection.insertOne(doc);
  }

  public static boolean documentExists(Document filter, String collectionName) {
    MongoCollection<Document> collection = database.getCollection(
      collectionName
    );
    long count = collection.countDocuments(filter);
    return count > 0;
  }

  public static @Nullable Document find(
    Document filter,
    String collectionName
  ) {
    MongoCollection<Document> collection = database.getCollection(
      collectionName
    );
    return collection.find(filter).first();
  }

  public static boolean updateDoc(
    Document payload,
    Document filter,
    String collectionName
  ) {
    MongoCollection<Document> collection = database.getCollection(
      collectionName
    );
    Document updates = new Document();
    updates.put("$set", payload);
    collection.updateOne(filter, updates);
    return true;
  }

  public static boolean addToGameList(
    String key,
    String listKey,
    String payload
  ) {
    MongoCollection<Document> collection = database.getCollection("games");
    Bson update = Updates.push(listKey, payload);
    collection.updateOne(new Document("key", key), update);
    return true;
  }

  public static boolean removeFromGameList(
    String key,
    String listKey,
    String payload
  ) {
    MongoCollection<Document> collection = database.getCollection("games");
    Bson update = Updates.pull(listKey, payload);
    collection.updateOne(new Document("key", key), update);
    return true;
  }

  public static boolean playerIsInGame(String player) {
    MongoCollection<Document> collection = database.getCollection("games");
    Document filter = new Document(
      "players",
      new Document("$in", Collections.singletonList(player))
    );
    long count = collection.countDocuments(filter);
    return count > 0;
  }

  public static boolean updateGameProp(
    String gameId,
    String key,
    String value
  ) {
    Document doc = new Document(key, value);
    Document filter = new Document("key", gameId);
    Document updateObj = new Document();
    updateObj.put("$set", doc);
    MongoCollection<Document> collection = database.getCollection("games");
    collection.updateOne(filter, updateObj);
    return true;
  }

  public static boolean updateGameDoc(String gameId, String key, Document doc) {
    Document filter = new Document("key", gameId);
    Document updateObj = new Document();
    updateObj.put("$set", new Document(key, doc));
    MongoCollection<Document> collection = database.getCollection("games");
    collection.updateOne(filter, updateObj);
    return true;
  }

  public static Object getGameProp(String gameId, String key) {
    Document filter = new Document("key", gameId);
    MongoCollection<Document> collection = database.getCollection("games");
    Document obj = collection.find(filter).first();
    return obj.get(key);
  }

  public static Document findGame(String key) {
    return Database.find(new Document("key", key), "games");
  }
}
