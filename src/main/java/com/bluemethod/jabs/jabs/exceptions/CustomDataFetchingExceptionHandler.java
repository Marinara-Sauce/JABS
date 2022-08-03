package com.bluemethod.jabs.jabs.exceptions;

import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class CustomDataFetchingExceptionHandler implements DataFetcherExceptionHandler {

    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(DataFetcherExceptionHandlerParameters handlerParameters) {
        if (handlerParameters.getException() instanceof InvalidTokenException) {
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("somefield", "somevalue");

            GraphQLError gqlError = TypedGraphQLError.newInternalErrorBuilder()
                    .message("This went wrong!")
                    .debugInfo(debugInfo)
                    .path(handlerParameters.getPath()).build();

            DataFetcherExceptionHandlerResult result = DataFetcherExceptionHandlerResult.newResult()
                    .error(gqlError)
                    .build();

            return CompletableFuture.completedFuture(result);
        } else {
            return DataFetcherExceptionHandler.super.handleException(handlerParameters);
        }
    }
}
