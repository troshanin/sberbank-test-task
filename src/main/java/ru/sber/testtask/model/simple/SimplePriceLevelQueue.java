package ru.sber.testtask.model.simple;

import ru.sber.testtask.model.AddOrder;
import ru.sber.testtask.model.DeleteOrder;
import ru.sber.testtask.proc.MatchOrdersHandler;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author troshanin
 *         Date: 25.04.15
 */
public class SimplePriceLevelQueue {
    final MatchOrdersHandler matchOrdersHandler;
    final SimpleBook book;
    final String price;
    LinkedList<SimpleOrder> orders = new LinkedList<>();
    int currentTotalVolume;

    SimplePriceLevelQueue(MatchOrdersHandler matchOrdersHandler, SimpleBook book, String price) {
        this.matchOrdersHandler = matchOrdersHandler;
        this.book = book;
        this.price = price;
    }

    public void handleAddOrder(AddOrder req) {

        SimpleOrder topOrder = getTopOrder();

        if (topOrder == null || topOrder.operation == req.getOperation()) {
            addOrder(req);
        } else {

            while (topOrder != null) {
                if (topOrder.volume >= req.getVolume()) {
                    handleOrdersMatched(topOrder, req, req.getVolume());
                    topOrder.volume -= req.getVolume();
                    currentTotalVolume -= req.getVolume();
                    req.setVolume(0);
                    break;
                } else {
                    handleOrdersMatched(topOrder, req, req.getVolume());
                    req.decrementVolume(topOrder.volume);
                    book.unregisterOrder(orders.removeFirst());
                    currentTotalVolume -= topOrder.volume;
                    topOrder = getTopOrder();
                }
            }

            if (req.getVolume() > 0) {
                addOrder(req);
            }
        }

    }

    private void handleOrdersMatched(SimpleOrder topOrder, AddOrder req, int volume) {
        if (matchOrdersHandler != null) {
            matchOrdersHandler.matched(book.name, price, topOrder.id, req.getId());
        }
    }

    private void addOrder(AddOrder req) {
        SimpleOrder order = new SimpleOrder(this, req.getId(), req.getOperation(), req.getVolume());
        orders.add(order);
        currentTotalVolume += req.getVolume();
        book.registerOrder(order);
    }

    public SimpleOrder getTopOrder() {
        SimpleOrder topOrder;
        while ((topOrder = orders.peek()) != null) {
            if (!topOrder.deleted) {
                break;
            } else {
                book.unregisterOrder(orders.removeFirst());
            }
        }
        return topOrder;
    }

    public String getPrice() {
        return price;
    }

    public int getCurrentTotalVolume() {
        return currentTotalVolume;
    }
}
