/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twopirad;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twopirad.commons.AttributeValue;
import com.twopirad.commons.Crosswalk;
import com.twopirad.commons.EntityObject;
import com.twopirad.utils.FileUtils;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author akk
 */
public class Launcher {

    public static Gson gson = new Gson();
    public static List<String> crosswalkValuesFromSurveySourceWithProperFormat = new ArrayList<>();
    
    /**
     * Main method
     *
     * @param args
     * @throws IOException
     * @throws Exception
     */
    public static void main(String[] args) throws IOException, Exception {
        
        Type typeForTypeCasting = new TypeToken<Map<String, List<AttributeValue>>>(){}.getType();

        List<EntityObject> entityWithWrongCrosswalkSourceOrValue = new ArrayList<>();
        
        
        FileReader fileReader = new FileReader("C:\\Projects\\MSS\\Loading _AmenityData_from_Survey\\Codes\\ClubCorp\\JavaCode\\JavaCodeForRemovingOtherCrosswalks\\ProdMove\\Input\\Amenity_Extract_Ov_True.json");
        List<EntityObject> entities = gson.fromJson(fileReader, new TypeToken<List<EntityObject>>() {}.getType());
        
        
        // Checking Entity crosswalk has Survey Source or, not
        // Ifit has Survey Source, then whether it is in expected fromat or not
        for (EntityObject entity : entities) {
            List<Crosswalk> crosswalks = entity.getCrosswalks();
            
            if(isSurveySourceExists(crosswalks)){
                if(!isAmenityCrosswalkInProperForm(crosswalks)){
                    entityWithWrongCrosswalkSourceOrValue.add(entity);
                }
            }else{
                entityWithWrongCrosswalkSourceOrValue.add(entity);
            }          
        }
        
        // Removing all the entities that does not have proper crosswalk value as expected
        entities.removeAll(entityWithWrongCrosswalkSourceOrValue);
        
        // Iterating entities with proper crosswalk source and value 
        for (EntityObject entity : entities) {

            // Removing other crosswalks
            List<Crosswalk> crosswalks = entity.getCrosswalks();
            removeCrosswalkOtherThanSurvey(crosswalks,true);
            
            // removing other crosswalk from Address
            List<AttributeValue> addresses = entity.getAttributes().get("Address");
            if (addresses != null) {
                for (AttributeValue address : addresses) {
                    
                    // Checkeing Ref Entity Crosswalks
                    List<Crosswalk> refEntitycrosswalks = address.getRefEntity().getCrosswalks();
                    if(isSurveySourceExists(refEntitycrosswalks)){
                        removeCrosswalkOtherThanSurvey(refEntitycrosswalks, false);
                    
                        // Setting the crosswalk value as "Surrogate"
                        if(!address.getRefEntity().getCrosswalks().isEmpty()){
                            address.getRefEntity().getCrosswalks().get(0).setValue("Surrogate");
                        }
                    }else{
                        System.out.println("### No Survey Source Found for RefEntity(address)");
                    }
                    
                    // Checkeing Ref Relation Crosswalks
                    List<Crosswalk> refRelationCrosswalks = address.getRefRelation().getCrosswalks();
                    if(isSurveySourceExists(refRelationCrosswalks)){
                        removeCrosswalkOtherThanSurvey(refRelationCrosswalks, false);
                    }else{
                        System.out.println("$$$$ No Survey Source Found for RefRelation(Address)");
                    }
                }
            }

            // remove the unnecessary information
            Map<String, List<AttributeValue>> attributes = entity.getAttributes();
            removeUnnecessaryInfo(attributes);
        }
        
        
        // This code was for adding "Additional Information" in "Hours of Operation" for Dining Room.
        // Removing this code as it is already in the extracted Json
        
//        //creating map for additional information
//        //key is entityCode+type+index ex : 00170DiningRoom1
//        //value is the additional Informatiom
//        
//        Map<String, String> additionalInformationMap = new HashMap<>();
//        String sourceFileName = "C:\\Projects\\MSS\\Loading _AmenityData_from_Survey\\Codes\\ClubCorp\\JavaCode\\JavaCodeForRemovingOtherCrosswalks\\Input\\DiningRoom_AdditioanalInformation.csv";
//        // Prepare a list from Source File
//        FileReader sourceFileReader = new FileReader(sourceFileName);
//	//CSVParser inputCSVFileParser = new CSVParser(sourceFileReader, CSVFormat.newFormat(sourceFileDelimiter.charAt(0)).withHeader());
//        CSVParser inputCSVFileParser = new CSVParser(sourceFileReader, CSVFormat.DEFAULT.withHeader());
//        for(CSVRecord record : inputCSVFileParser){
//            String additionalDetails = record.get("AdditionalInformation");
//            String entityCode = record.get("EntityCode");
//            String paddedEntityCode = StringUtils.leftPad(entityCode, 5, "0");
//            String index = record.get("Index");
//            String type = record.get("Type");
//            
//            String key = paddedEntityCode + type + index;
//            additionalInformationMap.put(key, additionalDetails);
//        }
//        
//        // Adding Additional Information for HoursOfOperation in Dining Room
//        int additionalInfoFoundCount = 0;
//        int additionalInfoAddedCount = 0;
//        
//        List<String> additionalInfoAddedCrosswalks = new ArrayList<>();
//        for(EntityObject entity : entities){
//            Crosswalk crosswalk = entity.getCrosswalks().get(0);
//            
//            String crosswalkValue = crosswalk.getValue();
//            
//            String[] split = crosswalkValue.split("\\|");
//            String entityCode = split[3];
//            String type = split[4];
//            String index = split[5];
//            
//            if("DiningRoom".equals(type)){
//                
//                // generating key
//                String key = entityCode + type + index;
//                String additionalInformation = additionalInformationMap.get(key);
//                
//                if(!"".equals(StringUtils.trimToEmpty(additionalInformation))){
//                    additionalInfoFoundCount++;
//                    
//                    Object hoursOfOperationObj = entity.getAttributes().get("HrsOfOperation").get(0).getValue();                
//                    Map<String, List<AttributeValue>> hoursOfOperation = gson.fromJson(gson.toJson(hoursOfOperationObj), typeForTypeCasting);
//                    
//                    
//                    List<AttributeValue> hoursOfOperationList = new ArrayList<>();
//                    AttributeValue attributeValueHrsOfOperation = new AttributeValue();
//                    hoursOfOperationList.add(attributeValueHrsOfOperation);
//                    
//                    Object additionalInfoObj = hoursOfOperation.get("AdditionalInformation");
//                    
//                    if(additionalInfoObj != null){
//                        System.out.println(crosswalkValue);
//                    }else{
//                        additionalInfoAddedCount++;
//                        additionalInfoAddedCrosswalks.add(crosswalkValue);
//                        
//                        List<AttributeValue> additionalInfoList = new ArrayList<>();
//                        AttributeValue attributeValue = new AttributeValue();
//                        attributeValue.setValue(additionalInformation);
//                        additionalInfoList.add(attributeValue);
//                        
//                        hoursOfOperation.put("AdditionalInformation", additionalInfoList);
//                        
//                        attributeValueHrsOfOperation.setValue(hoursOfOperation);
//                        entity.getAttributes().put("HrsOfOperation", hoursOfOperationList);
//                    }
//                }           
//            }      
//        }
//        
//        System.out.println("AdditionalInfo found : " + additionalInfoFoundCount);
//        System.out.println("AdditionalInfo added : " + additionalInfoAddedCount);
//        System.out.println(additionalInfoAddedCrosswalks);

        // Writing the json for Amenity without unnecessary information
        // Writing 500 entities in a file
        FileUtils.writeToFile(gson.toJson(entities), "C:\\Projects\\MSS\\Loading _AmenityData_from_Survey\\Codes\\ClubCorp\\JavaCode\\JavaCodeForRemovingOtherCrosswalks\\ProdMove\\EntityWithSurveySource_AllData.json");
        
        int count=0;
        int fileCount = 1;
        List<EntityObject> entities500 = new ArrayList<>();
        for(EntityObject entity : entities){
            entities500.add(entity);
            count++;
            
            if(count == 500){
                FileUtils.writeToFile(gson.toJson(entities500), "C:\\Projects\\MSS\\Loading _AmenityData_from_Survey\\Codes\\ClubCorp\\JavaCode\\JavaCodeForRemovingOtherCrosswalks\\ProdMove\\EntityWithSurveySource"+ fileCount + ".json");
                count = 0;
                fileCount++;
                entities500 = new ArrayList<>();
            }
        }
        if(!entities500.isEmpty()){
            FileUtils.writeToFile(gson.toJson(entities500), "C:\\Projects\\MSS\\Loading _AmenityData_from_Survey\\Codes\\ClubCorp\\JavaCode\\JavaCodeForRemovingOtherCrosswalks\\ProdMove\\EntityWithSurveySource"+ fileCount + ".json");
        }
        
        
        // writing entities that do not have Survey Source or, proper crosswalk value format
        for (EntityObject entity : entityWithWrongCrosswalkSourceOrValue) {
            // remove the unnecessary information
            Map<String, List<AttributeValue>> attributes = entity.getAttributes();
            removeUnnecessaryInfo(attributes);
        }
        FileUtils.writeToFile(gson.toJson(entityWithWrongCrosswalkSourceOrValue), "C:\\Projects\\MSS\\Loading _AmenityData_from_Survey\\Codes\\ClubCorp\\JavaCode\\JavaCodeForRemovingOtherCrosswalks\\ProdMove\\EntityWithOutSurveySource.json");
        
        // creating csv containing amenity crosswalk value and Entity Code
        List<String> headers = new ArrayList<>();
        headers.add("AmenityCrosswalkValue");
        headers.add("EntityCode");
        writeTheContentToFile(headers);
    }
    
