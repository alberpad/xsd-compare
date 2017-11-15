package com.compare.xsd;

import com.compare.xsd.loaders.ViewLoader;
import com.compare.xsd.managers.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class XsdCompareApplication extends Application {
    private static ApplicationContext APPLICATION_CONTEXT;

    public static void main(String[] args) {
        APPLICATION_CONTEXT = SpringApplication.run(XsdCompareApplication.class, args);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ViewManager viewManager = APPLICATION_CONTEXT.getBean(ViewManager.class);
        ViewLoader loader = APPLICATION_CONTEXT.getBean(ViewLoader.class);

        viewManager.setStage(primaryStage);
        primaryStage.setTitle("XSD Compare");
        primaryStage.setMaximized(true);
        loader.show("main.fxml");
    }
}
