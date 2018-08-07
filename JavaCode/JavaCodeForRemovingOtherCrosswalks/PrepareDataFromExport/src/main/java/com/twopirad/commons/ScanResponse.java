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
public class ScanResponse {
    
    private Cursor cursor;
    
    private List<EntityObject> objects;

    /**
     * @return the cursor
     */
    public Cursor getCursor() {
        return cursor;
    }

    /**
     * @param cursor the cursor to set
     */
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    /**
     * @return the objects
     */
    public List<EntityObject> getObjects() {
        return objects;
    }

    /**
     * @param objects the objects to set
     */
    public void setObjects(List<EntityObject> objects) {
        this.objects = objects;
    }
}
