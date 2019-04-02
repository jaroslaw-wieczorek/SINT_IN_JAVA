import com.sun.net.httpserver.*;

import java.io.*;


import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;



public class ProxyServer {

    public static void main(String[] args) throws Exception {

        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new ProxyHandler());

        System.out.println("Starting server on port:" + port);
        server.start();

    }

    private static HttpURLConnection SetupHeaders(HttpURLConnection connection, Headers headers){
        /*
         * Method to setup headers
         *
         */

        for (String header_name : headers.keySet()){
            for (String header_value : headers.get(header_name))
            {
                //if (!header_name.equals("Transfer-Encoding")) {

                // put headers in connection
                connection.addRequestProperty(header_name, header_value);

                //}
            }
        }
        return connection;
    }

    private static HttpURLConnection putNotNullRequestProperties(HttpURLConnection connection, Headers headers)
        /*
        * Method to
        *
        */
    {
        for (String header_name : headers.keySet())
        {
            if (header_name != null) //&& (!header_name.equals("Transfer-Encoding")))
            {

                connection.getRequestProperties().put(header_name, headers.get(header_name));

            }
        }
        return connection;
    }


    public static class ProxyHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {

            // Load data about requst
            URI uri = exchange.getRequestURI();
            Headers headers = exchange.getRequestHeaders();
            String method = exchange.getRequestMethod();


            URL url = new URL(uri.toString());
            // Throw URL.openConnection() to HttpURLConnection
            // URL was created from uri (uri.toString) string to HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();


            //Turn on follow redirects
            //connection.setInstanceFollowRedirects(true);

            //Setup allow users interactions
            connection.setAllowUserInteraction(true);

            // Turn on user input
            connection.setDoInput(true);

            //Turn on user output
            connection.setDoOutput(true);

            //Turn off
            connection.setUseCaches(false);
            connection.setRequestMethod(method);

            // Setup got request headers
            connection = SetupHeaders(connection, headers);


            if (exchange.getRequestHeaders().containsKey("Content-Length")) {

                String str_content_length = exchange.getRequestHeaders().get("Content-Length").get(0);
                int conntent_length = Integer.parseInt(str_content_length);

                if (conntent_length >= 0) {

                    IOUtils.copy(exchange.getRequestBody(), connection.getOutputStream());

                    // connecting
                    connection.connect();
                    System.out.println("[*] Connecting ");

                }
            }

            // Get data about response

            Map<String, List<String>> responseHeaders = connection.getHeaderFields();
            System.out.println(responseHeaders);

            connection = putNotNullRequestProperties(connection, headers);


            // Setup response
            int content_length = 0;
            try{
                if (responseHeaders.containsKey("Content-Length")){

                    String content_length_str =  responseHeaders.get("Content-Length").get(0);
                    content_length = Integer.parseInt(content_length_str);

                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            // Print length of content
            System.out.println("[*] Response content length: "+content_length);
            exchange.sendResponseHeaders(connection.getResponseCode(), content_length);

            // Print response code
            System.out.println(connection.getResponseCode());

            //message body
            if ( connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {

                IOUtils.copy(connection.getInputStream(),exchange.getResponseBody());

            } else {

                IOUtils.copy(connection.getErrorStream(),exchange.getResponseBody());

            }
            connection.getInputStream().close();
            exchange.getResponseBody().close();


        }

    }
}

