package ru.sber.testtask.model;

/**
 * @author troshanin
 *         Date: 25.04.15
 */
public abstract class OrderRequest {

    String id;

    String book;

    protected OrderRequest() {
    }

    protected OrderRequest(String id, String book) {
        this.id = id;
        this.book = book;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }
}