    public static void removeUnnecessaryInfo(Map<String, List<AttributeValue>> attributes) {
        for (String attributeName : attributes.keySet()) {
            List<AttributeValue> attributeValues = attributes.get(attributeName);
            for (AttributeValue attributeValue : attributeValues) {
                convertToMap(attributeValue);
            }
        }
    }
    
    public static void convertToMap(AttributeValue attributeValue){
        Type type = new TypeToken<Map<String, List<AttributeValue>>>() {}.getType();
        Object value = attributeValue.getValue(); 
        
        if (value instanceof String) {
        }else {
            Map<String, List<AttributeValue>> nestedAttributes = gson.fromJson(gson.toJson(value), type);
            removeUnnecessaryInfo(nestedAttributes);
            attributeValue.setValue(nestedAttributes);
        }
    }

    //Removing all the crosswalk other than Survey
    //Also adding the Survey Crosswalk Value to a List
    public static void removeCrosswalkOtherThanSurvey(List<Crosswalk> crosswalks, boolean isRequired) {
        List<Crosswalk> toRemove = new ArrayList<>();
        for (Crosswalk crosswalk : crosswalks) {
            String type = crosswalk.getType();

            if (!"configuration/sources/Survey".equals(type)) {
                toRemove.add(crosswalk);
            }else{
                if(isRequired){
                    crosswalkValuesFromSurveySourceWithProperFormat.add(crosswalk.getValue());
                }
            }
        }
        crosswalks.removeAll(toRemove);
    }
    
