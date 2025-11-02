package br.com.dio.expcetion;

public class InvestmentNotFoundException extends RuntimeException {
    public InvestmentNotFoundException(String message) {
        super(message);
    }
}
