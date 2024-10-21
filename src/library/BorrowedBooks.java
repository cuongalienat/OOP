package library;

import java.time.LocalDate;

public class BorrowedBooks extends Book {
    private LocalDate borrowDate;
    private LocalDate dueDate;

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public BorrowedBooks(String collection, String name, String author, int id, LocalDate borrowDate, LocalDate dueDate) {
        super(collection, name, author, id);
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }
}
