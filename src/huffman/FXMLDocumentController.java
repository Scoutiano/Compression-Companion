/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Mohanad
 */
public class FXMLDocumentController implements Initializable {

    //Declare controls for FXML document
    @FXML
    TextField filePathTF = new TextField();
    @FXML
    Button browseBtn = new Button();
    @FXML
    Button compressBtn = new Button();
    @FXML
    Button decompressBtn = new Button();
    @FXML
    Button headerBtn = new Button();
    @FXML
    Button statsBtn = new Button();
    @FXML
    TextArea infoTA = new TextArea();

    //List used to display stats
    static Node[] frequencyListSave;

    String headerSave = "";
    
    String extensionSave;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            //filePathTF is only accessable through the browse button
            filePathTF.setEditable(false);

            //infoTA is to track progress
            infoTA.setEditable(false);

            //Initial state for buttons
            decompressBtn.setDisable(true);
            headerBtn.setDisable(true);
            statsBtn.setDisable(true);

        } catch (Exception e) {
            System.out.println("File not found");
        }
    }

    //Display header
    public void headerBtnAction() {
        infoTA.appendText("Header structure: \nSignature + Extension + . +  3 Bits for last byte + 13 Bits for tree size in bits + Encoded Tree\n");
        infoTA.appendText("Header: \n" + headerSave + "\n");
        infoTA.appendText("---------------------------------\n");
        infoTA.setScrollLeft(0);
        infoTA.setScrollTop(0);
    }

    //Display node frequencies for last compressed file
    public void statsBtnAction() {

        //Set measurements for stage
        double cellWidth = 80;
        double cellHeight = 40;
        double height = (cellHeight * 2) + 56;
        double width = (cellWidth * frequencyListSave.length) + 55;
        if (frequencyListSave.length > 10) {
            width = (cellWidth * 10);
        }

        //Setting up panes
        ScrollPane sp = new ScrollPane();
        GridPane gp = new GridPane();

        sp.getStylesheets().add("huffman/Style.css");

        //Centering component alignment within cells
        gp.setStyle("-fx-background-color: #282828;");
        gp.setGridLinesVisible(true);
        GridPane.setHalignment(gp, HPos.CENTER);
        GridPane.setValignment(gp, VPos.CENTER);

        sp.setContent(gp);

        //Setting up stage and scene
        Scene scene = new Scene(sp);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setScene(scene);

        //Stage size
        stage.setHeight(height);
        stage.setWidth(width);

        //Set up rows and columns for GridPane
        ColumnConstraints[] gpColumn = new ColumnConstraints[frequencyListSave.length + 1];
        RowConstraints[] gpRow = new RowConstraints[2];

        for (int i = 0; i < 2; i++) {
            gpRow[i] = new RowConstraints();
            gpRow[i].setValignment(VPos.BOTTOM);
            gpRow[i].setMaxHeight(cellHeight);
            gpRow[i].setMinHeight(cellHeight);
            gpRow[i].setPrefHeight(cellHeight);
            gp.getRowConstraints().add(gpRow[i]);
        }

        for (int i = 0; i < gpColumn.length; i++) {
            gpColumn[i] = new ColumnConstraints();
            gpColumn[i].setHalignment(HPos.CENTER);
            gpColumn[i].setMaxWidth(cellWidth);
            gpColumn[i].setMinWidth(cellWidth);
            gpColumn[i].setPrefWidth(cellWidth);
            gp.getColumnConstraints().add(gpColumn[i]);
        }

        //Set up components to fill GridPane
        Label[] byteValueLbl = new Label[frequencyListSave.length + 1];
        Label[] frequencyLbl = new Label[frequencyListSave.length + 1];

        //Labels for each row
        byteValueLbl[0] = new Label("Byte");
        frequencyLbl[0] = new Label("Freq");
        byteValueLbl[0].setTextFill(Color.web("#006600"));
        frequencyLbl[0].setTextFill(Color.web("#006600"));
        GridPane.setConstraints(byteValueLbl[0], 0, 0);
        GridPane.setConstraints(frequencyLbl[0], 0, 1);
        gp.getChildren().addAll(byteValueLbl[0], frequencyLbl[0]);

        //Labels for each cell and their properties
        for (int i = 0; i < frequencyListSave.length; i++) {
            byteValueLbl[i + 1] = new Label("" + (int) (frequencyListSave[i].value & 0xff));
            frequencyLbl[i + 1] = new Label("" + frequencyListSave[i].freq);
            byteValueLbl[i + 1].setTextFill(Color.web("#006600"));
            frequencyLbl[i + 1].setTextFill(Color.web("#006600"));
            GridPane.setConstraints(byteValueLbl[i + 1], i + 1, 0);
            GridPane.setConstraints(frequencyLbl[i + 1], i + 1, 1);
            gp.getChildren().addAll(byteValueLbl[i + 1], frequencyLbl[i + 1]);
        }

        stage.getIcons().add(new Image("huffman/resources/images/icon.png"));
        stage.setTitle("Byte frequencies");
        stage.show();
    }
    
    public void browseBtnAction() {

        //Choose file
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);

        //Check if file is null
        if (selectedFile == null) {
            return;
        }

        //Get extension
        String selectedFilePath = selectedFile.getAbsolutePath();
        String extension = selectedFilePath.substring(selectedFilePath.lastIndexOf("."));

        //In case of extension being .huff, enable decompressBtn, else disable it
        if (extension.compareTo(".huff") == 0) {
            decompressBtn.setDisable(false);
        } else {
            decompressBtn.setDisable(true);
        }

        //Add file path into filePathTF
        filePathTF.setText(selectedFile.getAbsolutePath());
    }

    public void compressBtnAction() throws IOException {

        compressBtn.setDisable(true);
        browseBtn.setDisable(true);
        
        //Get filePath from TextField
        String filePath = filePathTF.getText();
        
        //Check if TextField is empty, if empty return
        if (filePath.compareTo("") == 0) {
            compressBtn.setDisable(false);
            browseBtn.setDisable(false);
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File does not exist");
            infoTA.setText(infoTA.getText() + "File does not exist\n");
            compressBtn.setDisable(false);
            browseBtn.setDisable(false);
            return;
        }

        Path path = Paths.get(filePath);
        if (Files.size(path) == 0) {
            System.out.println("File is too small");
            infoTA.setText(infoTA.getText() + "File is empty!\n");
            compressBtn.setDisable(false);
            browseBtn.setDisable(false);
            return;
        }

        //Choose directory
        DirectoryChooser dc = new DirectoryChooser();
        File selectedDirectory = dc.showDialog(null);

        //Check if directory was chosen
        if (selectedDirectory == null) {
            System.out.println("No directory chosen");
            compressBtn.setDisable(false);
            browseBtn.setDisable(false);
            return;
        }

        String fileName = file.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        String directoryPath = selectedDirectory.getAbsolutePath();

        //Get specific file path
        String destination = directoryPath + "\\" + fileName + ".huff";

        //Destination file must be a new file
        File destinationFile = new File(destination);
        for (int i = 1; destinationFile.exists(); i++) {
            destination = directoryPath + "\\" + fileName + " (" + i + ")" + ".huff";
            destinationFile = new File(destination);
        }
        infoTA.setText("");
        infoTA.appendText("---------------------------------\n");
        infoTA.appendText("Compressing...\n" + "From: \n" + filePathTF.getText()
                + "\nTo: \n" + destination + "\n");
        infoTA.appendText("Finding byte frequencies...\n");
        //Find byte frequencies
        int[] frequency = findByteFrequencies(filePathTF.getText());

        //Trim down byte frequencies to the used ones and move them into nodes
        Node[] freq = trimFrequency(frequency);

        frequencyListSave = new Node[freq.length];
        for (int i = 0; i < frequencyListSave.length; i++) {
            frequencyListSave[i] = new Node(freq[i].freq, freq[i].value, freq[i].isUsed);
        }

        infoTA.appendText("Building tree...\n");
        //Build tree from trimmed frequencies
        Node tree = buildHuffmanTree(freq);

        infoTA.appendText("Getting codes...\n");
        
        //Get list of codes out of tree
        LinkedList<Code> codeList;
        codeList = getCodes(tree);

        //Calculate expected number of bits from frequency list and code list
        long expBits = calculateBits(frequency, codeList);

        infoTA.appendText("Building header...\n");
        //Build header
        String header = buildHeader(expBits, tree, filePathTF.getText(), destination);

        infoTA.appendText("Writing file contents...\n");
        //Compress file contents and write header
        compressFile(codeList, filePathTF.getText(), destination, expBits, header);

        compressBtn.setDisable(false);
        statsBtn.setDisable(false);
        headerBtn.setDisable(false);
        browseBtn.setDisable(false);

        infoTA.appendText("Done!\n");
        infoTA.appendText("Size before: " + file.length() + " bytes\nSize after: " + destinationFile.length() + " bytes\n");
        infoTA.appendText("---------------------------------\n");
        infoTA.setScrollLeft(0);
        infoTA.setScrollTop(0);
    }

    public void decompressBtnAction() throws IOException {
        
        compressBtn.setDisable(true);
        statsBtn.setDisable(true);
        headerBtn.setDisable(true);
        decompressBtn.setDisable(true);
        browseBtn.setDisable(true);
        
        //Get file to be decompressed
        String fromFilePath = filePathTF.getText();

        //Check if file exists(could be deleted after selection)
        File selectedFile = new File(fromFilePath);
        if (!selectedFile.exists()) {
            System.out.println("File does not exist");
            if(headerSave.compareTo("") != 0){
                statsBtn.setDisable(false);
                headerBtn.setDisable(false);
            }
            compressBtn.setDisable(false);
            decompressBtn.setDisable(false);
            browseBtn.setDisable(false);
            return;
        }

        String fileName = selectedFile.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf(".") + 1);

        //Select directory
        DirectoryChooser dc = new DirectoryChooser();
        File selectedDirectory = dc.showDialog(null);

        //Check if directory was chosen
        if (selectedDirectory == null) {
            System.out.println("No directory chosen");
            if(headerSave.compareTo("") != 0){
                statsBtn.setDisable(false);
                headerBtn.setDisable(false);
            }
            compressBtn.setDisable(false);
            decompressBtn.setDisable(false);
            browseBtn.setDisable(false);
            return;
        }

        if (!selectedDirectory.exists()) {
            System.out.println("Directory does not exist");
            if(headerSave.compareTo("") != 0){
                statsBtn.setDisable(false);
                headerBtn.setDisable(false);
            }
            compressBtn.setDisable(false);
            decompressBtn.setDisable(false);
            browseBtn.setDisable(false);
            return;
        }
        
        String toDirectoryPath = selectedDirectory.getAbsolutePath() + "\\" + fileName;
        
        
        
        try {
        decompressFile(fromFilePath, toDirectoryPath);
        } catch (IllegalArgumentException e){
            alert();
            if(headerSave.compareTo("") != 0){
                statsBtn.setDisable(false);
                headerBtn.setDisable(false);
            }
            compressBtn.setDisable(false);
            decompressBtn.setDisable(false);
            browseBtn.setDisable(false);
            return;
        }
        infoTA.setText("");
        infoTA.appendText("---------------------------------\n");
        infoTA.appendText("Decompressing...\n" + "From: \n" + filePathTF.getText()
                + "\nTo: \n" + toDirectoryPath + extensionSave + "\n");
        
        infoTA.appendText("Done!\n");
        File before = new File(filePathTF.getText());
        File after = new File(toDirectoryPath + extensionSave);
        
        infoTA.appendText("Size before: " + before.length() + " bytes\nSize after: " + after.length() + " bytes\n");
        infoTA.appendText("---------------------------------\n");
        
        decompressBtn.setDisable(false);
        compressBtn.setDisable(false);
        browseBtn.setDisable(false);
    }

    //Mehtod to count the byte frequencies in given file
    public int[] findByteFrequencies(String filePath) throws IOException {

        int[] frequency = new int[256];         //Frequncy array to count the frequency of each byte
        byte[] stream = new byte[32];           //Byte array to take in a number of bytes at once from the InputStream

        //Use given file path to initialize InputStream
        InputStream input = new FileInputStream(filePath);

        //Keep track of the number of availible bytes
        long avBytes = input.available();

        while (input.available() > 0) {

            //Fills stream up with new bytes
            input.read(stream);

            //Goes over bytes and adds up their frequencies
            for (int i = 0; i < stream.length; i++) {

                //Check if end of file has been reached
                if (avBytes == 0) {
                    break;
                }

                //Increase the frequency of the unsigned version of the byte found
                frequency[stream[i] & 0xff]++;

                avBytes--;
            }
        }
        input.close();
        return frequency;
    }

    //Trims down the unused frequencies
    public Node[] trimFrequency(int[] frequency) {

        //List of Nodes that are used
        LinkedList<Node> frequencyList = new LinkedList<>();

        //Iterate over frequency array to check for used bytes and their frequencies
        for (int i = 0; i < frequency.length; i++) {
            if (frequency[i] > 0) {
                frequencyList.add(new Node(frequency[i], i, true));
            }
        }

        //Turn list into array
        Node[] nodeFrequency = new Node[frequencyList.size()];
        for (int i = 0; i < frequencyList.size(); i++) {
            nodeFrequency[i] = frequencyList.get(i);
        }

        return nodeFrequency;
    }

    //Build Huffman tree using the resulted frequency array in trimFrequency
    public Node buildHuffmanTree(Node[] frequency) {

        //Build Heap from the frequency array
        Heap freq = new Heap(frequency, frequency.length, frequency.length);

        //Iterate over the heap
        for (int i = 0; i < frequency.length; i++) {

            //Remove and save 2 min nodes from min-heap
            Node x = freq.deleteMin();
            Node y = freq.deleteMin();

            //Combine them into a subtree
            Node newNode = new Node(x.freq + y.freq, -1, false);
            if (y.freq != 0) {
                newNode.left = y;
            }
            newNode.right = x;

            //Add the subtree to the heap
            freq.add(newNode);
        }

        return freq.heap[0];
        
    }

    //Traverse huffman tree in order to get codes
    public void traverseHuffman(LinkedList<Code> codes, Node node, String prefix) {

        //If current node is null, return
        if (node == null) {
            return;
        }

        //Post order: left, right, root
        traverseHuffman(codes, node.left, prefix + "0");

        traverseHuffman(codes, node.right, prefix + "1");

        //Nodes with value -1 are not used
        if (node.isUsed) {

            //Add a code node with the current node value and the prefix
            codes.add(new Code(prefix, node.value));
        }
    }

    //Gets codes from huffman tree by recursively calling traverseHuffman function
    public LinkedList<Code> getCodes(Node root) {
        LinkedList<Code> code = new LinkedList<>();
        if (root == null) {
            return null;
        }
        
        //Irrelevant mpty node -> actual first node
        if(root.right.isUsed){
            code.add(new Code("0",root.right.value));
            return code;
        }
        
        traverseHuffman(code, root.left, "");
        traverseHuffman(code, root.right, "");
        return code;
    }

    //Gets codes from huffman tree by recursively calling traverseHuffman function, used in decompression
    public LinkedList<Code> getCodes2(Node root) {
        LinkedList<Code> code = new LinkedList<>();
        if (root == null) {
            return null;
        }
        
        if(root.isUsed){
            code.add(new Code("0",root.value));
            return code;
        }
        
        traverseHuffman(code, root.left, "0");
        traverseHuffman(code, root.right, "1");
        return code;
    }

    public void compressFile(LinkedList<Code> codeList, String filePath, String destination, long expBits, String header) throws FileNotFoundException, IOException {
        System.out.println("Codes: " + codeList);
        //Create array of codes where each index corresponds to the byte it represents
        Code[] codes = new Code[256];
        for (int i = 0; i < codeList.size(); i++) {
            codes[codeList.get(i).value & 0xff] = codeList.get(i);
        }

        int currentBit = 0;

        byte[] streamIn = new byte[32];           //Byte array to take in a number of bytes at once from the InputStream
        byte[] streamOut = new byte[16];          //Byte array to output the result of compression to the OutputStream

        //Use given file path to initialize InputStream
        InputStream input = new FileInputStream(filePath);

        //Create new file as a destination of the OutputStream with extension .huff
        OutputStream output = new FileOutputStream(destination, true);

        //Write header into file
        Wrap res = writeBinaryString(header, output, streamOut, currentBit, 0, 0);
        output = res.output;
        streamOut = res.streamOut;
        currentBit = res.currentBit;

        //Keep track of the number of availible bytes
        int avBytes = input.available();

        while (input.available() > 0) {

            input.read(streamIn);

            for (int i = 0; i < streamIn.length; i++) {
                avBytes--;

                String current = codes[streamIn[i] & 0xff].prefix;

                res = writeBinaryString(current, output, streamOut, currentBit, avBytes, expBits);
                output = res.output;
                streamOut = res.streamOut;
                currentBit = res.currentBit;
                avBytes = res.avBytes;
                expBits = res.expBits;

                //Write last buffer if it hasn't been written already
                if (avBytes == 0 && currentBit != 1) {
                    int size;
                    if (currentBit % 8 != 0) {

                        size = (int) ((currentBit / 8) + 1);
                    } else {
                        size = (int) (currentBit / 8);
                    }
                    byte[] lastStreamOut = new byte[size];
                    for (int k = 0; k < size; k++) {
                        lastStreamOut[k] = streamOut[k];
                    }
                    output.write(lastStreamOut);
                    break;
                }
            }
        }

        input.close();
        output.flush();
        output.close();
    }

    //Utility method to find the expected number of bits
    public long calculateBits(int[] frequency, LinkedList<Code> codes) {

        //Add up the frequencies * code size
        long result = 0;
        for (int i = 0; i < codes.size(); i++) {
            result = result + (frequency[codes.get(i).value & 0xff] * codes.get(i).prefix.length());
        }

        return result;
    }

    public String buildHeader(long expBits, Node tree, String filePath, String destination) throws IOException {

        //Find file type
        String fileType = filePath.substring(filePath.lastIndexOf('.') + 1, filePath.length());

        //Create file to write into
        File file = new File(destination);

        //Add file type to the header in full form followed by . to denote end of file type
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("CC!" + fileType + ".");   //Change here 
        bw.flush();
        bw.close();

        //Encode tree to string of bits
        String encodedTree = encodeTree(tree, "");

        //Encode treeSize to string of bits
        int treeBitSize = encodedTree.length();
        
        String treeSize = "";
        for (int i = 0; i < 13; i++) {
            int andWith = 1 << i;
            int equals = (int) (andWith & (treeBitSize));
            if (equals == andWith) {
                treeSize = "1" + treeSize;
            } else {
                treeSize = "0" + treeSize;
            }
        }

        //Calculate and encode number of bits in last byte to a string of bits
        int lastByteBits = ((int) ((expBits % 8) + treeBitSize) % 8) % 8;
        if (lastByteBits == 0) {
            lastByteBits = 8;
        }
        String bits = "";
        for (int i = 0; i < 3; i++) {
            int andWith = 1 << i;
            int equals = (int) (andWith & (lastByteBits));
            if (equals == andWith) {
                bits = "1" + bits;
            } else {
                bits = "0" + bits;
            }
        }
        
        //Combine header 3bits + 13bits + encodedTree;
        String header = bits + treeSize + encodedTree;
        headerSave = "CC!" + fileType + "." + header;
        
        return header;

    }

    //Encode huffman tree to print as bits into file
    public String encodeTree(Node node, String encodedTree) {

        //End case
        if (node == null) {
            return "";
        }

        //Check if node is leaf node, for leaf nodes insert 1-bit + full byte representation of the reached value
        if (node.left == null && node.right == null) {

            //Get full representation of the byte value
            String character = "";
            for (int i = 0; i < 8; i++) {
                int andWith = 1 << i;
                int equals = (int) (andWith & (node.value & 0xff));
                if (equals == andWith) {
                    character = "1" + character;
                } else {
                    character = "0" + character;
                }
            }

            encodedTree = encodedTree + "1" + character;
        } else {
            //If not leaf node add 0 to continue along tree and call the function recursively
            encodedTree = encodedTree + "0";
            encodedTree = encodeTree(node.left, encodedTree);
            encodedTree = encodeTree(node.right, encodedTree);
        }

        return encodedTree;
    }

    //Method to decompressFile from given location to given location
    public void decompressFile(String fromPath, String toPath) throws FileNotFoundException, IOException {

        //Input and output buffers to read from file and write into destination
        byte[] streamIn = new byte[16];
        byte[] streamOut = new byte[32];

        //Use given file path to initialize InputStream
        InputStream input = new FileInputStream(fromPath);

        //Keep track of the amount of bytes remaining in the file
        long avBytes = input.available();
        input.read(streamIn);

        //Declare extension string
        String extension = "";

        //Read signature
        String signature = "";
        for (int i = 0; i < 3; i++) { //CHANGE HERE
            signature += (char) (streamIn[i] & 0xff);
        }

        if (signature.compareTo("CC!") != 0) {   //CHANGE HERE
            System.out.println("Invalid header");
            throw new IllegalArgumentException("File has an invalid header!");
        }

        avBytes = avBytes - 3;

        //Variable to keep track of which bit the decompression is at
        int currentBit = 24; //Change here int currentBit = 24

        //Get extension, end of extension is denoted by '.'
        while (streamIn[currentBit / 8] != '.') {

            //Extension could be larger than the buffer, read more in such case
            if ((currentBit) / 8 == streamIn.length - 1) {
                input.read(streamIn);
                currentBit = -1;
            }

            //End of extension
            if (streamIn[currentBit / 8] == '.') {
                break;
            }

            //Add byte to extension until '.' is reached
            extension = extension + (char) streamIn[currentBit / 8];

            //Increment currentBit
            currentBit = currentBit + 8;

            //Decrement avBytes
            avBytes--;
        }
        
        extensionSave = extension;

        //8 bits for '.' character
        currentBit++;
        currentBit = currentBit + 8;

        //In case the buffer was all read, read more bytes into it
        if ((currentBit) / 8 == streamIn.length) {
            input.read(streamIn);
            currentBit = 0;
        }

        //'.' character byte
        avBytes--;

        //Create new file as a destination of the OutputStream with extension found in .huff header
        OutputStream output = new FileOutputStream(toPath + extension);

        //Find the number of bits in the last byte
        String byte1 = String.format("%8s", Integer.toBinaryString(streamIn[currentBit / 8] & 0xFF)).replace(' ', '0');
        String lastByteBitsBi = byte1.substring(0, 3);
        int lastByteBits = binaryToInteger(lastByteBitsBi);

        //Special case for bits in last byte, having 0 bits is the same as having a full last byte
        if (lastByteBits == 0) {
            lastByteBits = 8;
        }

        //Increment currentBit
        currentBit = currentBit + 8;

        //Read in case buffer ran out
        if ((currentBit) / 8 == streamIn.length) {
            input.read(streamIn);
            currentBit = 0;
        }

        //Find tree size in bits
        String treeSizeBi = byte1.substring(3, 8);
        treeSizeBi += String.format("%8s", Integer.toBinaryString(streamIn[(currentBit / 8)] & 0xFF)).replace(' ', '0');
        int treeSize = binaryToInteger(treeSizeBi);

        //Decrement avBytes, increment currentBit
        avBytes = avBytes - 2;
        currentBit = currentBit + 7;

        String encodedTree = "";
        for (int i = 0; i < treeSize; i++) {

            if (currentBit / 8 == streamIn.length) {
                input.read(streamIn);
                currentBit = 0;
            }

            char bit = getBit(streamIn, currentBit);
            encodedTree += bit;
            currentBit++;

            if (currentBit % 8 == 0) {
                avBytes--;
            }
        }

        int lastTreeByteBits = treeSize % 8;

        //Calculate remaining bits in file
        long bitsInFile = (8 - lastTreeByteBits) + ((avBytes - 2) * 8) + lastByteBits;

        //Build tree from header
        Node tree = decodeTree(encodedTree, 0);

        //Get codes for display purposes
        LinkedList<Code> codes = getCodes2(tree);

        //Count to for current byte in streamOut
        int count = 0;

        //Use tree to decode file contents
        Node currentNode = tree;
        
        int i = 0;
        
        for (;tree.isUsed && i < bitsInFile;i++) {
            if (tree.isUsed) {
                streamOut[count] = tree.value;
                count++;
                if (currentBit / 8 == streamIn.length) {
                    input.read(streamIn);
                    currentBit = 0;
                }

            }
        }

        for (; i < bitsInFile; i++) {



                //Read bit and increment currentBit
                char bit = getBit(streamIn, currentBit);
                currentBit++;

                //if the stream in buffer is done, read more
                if (currentBit / 8 == streamIn.length) {
                    input.read(streamIn);
                    currentBit = 0;
                }

                //Two cases: '0' and '1'
                if (bit == '0') {
                    //if bit is 0 go to the left 
                    currentNode = currentNode.left;

                    //Check if node is leaf node, if leaf node, write its value into file
                    if (currentNode.isUsed) {

                        streamOut[count] = currentNode.value;
                        currentNode = tree;
                        count++;
                    }

                } else {
                    //if bit is 1 go to the right
                    currentNode = currentNode.right;

                    //Check if node is leaf node, if leaf node, write its value into file
                    if (currentNode.isUsed) {
                        streamOut[count] = currentNode.value;
                        currentNode = tree;
                        count++;
                    }
                }
            
            //If out buffer is full, write and empty it
            if (count == streamOut.length) {
                output.write(streamOut);
                for (int j = 0; j < streamOut.length; j++) {
                    streamOut[j] = (byte)0;
                }
                count = 0;
            }
        }

        //Write any left over bytes in streamOut
        if (count != 0) {
            byte[] leftOverOut = new byte[count];
            for (int k = 0; k < count; k++) {
                leftOverOut[k] = streamOut[k];
            }
            output.write(leftOverOut);
        }

        //Flush and close input and output streams
        input.close();
        output.flush();
        output.close();
    }

