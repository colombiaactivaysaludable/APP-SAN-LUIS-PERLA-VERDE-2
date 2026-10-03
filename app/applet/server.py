#!/usr/bin/env python3
import http.server
import os
import socketserver
import sys

PORT = 3000
DIRECTORY = "/app/applet/public"

class CustomHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def end_headers(self):
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Cache-Control", "no-cache, must-revalidate")
        super().end_headers()

    def guess_type(self, path):
        if path.endswith(".apk"):
            return "application/vnd.android.package-archive"
        if path.endswith(".json"):
            return "application/json"
        if path.endswith(".js"):
            return "application/javascript"
        return super().guess_type(path)

    def do_GET(self):
        if self.path.startswith("/PerlaVerde_ColoniaSanLuis.apk"):
            apk_path = "/app/applet/PerlaVerde_ColoniaSanLuis.apk"
            if os.path.exists(apk_path):
                self.send_response(200)
                self.send_header("Content-Type", "application/vnd.android.package-archive")
                self.send_header("Content-Disposition", 'attachment; filename="PerlaVerde_ColoniaSanLuis.apk"')
                self.send_header("Content-Length", str(os.path.getsize(apk_path)))
                self.end_headers()
                with open(apk_path, "rb") as f:
                    while chunk := f.read(65536):
                        self.wfile.write(chunk)
                return
        super().do_GET()

class ThreadingTCPServer(socketserver.ThreadingMixIn, socketserver.TCPServer):
    allow_reuse_address = True

if __name__ == "__main__":
    os.chdir(DIRECTORY)
    with ThreadingTCPServer(("", PORT), CustomHandler) as httpd:
        print(f"Perla Verde Server running on port {PORT}")
        httpd.serve_forever()
