/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twopirad.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author akk
 */
public class FileUtils {
    
    public static void writeToFile(String content, String fileName) throws IOException {

        File file = new File(fileName);

        // if file doesnt exists, then create it
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                System.out.println("Error in creating file with name : " + fileName + "\n" + ex);
            }
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(file.getAbsoluteFile(), false);
        } catch (IOException ex) {
            System.out.println("Error in getting file writer." + "\n" + ex);
        }
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
    }
    
}
