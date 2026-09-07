package com.htmlviewer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

/**
 * Modern Liquid Glass themed HTML Viewer
 * Futuristic glassmorphism design with translucent panels
 */
public class HTMLViewerApp extends Application {

    private WebView webView;
    private WebEngine webEngine;
    private Label titleLabel;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.initStyle(StageStyle.TRANSPARENT); // For glass effect

        // Root container with gradient background
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");

        // Animated gradient background
        Region background = createLiquidBackground();
        root.getChildren().add(background);

        // Main glass panel
        VBox glassPanel = createGlassPanel();
        root.getChildren().add(glassPanel);

        Scene scene = new Scene(root, 1200, 800);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/styles/liquid-glass.css").toExternalForm());

        stage.setTitle("HTML Viewer • Liquid Glass");
        stage.setScene(scene);
        stage.show();
    }

    private Region createLiquidBackground() {
        Region bg = new Region();
        bg.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Deep space + neon gradient
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#0a0a1f")),
                new Stop(0.3, Color.web("#0f1c3f")),
                new Stop(0.6, Color.web("#1a0a2e")),
                new Stop(1.0, Color.web("#0d1b2a"))
        );
        bg.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));
        return bg;
    }

    private VBox createGlassPanel() {
        VBox panel = new VBox(16);
        panel.setPadding(new Insets(24));
        panel.setAlignment(Pos.TOP_CENTER);
        panel.getStyleClass().add("glass-panel");

        // Top bar with glass effect
        HBox topBar = createTopBar();
        panel.getChildren().add(topBar);

        // WebView container with glass border
        StackPane webContainer = new StackPane();
        webContainer.getStyleClass().add("web-container");
        VBox.setVgrow(webContainer, Priority.ALWAYS);

        webView = new WebView();
        webEngine = webView.getEngine();
        webView.setContextMenuEnabled(true);
        webView.getStyleClass().add("web-view");

        // Load welcome page
        webEngine.loadContent(getWelcomeHtml());

        webContainer.getChildren().add(webView);
        panel.getChildren().add(webContainer);

        // Status bar
        HBox statusBar = createStatusBar();
        panel.getChildren().add(statusBar);

        return panel;
    }

    private HBox createTopBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(12, 16, 12, 16));
        bar.getStyleClass().add("glass-bar");

        titleLabel = new Label("HTML Viewer");
        titleLabel.getStyleClass().add("title-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button openBtn = createGlassButton("Open HTML");
        openBtn.setOnAction(e -> openFile());

        Button refreshBtn = createGlassButton("Refresh");
        refreshBtn.setOnAction(e -> webEngine.reload());

        Button homeBtn = createGlassButton("Home");
        homeBtn.setOnAction(e -> webEngine.loadContent(getWelcomeHtml()));

        bar.getChildren().addAll(titleLabel, spacer, homeBtn, refreshBtn, openBtn);
        return bar;
    }

    private Button createGlassButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("glass-button");
        return btn;
    }

    private HBox createStatusBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(8, 16, 8, 16));
        bar.getStyleClass().add("status-bar");

        Label status = new Label("Ready • Liquid Glass Theme");
        status.getStyleClass().add("status-label");
        bar.getChildren().add(status);
        return bar;
    }

    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open HTML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("HTML Files", "*.html", "*.htm"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            String url = file.toURI().toString();
            webEngine.load(url);
            titleLabel.setText("HTML Viewer • " + file.getName());
        }
    }

    private String getWelcomeHtml() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body {
                        font-family: 'Segoe UI', system-ui, sans-serif;
                        background: linear-gradient(135deg, #0a0a1f 0%, #1a0a2e 50%, #0d1b2a 100%);
                        color: #e0e7ff;
                        height: 100vh;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        overflow: hidden;
                    }
                    .container {
                        text-align: center;
                        padding: 60px;
                        background: rgba(255, 255, 255, 0.05);
                        backdrop-filter: blur(20px);
                        border-radius: 24px;
                        border: 1px solid rgba(255, 255, 255, 0.12);
                        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4),
                                    inset 0 1px 0 rgba(255, 255, 255, 0.1);
                        max-width: 520px;
                    }
                    h1 {
                        font-size: 2.4rem;
                        font-weight: 300;
                        letter-spacing: 2px;
                        background: linear-gradient(90deg, #a78bfa, #60a5fa, #34d399);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        margin-bottom: 16px;
                    }
                    p {
                        font-size: 1.05rem;
                        opacity: 0.75;
                        line-height: 1.6;
                        margin-bottom: 28px;
                    }
                    .hint {
                        font-size: 0.9rem;
                        opacity: 0.5;
                        letter-spacing: 1px;
                    }
                    .orb {
                        width: 80px;
                        height: 80px;
                        margin: 0 auto 30px;
                        border-radius: 50%;
                        background: linear-gradient(135deg, #a78bfa55, #60a5fa55);
                        box-shadow: 0 0 40px #a78bfa66, 0 0 80px #60a5fa44;
                        animation: pulse 3s ease-in-out infinite;
                    }
                    @keyframes pulse {
                        0%, 100% { transform: scale(1); opacity: 0.8; }
                        50% { transform: scale(1.08); opacity: 1; }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="orb"></div>
                    <h1>LIQUID GLASS</h1>
                    <p>Modern HTML Viewer<br>Click <b>Open HTML</b> to load a file</p>
                    <div class="hint">Futuristic • Transparent • Fluid</div>
                </div>
            </body>
            </html>
            """;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
