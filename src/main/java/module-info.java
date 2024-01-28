module com.example.srfront {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.rmi;

    opens com.example.srfront to javafx.fxml;
    exports com.example.srfront;
}