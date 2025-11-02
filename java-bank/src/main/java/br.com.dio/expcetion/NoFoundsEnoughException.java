package br.com.dio.expcetion;

public class NoFoundsEnoughException extends RuntimeException {
    public NoFoundsEnoughException(String message) {
        super(message);
    }
}
