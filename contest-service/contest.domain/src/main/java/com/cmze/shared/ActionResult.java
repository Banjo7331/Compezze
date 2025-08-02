package com.cmze.shared;

import org.springframework.http.ProblemDetail;

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
}