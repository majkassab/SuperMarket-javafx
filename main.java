/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package minitroy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;


public class main extends Application 
{
    
    Stage window;
    Scene loginScene, appScene, SignUpScene;
    String AccountName;
    TextField UserNameInput;
    PasswordField passwordInput;
    Button onButton;
    Button offButton;
    Button showButton;
    Button insertButton;
    Button updateButton;
    Button deleteButton;
    String DBurl       = "jdbc:mysql://127.0.0.1:3306/aulrdb";
    String DBuser      = "root";
    String DBpassword  = "";
    Connection conn;
    Statement stmt;
    Label StatusMessage;
    Pane CenterArea;
    TextField UserNameSignUpInput;
    PasswordField passwordSignUpInput;
    TextField nameSignUpInput;
    TextField familySignUpInput;
    DatePicker birthdaySignUpInput;
    TextField salarySignUpInput;
    ComboBox<String> roleSignUpInput;
    Label WelcomeNameApp;
    LocalDate today = LocalDate.now();
    
    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        AccessToSQLserver();

        // Login scene ---------------------------------------------------------
        Label UserNameLabel = new Label("Username:");
        UserNameInput = new TextField();
        Label passwordLabel = new Label("Password:");
        passwordInput = new PasswordField();
        Button loginButton = new Button("Log In");
        Button SignUpButton = new Button("Sign Up");
        loginButton.setOnAction(e -> 
        {   
            if (isValidCredentials()) 
            {
                window.setScene(appScene);
            } else {
                passwordInput.setText("");
                UserNameInput.setText("");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Credentials");
                alert.setHeaderText(null);
                alert.setContentText("The username and/or password entered is invalid.");
                alert.showAndWait();
            }
        });
        
        SignUpButton.setOnAction(e -> 
        {   
            window.setScene(SignUpScene);
            
        });

        StackPane loginpane = new StackPane();
        GridPane loginLayout = new GridPane();
        //loginLayout.setMaxSize(300, 150);
        loginLayout.setPadding(new Insets(10, 10, 10, 10));
        loginLayout.setVgap(8);
        loginLayout.setHgap(10);
        loginLayout.add(UserNameLabel, 0, 0);
        loginLayout.add(UserNameInput, 1, 0);
        loginLayout.add(passwordLabel, 0, 1);
        loginLayout.add(passwordInput, 1, 1);
        loginLayout.add(SignUpButton, 0, 2);
        loginLayout.add(loginButton, 1, 2);
        // Set the background color of the login layout to transparent
        loginLayout.setStyle("-fx-background-color:  #FFEBCD;");
        loginpane.getChildren().add(loginLayout);

        loginpane.setLayoutX(250);
        loginpane.setLayoutY(200);

        loginScene = new Scene(loginpane, 800, 600);
        loginScene.setFill(Color.BLANCHEDALMOND);
        // END Login scene -----------------------------------------------------
        
        // App scene -----------------------------------------------------------

        //Top Area      --------------------------------
        WelcomeNameApp = new Label("");
        //WelcomeNameApp.setText("Welcome "+UserNameInput+".");
        WelcomeNameApp.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        HBox TopArea = new HBox();
        TopArea.getChildren().addAll(WelcomeNameApp );
        TopArea.setAlignment(Pos.CENTER);
        TopArea.setSpacing(20);
        TopArea.setPadding(new Insets(10));
        TopArea.setStyle("-fx-background-color: #FADBD8; -fx-border-width: 0 0 5 0; -fx-border-color: #C0392B;");
        
        //Right Area    --------------------------------
        showButton = new Button("Show Product");
        showButton.setOnAction(event -> ShowMethod());
        
        insertButton = new Button("Insert Product");
        //insertButton.setDisable(true);
        insertButton.setOnAction(event -> InsertMethod());
        
        updateButton = new Button("Update Product");
        //updateButton.setDisable(true);
        updateButton.setOnAction(event -> UpdateMethod());
        
        deleteButton = new Button("Delete Product");
        //deleteButton.setDisable(true);
        deleteButton.setOnAction(event -> DeleteMethod());
        
        offButton = new Button("Sign Out");
        //offButton.setDisable(true);
        offButton.setOnAction(event -> SignOutMethod());
        
