package br.com.orbitapi.exceptions;

public class AtividadeNaoEncontradaException extends RuntimeException {

    public AtividadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}