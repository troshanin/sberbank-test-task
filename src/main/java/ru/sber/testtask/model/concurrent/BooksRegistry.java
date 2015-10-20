package ru.sber.testtask.model.concurrent;

import ru.sber.testtask.model.stat.BookStatistic;
import ru.sber.testtask.model.stat.PriceLevelStat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author troshanin
 *         Date: 25.04.15
 */
public class BooksRegistry {

    ConcurrentMap<String, Book> books = new ConcurrentHashMap<>();

    public Book getBook(final String bookId) {
        Book book = books.get(bookId);
        if (book == null) {
            book = new Book(this, bookId);
            books.put(bookId, book);
        }
        try {
            book.readLock.lock();
            if (book.removed) {
                return getBook(bookId);
            }
            book.accessCount++;
        } finally {
            book.readLock.unlock();
        }
        return book;
    }

    void handleBookEmptyEvent(final Book book) {
        final long expectedAccessCount = book.accessCount;
        try {
            book.writeLock.lock();
            if (!book.removed && expectedAccessCount == book.accessCount) {
                books.remove(book.name);
                book.removed = true;
            }
        } finally {
            book.writeLock.unlock();
        }
    }

    public List<BookStatistic> getBookStatistics() {
        List<BookStatistic> result = new ArrayList<>(books.size());
        for (Book book : books.values()) {
            List<PriceLevelStat> bids = new ArrayList<>();
            List<PriceLevelStat> asks = new ArrayList<>();
            for (PriceLevelOrdersQueue queue : book.priceQueues.values()) {
                PriceLevelStat priceLevelStat = queue.getPriceLevelStat();
                if (priceLevelStat.getOperation() == null || priceLevelStat.getVolume() == 0) {
                    continue;
                }
                switch (priceLevelStat.getOperation()) {
                    case BUY:
                        bids.add(priceLevelStat);
                        break;
                    case SELL:
                        asks.add(priceLevelStat);
                        break;
                }
            }
            if (bids.isEmpty() && asks.isEmpty()) {
                continue;
            }
            result.add(new BookStatistic(book.name, bids, asks));
        }
        Collections.sort(result);
        return result;
    }

}
