package com.lee.grpc.greeting.server;

import com.proto.greet.*;
import io.grpc.stub.StreamObserver;
import java.util.Random;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
    @Override
    public void visitorCheck(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        //get the fields  needed
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();
        String lastName = greeting.getLastName();
        //create response
        String result = firstName + " " + lastName + " has been added to today's visitor list.";
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(result)
                .build();

        //send response to client
        responseObserver.onNext(response);

        //complete the RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void tempCheck(tempCheckRequest request, StreamObserver<tempCheckResponse> responseObserver) {
            String firstName = request.getGreeting().getFirstName();
            for (int i =0; i < 10; i++){
                String result = ("Your ticket number is: " + i + ". We will contact you soon with a response");
                tempCheckResponse response = tempCheckResponse.newBuilder()
                        .setResult(result)
                        .build();

                //call onNext 10 times
                responseObserver.onNext(response);
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<occupancyCheckRequest> occupancyCheck(StreamObserver<occupancyCheckResponse> responseObserver) {
        StreamObserver<occupancyCheckRequest> requestObserver = new StreamObserver<occupancyCheckRequest>() {

            String result = "";

            @Override
            public void onNext(occupancyCheckRequest value) {
                // client sends message
                result += "Number of people in lobby is okay.";
            }

            @Override
            public void onError(Throwable t) {
                // client sends error
                System.out.println("Error");
            }

            @Override
            public void onCompleted() {
                // client is finished
                responseObserver.onNext(
                        occupancyCheckResponse.newBuilder()
                                .setResult(result)
                                .build()
                );
                responseObserver.onCompleted();
                // response is returned with responseObserver
            }
        };

        return requestObserver;
    }

    @Override
    public StreamObserver<elevatorStatusRequest> elevatorStatus(StreamObserver<elevatorStatusResponse> responseObserver) {
        StreamObserver<elevatorStatusRequest> requestObserver =
                new StreamObserver<elevatorStatusRequest>() {
                    @Override
                    public void onNext(elevatorStatusRequest value) {
                        String result = "The current status is: " + value.getGreeting().getFirstName();
                        elevatorStatusResponse elevatorStatusResponse = com.proto.greet.elevatorStatusResponse.newBuilder()
                                .setResult(result)
                                .build();

                        responseObserver.onNext(elevatorStatusResponse);
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("Error");
                    }

                    @Override
                    public void onCompleted() {
                        responseObserver.onCompleted();
                    }
                };
        return requestObserver;
    }
}
