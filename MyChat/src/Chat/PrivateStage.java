package Chat;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrivateStage extends Stage {

    private double x;
    private double y;

    String nickTo;
    DataOutputStream out;


    public PrivateStage(String nickTo, DataOutputStream out) {

        this.nickTo = nickTo;
        this.out = out;

        try {
            Parent root = FXMLLoader.load(getClass().getResource("privateStage.fxml"));
            initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    x = getX() - event.getScreenX();
                    y = getY() - event.getScreenY();
                }
            });
            scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    setX(event.getScreenX() + x);
                    setY(event.getScreenY() + y);
                }
            });
            scene.setFill(Color.TRANSPARENT);
            getIcons().add(new Image(this.getClass().getResourceAsStream("img/label-chat.png")));
            setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
