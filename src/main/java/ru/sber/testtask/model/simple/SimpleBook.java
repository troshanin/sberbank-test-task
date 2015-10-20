package ru.sber.testtask.model.simple;

import ru.sber.testtask.model.AddOrder;
import ru.sber.testtask.model.DeleteOrder;
import ru.sber.testtask.proc.MatchOrdersHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author troshanin
 *         Date: 25.04.15
 */
public class SimpleBook {

    final String name;

    Map<String, SimplePriceLevelQueue> queues = new HashMap<>();
    Map<String, SimpleOrder> orders = new HashMap<>();

    public SimpleBook(String name) {
        this.name = name;
    }

    public void handleAddOrder(MatchOrdersHandler matchOrdersHandler, AddOrder req) {
        SimplePriceLevelQueue queue = queues.get(req.getPrice());
        if (queue == null) {
            queue = new SimplePriceLevelQueue(matchOrdersHandler, this, req.getPrice());
            queues.put(req.getPrice(), queue);
        }

        queue.handleAddOrder(req);

        if (queue.currentTotalVolume == 0) {
            queues.remove(req.getPrice());
        }
    }

    public void handleDeleteOrder(DeleteOrder req) {
        SimpleOrder order = orders.get(req.getId());
        if (order == null) {
            return;
        }
        order.queue.currentTotalVolume -= order.volume;
        order.deleted = true;
    }

    public String getName() {
        return name;
    }

    public Map<String, SimplePriceLevelQueue> getQueues() {
        return queues;
    }

    void registerOrder(SimpleOrder order) {
        orders.put(order.id, order);
    }

    void unregisterOrder(SimpleOrder order) {
        orders.remove(order.id);
    }
}