package com.mk.controller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
public class SampleController{

    @Autowired
    private ResourceLoader resourceLoader;
    MongoClient mongoClient = MongoClients.create();
    MongoDatabase mongoDatabase= mongoClient.getDatabase("sample_json");

    @GetMapping("/getJson")
    public String getJson() throws IOException {
        Resource resource=resourceLoader.getResource("classpath:sample.json");
        InputStreamReader inputStreamReader=new InputStreamReader(resource.getInputStream());
        String string =FileCopyUtils.copyToString(inputStreamReader);

        JSONObject jsonObject=new JSONObject(string);
        jsonObject.remove("company");
        for(String key:jsonObject.keySet()){
            System.out.println(key);
            MongoCollection<Document> collection=mongoDatabase.getCollection(key);
            JSONArray jsonArray=jsonObject.getJSONArray(key);
            for (int i=0;i< jsonArray.length();i++) {
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                collection.insertOne(new Document(jsonObject1.toMap()));
            }
        }
        return  "Data converted from json file to mongodb database successfully...";
    }
}
