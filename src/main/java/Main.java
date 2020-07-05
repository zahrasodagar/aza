import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

import static java.awt.Color.*;
import static javafx.scene.paint.Color.gray;


public class Main extends Application {
    static String path=System.getProperty("user.dir")+"\\input.txt"; //default
    Stage window;
    Scene mainScene;

    public static void main(String[] args){
        launch(args);
        //Simulator.simulateFile();
    }

    @Override
    public void start(Stage stage) throws Exception {
        window = stage;
        VBox layout = new VBox();
        mainScene = new Scene(layout,800,600);

        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu helpMenu = new Menu("Help");

        MenuItem newMenu = new MenuItem("New");
        MenuItem openMenu = new MenuItem("Open");
        Menu openRecentMenu = new Menu("Open Recent");
        MenuItem saveMenu = new MenuItem("Save");
        MenuItem reloadFileMenu = new MenuItem("Reload File");
        MenuItem exitMenu = new MenuItem("Exit");
        MenuItem addElementMenu = new MenuItem("Add Element");

        addMenuItems(fileMenu,newMenu,openMenu,openRecentMenu,saveMenu,reloadFileMenu,exitMenu);
        addMenuItems(editMenu,addElementMenu);
        addMenus(menuBar,fileMenu,editMenu,helpMenu);
        layout.getChildren().addAll(menuBar);

        /////////// add toolbar here

        HBox mainHBox= new HBox();
        mainHBox.setBackground(new Background(new BackgroundFill(gray(0.865), CornerRadii.EMPTY, Insets.EMPTY)));
        mainHBox.setPadding(new Insets(5,5,5,5));
        layout.getChildren().add(mainHBox);

        TextArea inputTextArea = new TextArea();
        updateTextArea(inputTextArea);
        inputTextArea.setMinHeight(560);


        VBox circuitAndData= new VBox();
        circuitAndData.setBackground(new Background(new BackgroundFill(gray(0.5), CornerRadii.EMPTY, Insets.EMPTY)));
        mainHBox.getChildren().addAll(inputTextArea,circuitAndData);

        //Listeners
        newMenu.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            File selectedDirectory = directoryChooser.showDialog(stage);
            if (selectedDirectory != null) {
                //System.out.println(selectedDirectory.getPath());
                String newFilePath = selectedDirectory.getPath();

                Stage window= new Stage();
                window.initModality(Modality.APPLICATION_MODAL);
                window.setTitle("New Project");
                window.setMinWidth(250);
                VBox layout1= new VBox();
                layout1.setPadding(new Insets(10,50,10,50));


                Label label=new Label("Enter your project's name");
                Button create=new Button("Create");
                Button cancel=new Button("Cancel");
                TextField textField=new TextField();
                textField.setPromptText("project's name");
                create.setOnAction(event1 -> {
                    if (!textField.getText().isEmpty()){
                        String newPath=newFilePath+"\\"+textField.getText()+".txt";
                        File newFile= new File(newPath);
                        try {
                            newFile.createNewFile();
                            path=newPath;
                            //System.out.println(path);
                            try {
                                updateTextArea(inputTextArea);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            window.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                cancel.setOnAction(event1 -> window.close());
                layout1.setAlignment(Pos.CENTER);
                HBox buttons= new HBox(create,cancel);
                buttons.setAlignment(Pos.CENTER);
                layout1.setSpacing(9);
                buttons.setSpacing(14);
                layout1.getChildren().addAll(label,textField,buttons);

                Scene scene=new Scene(layout1);
                window.setScene(scene);
                window.show();
            }
        });
        openMenu.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt")
            );
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            File file = fileChooser.showOpenDialog(window);
            if (file != null) {
                path=file.getPath();
                //System.out.println(path);
                try {
                    updateTextArea(inputTextArea);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        reloadFileMenu.setOnAction(event -> {
            try {
                updateTextArea(inputTextArea);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        saveMenu.setOnAction(event -> {
            try {
                saveFile(inputTextArea.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        exitMenu.setOnAction(event -> {
            Stage window= new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("New Project");
            window.setMinWidth(250);
            VBox layout1= new VBox();
            layout1.setPadding(new Insets(10,50,10,50));


            Label label=new Label("Are you sure you want to exit AZA?");
            Button yes=new Button("Yes");
            Button cancel=new Button("No");

            yes.setOnAction(event1 -> {
                // TODO: 20/07/05 Ask to save before exit
                System.exit(0);
            });
            cancel.setOnAction(event1 -> window.close());
            layout1.setAlignment(Pos.CENTER);
            HBox buttons= new HBox(yes,cancel);
            buttons.setAlignment(Pos.CENTER);
            layout1.setSpacing(9);
            buttons.setSpacing(14);
            layout1.getChildren().addAll(label,buttons);

            Scene scene=new Scene(layout1);
            window.setScene(scene);
            window.show();
        });
        addElementMenu.setOnAction(event -> {
            addElementDialogue();
        });

        window.setScene(mainScene);
        window.setTitle("input.txt Simulation");
        window.show();
    }

    public static void addMenuItems(Menu menu, MenuItem ... items){
        for (MenuItem item: items){
            menu.getItems().add(item);
        }
    }

    public static void addMenus(MenuBar menuBar, Menu ... menus){
        for (Menu menu: menus){
            menuBar.getMenus().add(menu);
        }
    }

    public static void updateTextArea(TextArea textArea) throws FileNotFoundException {
        StringBuilder text= new StringBuilder();
        File file = new File(path);
        Scanner scanner=new Scanner(file);
        while (scanner.hasNextLine()){
            text.append(scanner.nextLine()).append("\n");
        }
        textArea.setText(text.toString());

    }

    public static void saveFile(String text) throws IOException {
        File dataFile=new File(path);
        FileWriter fw=new FileWriter(dataFile);
        BufferedWriter writer = new BufferedWriter(fw);
        String[] lines=text.split("\n");
        for (String line:lines){
            writer.write(line);
            writer.newLine();
        }
        writer.close();
        fw.close();
    }

    public static void addElementDialogue(){
        Element element=null;

        Stage window= new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add Element");
        window.setMinWidth(250);
        VBox layout1= new VBox();
        layout1.setPadding(new Insets(10,50,10,50));


        Label label=new Label("Select the type of element");
        ComboBox comboBox=new ComboBox();
        comboBox.getItems().addAll("Resistor","Capacitor","Inductor","Diode 1","Diode 2",
                "VSource DC","VSource AC", "ISource DC","ISource AC",
                "ESource", "HSource", "GSource", "FSource");
        Button next=new Button("Next");
        Button cancel=new Button("Cancel");

        layout1.setAlignment(Pos.CENTER);
        HBox buttons= new HBox(next,cancel);
        buttons.setAlignment(Pos.CENTER);
        layout1.setSpacing(9);
        buttons.setSpacing(14);
        layout1.getChildren().addAll(label,comboBox,buttons);

        Scene scene1=new Scene(layout1);

        next.setOnAction(event1 -> {
            if (comboBox.getValue()!=null){
                Scene scene2=new Scene(getLayout((String) comboBox.getValue(),window));
                window.setScene(scene2);
            }
        });
        cancel.setOnAction(event1 -> window.close());

        window.setScene(scene1);
        window.show();


        addElement(element);
    }

    public static VBox getLayout(String type, Stage window){
        window.setTitle("New "+type);
        String line="";
        VBox layout= new VBox();
        layout.setPadding(new Insets(10,50,10,50));

        Label label=new Label("Enter the parameters");
        Button add=new Button("Add");
        Button cancel=new Button("Cancel");
        GridPane grid=new GridPane();
        layout.setAlignment(Pos.CENTER);
        HBox buttons= new HBox(add,cancel);
        buttons.setAlignment(Pos.CENTER);
        layout.setSpacing(9);
        buttons.setSpacing(14);
        layout.getChildren().addAll(label,grid);
        add.setOnAction(event -> {
            InputManager manager=InputManager.getInstance();
            manager.input=line;
            if (manager.checkInputFormat()) // TODO: 20/07/06 DIALOGUE!!!
                window.close();
        });
        cancel.setOnAction(event1 -> window.close());

        Label n=new Label("Node 1");
        TextField name=new TextField();

        Label n1=new Label("Node 1");
        TextField node1=new TextField();

        Label n2=new Label("Node 2");
        TextField node2=new TextField();

        Label val=new Label();
        TextField value=new TextField();

        grid.setHgap(10);
        grid.setVgap(7);

        grid.add(n,0,0);
        grid.add(name,1,0);
        grid.add(n1,0,1);
        grid.add(node1,1,1);
        grid.add(n2,0,2);
        grid.add(node2,1,2);






        if (type.equals("Resistor")){
            val.setText("Resistance");
            grid.add(val,0,3);
            grid.add(value,1,3);
        }
        if (type.equals("Capacitor")){
            val.setText("Capacity");
            grid.add(val,0,3);
            grid.add(value,1,3);
        }
        if (type.equals("Inductor")){
            val.setText("Inductance");
            grid.add(val,0,3);
            grid.add(value,1,3);
        }
        if (type.equals("Diode 1")){

        }
        if (type.equals("Diode 2")){

        }

        if (type.equals("VSource DC")){
            val.setText("Offset Value");
            grid.add(val,0,3);
            grid.add(value,1,3);

        }
        if (type.equals("VSource AC")){
            val.setText("Offset Value");
            grid.add(val,0,3);
            grid.add(value,1,3);

        }
        if (type.equals("ISource DC")){
            val.setText("Offset Value");
            grid.add(val,0,3);
            grid.add(value,1,3);
        }
        if (type.equals("ISource AC")){
            val.setText("Offset Value");
            grid.add(val,0,3);
            grid.add(value,1,3);

        }

        if (type.equals("ESource")){

        }
        if (type.equals("HSource")){

        }

        if (type.equals("GSource")){

        }
        if (type.equals("FSource")){

        }
        layout.getChildren().addAll(buttons);


        return layout;
    }

    public static void addElement(Element element){

    }

}
