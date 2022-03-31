package com.lee.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.tempCheckRequest;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;


import javax.swing.*;
import java.lang.management.ManagementFactory;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("I am a gRPC Client!");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        System.out.println("creating stub");
        String firstName = JOptionPane.showInputDialog("What is your first name?");
        String lastName = JOptionPane.showInputDialog("What is your last name?");

        // dummy code from demo I followed
        // DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

        // greet service client (blocking and synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);


        // Unary
        // protocol buffer greeting message
       /* Greeting greeting = Greeting.newBuilder()
                        .setFirstName(firstName)
                        .setLastName(lastName)
                        .build();

        // same but for greetRequest
        GreetRequest greetRequest = GreetRequest.newBuilder()
                        .setGreeting(greeting)
                        .build();

        //call the rpc and get back a response
        GreetResponse greetResponse = greetClient.visitorCheck(greetRequest);

        System.out.println(greetResponse.getResult());
        */

        // Server Streaming
        // prepare request
        tempCheckRequest tempCheckRequest = com.proto.greet.tempCheckRequest.newBuilder()
                        .setGreeting(Greeting.newBuilder().setFirstName(firstName))
                        .setGreeting(Greeting.newBuilder().setLastName(lastName))
                                .build();
        // stream responses
        greetClient.tempCheck(tempCheckRequest)
                        .forEachRemaining(tempCheckResponse -> {
                            System.out.println(tempCheckResponse.getResult());
                        });

        System.out.println("shutting down channel");
        channel.shutdown();
    }
}
