package kz.kbtu.walletservice.exception;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
      super(message);
    }
}
