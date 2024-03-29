/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Mohanad
 */
public class Huffman extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("resources/fxml/FXMLDocument.fxml"));

        Scene scene = new Scene(root);
        
        stage.setTitle("Compression Companion");
        stage.getIcons().add(new Image(Huffman.class.getResourceAsStream("resources/images/icon.png")));
        stage.setResizable(false);
        scene.getStylesheets().add("huffman/resources/css/Style.css");
        stage.setScene(scene);
        stage.show();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    
}