        VBox RightArea = new VBox(showButton, insertButton, updateButton, deleteButton, offButton );
        RightArea.setAlignment(Pos.CENTER);
        RightArea.setSpacing(20);
        RightArea.setPadding(new Insets(10));
        RightArea.setStyle("-fx-background-color: #F9E79F; -fx-border-width: 0 0 0 5; -fx-border-color: #F39C12;");
                
        //Bottom Area    --------------------------------
        HBox BottomArea = new HBox();
        HBox BottomAreaLeft = new HBox();
        Pane BottomAreaRight = new Pane();
        
        //Status
        Label StatusLabel = new Label("Status: ");
        StatusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        StatusMessage = new Label("Welcome");
        StatusMessage.setFont(Font.font(16));
        
        // Create a label to display the date and time
        Label dateTimeLabel = new Label();
        dateTimeLabel.setStyle("-fx-font-size: 16px;");

        // Create a Timeline to update the label every second
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String formattedDateTime = now.format(formatter);
            dateTimeLabel.setText(formattedDateTime);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        
        //BottomArea Style and Children
        BottomAreaLeft.getChildren().addAll(StatusLabel, StatusMessage);
        BottomAreaRight.getChildren().addAll(dateTimeLabel);
        BottomArea.getChildren().addAll(BottomAreaLeft, BottomAreaRight);
        BottomArea.setSpacing(460);
        BottomArea.setPadding(new Insets(10));
        BottomArea.setStyle("-fx-background-color: #D6EAF8; -fx-border-width: 5 0 0 0; -fx-border-color: #2980B9;");
        
        //Center Area    --------------------------------
        CenterArea = new Pane();
        CenterArea.setStyle("-fx-background-color: #E5E7E9;");
        
        //Set Layout    --------------------------------
        BorderPane appLayout = new BorderPane();
        appLayout.setTop(TopArea);
        appLayout.setRight(RightArea);
        appLayout.setBottom(BottomArea);
        appLayout.setCenter(CenterArea);
        
        
        
        appScene = new Scene(appLayout, 800, 600);
        // END App scene -------------------------------------------------------
        
        // SignUp scene --------------------------------------------------------
        
        Label testSignUplabel = new Label("Welcome to SignUp page");
        
       
        Label UserNameSignUpLabel = new Label("Username:");
        UserNameSignUpInput = new TextField();
        Label passwordSignUpLabel = new Label("Password:");
        passwordSignUpInput = new PasswordField();
        
        Label nameSignUpLabel = new Label("Name:");
        nameSignUpInput = new TextField();
        
        Label familySignUpLabel = new Label("Family:");
        familySignUpInput = new TextField();
        
