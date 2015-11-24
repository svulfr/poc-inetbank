package ru.ulfr.poc.modules.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class HTTP500Exception extends RuntimeException {
}
