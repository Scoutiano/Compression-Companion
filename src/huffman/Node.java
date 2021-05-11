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
public class Node implements Comparable<Node>{
    Node left;
    Node right;
    int freq;
    byte value;
    boolean isUsed;
    
    //Default constructor
    public Node(){
        
    }
        
    //Non-default constructor
    public Node(Node left, Node right, byte value, boolean isUsed,int freq){
        this.left = left;
        this.right = right;
        this.value = value;
        this.isUsed = isUsed;
        this.freq = freq;
    }
    
    //Non-default constructor
    public Node(Node left, Node right, int freq){
        this.left = left;
        this.right = right;
        this.freq = freq;
    }
    
    //Non-default constructor
    public Node(int freq, int value, boolean isUsed){
        this.value = (byte)value;
        this.freq = freq;
        this.isUsed = isUsed;
    }
    
    //Turn node into string
    @Override
    public String toString(){
        return this.freq + " " + this.value + " " + isUsed +"\n";
    }

    @Override
    public int compareTo(Node o) {
        if(freq > o.freq){
            return 1;
        }
        return -1;
    }
}
