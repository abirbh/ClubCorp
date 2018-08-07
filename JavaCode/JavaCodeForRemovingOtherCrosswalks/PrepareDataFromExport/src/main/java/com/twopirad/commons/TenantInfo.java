/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twopirad.commons;

/**
 *
 * @author akk
 */
public class TenantInfo {

    // tenantId
    private String tenantUrl;

    // auth url
    private String authUrl;

    // api key
    private String apiKey;

    /**
     * @return the authUrl
     */
    public String getAuthUrl() {
        return authUrl;
    }

    /**
     * @param authUrl the authUrl to set
     */
    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    /**
     * @return the tenantUrl
     */
    public String getTenantUrl() {
        return tenantUrl;
    }

    /**
     * @param tenantUrl the tenantUrl to set
     */
    public void setTenantUrl(String tenantUrl) {
        this.tenantUrl = tenantUrl;
    }

    /**
     * @return the apiKey
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @param apiKey the apiKey to set
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

}
