/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fop_alphabet_2048;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 *
 * @author Hp
 */
public class WriteScore {
    
   public static void main(String[] args) {
    try{  //write to                                                                                                                             ////////     
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("C:\\Users\\Hp\\Documents\\NetBeansProjects\\fop_alphabet_2048\\score7x7.dat"));  
            for(int i=0; i<10; i++){
                out.writeUTF(" ");  //name
                out.writeInt(0);  //score
            }
                out.close();
                System.out.println("Successfully write to the binary file");
            
        }catch(IOException e){
            System.out.println("Problem with file output");
        }

   }
}

//////////
//3x3
//3x4
//3x5
//
//4x4
//4x5
//4x6
//
//5x5
//5x6
//5x7
//
//6x6
//6x7
//6x8
//
//7x7
//7x8
//
//8x8
