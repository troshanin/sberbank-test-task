package ru.sber.testtask.model.stat;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author troshanin
 *         Date: 26.04.15
 */
public class BookStatistic implements Comparable<BookStatistic> {

    final String book;

    // buy
    final List<PriceLevelStat> bids;

    // sell
    final List<PriceLevelStat> asks;

    int maxBidTextLen;
    String emptyValue;
    String valueAsString;

    public BookStatistic(String book, List<PriceLevelStat> bids, List<PriceLevelStat> asks) {
        this.book = book;
        this.bids = bids;
        this.asks = asks;

        init();
    }

    void init() {
        maxBidTextLen = bids.isEmpty() ? 5 : bids.get(0).valueAsString.length();
        Collections.sort(bids, new Comparator<PriceLevelStat>() {
            @Override
            public int compare(PriceLevelStat pl1, PriceLevelStat pl2) {
                maxBidTextLen = Math.max(maxBidTextLen, Math.max(pl1.valueAsString.length(), pl2.valueAsString.length()));
                return pl1.compareTo(pl2);
            }
        });
        Collections.sort(asks);
        StringBuilder buf = new StringBuilder(maxBidTextLen);
        for (int i = 0; i < maxBidTextLen; i++) {
            buf.append('-');
        }
        this.emptyValue = buf.toString();
        this.valueAsString = getValueAsString();
    }


    String getValueAsString() {
        StringBuilder builder = new StringBuilder("Order book: " + book).append("\n");
        builder.append(String.format("%-" + maxBidTextLen +"s", "BID")).append("   ").append("ASK").append("\n");
        // builder.append("Qty@Price – Qty@Price").append("\n");
        Iterator<PriceLevelStat> bidsIter = bids.iterator();
        Iterator<PriceLevelStat> asksIter = asks.iterator();
        while (bidsIter.hasNext() || asksIter.hasNext()) {

            String bid = getPriceLevelAsString(bidsIter);
            String ask = getPriceLevelAsString(asksIter);
            builder.append(String.format("%-" + maxBidTextLen +"s", bid));
            builder.append(" - ").append(ask).append("\n");
        }
        return builder.toString();
    }

    String getPriceLevelAsString(Iterator<PriceLevelStat> iter) {
        return iter.hasNext() ? iter.next().valueAsString : emptyValue;
    }

    @Override
    public int compareTo(BookStatistic that) {
        return this.book.compareTo(that.book);
    }

    @Override
    public String toString() {
        return valueAsString;
    }

}
