package ru.sber.testtask;

import junit.framework.Assert;
import org.junit.Test;
import ru.sber.testtask.proc.OrdersProcessor;
import ru.sber.testtask.proc.OrdersProcessorConcurrentImpl;
import ru.sber.testtask.proc.OrdersProcessorSimpleImpl;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author troshanin
 *         Date: 26.04.15
 */
public class TestProcessors {

    @Test
    public void test() throws Exception {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        OrdersProcessor processor1 = new OrdersProcessorConcurrentImpl(null, 1);
        saxParser.parse(getTestInput(), new OrderEventsSaxHandler(processor1));
        processor1.shutdownAndWaitAllFinished();
        OrdersProcessor processor2 = new OrdersProcessorConcurrentImpl(null, 2);
        saxParser.parse(getTestInput(), new OrderEventsSaxHandler(processor2));
        processor2.shutdownAndWaitAllFinished();
        OrdersProcessor processor3 = new OrdersProcessorSimpleImpl(null);
        saxParser.parse(getTestInput(), new OrderEventsSaxHandler(processor3));
        processor3.shutdownAndWaitAllFinished();

        String p1out = processor1.getBookStatistics().toString();
        String p2out = processor2.getBookStatistics().toString();
        String p3out = processor3.getBookStatistics().toString();

        Assert.assertEquals(p1out, p2out);
        Assert.assertEquals(p3out, p2out);
    }

    private InputStream getTestInput() throws IOException {
//        return new FileInputStream("orders.xml");
        return TestProcessors.class.getResourceAsStream("/test.orders.xml");
    }

}
