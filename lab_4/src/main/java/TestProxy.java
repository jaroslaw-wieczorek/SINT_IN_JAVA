import com.sun.net.httpserver.*;

import java.io.*;
import org.apache.commons.io.IOUtils;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;


public class TestProxy {


    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RootHandler());
        System.out.println("Starting server on port: " + port);
        server.start();
    }

    static class RootHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
//            try{
                //getting request info
                URI uri = exchange.getRequestURI();
                Headers headers = exchange.getRequestHeaders();
                String method = exchange.getRequestMethod();


                URL url = new URL(uri.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                //set up connection properties
                connection.setAllowUserInteraction(true);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod(method);

                for (String headerName : headers.keySet()) {
                    for (String headerValue : headers.get(headerName)) {
                        connection.addRequestProperty(headerName, headerValue);
                    }
                }
                byte[] requestBytes = new byte[0];
                if (exchange.getRequestHeaders().containsKey("Content-Length")) {
                    if (Integer.parseInt(exchange.getRequestHeaders().get("Content-Length").get(0)) >= 0) {
                        IOUtils.copy(exchange.getRequestBody(), connection.getOutputStream());
                    }
                }


                //connecting
                connection.connect();
                System.out.println("Connecting...");

                //getting the response
                //headers
                Map<String, List<String>> responseHeaders = connection.getHeaderFields();
                for (String headerName : responseHeaders.keySet()) {
                    if (headerName != null) {
                        exchange.getRequestHeaders().put(headerName, responseHeaders.get(headerName));
                        // exchange.getRequestHeaders().add(headerName, String.join(",", responseHeaders.get(headerName)));
                    }
                }


                //response code
//                int contentLength = 0;
//
//                if (responseHeaders.containsKey("Content-Length")) {
//                    String contentLengthStr = responseHeaders.get("Content-Length").get(0);
//                    contentLength = Integer.parseInt(contentLengthStr);
//                    System.out.println(contentLength);
//                    }

                int contentLength = 0;
                try {
                    if (responseHeaders.containsKey("Content-Length")) {
                        String contentLengthStr = responseHeaders.get("Content-Length").get(0);
                        contentLength = Integer.parseInt(contentLengthStr);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }


                System.out.println("content length: " + contentLength);

                exchange.sendResponseHeaders(connection.getResponseCode(), contentLength);
                //OutputStream os = connection.getOutputStream();


                System.out.println(connection.getResponseCode());

                //message body
                byte[] bytes;
                if (200 <= connection.getResponseCode() && connection.getResponseCode() < 300) {
                    InputStream is = connection.getInputStream();
                    bytes = IOUtils.toByteArray(is);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    is.close();
                    os.close();
                }
                else if (300 <= connection.getResponseCode() && connection.getResponseCode() < 400) {
                        InputStream is = connection.getInputStream();
                        bytes = IOUtils.toByteArray(is);
                        OutputStream os = exchange.getResponseBody();
                        os.write(bytes);
                        is.close();
                        os.close();
                } else {
                    InputStream es = connection.getErrorStream();
                    bytes = IOUtils.toByteArray(es);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    es.close();
                    os.close();
                }
                //message body
//                if (200 <= connection.getResponseCode() && connection.getResponseCode() < 300) {
//                    IOUtils.copy(connection.getInputStream(), exchange.getResponseBody());
//                    connection.getInputStream().close();
//                } else if (300 <= connection.getResponseCode() && connection.getResponseCode() < 400) {
//                    IOUtils.copy(connection.getInputStream(), exchange.getResponseBody());
//                    connection.getInputStream().close();
//                }else if (connection.getResponseCode() >= 400) {
//                    IOUtils.copy(connection.getErrorStream(), exchange.getResponseBody());
//                    connection.getErrorStream().close();
//                }

//                //
//                //exchange.getResponseBody().close();
//            } catch (Exception e) {
//                e.printStackTrace();
//                e.getLocalizedMessage();
//            }
        }
    }
}