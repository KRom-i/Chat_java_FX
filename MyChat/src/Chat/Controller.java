package Chat;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    private TextField info;
    @FXML
    private HBox dnPanel;
    @FXML
    private VBox upPanel;
    @FXML
    private TextField log;
    @FXML
    private PasswordField pass;
    @FXML
    private TextArea chatArea;
    @FXML
    private TextField textField;
    @FXML
    private ListView<String> listContacts;

    private List<TextArea> TextAreas;

    private boolean isAuth;
    private boolean listContactsActive;

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    public static final String ADDRESS = "localhost";
    public static final int PORT = 6000;

    public void setAuth(boolean auth){
        this.isAuth = auth;

        if(!auth){
            upPanel.setVisible(true);
            upPanel.setManaged(true);

            dnPanel.setVisible(false);
            dnPanel.setManaged(false);
        } else {
            upPanel.setVisible(false);
            upPanel.setManaged(false);

            dnPanel.setVisible(true);
            dnPanel.setManaged(true);
        }
    }


    public void chatExit(ActionEvent actionEvent) {

        if (socket != null && !socket.isClosed()) {
            try {
                out.writeUTF("/end");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        Main.primaryStage.close();
        System.exit(0);
    }

    public void rollUp(ActionEvent actionEvent){
        Main.primaryStage.setIconified(true);
    }


    public void listCon() {

        if (listContacts.getPrefWidth() == 0 && isAuth){
            try {
                out.writeUTF("/clientlist");
            } catch (IOException e) {
                e.printStackTrace();
            }
            chatArea.setPrefWidth(400);
            listContacts.setPrefWidth(190);
            listContactsActive = true;
        } else {
            listContacts.getItems().clear();
            chatArea.setPrefWidth(590);
            listContacts.setPrefWidth(0);
            listContactsActive = false;
        }

    }


    public void sendMsg() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void connect() {

        try {

            socket = new Socket(ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() ->{

                try {
                        while (true){
                            String str = in.readUTF();
                            if (str.startsWith("/endTimeAuth")){
                                out.writeUTF("/end");
                                break;
                            }
                            if (str.startsWith("/auth-OK")){
                                String[] tokens = str.split(" ");
                                setAuth(true);
                                chatArea.clear();
                                info.setText(tokens[1]);
                                break;
                            } else {
                                authFalse();
                            }

                        }


                    while (true){
                        String str = in.readUTF();
                        if ("/serverClosed".equals(str)){
                            chatArea.clear();
                            break;
                        } else if (str.startsWith("/clientlist")){
                            String[] tokensClientList = str.split(" ");
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    if (listContactsActive){
                                        listContacts.getItems().clear();
                                        String user = info.getText();
                                        for (int i = 1; i < tokensClientList.length; i++) {
                                            if (!user.equals(tokensClientList[i])){
                                                listContacts.getItems().add(tokensClientList[i]);
                                            }
                                        }
                                    }
                                }
                            });

                        } else {
                            chatArea.appendText(str + "\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    info.clear();
                    setAuth(false);
                    listContacts.setPrefWidth(0);
                }
            }).start();




        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void singIn() {

        if (socket == null || socket.isClosed()){
            connect();
        }
        
        try {
            if (log.getText().length() > 0 && pass.getText().length() > 0){
                out.writeUTF("/auth " + log.getText() + " " + pass.getText());
                log.clear();
                pass.clear();
            } else {
                authFalse();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void authFalse(){
        log.setStyle("-fx-background-color: #800000");
        pass.setStyle("-fx-background-color: #800000");
    }

    public void colorBtn(MouseEvent mouseEvent) {
        log.setStyle("-fx-background-color: #696969");
        pass.setStyle("-fx-background-color: #696969");
    }

    public void selectClient(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            PrivateStage ps = new PrivateStage(listContacts.getSelectionModel().getSelectedItem(), out);
            ps.setX(Main.primaryStage.getX() + 10);
            ps.setY(Main.primaryStage.getY() + 200);
            ps.show();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TextAreas = new ArrayList<>();
        TextAreas.add(chatArea);
    }

    public void singUp(ActionEvent actionEvent) {

        RegistrationStage registrationStage = new RegistrationStage();
        registrationStage.setX(Main.primaryStage.getX() + 50);
        registrationStage.setY(Main.primaryStage.getY() + 50);
        registrationStage.show();

    }

}
