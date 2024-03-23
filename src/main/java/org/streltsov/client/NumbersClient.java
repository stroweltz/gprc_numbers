package org.streltsov.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streltsov.grpc.NumbersServiceGrpc;
import org.streltsov.grpc.NumbersServiceOuterClass;

import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NumbersClient {

    private static final Logger logger = LoggerFactory.getLogger(NumbersClient.class);
    private static volatile int streamValue;

    public static void main(String[] args) throws InterruptedException {
        //открытие канала
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8088")
                .usePlaintext()
                .build();

        //создание реквеста
        NumbersServiceGrpc.NumbersServiceBlockingStub stub = NumbersServiceGrpc.newBlockingStub(channel);
        NumbersServiceOuterClass.NumbersRequest request = NumbersServiceOuterClass.NumbersRequest
                .newBuilder().setFirstValue(0).setLastValue(30).build();


        int currentValue = request.getFirstValue();
        int usedValue = currentValue - 1;

        //передача стримового респонза в параллельный поток
        Executor streamThread = Executors.newSingleThreadExecutor();
        streamThread.execute(() -> {
            Iterator<NumbersServiceOuterClass.NumbersResponse> iterator = stub.generateNumbers(request);
            while (iterator.hasNext()) {
                streamValue = iterator.next().getValue();
                logger.info("new value = {} ", streamValue);
            }
        });

        for (int i = 0; i < 50; i++) {
            if (streamValue == usedValue) {
                logger.info("current value = {} ", ++currentValue);

            } else {
                currentValue += streamValue + 1;
                logger.info("current value = {} ", ++currentValue);
                usedValue = streamValue;
            }
            Thread.sleep(1000);
        }

        channel.shutdown();
    }
}
