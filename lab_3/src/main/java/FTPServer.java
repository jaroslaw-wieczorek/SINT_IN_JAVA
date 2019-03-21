import com.sun.net.httpserver.*;

import java.io.*;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FTPServer {

    private static String rootPath;

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Directory path was not provided!");
        } else if (args.length > 1) {
            System.out.println("You can enter only one path!");
        } else {

            rootPath = args[0];
            // Sprawdzanie czy ścieżka prowadzi do katalogu
            if (!new File(rootPath).isDirectory()) {
                System.out.println("It is not a directory!");
            } else {
                int port = 8000;
                HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
                server.createContext("/", new RootHandler());
                System.out.println("Starting server on port: " + port);
                server.start();
            }
        }
    }

    public static boolean pathTraversalPreventDetection(File root, File file) {
        try {

            return file.getCanonicalPath().startsWith(root.getCanonicalPath());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    static class RootHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {

            try {
                URI myURI = exchange.getRequestURI();
                File currentFileOrDir = new File(rootPath, myURI.getPath());

                try {
                    System.out.println("CanRead? :" + currentFileOrDir.canRead());
                } catch (SecurityException e) {
                    System.out.println("Error: " + e.getMessage());
                }
                if (!currentFileOrDir.exists()) {
                    System.out.println("Not Exists");

                    byte[] info = "File not exist".getBytes();

                    exchange.sendResponseHeaders(404, info.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(info);
                    os.close();

                }

                else if (!pathTraversalPreventDetection(new File(rootPath), currentFileOrDir)) {
                    System.out.println("Path Traversal Prevent Detection");

                    byte[] info = "Path Traversal Prevent Detection".getBytes();

                    exchange.sendResponseHeaders(403, info.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(info);
                    os.close();

                } else if (currentFileOrDir.isDirectory()) {
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");

                    StringBuilder html = new StringBuilder();
                    html.append("<!DOCTYPE html>");
                    html.append("<html>");
                    html.append("<head><title>" + currentFileOrDir.getName() + "</title>");
                    html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
                    html.append("</head>");
                    html.append("<body>");


                    File[] directoryFiles = currentFileOrDir.listFiles();

                    for (int i = 0; i < directoryFiles.length; i++) {
                        if (directoryFiles[i].isDirectory()) {
                            html.append("<i>folder</i>");
                        } else {
                            html.append("<i>plik</i>");
                        }
                        html.append("<a href=\"http://localhost:8000");
                        html.append(myURI.getPath());
                        if (!myURI.toString().equals("/")) {
                            html.append("/");
                        }
                        html.append(directoryFiles[i].getName());
                        html.append("\">");
                        html.append(directoryFiles[i].getName());
                        html.append("</a><br>");
                    }

                    html.append("</body>");
                    html.append("</html>");

                    String myHTML = html.toString();
                    byte [] data = myHTML.getBytes();

                    exchange.sendResponseHeaders(200, data.length);
                    OutputStream os = exchange.getResponseBody();

                    System.out.println(myHTML);
                    os.write(data);
                    os.close();

                } else if (currentFileOrDir.isFile()) {
                    byte[] bytes = Files.readAllBytes(Paths.get(currentFileOrDir.getPath()));

                    exchange.getResponseHeaders().set("Content-Type", "application/octet-stream"); //zawsze będzie pobierać
                    exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"" + currentFileOrDir.getName() + "\"");
                    // powoduje podobny efekt ze przeglądarka będzie pobierać
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                } else {

                    exchange.sendResponseHeaders(400, -1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

