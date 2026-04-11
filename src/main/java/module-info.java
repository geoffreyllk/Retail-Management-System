module com.example.coursework {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;

    opens com.example.coursework.controllers to javafx.fxml;
    opens com.example.coursework.controllers.admin to javafx.fxml;
    opens com.example.coursework.controllers.cashier to javafx.fxml;
    opens com.example.coursework.models to javafx.base;
    opens com.example.coursework.database to javafx.base;

    exports com.example.coursework;
}