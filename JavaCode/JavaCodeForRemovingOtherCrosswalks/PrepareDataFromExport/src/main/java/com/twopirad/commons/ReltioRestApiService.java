/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twopirad.commons;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author akk
 */
public class ReltioRestApiService {
    
    private final Gson gson = new Gson();
    
    private final String username;
    
    private final String password;
    
    private final String authUrl;
    
    private final String tenantUrl;
    
    private String accessToken = "";
    
    private final String apiKey;
    
    /**
     * Constructor
     * @param tenantUrl
     * @param authUrl
     * @param username
     * @param password 
     * @param accessToken 
     * @param apiKey 
     */
    public ReltioRestApiService(String tenantUrl, String authUrl, String username, String password, String accessToken, String apiKey) {
        this.username = username;
        this.password = password;
        this.authUrl = authUrl;
        this.tenantUrl = tenantUrl;
        this.accessToken = accessToken;
        this.apiKey = apiKey;
    }
    
    /**
     * Execute
     *
     * @param requestBody
     * @param method
     * @param sourceUrl
     * @return
     * @throws IOException
     * @throws ReltioException
     */
    public String execute(String sourceUrl, String method, String requestBody) throws IOException, ReltioException {

        HttpURLConnection connection = null;
        InputStream response = null;
        
        try {
            URL url = new URL(sourceUrl);
            connection = (HttpURLConnection) url.openConnection();
            setUpConnection(connection, method, requestBody);

            int iteration = 0;
            int responseCode = connection.getResponseCode();

            // if there is any problem while fetching the response, response code will be 400 or, more
            // we will make 3 retries to get the correct response
            while (responseCode >= 400) {
                iteration++;
                System.out.println("ErrorCode:" + responseCode + "|Iteration:" + iteration + "|" + Thread.currentThread().getName());

                // we retrying 3 times, before giving it as a error 
                if (iteration >= 3) {
                    System.out.println("Failed to get data" + requestBody);
                    break;
                }

                // getting the access token if it is expired
                if (responseCode == 401) {
                    response = connection.getErrorStream();
                    String buildStringOutputOfResponse = buildStringOutputOfResponse(response);

                    Map<String, Object> inputValues = gson.fromJson(buildStringOutputOfResponse, Map.class);

                    Object error = inputValues.get(Constants.ERROR);
                    if (error != null) {
                        String errorStr = String.valueOf(error);

                        if (errorStr.equals(Constants.INVALID_TOKEN)) {
                            getAccessToken();
                        }
                    }
                }

                connection = (HttpURLConnection) url.openConnection();
                setUpConnection(connection, method, requestBody);
                responseCode = connection.getResponseCode();
            }

            // if there is still a problem, we are throwing reltio exception
            if (responseCode >= 400) {
                response = connection.getErrorStream();
                if (response == null) {
                    throw new ReltioException("Invalid url given : " + sourceUrl);
                } else {
                    String errorResponse = buildStringOutputOfResponse(response);
                    throw new ReltioException(errorResponse);
                }
            } // other wise we try to get the response and return it back
            else {
                response = connection.getInputStream();
            }

            return buildStringOutputOfResponse(response);
        } finally {

            if (response != null) {
                response.close();
            }
            if(connection != null){
                connection.disconnect();
            }
        }
    }
    

    /**
     * Execute 
     * @param requestBody
     * @param method
     * @param sourceUrl
     * @return 
     * @throws IOException 
     * @throws ReltioException 
     */
    public String executePrependingTenantUrl(String sourceUrl, String method, String requestBody) throws IOException, ReltioException{       
        return execute(this.tenantUrl + sourceUrl, method, requestBody);
    }
    
