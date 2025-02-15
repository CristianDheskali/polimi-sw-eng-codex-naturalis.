module it.polimi.ingsw {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.rmi;
    requires com.google.gson;
    exports it.polimi.ingsw.network.server.RMI;
    exports it.polimi.ingsw.network.server.handler;
    exports it.polimi.ingsw.model.Data;
    exports it.polimi.ingsw.model.Enumerations;
    exports it.polimi.ingsw.model.GameComponents;
    exports it.polimi.ingsw.view.GUI;
    exports it.polimi.ingsw.view.GUI.controllers;

    opens it.polimi.ingsw.network.server.RMI to java.rmi;
    opens it.polimi.ingsw.network.server.handler to java.rmi;
    opens it.polimi.ingsw.model.Data to com.google.gson;
    opens it.polimi.ingsw.model.Enumerations to com.google.gson;
    opens it.polimi.ingsw.model.GameComponents to com.google.gson;
    opens it.polimi.ingsw.view.GUI to javafx.graphics;
    opens it.polimi.ingsw to javafx.fxml;
    opens it.polimi.ingsw.view.GUI.controllers to javafx.fxml;
}