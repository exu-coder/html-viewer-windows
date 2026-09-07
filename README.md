# HTML Viewer for Windows

A simple desktop HTML Viewer application for Windows that can be compiled into a standalone EXE.

## Features
- Open and view local HTML files
- Clean, minimal interface
- Built with Python + pywebview (easy to package as EXE)

## Quick Start (Development)

1. Install dependencies:
```bash
pip install pywebview
```

2. Run the app:
```bash
python html_viewer.py
```

## Build as Windows EXE

1. Install PyInstaller:
```bash
pip install pyinstaller
```

2. Build the executable:
```bash
pyinstaller --onefile --windowed --name "HTMLViewer" html_viewer.py
```

The EXE will be created in the `dist/` folder.

## Usage
- Launch the app
- Use **File → Open** to select an HTML file
- The content will be displayed in the embedded browser view

## Requirements
- Python 3.8+
- Windows 10/11 recommended
