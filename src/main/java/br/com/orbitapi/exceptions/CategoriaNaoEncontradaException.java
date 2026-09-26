package br.com.orbitapi.exceptions;

public class CategoriaNaoEncontradaException extends RuntimeException {

    public CategoriaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}