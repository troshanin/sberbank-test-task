package ru.sber.testtask.model.concurrent;

import ru.sber.testtask.model.AddOrder;
import ru.sber.testtask.model.Operation;
import ru.sber.testtask.model.stat.PriceLevelStat;
import ru.sber.testtask.proc.MatchOrdersHandler;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * All operations with one instance of this class will be invoked in a single thread
 *
 * @author troshanin
 *         Date: 25.04.15
 */
class PriceLevelOrdersQueue {

    final MatchOrdersHandler matchOrdersHandler;
    final Book book;
    final String price;

    Operation currentOperation;
    int currentTotalVolume;
    int size;

    final OrderEntry head = new OrderEntry(this, null, null, 0);

    final Lock lock = new ReentrantLock();

    PriceLevelOrdersQueue(MatchOrdersHandler matchOrdersHandler, Book book, String price) {
        this.matchOrdersHandler = matchOrdersHandler;
        this.book = book;
        this.price = price;
    }

    /**
     * creates new order entry if current list of entries has same operation with new order
     * or
     * removes entries from list(starting from head) if current list of entries has NOT same operation with new order
     *
     * @param request new oder request
     */
    OrderEntry handleAddOrder(AddOrder request) {
        try {
            lock.lock();
            OrderEntry topEntry = getFirstOrderEntry();
            if (topEntry == null || request.getOperation() == topEntry.operation) {
                return addOrderEntry(createNewOrderEntry(request));
            } else {

                while (topEntry != null && request.getVolume() > 0) {

                    if (topEntry.volume > request.getVolume()) {
                        handleOrdersMatched(topEntry, request, request.getVolume());
                        topEntry.volume -= request.getVolume();
                        currentTotalVolume -= request.getVolume();
                        request.setVolume(0);
                        break;
                    } else {
                        handleOrdersMatched(topEntry, request, topEntry.volume);
                        request.decrementVolume(topEntry.volume);
                        removeOrderEntry(topEntry);
                        topEntry = getFirstOrderEntry();
                    }
                }

                if (request.getVolume() > 0) {
                    return addOrderEntry(createNewOrderEntry(request));
                }
            }
        } finally {
            lock.unlock();
        }

        if (size == 0) {
            book.unregisterQueue(this);
        }
        return null;
    }

    void handleRemoveOrder(OrderEntry orderEntry) {
        try {
            lock.lock();
            removeOrderEntry(orderEntry);
            if (size == 0) {
                book.unregisterQueue(this);
            }
        } finally {
            lock.unlock();
        }
    }

    private OrderEntry getFirstOrderEntry() {
        return head.next;
    }

    private OrderEntry createNewOrderEntry(AddOrder order) {
        return new OrderEntry(this, order.getId(), order.getOperation(), order.getVolume());
    }

    private OrderEntry addOrderEntry(final OrderEntry orderEntry) {
        OrderEntry last = head.prev;
        last = last == null ? head : last;

        last.next = orderEntry;
        head.prev = orderEntry;
        orderEntry.prev = last;
        orderEntry.next = head;

        book.registerOrderEntry(orderEntry);
        currentTotalVolume += orderEntry.volume;
        currentOperation = orderEntry.operation;
        size++;
        return orderEntry;
    }

    private void removeOrderEntry(OrderEntry orderEntry) {
        if (orderEntry.next == null) {
            return;
        }
        orderEntry.next.prev = orderEntry.prev;
        orderEntry.prev.next = orderEntry.next;

        orderEntry.next = orderEntry.prev = null;
        book.unregisterOrderEntry(orderEntry);
        currentTotalVolume -= orderEntry.volume;
        size--;
        if (size == 0) {
            head.next = head.prev = null;
        }
    }

    private void handleOrdersMatched(OrderEntry orderEntry, AddOrder order, int volume) {
        if (matchOrdersHandler != null) {
            matchOrdersHandler.matched(book.name, price, orderEntry.id, order.getId());
        }
    }

    public PriceLevelStat getPriceLevelStat() {
        try {
            lock.lock();
            return new PriceLevelStat(currentOperation, price, currentTotalVolume);
        } finally {
            lock.unlock();
        }
    }

}
