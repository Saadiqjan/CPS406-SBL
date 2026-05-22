module cps406.sbl {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires transitive javafx.graphics;
    requires java.desktop;
    requires java.logging;

//    requires org.controlsfx.controls;
//    requires com.dlsc.formsfx;
//    requires org.kordamp.bootstrapfx.core;
//    requires eu.hansolo.tilesfx;

    opens com.cps406 to javafx.fxml;
    opens com.cps406.controllers to javafx.fxml;
    opens com.cps406.model to javafx.base;

    exports com.cps406;
    exports com.cps406.model;
}