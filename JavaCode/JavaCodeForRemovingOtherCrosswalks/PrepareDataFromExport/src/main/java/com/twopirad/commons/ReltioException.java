/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twopirad.commons;

/**
 * Reltio exception
 * @author akk
 */
public class ReltioException extends Exception {

    public ReltioException(String message) {
        super(message);
    }

    public ReltioException(Throwable cause) {
        super(cause);
    }

    public ReltioException(String message, Throwable cause) {
        super(message, cause);
    }
}
