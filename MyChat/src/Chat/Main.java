package Chat;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    protected static Stage primaryStage;
    protected static Stage dialog;
    private double x;
    private double y;

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        this.primaryStage = primaryStage;
        Scene s = new Scene(root);
        s.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                x = primaryStage.getX() - event.getScreenX();
                y = primaryStage.getY() - event.getScreenY();
            }
        });
        s.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() + x);
                primaryStage.setY(event.getScreenY() + y);
            }
        });
        s.setFill(Color.TRANSPARENT);
        primaryStage.setScene(s);
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("img/label-chat.png")));
        primaryStage.show();

    }
    public static void main(String[] args) {
        launch(args);
    }

}
