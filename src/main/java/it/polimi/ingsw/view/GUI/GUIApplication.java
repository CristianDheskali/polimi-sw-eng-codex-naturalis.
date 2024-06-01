package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Data.SerializedGame;
import it.polimi.ingsw.network.client.ClientController;
import it.polimi.ingsw.view.GUI.controllers.GameListMenuController;
import it.polimi.ingsw.view.GUI.controllers.ViewController;
import it.polimi.ingsw.view.TUI.View;
import it.polimi.ingsw.view.ViewInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class GUIApplication extends Application implements ViewInterface {
    private Stage mainStage, popUpStage;
    private StackPane root;

    @Override
    public void start(Stage stage) throws Exception {
        ClientController.getInstance().setViewInterface(this);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/polimi/ingsw/fxml/MainMenu.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        mainStage = stage;
        mainStage.setScene(scene);
        this.mainStage.setTitle("Codex Naturalis");
        mainStage.show();
        root = new StackPane();
    }

    public ViewController setMainScene(SceneEnum sceneName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneName.value()));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        this.mainStage.setScene(scene);
        return loader.getController();
    }

    public void openPopup(SceneEnum sceneName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneName.value()));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        popUpStage = new Stage();
        popUpStage.setTitle("Info");
        popUpStage.setResizable(false);
        popUpStage.setScene(scene);
        popUpStage.setX(mainStage.getX() + (mainStage.getWidth() - scene.getWidth()) * 0.5);
        popUpStage.setY(mainStage.getY() + (mainStage.getHeight() - scene.getHeight()) * 0.5);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.initOwner(mainStage);
        popUpStage.show();
    }

    public void selectAvailableMatch(ArrayList<SerializedGame> availableMatches, String error) {
        Platform.runLater(() -> {
                    try {
                        GameListMenuController controller = (GameListMenuController) ((GUIApplication) ClientController.getInstance().getViewInterface()).setMainScene(SceneEnum.GAME_LIST_MENU);
                        controller.populateGamesList(availableMatches);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void pickUsernameAndColor() {
        Platform.runLater(() -> {
            try {
                ((GUIApplication) ClientController.getInstance().getViewInterface()).setMainScene(SceneEnum.LOGIN_MENU);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void selectPersonalGoal() {
        Platform.runLater(() -> {
            try {
                ((GUIApplication) ClientController.getInstance().getViewInterface()).setMainScene(SceneEnum.PERSONAL_GOAL_SELECTION_MENU);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void waitingRoom() {
        Platform.runLater(() -> {
            try {
                ((GUIApplication) ClientController.getInstance().getViewInterface()).setMainScene(SceneEnum.WAITING_ROOM);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void updateInfo(String message, boolean clear) {
        Platform.runLater(() -> {
            try {
                ((GUIApplication) ClientController.getInstance().getViewInterface()).setMainScene(SceneEnum.CODEX);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void updateChatView(String error) {

    }
}
