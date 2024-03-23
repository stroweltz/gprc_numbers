package org.streltsov.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.streltsov.server.service.NumbersServiceImpl;

import java.io.IOException;

public class NumbersServer {

    private static final Logger logger = LogManager.getLogger(NumbersServer.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(8088).addService(new NumbersServiceImpl()).build();
        server.start();
        logger.log(Level.INFO, "server started");
        server.awaitTermination();
    }
}
