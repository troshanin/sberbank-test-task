package ru.sber.testtask.model;

/**
 * @author troshanin
 *         Date: 25.04.15
 */
public class DeleteOrder extends OrderRequest {
    public DeleteOrder() {
    }

    public DeleteOrder(String id, String book) {
        super(id, book);
    }
}
