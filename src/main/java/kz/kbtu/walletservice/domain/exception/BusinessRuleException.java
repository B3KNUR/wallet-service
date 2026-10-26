package kz.kbtu.walletservice.domain.exception;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
      super(message);
    }
}
