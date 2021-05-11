/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 *
 * @author Mohanad
 */

//Wrapper class for return statement while writing into file
public class Wrap {
    String s;
    OutputStream output;
    byte[] streamOut;
    int currentBit;
    int avBytes;
    long expBits;
    
    public Wrap(String s, OutputStream output, byte[] streamOut, int currentBit, int avBytes, long expBits){
        this.s = s;
        this.output = output;
        this.streamOut = streamOut;
        this.currentBit = currentBit;
        this.avBytes = avBytes;
        this.expBits = expBits;
    }
}
