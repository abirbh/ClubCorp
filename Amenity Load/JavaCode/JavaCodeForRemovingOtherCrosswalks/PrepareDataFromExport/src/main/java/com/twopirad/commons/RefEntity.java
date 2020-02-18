/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twopirad.commons;

import java.util.List;

/**
 *
 * @author akk
 */
public class RefEntity {
    private String type;
    private List<Crosswalk> crosswalks;

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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
    
    
    
}
