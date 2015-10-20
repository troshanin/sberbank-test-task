Description.

Implemented solution contains to variants of processing orders(add/delete).
First solution is simple, it handles incoming orders in a single thread - main thread.
Second solution is more complex, it processes orders concurrently with configured number of threads.

When we get orders match, then actually we do nothing, so simple solution works even little bit faster then second one.
In a real world when we get two orders matched, we do some job (write to database do some calculations, invoke remote services and etc).
So this job will take some time. And in such case solution with concurrent orders processing will show better results.

Now lets look at program restrictions:
 - it is not fault tolerant, i.e. all data is stored in memory and data will be lost in case of server failure.
 - the program does not use any external storage(database) to save orders(all not matched orders are in memory).
	So there is limitation on a number of not matched orders because of memory, i.e. at some time program will throw OutOfMemoryError.


----------------
Building program:
    simply execute command
    mvn clean package

Starting program:
    Use generated jar file to start program.
    It could be found by path: target/test-sbertask-1.0-SNAPSHOT.jar

    next command line will start program:
        java -cp test-sbertask-1.0-SNAPSHOT.jar ru.sber.testtask.Main -f <ORDERS-XML-PATH> [-p <ORDERS-PROCESSOR>] [-c <CONCURRENT-LEVEL>]
            ORDERS-XML-PATH - (required), path to xml file with orders
            ORDERS-PROCESSOR - (optional), orders processor type 'concurrent' or 'simple', default: 'concurrent'
            CONCURRENT-LEVEL - (optional), number of working threads for processor type 'concurrent', default: 1

        Examples:
            java -cp test-sbertask-1.0-SNAPSHOT.jar ru.sber.testtask.Main -f orders.xml -p simple
            java -cp test-sbertask-1.0-SNAPSHOT.jar ru.sber.testtask.Main -f orders.xml -p concurrent -c 4
