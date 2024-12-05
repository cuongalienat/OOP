package Controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import library.DbConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportController {

    @FXML
    private LineChart<String, Number> borrowingTrendsLineChart;

    @FXML
    private PieChart currentMonthPerformancePieChart;

    @FXML
    private PieChart offerCollectionPieChart;

    @FXML
    private NumberAxis yAxisBorrowingTrends;

    @FXML
    private void initialize() {
        populateBorrowingTrends();
        populateCurrentMonthPerformance();
        populateOfferCollectionDistribution();
    }

    /**
     * Populate the Borrowing Trends LineChart with real data.
     */
    private void populateBorrowingTrends() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Borrowing Trends (Last 6 Months)");

        String query = """
                WITH last_6_months AS (
                    SELECT DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL n MONTH), '%Y-%m') AS month
                    FROM (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL
                          SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS nums
                )
                SELECT m.month, IFNULL(COUNT(bl.book_id), 0) AS borrow_count
                FROM last_6_months m
                LEFT JOIN booklogs bl ON DATE_FORMAT(bl.borrowedDate, '%Y-%m') = m.month
                GROUP BY m.month
                ORDER BY m.month ASC;
                """;

        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String month = rs.getString("month");
                int borrowCount = rs.getInt("borrow_count");
                series.getData().add(new XYChart.Data<>(month, borrowCount));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        borrowingTrendsLineChart.getData().add(series);
    }

    /**
     * Populate the Library Performance PieChart (Current Month).
     */
    private void populateCurrentMonthPerformance() {
        String queryBorrowedBooks = """
                SELECT COUNT(DISTINCT book_id) AS borrowed_books
                FROM booklogs
                WHERE MONTH(borrowedDate) = MONTH(CURDATE()) AND YEAR(borrowedDate) = YEAR(CURDATE());
                """;

        String queryTotalBooks = """
                SELECT COUNT(*) AS total_books
                FROM book;
                """;

        try (Connection conn = DbConfig.connect()) {
            // Get borrowed books count
            int borrowedBooks = 0;
            try (PreparedStatement stmt = conn.prepareStatement(queryBorrowedBooks);
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    borrowedBooks = rs.getInt("borrowed_books");
                }
            }

            // Get total books count
            int totalBooks = 0;
            try (PreparedStatement stmt = conn.prepareStatement(queryTotalBooks);
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalBooks = rs.getInt("total_books");
                }
            }

            // Add data to PieChart
            int availableBooks = totalBooks - borrowedBooks;
            currentMonthPerformancePieChart.getData().addAll(
                    new PieChart.Data("Borrowed Books", borrowedBooks),
                    new PieChart.Data("Available Books", availableBooks));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Populate the Offer Collection Distribution PieChart.
     */
    private void populateOfferCollectionDistribution() {
        String query = """
                SELECT b.`Offer Collection` AS collection, COUNT(bl.book_id) AS borrow_count
                FROM booklogs bl
                JOIN book b ON bl.book_id = b.ID
                WHERE MONTH(bl.borrowedDate) = MONTH(CURDATE()) AND YEAR(bl.borrowedDate) = YEAR(CURDATE())
                GROUP BY b.`Offer Collection`
                ORDER BY borrow_count DESC;
                """;

        try (Connection conn = DbConfig.connect();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String collection = rs.getString("collection");
                int borrowCount = rs.getInt("borrow_count");
                offerCollectionPieChart.getData().add(new PieChart.Data(collection, borrowCount));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
