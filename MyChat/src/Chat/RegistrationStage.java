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

import java.io.IOException;

public class RegistrationStage extends Stage {

    private double x;
    private double y;


    public RegistrationStage() {
        try {
        Parent root = null;
        root = FXMLLoader.load(getClass().getResource("Registration.fxml"));
        initStyle(StageStyle.TRANSPARENT);
        Scene s = new Scene(root);
        s.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                x = getX() - event.getScreenX();
                y = getY() - event.getScreenY();
            }
        });
        s.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setX(event.getScreenX() + x);
                setY(event.getScreenY() + y);
            }
        });
        s.setFill(Color.TRANSPARENT);
        setScene(s);
        getIcons().add(new Image(this.getClass().getResourceAsStream("img/label-chat.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
