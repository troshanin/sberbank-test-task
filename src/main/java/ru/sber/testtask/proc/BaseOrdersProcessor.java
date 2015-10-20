package ru.sber.testtask.proc;

/**
 * User: troshanin
 * Date: 26.04.15
 */
public abstract class BaseOrdersProcessor implements OrdersProcessor {

    final MatchOrdersHandler matchOrdersHandler;

    protected BaseOrdersProcessor(MatchOrdersHandler matchOrdersHandler) {
        this.matchOrdersHandler = matchOrdersHandler;
    }

}
