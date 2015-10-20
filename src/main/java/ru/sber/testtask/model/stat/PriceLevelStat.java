package ru.sber.testtask.model.stat;

import ru.sber.testtask.model.Operation;

/**
 * @author troshanin
 *         Date: 26.04.15
 */
public class PriceLevelStat implements Comparable<PriceLevelStat> {

    final Operation operation;
    final String price;
    final double priceDouble;
    final int volume;
    final String valueAsString;


    public PriceLevelStat(Operation operation, String price, int volume) {
        this.operation = operation;
        this.price = price;
        this.priceDouble = Double.parseDouble(price);
        this.volume = volume;
        this.valueAsString = volume + "@" + price;
    }

    public Operation getOperation() {
        return operation;
    }

    public String getPrice() {
        return price;
    }

    public double getPriceDouble() {
        return priceDouble;
    }

    public int getVolume() {
        return volume;
    }

    public String getValueAsString() {
        return valueAsString;
    }

    public String toString() {
        return valueAsString;
    }

    @Override
    public int compareTo(PriceLevelStat that) {
        return -Double.compare(this.priceDouble, that.priceDouble);
    }
}
