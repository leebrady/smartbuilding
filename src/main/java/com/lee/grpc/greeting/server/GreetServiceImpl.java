package com.lee.grpc.greeting.server;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;

import java.io.StringReader;

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
}
