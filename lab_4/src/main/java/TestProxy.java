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

    public static BlackList blackList = new BlackList();
    public static Statistics statistics = new Statistics();

    public static void main(String[] args) throws Exception {

        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RootHandler());
        System.out.println("Starting server on port: " + port);
        server.start();
    }

    static class RootHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {

                statistics.readDataFromFile();

                ConnectionData connectionData = new ConnectionData();


                URI uri = exchange.getRequestURI();
                Headers requestHeaders = exchange.getRequestHeaders();
                String method = exchange.getRequestMethod();
                URL url = new URL(uri.toString());

                blackList.getBlackList();
                if (blackList.contains(url.toString()))
                {
                    byte[] info = "Fobridden 403".getBytes();

                    System.out.println("Fobridden URL - Nie przejdzeisz");
                    exchange.sendResponseHeaders(403, info.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(info);
                    os.close();
                }
                else {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    //set up connection properties
                    connection.setAllowUserInteraction(true);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setInstanceFollowRedirects(false);
                    connection.setRequestMethod(method);

                    for (String headerName : requestHeaders.keySet()) {
                        for (String headerValue : requestHeaders.get(headerName)) {
                            if (headerName.equals("Transfer-Encoding")) {
                                continue;
                            }

                            connection.addRequestProperty(headerName, headerValue);
                        }
                    }
                    byte[] requestBytes = new byte[0];

                    //connecting
                    connection.connect();
                    System.out.println("Connecting...");

                    if (exchange.getRequestHeaders().containsKey("Content-Length")) {
                        if ((Integer.parseInt(exchange.getRequestHeaders().get("Content-Length").get(0)))>=0) {
                            InputStream is = exchange.getRequestBody();
                            OutputStream os = connection.getOutputStream();
                            requestBytes = IOUtils.toByteArray(is);
                            os.write(requestBytes);
                            is.close();
                            os.close();
                        }
                    }

                    //getting the response
                    //headers
                    Map<String, List<String>> responseHeaders = connection.getHeaderFields();
                    System.out.println(responseHeaders);

                    for (String headerName : responseHeaders.keySet()) {
                        if (headerName != null) {

                            //exchange.getRequestHeaders().put(headerName, responseHeaders.get(headerName));
                            exchange.getResponseHeaders().put(headerName, responseHeaders.get(headerName));
                            //exchange.getRequestHeaders().add(headerName, String.join(",", responseHeaders.get(headerName)));
                        }
                    }

                    int contentLength = 0;
                    try {
                        if (responseHeaders.containsKey("Content-Length")) {
                            String contentLengthStr = responseHeaders.get("Content-Length").get(0);
                            contentLength = Integer.parseInt(contentLengthStr);
                        }
                    } catch (Exception e) {
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
                    } else if (300 <= connection.getResponseCode() && connection.getResponseCode() < 400) {
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
                    connectionData.url = exchange.getRequestHeaders().get("Host").get(0);
                    connectionData.dataSend = (long) requestBytes.length;
                    connectionData.dataGot = (long) bytes.length;
                    statistics.insertData(connectionData);

//                //
//                //exchange.getResponseBody().close();
//            } catch (Exception e) {
//                e.printStackTrace();
//                e.getLocalizedMessage();
//            }
                }
        }
    }
}