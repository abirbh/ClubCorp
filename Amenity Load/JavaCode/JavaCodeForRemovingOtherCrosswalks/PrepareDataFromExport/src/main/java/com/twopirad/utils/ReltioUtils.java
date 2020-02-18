/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twopirad.utils;

import com.google.gson.reflect.TypeToken;
import static com.twopirad.Launcher.gson;
import com.twopirad.commons.Constants;
import com.twopirad.commons.EntityObject;
import com.twopirad.commons.ReltioException;
import com.twopirad.commons.ReltioRestApiService;
import com.twopirad.commons.RequestBodyCursorForScan;
import com.twopirad.commons.ScanResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author akk
 */
public class ReltioUtils {
    
    
    
    /**
     * Get a entity for a entity uri
     * @param uri
     * @param reltioRestApiService
     * @return EntityObject 
     * @throws IOException 
     * @throws ReltioException 
     */
    public static EntityObject getEntityFromEntityUri(String uri, ReltioRestApiService reltioRestApiService) throws IOException, ReltioException{      
        String entityUrl = "/" + uri;
        String entityResponse = reltioRestApiService.executePrependingTenantUrl(entityUrl, Constants.GET, null);
        EntityObject entityObject = gson.fromJson(entityResponse, EntityObject.class);       
        return entityObject;
    }
    
    /**
     * Returns the last token of a String after Splitting it with seperator
     * @param typeStr
     * @param seperator
     * @return 
     */
    public static String getLastToken(String typeStr, String seperator) {
        typeStr = StringUtils.trimToEmpty(typeStr);
        String[] tokens = StringUtils.split(typeStr, seperator);
        String value = typeStr;
        if (tokens.length > 1) {
            value = tokens[tokens.length - 1];
        }
        return value;
    }
    
    
    /**
     * Retrieve Entities with the scan call with the filter condition specified
     * @param filterCondition : filter condition for fetching the entities
     * @param reltioRestApiService
     * @return 
     * @throws IOException 
     * @throws ReltioException 
     */
    public static List<EntityObject> retrieveEntitiesByScanCall(String filterCondition, ReltioRestApiService reltioRestApiService) throws IOException, ReltioException{
        List<EntityObject> entities = new ArrayList<>();
        // Retrieve entities using _scan api using the specified filter condition for the first time
        // it will retrieve 150 entities                       
        String apiUrl = "/entities/_scan?filter=" + filterCondition + "&max=" + Constants.MAX_NUMBER_OF_ENTITIES_PER_CALL;
        String response = reltioRestApiService.executePrependingTenantUrl(apiUrl, Constants.POST, null);

        // evaluating the response
        ScanResponse scanResponse = gson.fromJson(response, ScanResponse.class);
        if (scanResponse!= null && scanResponse.getObjects() != null) {
            entities.addAll(scanResponse.getObjects());

            int total = 0;
            int count = entities.size();
            total = total + count;

            // Calling _scan apis second time onwards, call it with the cursor that is retrieved from the 1st scan call
            // will call this api until we get the no entities in the response
            while (count > 0) {
                apiUrl = "/entities/_scan";
                // adding cursor value to the request body
                RequestBodyCursorForScan requestBody = new RequestBodyCursorForScan();
                requestBody.setCursor(scanResponse.getCursor());

                response = reltioRestApiService.executePrependingTenantUrl(apiUrl, Constants.POST, gson.toJson(requestBody));
                scanResponse = gson.fromJson(response, ScanResponse.class);

                //adding the required information from the respose for future use
                count = 0;
                if (scanResponse != null && scanResponse.getObjects() != null) {
                    count = scanResponse.getObjects().size();
                    total = total + count;
                    entities.addAll(scanResponse.getObjects());
                }
            }
        }
        return entities;
    }
    
}