//Method to get specific bit in byte array
    public char getBit(byte[] streamIn, int currentBit) {

        int position = currentBit % 8;
        int currentByteN = currentBit / 8;
        byte currentByte = streamIn[currentByteN];
        int bit = currentByte >> (8 - (position + 1)) & 0x0001;
        if (bit == 1) {
            return '1';
        }
        return '0';
    }

    public int binaryToInteger(String s) {
        return Integer.parseInt(s, 2);
    }

    int counter = 0;

    //Method to write binary strings into a file
    public Wrap writeBinaryString(String s, OutputStream output, byte[] streamOut, int currentBit, int avBytes, long expBits) throws IOException {

        int currentBitStart = currentBit;

        //Iterate over the entered string
        for (int i = currentBitStart; i < s.length() + currentBitStart; i++) {

            //If the buffer is full, write and reset it
            if ((currentBit) / 8 == streamOut.length) {
                output.write(streamOut);

                //Clear out buffer
                for (int j = 0; j < streamOut.length; j++) {
                    streamOut[j] = 0;
                }

                currentBit = 0;
            }

            //Add to byte bit by bit
            char c = s.charAt(i - currentBitStart);
            if (c == '1') {
                streamOut[currentBit / 8] |= 0x80 >> (i & 0x7);
            }

            //Increment currentBit and decrement expBits
            currentBit++;
            expBits--;

        }

        //Return Wrap class containing all changed variables
        return new Wrap(s, output, streamOut, currentBit, avBytes, expBits);
    }

    //Method to decode tree from string
    public Node decodeTree(String s, int index) {

        //1-bit is found, add a leaf node, in this case index is saved in the freq variable for convenience 
        if (s.charAt(index) == '1') {
            return new Node(null, null, (byte) binaryToInteger(s.substring(index + 1, index + 9)), true, index + 9);
        } else {
            //Left first then update index, then right then update index
            Node left = decodeTree(s, index + 1);
            index = (int) left.freq;
            Node right = decodeTree(s, index);
            index = (int) right.freq;

            return new Node(left, right, (byte) 0, false, index);
        }
    }
    
    int cheapCount = 0;
    @FXML
    Stage alertStage = new Stage();
    @FXML
    Button okBtn = new Button();
    public void alert() throws IOException {

        if (cheapCount == 0) {
            AnchorPane alertAP = (AnchorPane) FXMLLoader.load(getClass().getResource("resources/fxml/AlertFXML.fxml"));
            Scene scene = new Scene(alertAP);
            alertStage.setScene(scene);
            alertStage.setTitle("Invalid Header!");
            alertStage.getIcons().add(new Image(Huffman.class.getResourceAsStream("resources/images/Notification.png")));
            alertStage.setResizable(false);
            alertStage.initModality(Modality.APPLICATION_MODAL);
            cheapCount = 1;
        }
        alertStage.show();
    }
    
    public void okBtnAction() {
        ((Stage) (okBtn.getScene().getWindow())).hide();
        cheapCount = 1;
    }
}
