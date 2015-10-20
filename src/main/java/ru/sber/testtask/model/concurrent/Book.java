package ru.sber.testtask.model.concurrent;

import ru.sber.testtask.model.AddOrder;
import ru.sber.testtask.model.DeleteOrder;
import ru.sber.testtask.proc.MatchOrdersHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author troshanin
 *         Date: 25.04.15
 */
public class Book {

    final BooksRegistry booksRegistry;

    final String name;

    final ConcurrentMap<String, PriceLevelOrdersQueue> priceQueues = new ConcurrentHashMap<>();

    final ConcurrentMap<String, OrderEntry> orders = new ConcurrentHashMap<>();

    final ConcurrentMap<String, AddOrder> pendingRequests = new ConcurrentHashMap<>();

    final ReadWriteLock lock = new ReentrantReadWriteLock();
    final Lock readLock = lock.readLock();
    final Lock writeLock = lock.writeLock();

    volatile long accessCount;
    volatile boolean removed;


    Book(BooksRegistry booksRegistry, String name) {
        this.booksRegistry = booksRegistry;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void handleAddOrder(final AddOrder request, ExecutorService executor, final MatchOrdersHandler matchOrdersHandler) {

        if (!registerRequest(request)) {
            System.err.println("order with id " + request.getId() + " already exists");
            return;
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    PriceLevelOrdersQueue queue = priceQueues.get(request.getPrice());
                    if (queue == null) {
                        queue = new PriceLevelOrdersQueue(matchOrdersHandler, Book.this, request.getPrice());
                        registerQueue(queue);
                    }
                    queue.handleAddOrder(request);
                } finally {
                    pendingRequests.remove(request.getId());
                }
            }
        });

    }

    public void handleDeleteOrder(final DeleteOrder req, ExecutorService executor) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                OrderEntry orderEntry = getOrderEntry(req.getId());
                if (orderEntry != null) {
                    orderEntry.queue.handleRemoveOrder(orderEntry);
                }
            }
        });

    }

    boolean registerRequest(AddOrder request) {
        if (pendingRequests.putIfAbsent(request.getId(), request) != null) {
            return false;
        } else if (getOrderEntry(request.getId()) != null) {
            pendingRequests.remove(request.getId());
            return false;
        }
        return true;
    }

    OrderEntry getOrderEntry(String id) {
        return orders.get(id);
    }

    void registerQueue(PriceLevelOrdersQueue queue) {
        priceQueues.put(queue.price, queue);
    }

    void unregisterQueue(PriceLevelOrdersQueue queue) {
        priceQueues.remove(queue.price);
        if (priceQueues.isEmpty()) {
            booksRegistry.handleBookEmptyEvent(this);
        }
    }

    void registerOrderEntry(OrderEntry orderEntry) {
        orders.put(orderEntry.id, orderEntry);
    }

    void unregisterOrderEntry(OrderEntry orderEntry) {
        orders.remove(orderEntry.id);
    }

    public String getOrderPrice(String orderId) {
        AddOrder req = pendingRequests.get(orderId);
        if (req != null) {
            return req.getPrice();
        }
        OrderEntry orderEntry = getOrderEntry(orderId);
        return orderEntry != null ? orderEntry.queue.price : null;
    }
}
