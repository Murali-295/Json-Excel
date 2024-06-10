package com.mk.controller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@RestController
public class JsonController {

    @Autowired
    private ResourceLoader resourceLoader;
    MongoClient mongoClient = MongoClients.create();
    MongoDatabase mongoDatabase= mongoClient.getDatabase("movies_excel");

    @GetMapping("/jsontoexcel")
    public String convertJsonToExcel() throws IOException {

        Resource resource= resourceLoader.getResource("classpath:movies.json");
        Workbook wb = new XSSFWorkbook();
        Sheet sheet=wb.createSheet("Movies");

        int rowNum=0;
        Row row=sheet.createRow(rowNum++);
        InputStreamReader inputStreamReader=new InputStreamReader(resource.getInputStream());

        String s= FileCopyUtils.copyToString(inputStreamReader);
        JSONObject jsonObject=new JSONObject(s);

        for(String key:jsonObject.keySet()){
           JSONObject jsonObject1=jsonObject.getJSONObject(key);
           int cellNum=0;
           for(String key1:jsonObject1.keySet()){
               Cell cell=row.createCell(cellNum++);
               cell.setCellValue(key1);
           }
           break;
        }
        for(String key:jsonObject.keySet()){
            JSONObject jsonObject1=jsonObject.getJSONObject(key);
            Row row1=sheet.createRow(rowNum++);
            int cellNum=0;
            for(String key1:jsonObject1.keySet()){
                Cell cell=row1.createCell(cellNum++);
                cell.setCellValue(jsonObject1.getString(key1));
            }
        }

        FileOutputStream fileOutputStream=new FileOutputStream("Movies.xlsx");
        wb.write(fileOutputStream);
        wb.close();
        fileOutputStream.close();

        return "Data Converted from Json file to Excel succesfully...";
    }

    @GetMapping("/jsontodb")
    public String convertJsonToDB() throws IOException {
        Resource resource= resourceLoader.getResource("classpath:movies.json");
        InputStreamReader inputStreamReader=new InputStreamReader(resource.getInputStream());

        MongoCollection<Document> collection=mongoDatabase.getCollection("movies_excel");
        String s= FileCopyUtils.copyToString(inputStreamReader);
        JSONObject jsonObject=new JSONObject(s);

        for(String key:jsonObject.keySet()){
            JSONObject jsonObject1=jsonObject.getJSONObject(key);
            Map<String, Object> map= jsonObject1.toMap();
           Document document=new Document(map);
           collection.insertOne(document);
        }
        return "Data converted from Json To DB successfully...";
    }

    @GetMapping("/jsontodbexcel")
    public String convertJsontoDBAndExcel() throws IOException {

        Resource resource= resourceLoader.getResource("classpath:movies.json");
        MongoCollection<Document> collection=mongoDatabase.getCollection("movies_excel_json");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet=workbook.createSheet("Movies_Excel");

        int rowNum=0;
        Row row=sheet.createRow(rowNum++);
        InputStreamReader inputStreamReader=new InputStreamReader(resource.getInputStream());

        String s= FileCopyUtils.copyToString(inputStreamReader);
        JSONObject jsonObject=new JSONObject(s);

        for(String key:jsonObject.keySet()){
            JSONObject jsonObject1=jsonObject.getJSONObject(key);

            int cellNum=0;
            for(String key1:jsonObject1.keySet()){
                Cell cell=row.createCell(cellNum++);
                cell.setCellValue(key1);
            }
            break;
        }

        for(String key:jsonObject.keySet()){
            Row row1=sheet.createRow(rowNum++);
            JSONObject jsonObject1=jsonObject.getJSONObject(key);
            int cellNum=0;
            for(String key1:jsonObject1.keySet()){
                Cell cell=row1.createCell(cellNum++);
                cell.setCellValue(jsonObject1.getString(key1));
            }
            collection.insertOne(new Document(jsonObject1.toMap()));
        }

        FileOutputStream fileOutputStream=new FileOutputStream("Movies_Excel.xlsx");
        workbook.write(fileOutputStream);
        workbook.close();
        fileOutputStream.close();
        return "Json to DB and Excel successfully...";
    }
}
