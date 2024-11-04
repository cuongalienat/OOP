package library;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class GoogleBooksAPI {
    private static final String API_KEY = "YOUR_API_KEY";
    private static final int MAX_RETRIES = 5;
    private static final long INITIAL_WAIT_TIME = 1000; // 1 giây
    private static final long MAX_WAIT_TIME = 16000; // 16 giây
    private static final Random random = new Random();

    // Sử dụng ExecutorService với daemon threads
    private static final ExecutorService executor = Executors.newFixedThreadPool(4, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true); // Thiết lập luồng là daemon
            return t;
        }
    });

    // Tìm kiếm sách theo tên và trả về kết quả dưới dạng JSON String qua callback
    public static void searchBookByTitle(String title, Consumer<String> callback) {
        Task<Void> task = new Task<Void>() {   
            @Override
            protected Void call() throws Exception {
                int retryCount = 0;
                long waitTime = INITIAL_WAIT_TIME;

                while (retryCount < MAX_RETRIES) {
                    try {
                        URI uri = new URI("https", "www.googleapis.com", "/books/v1/volumes",
                                "q=" + title + "&key=" + API_KEY, null);
                        URL url = uri.toURL();
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        int responseCode = connection.getResponseCode();

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            try (BufferedReader in = new BufferedReader(
                                    new InputStreamReader(connection.getInputStream()))) {
                                StringBuilder response = new StringBuilder();
                                String inputLine;
                                while ((inputLine = in.readLine()) != null) {
                                    response.append(inputLine);
                                }
                                // Cập nhật UI trên luồng JavaFX Application Thread
                                Platform.runLater(() -> callback.accept(response.toString()));
                            }
                            break;
                        } else if (responseCode == 429) {
                            retryCount++;
                            String retryAfter = connection.getHeaderField("Retry-After");
                            if (retryAfter != null) {
                                waitTime = Long.parseLong(retryAfter) * 1000;
                            } else {
                                waitTime = Math.min(waitTime * 2, MAX_WAIT_TIME);
                                waitTime += random.nextInt(1000); // Thêm jitter
                            }
                            System.out.println("Received 429. Retrying in " + waitTime + " ms. Attempt " + retryCount + "/" + MAX_RETRIES);
                            Thread.sleep(waitTime);
                        } else if (responseCode == 400) {
                            System.out.println("Bad Request: Kiểm tra lại tham số yêu cầu.");
                            break;
                        } else {
                            System.out.println("Request failed with response code: " + responseCode);
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        retryCount++;
                        System.out.println("Exception occurred. Retrying in " + waitTime + " ms. Attempt " + retryCount + "/" + MAX_RETRIES);
                        Thread.sleep(waitTime);
                    }
                }

                if (retryCount == MAX_RETRIES) {
                    Platform.runLater(() -> callback.accept("{\"error\":\"Đã đạt số lần thử lại tối đa. Vui lòng thử lại sau.\"}"));
                }

                return null;
            }
        };

        executor.submit(task);
    }

    public static void shutdownExecutor() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
            System.out.println("ExecutorService đã được tắt.");
        } catch (InterruptedException e) {
            executor.shutdownNow();
            System.out.println("ExecutorService bị gián đoạn trong quá trình tắt.");
        }
    }
}
