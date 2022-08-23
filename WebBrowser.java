import javafx.application.Application;
import javafx.concurrent.Worker.State;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.List;

public class WebBrowser extends Application {
    // INSTANCE VARIABLES
    // These variables are included to get you started.
    private Stage stage = null;
    private WebEngine webEngine = null;
    private TextField statusbar = null;
    private TextField webAddress = null;


    // HELPER METHODS
    /**
     * Retrieves the value of a command line argument specified by the index.
     *
     * @return The value of the command line argument.
     */
    private String getParameter() {
        Parameters params = getParameters();
        List<String> parameters = params.getRaw();
        return !parameters.isEmpty() ? parameters.get(0) : "";
    }

    /**
     * Creates a WebView which handles mouse and some keyboard events, and
     * manages scrolling automatically, so there's no need to put it into a ScrollPane.
     * The associated WebEngine is created automatically at construction time.
     *
     * @return browser - a WebView container for the WebEngine.
     */
    private WebView makeHtmlView( ) {
        WebView view = new WebView();
        webEngine = view.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState)-> {
                    if (newState == State.SUCCEEDED) {
                        stage.setTitle(webEngine.getTitle());
                        webAddress.setText(webEngine.getLocation());
                    }

                });
        webEngine.setOnStatusChanged(
                (ev) -> statusbar.setText(ev.getData()));
        return view;
    }

    /**
     * Generates the status bar layout and text field.
     *
     * @return statusbarPane - the HBox layout that contains the statusbar.
     */
    private HBox makeStatusBar( ) {
        HBox statusbarPane = new HBox();
        statusbarPane.setPadding(new Insets(5, 4, 5, 4));
        statusbarPane.setSpacing(10);
        statusbarPane.setStyle("-fx-background-color: #336699;");
        statusbar = new TextField();
        HBox.setHgrow(statusbar, Priority.ALWAYS);
        statusbarPane.getChildren().addAll(statusbar);
        return statusbarPane;
    }

    private HBox makeToolBar(){
        HBox toolbarPane = new HBox();
        toolbarPane.setPadding(new Insets(5, 4, 5, 4));
        toolbarPane.setSpacing(10);
        toolbarPane.setStyle("-fx-background-color: #336699;");
        webAddress = new TextField();

        Button backButton = new Button("<-");
        backButton.setOnAction(event -> {
            if (webEngine.getHistory().getCurrentIndex() > 0) {
                webEngine.getHistory().go(-1);
                webAddress.setText(webEngine.getLocation());
            }
        });

        Button forwardButton = new Button("->");
        forwardButton.setOnAction(event->{
            if(webEngine.getHistory().getCurrentIndex() < webEngine.getHistory().getEntries().size()-1) {
                webEngine.getHistory().go(1);
                webAddress.setText(webEngine.getLocation());

            }
        });

        Button helpButton = new Button("?");


        HBox.setHgrow(webAddress, Priority.ALWAYS);
        webAddress.setOnAction(event->{
            String text = webAddress.getText();
            if(!text.startsWith("https")){
                text = "https://"+text;
            }
            webEngine.load(text);
            webAddress.setText(text);
        });
        toolbarPane.getChildren().addAll(backButton, forwardButton, webAddress, helpButton);
        return toolbarPane;

    }

    // REQUIRED METHODS
    /**
     * The main entry point for all JavaFX applications. The start method is
     * called after the init method has returned, and after the system is ready
     * for the application to begin running.
     *
     * NOTE: This method is called on the JavaFX Application Thread.
     *
     * @param primaryStage - the primary stage for this application, onto which
     * the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        // Build your window here.
        stage = primaryStage;
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 800, 600);

        borderPane.setCenter(makeHtmlView());
        String commandLineArg = getParameter();
        webEngine.load(commandLineArg.isEmpty() ? "https://www.google.com" : commandLineArg);

        borderPane.setTop(makeToolBar());
        borderPane.setBottom(makeStatusBar());
        statusbar.setText("This is the statusbar");


        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main( ) method is ignored in JavaFX applications.
     * main( ) serves only as fallback in case the application is launched
     * as a regular Java application, e.g., in IDEs with limited FX
     * support.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
