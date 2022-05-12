package net.patterns.saga.storingservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import net.patterns.saga.common.grpc.StoreResponse;
import net.patterns.saga.common.grpc.StoringServiceGrpc;
import net.patterns.saga.storingservice.service.StoringService;

@GrpcService
@AllArgsConstructor
@Slf4j
public class StoringGrpcController extends StoringServiceGrpc.StoringServiceImplBase {
    private final StoringService storingService;

    @Override
    public void store(net.patterns.saga.common.grpc.StoreRequest request,
                      io.grpc.stub.StreamObserver<net.patterns.saga.common.grpc.StoreResponse> responseObserver) {

        var item = request.getItem();
        log.info("Grpc received item: {}", item);

        int intRes = storingService.store(item);

        StoreResponse resp = StoreResponse.newBuilder().setResults(intRes).build();

        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

}
