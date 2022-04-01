package com.lee.grpc.greeting.server;

import com.proto.greet.*;
import io.grpc.stub.StreamObserver;

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
            String lastName = request.getGreeting().getLastName();

            for (int i =0; i < 10; i++){
                String result = (firstName + " " + lastName + " here is the response number: " + i);
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
                result += ". Hello " + value.getGreeting().getFirstName() + "! ";
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
}
