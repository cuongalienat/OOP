package library;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.function.Consumer;

public class GoogleBooksAPI {
    private static final String API_KEY = "AIzaSyB6KChEpyv8kVxx_3_D0FpHsjw1sOUnHBA";

    // Tìm kiếm sách theo tên và trả về kết quả dưới dạng JSON String qua callback
    public static void searchBookByTitle(String title, Consumer<String> callback) {
        try {
            // Xây dựng URL với API key và tiêu đề sách
            URI uri = new URI("https", "www.googleapis.com", "/books/v1/volumes",
                    "q=" + title + "&key=" + API_KEY, null);

            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Gọi callback để xử lý JSON response
                callback.accept(response.toString());

            } else {
                System.out.println("Request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}