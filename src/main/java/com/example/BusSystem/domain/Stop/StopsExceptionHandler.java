package com.example.BusSystem.domain.Stop;
import com.example.BusSystem.controller.StopsController;
import com.example.BusSystem.utils.ApiErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = {StopsController.class})
public class StopsExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handle404Error() {
        var errorResponse = new ApiErrorResponse("Stop with the specified ID was not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handle400Error(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        String errorMessage = "Error in fields: " + String.join("; ", errors);
        ApiErrorResponse errorResponse = new ApiErrorResponse(errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ApiErrorResponse> handle405Error() {
        var errorResponse = new ApiErrorResponse("Method not allowed");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
