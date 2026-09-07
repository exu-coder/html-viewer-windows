import webview
import tkinter as tk
from tkinter import filedialog
import os

class HTMLViewer:
    def __init__(self):
        self.window = None
        self.current_file = None

    def open_file(self):
        """Open a file dialog to select an HTML file"""
        root = tk.Tk()
        root.withdraw()  # Hide the root window
        
        file_path = filedialog.askopenfilename(
            title="Open HTML File",
            filetypes=[
                ("HTML files", "*.html *.htm"),
                ("All files", "*.*")
            ]
        )
        root.destroy()
        
        if file_path:
            self.load_file(file_path)

    def load_file(self, file_path):
        """Load the selected HTML file into the webview"""
        self.current_file = file_path
        # Convert to file:// URL
        url = "file:///" + os.path.abspath(file_path).replace("\\", "/")
        
        if self.window:
            self.window.load_url(url)
            self.window.set_title(f"HTML Viewer - {os.path.basename(file_path)}")

    def create_menu(self):
        """Create the application menu"""
        menu_items = [
            {
                "Open HTML File": self.open_file,
            }
        ]
        return menu_items

    def run(self):
        """Start the application"""
        self.window = webview.create_window(
            title="HTML Viewer",
            url="about:blank",
            width=1024,
            height=768,
            resizable=True,
            text_select=True
        )
        
        # Add a simple menu via JS evaluation after load if needed
        # For simplicity we use a button approach or just open dialog on start
        
        webview.start(self.on_start, debug=False)

    def on_start(self):
        """Called when the window is ready"""
        # Optionally open file dialog immediately
        # self.open_file()
        pass

if __name__ == "__main__":
    app = HTMLViewer()
    app.run()
