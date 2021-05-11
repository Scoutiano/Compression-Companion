/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman;

/**
 *
 * @author Mohanad
 */
public class Code {
    String prefix;
    byte value;
    
    public Code(String prefix,byte value){
        this.prefix = prefix;
        this.value = value;
        
    }
    
    public String toString(){
        return prefix + " " + (value & 0xff);
    }
}
