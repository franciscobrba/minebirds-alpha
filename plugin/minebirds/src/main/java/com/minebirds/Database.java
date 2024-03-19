package com.minebirds;

import com.google.common.base.Objects;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.model.Updates;

import java.util.Collections;
import java.util.List;

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
    static final String DATABASE_CONNECTION = "mongodb://"+DB_USER+":"+DB_PASS+"@"+SERVER+":"+PORT;


    static MongoClient client;
    static MongoDatabase database;

    static public boolean connect(){
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

    static public String create(Document doc, String collectionName) {
      MongoCollection<Document> collection = database.getCollection(collectionName);
      InsertOneResult inserted = collection.insertOne(doc);
      return inserted.getInsertedId().asObjectId().getValue().toHexString();
    }

    static public boolean documentExists(Document filter, String collectionName) {
      MongoCollection<Document> collection = database.getCollection(collectionName);
      long count = collection.countDocuments(filter);
      return count > 0;
    }

    static public boolean updateDoc(Document payload, Document filter, String collectionName) {
      MongoCollection<Document> collection = database.getCollection(collectionName);
      Document updates = new Document();
      updates.put("$set", payload);
      collection.updateOne(filter, updates);
      return true;
    }

    static public boolean updateDocList(Document filter, String action, String payload, String collectionName) {
      MongoCollection<Document> collection = database.getCollection(collectionName);
      if (action == "add") {
        Bson update = Updates.push("players", payload);
        collection.updateOne(filter, update);
        return true;
      }
      if (action == "remove") {
        Bson update = Updates.pull("players", payload);
        collection.updateOne(filter, update);
        return true;
      }
      return false;
    }

    static public boolean playerIsInGame(String player) {
      MongoCollection<Document> collection = database.getCollection("games");
      Document filter = new Document("players", new Document("$in", Collections.singletonList(player)));
      long count = collection.countDocuments(filter);
      return count > 0;
    }


    static public boolean updateGameProp(String gameId, String key, String value) {
      Document doc = new Document(key, value);
      Document filter = new Document("key", gameId);
      Document updateObj = new Document();
      updateObj.put("$set", doc);
      MongoCollection<Document> collection = database.getCollection("games");
      collection.updateOne(filter, updateObj);
      return true;
    }

    static public Object getGameProp(String gameId, String key) {
      Document filter = new Document("key", gameId);
      MongoCollection<Document> collection = database.getCollection("games");
      Document obj = collection.find(filter).first();
      return obj.get(key);
    }


}
