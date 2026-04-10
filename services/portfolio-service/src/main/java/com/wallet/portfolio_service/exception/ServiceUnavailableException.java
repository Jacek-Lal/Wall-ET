package com.wallet.portfolio_service.exception;

public class ServiceUnavailableException extends RuntimeException {
  public ServiceUnavailableException(String message) { super(message); }
}
