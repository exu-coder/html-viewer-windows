package com.htmlviewer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 * Modern Liquid Glass themed HTML Viewer
 * Supports: Open file, Drag & Drop, Paste (Ctrl+V)
 */
public class HTMLViewerApp extends Application {

    private WebView webView;
    private WebEngine webEngine;
    private Label titleLabel;
    private Label statusLabel;
    private Stage primaryStage;
    private StackPane webContainer;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");

        Region background = createLiquidBackground();
        root.getChildren().add(background);

        VBox glassPanel = createGlassPanel();
        root.getChildren().add(glassPanel);

        // Global drag & drop on the whole window
        setupDragAndDrop(root);

        // Global keyboard shortcuts (Ctrl+V paste, Ctrl+O open)
        root.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);

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

        HBox topBar = createTopBar();
        panel.getChildren().add(topBar);

        webContainer = new StackPane();
        webContainer.getStyleClass().add("web-container");
        VBox.setVgrow(webContainer, Priority.ALWAYS);

        webView = new WebView();
        webEngine = webView.getEngine();
        webView.setContextMenuEnabled(true);
        webView.getStyleClass().add("web-view");

        // Also allow drag & drop directly on the WebView area
        setupDragAndDrop(webContainer);

        webEngine.loadContent(getWelcomeHtml());
        webContainer.getChildren().add(webView);
        panel.getChildren().add(webContainer);

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

        Button pasteBtn = createGlassButton("Paste");
        pasteBtn.setOnAction(e -> pasteFromClipboard());

        Button refreshBtn = createGlassButton("Refresh");
        refreshBtn.setOnAction(e -> webEngine.reload());

        Button homeBtn = createGlassButton("Home");
        homeBtn.setOnAction(e -> {
            webEngine.loadContent(getWelcomeHtml());
            titleLabel.setText("HTML Viewer");
            setStatus("Ready • Liquid Glass Theme");
        });

        bar.getChildren().addAll(titleLabel, spacer, homeBtn, refreshBtn, pasteBtn, openBtn);
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

        statusLabel = new Label("Ready • Drag & Drop HTML files or Ctrl+V to paste");
        statusLabel.getStyleClass().add("status-label");
        bar.getChildren().add(statusLabel);
        return bar;
    }

    private void setStatus(String text) {
        if (statusLabel != null) {
            Platform.runLater(() -> statusLabel.setText(text));
        }
    }

    // ==================== Drag & Drop ====================

    private void setupDragAndDrop(javafx.scene.Node node) {
        node.setOnDragOver(event -> {
            if (event.getGestureSource() != node && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        node.setOnDragEntered(event -> {
            if (event.getDragboard().hasFiles()) {
                webContainer.getStyleClass().add("drag-over");
                setStatus("Drop HTML file to open…");
            }
            event.consume();
        });

        node.setOnDragExited(event -> {
            webContainer.getStyleClass().remove("drag-over");
            setStatus("Ready • Drag & Drop HTML files or Ctrl+V to paste");
            event.consume();
        });

        node.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasFiles()) {
                List<File> files = db.getFiles();
                for (File file : files) {
                    String name = file.getName().toLowerCase();
                    if (name.endsWith(".html") || name.endsWith(".htm") || name.endsWith(".xhtml")) {
                        loadFile(file);
                        success = true;
                        break; // load first valid HTML file
                    }
                }
                if (!success && !files.isEmpty()) {
                    // Try loading any dropped file as text/html fallback
                    loadFile(files.get(0));
                    success = true;
                }
            }

            webContainer.getStyleClass().remove("drag-over");
            event.setDropCompleted(success);
            event.consume();
        });
    }

    // ==================== Paste support ====================

    private void handleKeyPress(KeyEvent event) {
        if (event.isControlDown() && event.getCode() == KeyCode.V) {
            pasteFromClipboard();
            event.consume();
        } else if (event.isControlDown() && event.getCode() == KeyCode.O) {
            openFile();
            event.consume();
        }
    }

    private void pasteFromClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();

        // 1. Prefer HTML content from clipboard
        if (clipboard.hasHtml()) {
            String html = clipboard.getHtml();
            if (html != null && !html.isBlank()) {
                webEngine.loadContent(html);
                titleLabel.setText("HTML Viewer • Pasted HTML");
                setStatus("Loaded HTML content from clipboard");
                return;
            }
        }

        // 2. Prefer plain text that looks like HTML
        if (clipboard.hasString()) {
            String text = clipboard.getString();
            if (text != null && !text.isBlank()) {
                String trimmed = text.trim();

                // Looks like HTML
                if (trimmed.toLowerCase().startsWith("<!doctype") ||
                    trimmed.toLowerCase().startsWith("<html") ||
                    trimmed.contains("<body") || trimmed.contains("<div")) {
                    webEngine.loadContent(trimmed);
                    titleLabel.setText("HTML Viewer • Pasted HTML");
                    setStatus("Loaded HTML content from clipboard");
                    return;
                }

                // Looks like a file path
                File possibleFile = new File(trimmed.replace("\"", ""));
                if (possibleFile.exists() && possibleFile.isFile()) {
                    loadFile(possibleFile);
                    return;
                }
            }
        }

        // 3. Files in clipboard (some systems put files there)
        if (clipboard.hasFiles()) {
            List<File> files = clipboard.getFiles();
            for (File file : files) {
                String name = file.getName().toLowerCase();
                if (name.endsWith(".html") || name.endsWith(".htm")) {
                    loadFile(file);
                    return;
                }
            }
        }

        setStatus("Clipboard has no HTML content or valid file path");
    }

    // ==================== File loading ====================

    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open HTML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("HTML Files", "*.html", "*.htm", "*.xhtml"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            loadFile(file);
        }
    }

    private void loadFile(File file) {
        try {
            String url = file.toURI().toString();
            webEngine.load(url);
            titleLabel.setText("HTML Viewer • " + file.getName());
            setStatus("Opened: " + file.getAbsolutePath());
        } catch (Exception e) {
            setStatus("Failed to open: " + e.getMessage());
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
                        padding: 60px 50px;
                        background: rgba(255, 255, 255, 0.05);
                        backdrop-filter: blur(20px);
                        border-radius: 24px;
                        border: 1px solid rgba(255, 255, 255, 0.12);
                        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4),
                                    inset 0 1px 0 rgba(255, 255, 255, 0.1);
                        max-width: 560px;
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
                        margin-bottom: 24px;
                    }
                    .actions {
                        display: flex;
                        gap: 12px;
                        justify-content: center;
                        flex-wrap: wrap;
                        margin-top: 8px;
                    }
                    .chip {
                        font-size: 0.82rem;
                        padding: 6px 14px;
                        background: rgba(167, 139, 250, 0.15);
                        border: 1px solid rgba(167, 139, 250, 0.35);
                        border-radius: 20px;
                        letter-spacing: 0.5px;
                        opacity: 0.9;
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
                    <p>Modern HTML Viewer<br>
                    Drag & drop an HTML file here<br>
                    or press <b>Ctrl + V</b> to paste</p>
                    <div class="actions">
                        <span class="chip">Drag & Drop</span>
                        <span class="chip">Ctrl + V Paste</span>
                        <span class="chip">Ctrl + O Open</span>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
