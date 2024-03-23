package org.streltsov.server.service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.streltsov.grpc.NumbersServiceGrpc;
import org.streltsov.grpc.NumbersServiceOuterClass;

public class NumbersServiceImpl extends NumbersServiceGrpc.NumbersServiceImplBase {

    @Override
    public void generateNumbers(NumbersServiceOuterClass.NumbersRequest request,
                                StreamObserver<NumbersServiceOuterClass.NumbersResponse> responseObserver) {

        int firstValue = request.getFirstValue();
        int lastValue = request.getLastValue();

        if (firstValue >= lastValue) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Некорректные значения: начальное значение должно быть меньше последнего")
                    .asRuntimeException());
            return;
        }

        for (int i = firstValue + 1; i <= lastValue; i++) {
            NumbersServiceOuterClass.NumbersResponse response = NumbersServiceOuterClass.NumbersResponse.newBuilder()
                    .setValue(i)
                    .build();
            responseObserver.onNext(response);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        responseObserver.onCompleted();
    }

}