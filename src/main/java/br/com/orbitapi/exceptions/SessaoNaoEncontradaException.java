package br.com.orbitapi.exceptions;

public class SessaoNaoEncontradaException extends RuntimeException {

    public SessaoNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}