package ru.sber.testtask.model.concurrent;

import ru.sber.testtask.model.Operation;

/**
 * User: troshanin
 * Date: 25.04.15
 */
class OrderEntry {

    OrderEntry prev;
    OrderEntry next;

    final PriceLevelOrdersQueue queue;

    final String id;

    final Operation operation;

    int volume;

    OrderEntry(PriceLevelOrdersQueue queue, String id, Operation operation, int volume) {
        this.queue = queue;
        this.id = id;
        this.operation = operation;
        this.volume = volume;
    }
}
