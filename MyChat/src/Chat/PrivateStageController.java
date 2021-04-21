package Chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.DataOutputStream;
import java.io.IOException;


public class PrivateStageController {

    @FXML
    private Button btnCancel;
    @FXML
    private TextField textField;
    @FXML
    private Button btn;

    public void sendPrivate(ActionEvent actionEvent) {
        String outPrivateMSG = textField.getText();
            DataOutputStream out = ((PrivateStage) btn.getScene().getWindow()).out;
            String nickTo = ((PrivateStage) btn.getScene().getWindow()).nickTo;
            try {
                out.writeUTF("@" + nickTo + " " + outPrivateMSG);
            } catch (IOException e) {
                e.printStackTrace();
            }
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }

    public void privateCancel(ActionEvent actionEvent) {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();

    }
}