        Label birthdaySignUpLabel = new Label("Birthday:");
        birthdaySignUpInput = new DatePicker();
        //Set Current date to today's date
        birthdaySignUpInput.setValue(LocalDate.now());
        birthdaySignUpInput.valueProperty().addListener(new ChangeListener<LocalDate>()
        {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
            //Print date change to console
            birthdaySignUpInput.setValue(newValue);
            System.out.println("New date selected: " + newValue);
        }
        });
        
        Label salarySignUpLabel = new Label("Salary:");
        salarySignUpInput = new TextField();
        
       
        Label roleSignUpLabel = new Label("Role:");
        roleSignUpInput = new ComboBox<>();
        roleSignUpInput.getItems().addAll("Owner","Manager", "Employee");
        roleSignUpInput.setPromptText("Select an item");
        
        Button submitSignUpButton = new Button("Sign Up");
        submitSignUpButton.setOnAction(e -> 
        {   
            
            if (isValidRegistration()) 
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("MiniTroy Application - ℗MAJKassab.org");
                alert.setHeaderText(null);
                alert.setContentText("Successfully registered user!");
                alert.showAndWait();
                window.setScene(loginScene);
                UserNameSignUpInput.setText("");
                passwordSignUpInput.setText("");
                nameSignUpInput.setText("");
                familySignUpInput.setText("");
                birthdaySignUpInput.setValue(LocalDate.now());
                salarySignUpInput.setText("");
                roleSignUpInput.setPromptText("Select an item");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Registration Status");
                alert.setHeaderText(null);
                alert.setContentText("Invalid Input");
                alert.showAndWait();
            }
        });
  
        Button goBackToLoginButton = new Button("Go Back");
        goBackToLoginButton.setOnAction(e -> 
        {   
            window.setScene(loginScene);
            UserNameSignUpInput.setText("");
            passwordSignUpInput.setText("");
            nameSignUpInput.setText("");
            familySignUpInput.setText("");
            birthdaySignUpInput.setValue(LocalDate.now());
            salarySignUpInput.setText("");
            roleSignUpInput.setPromptText("Select an item");
            
        });
        
        StackPane SignUpPane = new StackPane();
        GridPane SignUpLayout = new GridPane();
        //loginLayout.setMaxSize(300, 150);
        SignUpLayout.setPadding(new Insets(10, 10, 10, 10));
        SignUpLayout.setVgap(8);
        SignUpLayout.setHgap(10);
        SignUpLayout.add(testSignUplabel, 0, 0,2,1);
        SignUpLayout.add(UserNameSignUpLabel, 0, 1);
        SignUpLayout.add(UserNameSignUpInput, 1, 1);
        SignUpLayout.add(passwordSignUpLabel, 0, 2);
        SignUpLayout.add(passwordSignUpInput, 1, 2);
        SignUpLayout.add(nameSignUpLabel, 0, 3);
        SignUpLayout.add(nameSignUpInput, 1, 3);
        
        SignUpLayout.add(familySignUpLabel, 0, 4);
        SignUpLayout.add(familySignUpInput, 1, 4);
        
        SignUpLayout.add(birthdaySignUpLabel, 0, 5);
        SignUpLayout.add(birthdaySignUpInput, 1, 5);
        
        SignUpLayout.add(salarySignUpLabel, 0, 6);
        SignUpLayout.add(salarySignUpInput, 1, 6);
        
        SignUpLayout.add(roleSignUpLabel, 0, 7);
        SignUpLayout.add(roleSignUpInput, 1, 7);
        
        SignUpLayout.add(goBackToLoginButton, 0, 8);
        SignUpLayout.add(submitSignUpButton, 1, 8);
        
        // Set the background color of the login layout to transparent
        SignUpLayout.setStyle("-fx-background-color:  #FFEBCD;");
        SignUpPane.getChildren().add(SignUpLayout);
 
        SignUpPane.setLayoutX(250);
        SignUpPane.setLayoutY(200);

        SignUpScene = new Scene(SignUpPane, 800, 600);
        SignUpScene.setFill(Color.BLANCHEDALMOND);
        // END SignUp scene ----------------------------------------------------
        
        // Set initial scene ---------------------------------------------------
        window.setScene(loginScene);
        window.setTitle("MiniTroy Application - ℗MAJKassab.org");
        window.setOnCloseRequest(event -> 
            {
                TurnOffMethod();
            });
        window.show();
    }

    private boolean isValidCredentials()
    {
        String username = UserNameInput.getText().trim();
        String password = passwordInput.getText().trim();
            try
            {
                stmt = conn.createStatement();
                //String SQLquery = "SELECT `name` FROM `employee` left JOIN authentication ON employee.username = authentication.username WHERE employee.username = "+username+" AND authentication.password = '"+password+"'";
                String SQLquery = "SELECT `name` FROM `employee` left JOIN authentication ON employee.username = authentication.username WHERE employee.username = '"+username+"' AND authentication.password = '"+password+"'";
                ResultSet results = stmt.executeQuery(SQLquery);
                if(results.next())
                {
                    this.AccountName = results.getString(1);
                    WelcomeNameApp.setText("Welcome "+AccountName);
                    System.out.println("Debug:  (isValidCredentials) AccountName is "+AccountName);
                    System.out.println("Debug:  (isValidCredentials) Username and password is correct");
                    stmt.close();
                    return true;
                }
                else
                {
                    System.out.println("Debug:  (isValidCredentials) results is null ");
                }
            }
            catch(SQLException e)
            {
                System.out.println("Debug: (isValidCredentials) "+e.toString());
                return false;
            }
            catch (NullPointerException ee)
            {
                System.out.println("Debug: (isValidCredentials)  NullPointerException");
            }
        //stmt.close();
        System.out.println("Debug:  Username and password is NOT correct");
        return false;
        //return username.equals("admin") && password.equals("password");
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    private void AccessToSQLserver() 
    {
        //DataBase Connection
        try
        {
            //Load Drive
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Debug: Drive Loaded Sucessfuly");
            //Connect to Database
            conn = DriverManager.getConnection(DBurl, DBuser, DBpassword);
            System.out.println("Debug: DB connected Sucessfuly");
            
        }
        catch(ClassNotFoundException | SQLException e)
        {
            System.out.println("Error #1");
        }
    }

    private void SignOutMethod() 
    {
        this.CenterArea.getChildren().clear();
        UserNameInput.setText("");
        passwordInput.setText("");
        window.setScene(loginScene);
        
    }
    
    private void ShowMethod() 
    {
        CenterArea.getChildren().clear();
        
        GridPane InsertWindows = new GridPane();
        Label PName = new Label("Product Name:");
        TextField InPName = new TextField();
        
        Label PBarCode = new Label("Barcode:");
        TextField InPBarCode = new TextField();
        
        Label PFloorPrice = new Label("Floor Price:");
        TextField InPFloorPrice = new TextField();
        
        Label PExpiredDate = new Label("Selling Price:");
        TextField InPExpiredDate = new TextField();
        
        Label PSellingPrice = new Label("Expired Date:");
        TextField InPSellingPrice = new TextField();
        
        Label PDescription = new Label("Description:");
        TextField InPDescription = new TextField();
        
        Button submit = new Button("Submit");
        
        InsertWindows.add(PName, 0, 0);
        InsertWindows.add(InPName, 1, 0);

        InsertWindows.add(PBarCode, 0, 1);
        InsertWindows.add(InPBarCode, 1, 1);

        InsertWindows.add(PFloorPrice, 0, 2);
        InsertWindows.add(InPFloorPrice, 1, 2);

        InsertWindows.add(PSellingPrice, 0, 4);
        InsertWindows.add(InPSellingPrice, 1, 4);
        
        InsertWindows.add(PExpiredDate, 0, 3);
        InsertWindows.add(InPExpiredDate, 1, 3);

        InsertWindows.add(PDescription, 0, 5);
        InsertWindows.add(InPDescription, 1, 5);
        
        InsertWindows.add(submit, 1, 6);
        submit.setOnAction(event->
        {
            System.out.println("Debug:"+InPName.getText()+","+InPBarCode.getText()+","+InPFloorPrice.getText()+","+InPSellingPrice.getText()+","+InPExpiredDate.getText()+","+InPDescription.getText());
            try
            {
                //prepare a statement
                stmt = conn.createStatement();
                stmt.executeUpdate("INSERT INTO `products` VALUES ('"+InPName.getText()+"','"+InPBarCode.getText()+"','"+InPFloorPrice.getText()+"','"+InPExpiredDate.getText()+"','"+InPSellingPrice.getText()+"','"+InPDescription.getText()+"')");
                //stmt.executeUpdate("INSERT INTO `products`(`product_name`, `barcode`, `floor_price`, `selling_price`, `expired_date`, `description`) VALUES ('"+InPName.getText()+"','"+InPBarCode.getText()+"','"+InPFloorPrice.getText()+"','"+InPSellingPrice.getText()+"','"+InPExpiredDate.getText()+"','"+InPDescription.getText()+"')");
                StatusMessage.setText("Insert Done");
                stmt.close();
                InPName.setText("");
                InPBarCode.setText("");
                InPFloorPrice.setText("");
                InPExpiredDate.setText("");
                InPSellingPrice.setText("");
                InPDescription.setText("");
            }
            catch(SQLException e)
            {
                System.out.println("Debug Error: "+e.toString());
                StatusMessage.setText("Error #4");
            }
            
        });
        
        
        CenterArea.getChildren().add(InsertWindows);
    }
    
    private void InsertMethod() 
    {
        CenterArea.getChildren().clear();
        
        GridPane InsertWindows = new GridPane();
        Label PName = new Label("Product Name:");
        TextField InPName = new TextField();
        
        Label PBarCode = new Label("Barcode:");
        TextField InPBarCode = new TextField();
        
        Label PFloorPrice = new Label("Floor Price:");
        TextField InPFloorPrice = new TextField();
        
        Label PExpiredDate = new Label("Selling Price:");
        TextField InPExpiredDate = new TextField();
        
        Label PSellingPrice = new Label("Expired Date:");
        TextField InPSellingPrice = new TextField();
        
        Label PDescription = new Label("Description:");
        TextField InPDescription = new TextField();
        
        Button submit = new Button("Submit");
        
        InsertWindows.add(PName, 0, 0);
        InsertWindows.add(InPName, 1, 0);

        InsertWindows.add(PBarCode, 0, 1);
        InsertWindows.add(InPBarCode, 1, 1);

        InsertWindows.add(PFloorPrice, 0, 2);
        InsertWindows.add(InPFloorPrice, 1, 2);

        InsertWindows.add(PSellingPrice, 0, 4);
        InsertWindows.add(InPSellingPrice, 1, 4);
        
        InsertWindows.add(PExpiredDate, 0, 3);
        InsertWindows.add(InPExpiredDate, 1, 3);

        InsertWindows.add(PDescription, 0, 5);
        InsertWindows.add(InPDescription, 1, 5);
        
        InsertWindows.add(submit, 1, 6);
        submit.setOnAction(event->
        {
            System.out.println("Debug:"+InPName.getText()+","+InPBarCode.getText()+","+InPFloorPrice.getText()+","+InPSellingPrice.getText()+","+InPExpiredDate.getText()+","+InPDescription.getText());
            try
            {
                //prepare a statement
                stmt = conn.createStatement();
                stmt.executeUpdate("INSERT INTO `products` VALUES ('"+InPName.getText()+"','"+InPBarCode.getText()+"','"+InPFloorPrice.getText()+"','"+InPExpiredDate.getText()+"','"+InPSellingPrice.getText()+"','"+InPDescription.getText()+"')");
                //stmt.executeUpdate("INSERT INTO `products`(`product_name`, `barcode`, `floor_price`, `selling_price`, `expired_date`, `description`) VALUES ('"+InPName.getText()+"','"+InPBarCode.getText()+"','"+InPFloorPrice.getText()+"','"+InPSellingPrice.getText()+"','"+InPExpiredDate.getText()+"','"+InPDescription.getText()+"')");
                StatusMessage.setText("Insert Done");
                stmt.close();
                InPName.setText("");
                InPBarCode.setText("");
                InPFloorPrice.setText("");
                InPExpiredDate.setText("");
                InPSellingPrice.setText("");
                InPDescription.setText("");
            }
            catch(SQLException e)
            {
                System.out.println("Debug Error: "+e.toString());
                StatusMessage.setText("Error #4");
            }
            
        });
        
        
        CenterArea.getChildren().add(InsertWindows);
    }

    private void UpdateMethod() 
    {
        CenterArea.getChildren().clear();
        
        GridPane InsertWindows = new GridPane();
        Label PName = new Label("Product Name:");
        TextField InPName = new TextField();
        
        Label PBarCode = new Label("Barcode:");
        TextField InPBarCode = new TextField();
        
        Label PFloorPrice = new Label("Floor Price:");
        TextField InPFloorPrice = new TextField();
        
        Label PSellingPrice = new Label("Selling Price:");
        TextField InPSellingPrice = new TextField();
        
        Label PExpiredDate = new Label("Expired Date:");
        TextField InPExpiredDate = new TextField();
        
        Label PDescription = new Label("Description:");
        TextField InPDescription = new TextField();
        
        Button submit = new Button("Submit");

        InsertWindows.add(PBarCode, 0, 0);
        InsertWindows.add(InPBarCode, 1, 0);
        
        InsertWindows.add(PName, 0, 1);
        InsertWindows.add(InPName, 1, 1);

        InsertWindows.add(PFloorPrice, 0, 2);
        InsertWindows.add(InPFloorPrice, 1, 2);

        InsertWindows.add(PSellingPrice, 0, 3);
        InsertWindows.add(InPSellingPrice, 1, 3);
        
        InsertWindows.add(PExpiredDate, 0, 4);
        InsertWindows.add(InPExpiredDate, 1, 4);

        InsertWindows.add(PDescription, 0, 5);
        InsertWindows.add(InPDescription, 1, 5);
        
        InsertWindows.add(submit, 1, 6);
        submit.setOnAction(event->
        {
            try
            {
                //prepare a statement
                stmt = conn.createStatement();
                String SQLquery = "UPDATE `products` SET `product_name`='"+InPName.getText()+"',`floor_price`="+InPFloorPrice.getText()+",`selling_price`="+InPSellingPrice.getText()+",`expired_date`='"+InPExpiredDate.getText()+"',`description`='"+InPDescription.getText()+"' WHERE `barcode`="+InPBarCode.getText();
                //String SQLquery = "UPDATE `products` SET `product_name`='"+InPName.getText()+"',`floor_price`="+InPFloorPrice.getText()+",`selling_price`="+InPExpiredDate.getText()+",`expired_date`='"+InPSellingPrice.getText()+"',`description`='"+InPDescription.getText()+"' WHERE `barcode`="+InPBarCode.getText();
                System.out.println("Debug: (UpdateMethod) "+SQLquery);
                stmt.executeUpdate(SQLquery);
                StatusMessage.setText("Updated Done");
                stmt.close();
                InPName.setText("");
                InPBarCode.setText("");
                InPFloorPrice.setText("");
                InPExpiredDate.setText("");
                InPSellingPrice.setText("");
                InPDescription.setText("");   
            }
            catch(SQLException e)
            {
                System.out.println("Debug Error: "+e.toString());
                StatusMessage.setText("Error #5");
            }
            
        });
        
        
        CenterArea.getChildren().add(InsertWindows);
    }

    private void DeleteMethod() 
    {
        VBox DeleteWindows = new VBox(5);
        Label barcode = new Label("Enter the barcode");
        TextField barcodeInput = new TextField();
        Button submitDelete = new Button("Submit");
        submitDelete.setOnAction(event ->
        {
            try 
            {
                //prepare a statement
                stmt = conn.createStatement();
                stmt.executeUpdate("DELETE FROM `products` WHERE barcode='"+barcodeInput.getText()+"'");
                StatusMessage.setText("Deleted Done");
                stmt.close();
            } 
            catch (SQLException ex) 
            {
                System.out.println("Debug Error: "+ex.toString());
                StatusMessage.setText("Error #3");
            }
        });
        DeleteWindows.getChildren().addAll(barcode,barcodeInput,submitDelete);
        CenterArea.getChildren().clear();
        CenterArea.getChildren().add(DeleteWindows);
        
        
    }

    private void TurnOffMethod()
    {
        
        try 
        {
            conn.close();
            Platform.exit();
            System.out.println("Debug: (TurnOffMethod) System Exit");
            System.exit(0);
        } 
        catch (Exception ex) 
        {
            System.out.println("Debug: (TurnOffMethod) "+ex.toString());
        }
        
    }

    private boolean isValidRegistration() 
    {
        try 
            {
                //prepare a statement
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `authentication` WHERE username=11");
                if (rs.next())
                {
                    int count = rs.getInt(1);
                    System.out.println("Debug: (isValidRegistration) Username count is "+count);
                    if (count == 0)
                    {
                        stmt.close();
                        stmt = conn.createStatement();
                        String sqlQuery = "INSERT INTO `employee`(`username`, `name`, `family`, `birthday`, `recruit_date`, `salary`, `role`) VALUES ('"+UserNameSignUpInput.getText()+"','"+nameSignUpInput.getText()+"','"+familySignUpInput.getText()+"','"+birthdaySignUpInput.getValue()+"','"+today+"',"+salarySignUpInput.getText()+",'"+roleSignUpInput.getValue()+"')";
                        System.out.println("Debug: (isValidRegistration) SQL:"+sqlQuery);
                        stmt.executeUpdate(sqlQuery);
                        System.out.println("Debug: (isValidRegistration) Table employee done");
                        stmt.close();
                        stmt = conn.createStatement();
                        sqlQuery = "INSERT INTO `authentication`(`username`, `password`, `password_date`) VALUES ('"+UserNameSignUpInput.getText()+"','"+passwordSignUpInput.getText()+"','"+today+"')";
                        System.out.println("Debug: (isValidRegistration) SQL:"+sqlQuery);
                        stmt.executeUpdate(sqlQuery);
                        System.out.println("Debug: (isValidRegistration) Table authentication done");
                        stmt.close();
                        System.out.println("Debug: (isValidRegistration) return true");
                        return true;
                    }
                    else
                    {
                        System.out.println("Debug: (isValidRegistration) username is available");
                        System.out.println("Debug: (isValidRegistration) return false");
                        stmt.close();
                        return false;
                    }
                    
                }   
            } 
            catch (SQLException ex) 
            {
                System.out.println("Debug Error: "+ex.toString());
                System.out.println("Error #6");
            }
        return false;
    }
    
}