    // Check whether in the crosswalk, Survey Source Exist or not in the crosswalk
    public static boolean isSurveySourceExists(List<Crosswalk> crosswalks){
        
        boolean isExists = false;
        for (Crosswalk crosswalk : crosswalks) {
            String type = crosswalk.getType();

            if ("configuration/sources/Survey".equals(type)) {
                isExists = true;
                break;
            }
        }
        
        return isExists;
    }
    
    
    // Check whether in the crosswalk, the value from Survey Source is in proper format or, not
    // Proper format is : Survey|20180508|1|02069|BoatAndMarina|1
    public static boolean isAmenityCrosswalkInProperForm(List<Crosswalk> crosswalks){      
        boolean isInProperFormat = false;
        
        for (Crosswalk crosswalk : crosswalks) {
            String type = crosswalk.getType();
            String value = crosswalk.getValue();
            if ("configuration/sources/Survey".equals(type)) {
                if(value.startsWith("Survey|20180508|1|")){
                    String[] split = value.split("\\|");
                    if(split.length==6){
                        isInProperFormat = true;
                        break;
                    }
                }
            }
        }   
        return isInProperFormat;
    }
    
    /**
     * Write the content to the file
     * @param headers
     * @throws IOException 
     */
    public static void writeTheContentToFile(List<String> headers) throws IOException{
        
        // Generating the output file
        StringBuilder fileContent = new StringBuilder();
        
        fileContent.append(StringUtils.join(headers, ","));
        fileContent.append("\n");

        for(String crosswalkValue : crosswalkValuesFromSurveySourceWithProperFormat){
            List<String> values = new ArrayList<>();
                       
            String[] split = crosswalkValue.split("\\|");
            if(split.length==6){
                String entityCode = split[3];
                values.add(crosswalkValue);
                values.add(entityCode);
            }
            fileContent.append(StringUtils.join(values,","));
            fileContent.append("\n");
        }

        FileUtils.writeToFile(fileContent.toString(), "C:\\Projects\\MSS\\Loading _AmenityData_from_Survey\\Codes\\ClubCorp\\JavaCode\\JavaCodeForRemovingOtherCrosswalks\\ProdMove\\EntityCode_AmenityCrosswalk.csv"); 
    }
}
