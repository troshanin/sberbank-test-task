package ru.sber.testtask.proc;

import ru.sber.testtask.model.AddOrder;
import ru.sber.testtask.model.DeleteOrder;
import ru.sber.testtask.model.Operation;
import ru.sber.testtask.model.simple.SimpleBook;
import ru.sber.testtask.model.simple.SimpleOrder;
import ru.sber.testtask.model.simple.SimplePriceLevelQueue;
import ru.sber.testtask.model.stat.BookStatistic;
import ru.sber.testtask.model.stat.PriceLevelStat;

import java.util.*;

/**
 * @author troshanin
 *         Date: 25.04.15
 */
public class OrdersProcessorSimpleImpl extends BaseOrdersProcessor {

    Map<String, SimpleBook> books = new HashMap<>();

    public OrdersProcessorSimpleImpl(MatchOrdersHandler matchOrdersHandler) {
        super(matchOrdersHandler);
    }

    @Override
    public void handleAddOrder(AddOrder req) {
        SimpleBook book = books.get(req.getBook());
        if (book == null) {
            book = new SimpleBook(req.getBook());
            books.put(req.getBook(), book);
        }
        book.handleAddOrder(matchOrdersHandler, req);
    }

    @Override
    public void handleDeleteOrder(DeleteOrder req) {
        SimpleBook book = books.get(req.getBook());
        if (book == null) {
            book = new SimpleBook(req.getBook());
            books.put(req.getBook(), book);
        }
        book.handleDeleteOrder(req);
    }

    @Override
    public void shutdownAndWaitAllFinished() {
    }

    @Override
    public List<BookStatistic> getBookStatistics() {
        List<BookStatistic> result = new ArrayList<>(books.size());

        for (SimpleBook book : books.values()) {
            List<PriceLevelStat> bids = new ArrayList<>();
            List<PriceLevelStat> asks = new ArrayList<>();

            for (SimplePriceLevelQueue queue : book.getQueues().values()) {
                SimpleOrder topOrder;
                if ((topOrder = queue.getTopOrder()) == null) {
                    continue;
                }
                switch (topOrder.getOperation()) {
                    case BUY:
                        bids.add(new PriceLevelStat(Operation.BUY, queue.getPrice(), queue.getCurrentTotalVolume()));
                        break;
                    case SELL:
                        asks.add(new PriceLevelStat(Operation.SELL, queue.getPrice(), queue.getCurrentTotalVolume()));
                        break;
                }
            }

            result.add(new BookStatistic(book.getName(), bids, asks));
        }

        Collections.sort(result);
        return result;
    }

}

