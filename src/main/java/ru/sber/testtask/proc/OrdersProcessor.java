package ru.sber.testtask.proc;

import ru.sber.testtask.model.AddOrder;
import ru.sber.testtask.model.DeleteOrder;
import ru.sber.testtask.model.stat.BookStatistic;

import java.util.List;

/**
 * @author troshanin
 *         Date: 26.04.15
 */
public interface OrdersProcessor {

    void handleAddOrder(final AddOrder req);

    void handleDeleteOrder(DeleteOrder req);

    void shutdownAndWaitAllFinished();

    List<BookStatistic> getBookStatistics();
}
