package com.mensagemrsa.Mensagem.chat.dto;

// Supondo que o endpoint Rust /verificar-mensagem será modificado
// para retornar o texto descriptografado e o status.
public class RustVerifyResponseDto {
    private String mensagemDescriptografada;
    private String status; // Ex: "Mensagem íntegra e segura."

    // Getters e Setters
    public String getMensagemDescriptografada() { return mensagemDescriptografada; }
    public void setMensagemDescriptografada(String mensagemDescriptografada) { this.mensagemDescriptografada = mensagemDescriptografada; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}