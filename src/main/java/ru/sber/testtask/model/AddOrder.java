package ru.sber.testtask.model;

/**
 * @author troshanin
 *         Date: 25.04.15
 */
public class AddOrder extends OrderRequest {

    Operation operation;

    String price;

    int volume;

    public AddOrder() {
    }

    public AddOrder(String id, String book, Operation operation, String price, int volume) {
        super(id, book);
        this.operation = operation;
        this.price = price;
        this.volume = volume;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void decrementVolume(int dec) {
        volume -= dec;
    }
}
