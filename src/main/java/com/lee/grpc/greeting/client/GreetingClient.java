package com.lee.grpc.greeting.client;
import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import javax.swing.*;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {


    public static void main(String[] args) {
        System.out.println("I am a gRPC Client!");

        GreetingClient main = new GreetingClient();
        main.run();
    }

    private void run(){
       ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        // switch to enable which service is being called

        doUnaryCall(channel);

        //doServerStreamingCall(channel);

        //doClientStreamingCall(channel);

        //doBidirectionalStreamingCall(channel);


        System.out.println("shutting down channel");
        channel.shutdown();

    }

    private void doUnaryCall(ManagedChannel channel){
        // greet service client (blocking and synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        System.out.println("creating stub");
        String firstName = JOptionPane.showInputDialog("What is your first name?");
        String lastName = JOptionPane.showInputDialog("What is your last name?");

        // Unary
        // protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder()
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

    }

    private void doServerStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // Server Streaming
        // prepare request
        tempCheckRequest tempCheckRequest = com.proto.greet.tempCheckRequest.newBuilder()
                .build();
        // stream responses
        greetClient.tempCheck(tempCheckRequest)
                .forEachRemaining(tempCheckResponse -> {
                    System.out.println(tempCheckResponse.getResult());
                });
    }

    private void doClientStreamingCall(ManagedChannel channel){
        //asynchronous client
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

         StreamObserver<occupancyCheckRequest> requestObserver = asyncClient.occupancyCheck(new StreamObserver<occupancyCheckResponse>() {
            @Override
            public void onNext(occupancyCheckResponse value) {
                // response from server
                System.out.println("Received a response from the server");
                System.out.println(value.getResult());
                // onNext will be called one time
            }

            @Override
            public void onError(Throwable t) {
                // error from server

            }

            @Override
            public void onCompleted() {
                // server is finished
                System.out.println("Server is finished sending something");
                latch.countDown();

                // onCompleted will be called after onNext()

            }
        });

        // streaming message #1
        System.out.println("Current amount of people in lobby is (3)");
         requestObserver.onNext(occupancyCheckRequest.newBuilder()
                 .setGreeting(Greeting.newBuilder()
                         .build())
                 .build());

        // streaming message #2
        System.out.println("Current amount of people in lobby is (2)");
        requestObserver.onNext(occupancyCheckRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .build())
                .build());

        // streaming message #3
        System.out.println("Current amount of people in lobby is (4)");
        requestObserver.onNext(occupancyCheckRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .build())
                .build());

        // client tells the server it has finished sending data
        requestObserver.onCompleted();

        try {
            latch.await(10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doBidirectionalStreamingCall(ManagedChannel channel){
        //asynchronous client
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<elevatorStatusRequest> requestObserver = asyncClient.elevatorStatus(new StreamObserver<elevatorStatusResponse>() {
            @Override
            public void onNext(elevatorStatusResponse value) {
                System.out.println("Response from server: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is finished sending something ");
                latch.countDown();
            }
        });

        Arrays.asList("Elevator okay","Elevator okay","Elevator okay","Elevator okay","Elevator okay").forEach(
                elevatorCondition -> {
                    System.out.println("Sending: " + elevatorCondition);
                    requestObserver.onNext(elevatorStatusRequest.newBuilder()
                            .setGreeting(Greeting.newBuilder()
                                    .setFirstName(elevatorCondition))
                            .build());
                }
        );

        requestObserver.onCompleted();

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
