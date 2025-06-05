package com.mensagemrsa.Mensagem.chat.dto;

public class RustProcessRequestDto {
    private String texto;

    // Construtores, Getters e Setters
    public RustProcessRequestDto() {}
    public RustProcessRequestDto(String texto) { this.texto = texto; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}