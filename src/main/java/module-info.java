module com.guiyomi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires transitive javafx.graphics;

    opens com.guiyomi to javafx.fxml;
    exports com.guiyomi;
}