    /**
     * Set up the connection with the parameters
     * @param connection
     * @param method
     * @param requestBody
     * @param token
     * @throws IOException 
     */
    private void setUpConnection(HttpURLConnection connection, String method, String requestBody) throws IOException, ReltioException{
        connection.setRequestMethod(method);
        if(!Constants.GET.equals(method)){
            connection.setDoOutput(true); // Triggers POST.
        }
        if(StringUtils.isEmpty(StringUtils.trimToEmpty(accessToken))){
            getAccessToken();
        }
        connection.setRequestProperty("Authorization", "Bearer " + this.accessToken);
        connection.setRequestProperty("Content-Type", "application/json");
        
        if (requestBody != null) {
            OutputStream output = connection.getOutputStream();
            try {
                output.write(requestBody.getBytes());
            } finally {
                try {
                    output.close();
                } catch (IOException logOrIgnore) {
                    System.out.println("Failed to close output stream : " + logOrIgnore);
                }
            }
        }
    }
    
    /**
     * Build the string response from input stream
     * @param response
     * @return 
     */
    private String buildStringOutputOfResponse(InputStream response){
        StringBuilder builder = new StringBuilder();
        try {
            byte[] buffer = new byte[100000];
            int count = response.read(buffer);
            while (count > 0) {
                builder.append(new String(buffer, 0, count));
                count = response.read(buffer);
            }
        } catch (IOException ex) {
            Logger.getLogger(ReltioRestApiService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ex) {
                    Logger.getLogger(ReltioRestApiService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return builder.toString();
    }

    /**
     * Get the access token
     * @throws IOException 
     * @throws ReltioException 
     */
    public void getAccessToken() throws IOException, ReltioException{

        //This change is made as we expect authurl upto https://auth02.reltio.com/oauth/token
        //previously the expectation was https://auth02.reltio.com
        HttpURLConnection connection = (HttpURLConnection)new URL(this.authUrl + "?username=" + this.username + "&password=" + this.password + "&grant_type=password").openConnection();
        //HttpURLConnection connection = (HttpURLConnection)new URL(this.authUrl + "/oauth/token?username=" + this.username + "&password=" + this.password + "&grant_type=password").openConnection();
        connection.setDoOutput(true); // Triggers POST.
        
        connection.setRequestProperty("Authorization", "Basic " + apiKey);
        //connection.setRequestProperty("Authorization", "Basic cmVsdGlvX3VpOm1ha2l0YQ==");

        OutputStream output = connection.getOutputStream();
        output.write("".getBytes());

        InputStream response = null;
        
        String responseStr = "";
        try {
            int responseCode = connection.getResponseCode();
            
            // if there is still a problem, we are throwing reltio exception
            if (responseCode >= 400) {
                response = connection.getErrorStream();
                if (response == null) {
                    throw new ReltioException("Invalid url given");
                } else {
                    String errorResponse = buildStringOutputOfResponse(response);
                    throw new ReltioException(errorResponse);
                }
            } // other wise we try to get the response and return it back
            else {
                response = connection.getInputStream();
                responseStr = buildStringOutputOfResponse(response);
            }
        } finally {
            if (response != null) {
                response.close();
            }
            connection.disconnect();
        }

        responseStr = StringUtils.trimToEmpty(responseStr);
        if(!responseStr.isEmpty()){
            int index = responseStr.indexOf("\"access_token\":\"");
            if (index >= 0) {
                int endIndex = responseStr.indexOf("\"", index + "\"access_token\":\"".length());
                String access_token = responseStr.substring(index + "\"access_token\":\"".length(), endIndex);
                this.accessToken = access_token;
            }
        } 
    }
    
    /**
     * Tag a entity
     * if tagName is empty, it will  remove tag from the entity
     * @param entityURI : uri of the entity
     * @param tagName : name of the tag 
     * @throws IOException 
     * @throws ReltioException 
     */
    public void tagEntity(String entityURI, String tagName) throws IOException, ReltioException{
        String url = "/" + entityURI + "/_update?overwriteDefaultCrosswalkValue=false";
        
        tagName = StringUtils.trimToEmpty(tagName);
        String requestBody = "[{\"type\":\"UPDATE_TAGS\",\"newValue\":[\"" + tagName + "\"]}]";
        if(tagName.isEmpty()){
            requestBody = "[{\"type\":\"UPDATE_TAGS\",\"newValue\":[]}]"; 
        }
        executePrependingTenantUrl(url, Constants.POST, requestBody);
    }

}
