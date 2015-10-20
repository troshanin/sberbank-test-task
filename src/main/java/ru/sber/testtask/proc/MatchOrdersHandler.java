package ru.sber.testtask.proc;

/**
 * User: troshanin
 * Date: 26.04.15
 */
public interface MatchOrdersHandler {

    void matched(String book, String price, String orderId1, String orderId2);

}
