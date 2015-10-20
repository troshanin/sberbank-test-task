package ru.sber.testtask;

import ru.sber.testtask.model.stat.BookStatistic;
import ru.sber.testtask.proc.OrdersProcessor;
import ru.sber.testtask.proc.OrdersProcessorConcurrentImpl;
import ru.sber.testtask.proc.OrdersProcessorSimpleImpl;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;

/**
 * @author troshanin
 *         Date: 25.04.15
 */
public class Main {

    private static final String SIMPLE_PROCESSOR_TYPE = "simple";
    private static final String CONCURRENT_PROCESSOR_TYPE = "concurrent";

    private static File ordersXmlPath;
    private static String ordersProcessorType = CONCURRENT_PROCESSOR_TYPE;
    private static int concurrentLevel = 1;

    public static void main(String[] args) {
        parseArgs(args);
        OrdersProcessor processor = createOrdersProcessor(ordersProcessorType, concurrentLevel);
        parseOrdersAndProcess(ordersXmlPath, processor);
        waitUntilProcessorFinishAndPrintBookOrdersStatistics(processor);
    }

    private static void parseOrdersAndProcess(File ordersXmlPath, OrdersProcessor processor) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(new FileInputStream(ordersXmlPath), new OrderEventsSaxHandler(processor));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void waitUntilProcessorFinishAndPrintBookOrdersStatistics(OrdersProcessor processor) {
        processor.shutdownAndWaitAllFinished();

        for (BookStatistic bookStat : processor.getBookStatistics()) {
            System.out.println(bookStat);
            System.out.println();
        }
    }

    private static OrdersProcessor createOrdersProcessor(String type, int concurrentLevel) {
        switch (type) {
            case SIMPLE_PROCESSOR_TYPE:
                return new OrdersProcessorSimpleImpl(null);
            case CONCURRENT_PROCESSOR_TYPE:
                return new OrdersProcessorConcurrentImpl(null, concurrentLevel);
        }
        throw new RuntimeException("unknown processor type " + type);
    }

    private static void parseArgs(String[] args) {
        if (args == null) {
            printUsage();
            System.exit(1);
            return;
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-f":
                    if (++i < args.length) {
                        ordersXmlPath = new File(args[i]);
                        if (!ordersXmlPath.exists() || !ordersXmlPath.isFile()) {
                            System.out.println("file " + args[i] + " not found");
                            printUsage();
                            System.exit(1);
                        }
                    }
                    break;
                case "-p":
                    if (++i < args.length) {
                        ordersProcessorType = args[i];
                        if (!ordersProcessorType.equals(SIMPLE_PROCESSOR_TYPE) &&
                                !ordersProcessorType.equals(CONCURRENT_PROCESSOR_TYPE)) {
                            System.out.println("Incorrect processor type: " + args[i]);
                            printUsage();
                            System.exit(1);
                        }
                    }
                    break;
                case "-c":
                    if (++i < args.length) {
                        try {
                            concurrentLevel = Integer.parseInt(args[i]);
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect concurrentLevel value: " + args[i]);
                            printUsage();
                            System.exit(1);
                        }
                    }
                    break;
            }
        }
        if (ordersXmlPath == null) {
            printUsage();
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("java -cp test-sbertask-1.0-SNAPSHOT.jar ru.sber.testtask.Main -f <ORDERS-XML-PATH> [-p <ORDERS-PROCESSOR>] [-c <CONCURRENT-LEVEL>]");
        System.out.println("\tORDERS-XML-PATH - (required), path to xml file with orders");
        System.out.println("\tORDERS-PROCESSOR - (optional), orders processor type 'concurrent' or 'simple', default: 'concurrent'");
        System.out.println("\tCONCURRENT-LEVEL - (optional), number of working threads for processor type 'concurrent', default: 1");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("\tjava -cp test-sbertask-1.0-SNAPSHOT.jar ru.sber.testtask.Main -f orders.xml -p simple");
        System.out.println("\tjava -cp test-sbertask-1.0-SNAPSHOT.jar ru.sber.testtask.Main -f orders.xml -p concurrent -c 4");
    }

}
