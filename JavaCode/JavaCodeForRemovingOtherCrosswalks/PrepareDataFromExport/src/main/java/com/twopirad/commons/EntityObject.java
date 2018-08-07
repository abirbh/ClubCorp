/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twopirad.commons;

import java.util.List;
import java.util.Map;

/**
 *
 * @author akk
 */
public class EntityObject {

    private String uri;
    
    private String type = "configuration/entityTypes/Amenity";
    
    private Map<String, List<AttributeValue>> attributes;
    
    private List<Crosswalk> crosswalks;

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the attributes
     */
    public Map<String, List<AttributeValue>> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(Map<String, List<AttributeValue>> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return the crosswalks
     */
    public List<Crosswalk> getCrosswalks() {
        return crosswalks;
    }

    /**
     * @param crosswalks the crosswalks to set
     */
    public void setCrosswalks(List<Crosswalk> crosswalks) {
        this.crosswalks = crosswalks;
    }

    public String getType() {
        return "configuration/entityTypes/Amenity";
    }

    public void setType(String type) {
        this.type = type;
    }
    
    
}
