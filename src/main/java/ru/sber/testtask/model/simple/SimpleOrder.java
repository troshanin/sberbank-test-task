package ru.sber.testtask.model.simple;

import ru.sber.testtask.model.Operation;

/**
 * @author troshanin
 *         Date: 25.04.15
 */
public class SimpleOrder {
    final SimplePriceLevelQueue queue;
    final String id;
    final Operation operation;
    int volume;
    boolean deleted;

    SimpleOrder(SimplePriceLevelQueue queue, String id, Operation operation, int volume) {
        this.queue = queue;
        this.id = id;
        this.operation = operation;
        this.volume = volume;
    }

    public Operation getOperation() {
        return operation;
    }
}
