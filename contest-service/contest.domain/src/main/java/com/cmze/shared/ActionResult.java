package com.cmze.shared;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public record ActionResult<T>(boolean success, T data, ProblemDetail error) {

    public static <T> ActionResult<T> success(T data) {
        return new ActionResult<>(true, data, null);
    }

    public static <T> ActionResult<T> failure(ProblemDetail problem) {
        return new ActionResult<>(false, null, problem);
    }

    public boolean isFailure() {
        return !success;
    }

    public Optional<T> getData() {
        return Optional.ofNullable(data);
    }

    public Optional<ProblemDetail> getError() {
        return Optional.ofNullable(error);
    }

    public ResponseEntity<?> toResponseEntity(HttpStatusCode successStatus) {
        if (success) {
            // 204 nie może mieć body
            return (successStatus.value() == 204)
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.status(successStatus).body(data);
        } else {
            ProblemDetail pd = (error != null) ? error : ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            HttpStatusCode st = (pd.getStatus() != null) ? pd.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(st).body(pd);
        }
    }
}