/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twopirad.commons;

import com.google.gson.reflect.TypeToken;
import static com.twopirad.Launcher.gson;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 *
 * @author akk
 */
public class AttributeValue {
    
    private Object value;
    
    String lookupCode;
    
    String lookupRawValue;
    
    private RefEntity refEntity;
    
    private RefEntity refRelation;

    /**
     * @return the value
     */
    public Object getValue() {
        if(value instanceof String){
            return value;
        }
        else{
            Type type = new TypeToken<Map<String, List<AttributeValue>>>(){}.getType();
            Map<String, List<AttributeValue>> value1 = gson.fromJson(gson.toJson(this.value), type);
            return value1;
        }
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        
        if(value instanceof String){
            this.value = value;
        }else{
            Type type = new TypeToken<Map<String, List<AttributeValue>>>(){}.getType();
            Map<String, List<AttributeValue>> value1 = gson.fromJson(gson.toJson(value), type);
            this.value = value1;
        }
    }

    public RefEntity getRefEntity() {
            return refEntity;
    }

    public RefEntity getRefRelation() {
        return refRelation;
    }

    public void setRefRelation(RefEntity refRelation) {
        this.refRelation = refRelation;
    }

    public void setRefEntity(RefEntity refEntity) {
            this.refEntity = refEntity;
    }

    public String getLookupCode() {
        return lookupCode;
    }

    public void setLookupCode(String lookupCode) {
        this.lookupCode = lookupCode;
    }

    public String getLookupRawValue() {
        return lookupRawValue;
    }

    public void setLookupRawValue(String lookupRawValue) {
        this.lookupRawValue = lookupRawValue;
    }
    
    
}
