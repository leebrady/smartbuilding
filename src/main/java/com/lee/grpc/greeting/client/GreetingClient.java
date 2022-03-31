package com.lee.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.lang.management.ManagementFactory;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("I am a gRPC Client!");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        System.out.println("creating stub");

        DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

        //do something
        System.out.println("shutting down channel");
        channel.shutdown();
    }
}
