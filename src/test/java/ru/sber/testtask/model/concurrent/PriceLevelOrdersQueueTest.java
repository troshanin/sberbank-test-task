package ru.sber.testtask.model.concurrent;

import junit.framework.Assert;
import org.junit.Test;
import ru.sber.testtask.model.AddOrder;
import ru.sber.testtask.model.Operation;

/**
 * User: troshanin
 * Date: 26.04.15
 */
public class PriceLevelOrdersQueueTest {

    @Test
    public void testHandleAddOrder() throws Exception {
        PriceLevelOrdersQueue queue = new PriceLevelOrdersQueue(null, new Book(new BooksRegistry(), "book"), "100.55");

        queue.handleAddOrder(new AddOrder("1", "book", Operation.BUY, "100.5", 10));
        Assert.assertEquals(queue.currentOperation, Operation.BUY);
        Assert.assertEquals(queue.currentTotalVolume, 10);
        Assert.assertEquals(queue.size, 1);
        queue.handleAddOrder(new AddOrder("2", "book", Operation.BUY, "100.5", 5));
        Assert.assertEquals(queue.currentOperation, Operation.BUY);
        Assert.assertEquals(queue.currentTotalVolume, 15);
        Assert.assertEquals(queue.size, 2);
        queue.handleAddOrder(new AddOrder("3", "book", Operation.BUY, "100.5", 7));
        Assert.assertEquals(queue.currentOperation, Operation.BUY);
        Assert.assertEquals(queue.currentTotalVolume, 22);
        Assert.assertEquals(queue.size, 3);

        queue.handleAddOrder(new AddOrder("4", "book", Operation.SELL, "100.5", 5));
        Assert.assertEquals(queue.currentOperation, Operation.BUY);
        Assert.assertEquals(queue.currentTotalVolume, 17);
        Assert.assertEquals(queue.size, 3);
        queue.handleAddOrder(new AddOrder("5", "book", Operation.SELL, "100.5", 5));
        Assert.assertEquals(queue.currentOperation, Operation.BUY);
        Assert.assertEquals(queue.currentTotalVolume, 12);
        Assert.assertEquals(queue.size, 2);
        queue.handleAddOrder(new AddOrder("6", "book", Operation.SELL, "100.5", 7));
        Assert.assertEquals(queue.currentOperation, Operation.BUY);
        Assert.assertEquals(queue.currentTotalVolume, 5);
        Assert.assertEquals(queue.size, 1);

        queue.handleAddOrder(new AddOrder("7", "book", Operation.BUY, "100.5", 5));
        Assert.assertEquals(queue.currentOperation, Operation.BUY);
        Assert.assertEquals(queue.currentTotalVolume, 10);
        Assert.assertEquals(queue.size, 2);

        OrderEntry oe1 = queue.handleAddOrder(new AddOrder("6", "book", Operation.SELL, "100.5", 17));
        Assert.assertEquals(queue.currentOperation, Operation.SELL);
        Assert.assertEquals(queue.currentTotalVolume, 7);
        Assert.assertEquals(queue.size, 1);

        OrderEntry oe2 = queue.handleAddOrder(new AddOrder("7", "book", Operation.SELL, "100.5", 10));
        Assert.assertEquals(queue.currentOperation, Operation.SELL);
        Assert.assertEquals(queue.currentTotalVolume, 17);
        Assert.assertEquals(queue.size, 2);

        queue.handleRemoveOrder(oe1);
        Assert.assertEquals(queue.currentOperation, Operation.SELL);
        Assert.assertEquals(queue.currentTotalVolume, 10);
        Assert.assertEquals(queue.size, 1);

        queue.handleRemoveOrder(oe2);
        Assert.assertEquals(queue.currentTotalVolume, 0);
        Assert.assertEquals(queue.size, 0);
    }

}
