package ru.sber.testtask;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.sber.testtask.model.AddOrder;
import ru.sber.testtask.model.DeleteOrder;
import ru.sber.testtask.model.Operation;
import ru.sber.testtask.proc.OrdersProcessor;

/**
 * @author troshanin
 *         Date: 26.04.15
 */
public class OrderEventsSaxHandler extends DefaultHandler {

    private static final String ADD_ORDER_ELEMENT = "AddOrder";
    private static final String DELETE_ORDER_ELEMENT = "DeleteOrder";
    private static final String BOOK_ATTR = "book";
    private static final String ORDER_ID_ATTR = "orderId";
    private static final String PRICE_ATTR = "price";
    private static final String OPERATION_ATTR = "operation";
    private static final String VOLUME_ATTR = "volume";

    final OrdersProcessor processor;

    public OrderEventsSaxHandler(OrdersProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (qName) {
            case ADD_ORDER_ELEMENT: {
                String bookId = getStringAttributeValue(attributes, BOOK_ATTR, false);
                String orderId = getStringAttributeValue(attributes, ORDER_ID_ATTR, false);
                String price = getPriceAttributeValue(attributes, PRICE_ATTR);
                Operation operation = getOperationValue(attributes, OPERATION_ATTR);
                int volume = getIntAttributeValue(attributes, VOLUME_ATTR);

                processor.handleAddOrder(new AddOrder(orderId, bookId, operation, price, volume));
            }
            break;
            case DELETE_ORDER_ELEMENT: {
                String bookId = getStringAttributeValue(attributes, BOOK_ATTR, false);
                String orderId = getStringAttributeValue(attributes, ORDER_ID_ATTR, false);
                processor.handleDeleteOrder(new DeleteOrder(orderId, bookId));
            }
            break;
        }
    }

    private String getStringAttributeValue(Attributes attributes, String name, boolean nullable) {
        String val = attributes.getValue(name);
        if (val == null && !nullable) {
            throw new IllegalArgumentException("element does not contain " + name + " attribute");
        }
        return val != null ? val.trim() : null;
    }

    private Operation getOperationValue(Attributes attributes, String name) {
        String oper = getStringAttributeValue(attributes, name, false);
        try {
            return Operation.valueOf(oper);
        } catch (Exception e) {
            throw new IllegalArgumentException("element contain incorrect " + name + " attribute value");
        }
    }

    private String getPriceAttributeValue(Attributes attributes, String name) {
        String price = getStringAttributeValue(attributes, name, false);
        try {
            Double.parseDouble(price);
            return price;
        } catch (Exception e) {
            throw new IllegalArgumentException("element contain incorrect " + name + " attribute value");
        }
    }

    private int getIntAttributeValue(Attributes attributes, String name) {
        try {
            return Integer.parseInt(getStringAttributeValue(attributes, name, false));
        } catch (Exception e) {
            throw new IllegalArgumentException("element contain incorrect " + name + " attribute value");
        }
    }
}
