# HTML Viewer for Windows (Java + JavaFX)

A modern, futuristic **Liquid Glass** themed HTML Viewer desktop application for Windows.  
Built with **Java 17+** and **JavaFX** WebView.

## Features
- Clean Liquid Glass / Glassmorphism UI
- Open and view local HTML files
- Smooth translucent panels + blur effects
- Modern dark futuristic aesthetic
- Standalone executable (via jpackage or Launch4j)

## Requirements
- JDK 17 or newer
- Maven 3.8+

## Run (Development)

```bash
mvn clean javafx:run
```

## Build JAR

```bash
mvn clean package
```

The runnable JAR will be in `target/html-viewer-1.0.0.jar`

## Create Windows EXE (recommended)

After building the JAR:

```bash
jpackage --input target \
  --name "HTMLViewer" \
  --main-jar html-viewer-1.0.0.jar \
  --main-class com.htmlviewer.HTMLViewerApp \
  --type exe \
  --win-console \
  --app-version 1.0.0 \
  --description "Modern Liquid Glass HTML Viewer"
```

Or use Launch4j / jlink for a smaller native image.

## Theme
The application uses a custom **Liquid Glass** theme:
- Translucent panels
- Soft blur / frosted glass effect
- Neon accent gradients
- Dark futuristic background
