package ru.sber.testtask.proc;

import ru.sber.testtask.model.AddOrder;
import ru.sber.testtask.model.DeleteOrder;
import ru.sber.testtask.model.concurrent.Book;
import ru.sber.testtask.model.concurrent.BooksRegistry;
import ru.sber.testtask.model.stat.BookStatistic;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static sun.misc.Hashing.stringHash32;

/**
 * @author troshanin
 *         Date: 25.04.15
 */
public class OrdersProcessorConcurrentImpl extends BaseOrdersProcessor {

    final ExecutorService[] executors;
    final BooksRegistry booksRegistry;

    public OrdersProcessorConcurrentImpl(MatchOrdersHandler matchOrdersHandler, int concurrentLevel) {
        this(matchOrdersHandler, concurrentLevel, new BooksRegistry());
    }

    public OrdersProcessorConcurrentImpl(MatchOrdersHandler matchOrdersHandler, int concurrentLevel, BooksRegistry booksRegistry) {
        super(matchOrdersHandler);
        executors = new ExecutorService[concurrentLevel];
        for(int i = 0; i< concurrentLevel; i++) {
            executors[i] = Executors.newSingleThreadExecutor();
        }
        this.booksRegistry = booksRegistry;
    }

    public void shutdownAndWaitAllFinished() {
        for(ExecutorService executor : executors) {
            executor.shutdown();
        }
        for(ExecutorService executor : executors) {
            try {
                while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                }
            } catch (InterruptedException e) {
                // skip it
            }
        }
    }

    public void handleAddOrder(final AddOrder req) {
        final Book book = booksRegistry.getBook(req.getBook());
        book.handleAddOrder(req, getExecutor(book.getName(), req.getPrice()), matchOrdersHandler);
    }

    public void handleDeleteOrder(DeleteOrder req) {
        final Book book = booksRegistry.getBook(req.getBook());
        final String orderPrice = book.getOrderPrice(req.getId());
        if (orderPrice == null) {
            return;
        }
        book.handleDeleteOrder(req, getExecutor(book.getName(), orderPrice));
    }

    @Override
    public List<BookStatistic> getBookStatistics() {
        return booksRegistry.getBookStatistics();
    }

    private ExecutorService getExecutor(String book, String price) {
        int hash = stringHash32(book) * stringHash32(price) >> 7;
        int index = (hash & Integer.MAX_VALUE) % executors.length;
        return executors[index];
    }

}
