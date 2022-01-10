module com.example.chessgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.desktop;

    opens Chessgame to javafx.fxml;
    exports Chessgame;
    exports Chessgame.board;
    opens Chessgame.board to javafx.fxml;
    exports Chessgame.pieces;
    opens Chessgame.pieces to javafx.fxml;
}