package br.com.orbitapi.exceptions;

public class CategoriaDuplicadaException extends RuntimeException {

    public CategoriaDuplicadaException(String mensagem) {
        super(mensagem);
    }
}