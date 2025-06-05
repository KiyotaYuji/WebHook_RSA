package com.mensagemrsa.Mensagem.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List; // Mantenha se ainda houver uso para o construtor de lista
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL) // Não serializa campos nulos (ex: 'd' para chave pública)
public class ChaveDto {
    private Long e; // Componente 'e' para chave pública ou primeiro componente
    private Long d; // Componente 'd' para chave privada
    private Long n; // Componente 'n' para ambas as chaves ou segundo componente

    public ChaveDto() {
    }

    // Fábrica para chave pública
    public static ChaveDto publica(Long e, Long n) {
        ChaveDto chave = new ChaveDto();
        chave.setE(e);
        chave.setN(n);
        return chave;
    }

    // Fábrica para chave privada
    public static ChaveDto privada(Long d, Long n) {
        ChaveDto chave = new ChaveDto();
        chave.setD(d);
        chave.setN(n);
        return chave;
    }

    public Long getE() { return e; }
    public void setE(Long e) { this.e = e; }
    public Long getD() { return d; }
    public void setD(Long d) { this.d = d; }
    public Long getN() { return n; }
    public void setN(Long n) { this.n = n; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ChaveDto{");
        if (e != null) sb.append("e=").append(e);
        if (d != null) {
            if (e != null) sb.append(", ");
            sb.append("d=").append(d);
        }
        if (n != null) {
            if (e != null || d != null) sb.append(", ");
            sb.append("n=").append(n);
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChaveDto chaveDto = (ChaveDto) o;
        return Objects.equals(e, chaveDto.e) &&
                Objects.equals(d, chaveDto.d) &&
                Objects.equals(n, chaveDto.n);
    }

    @Override
    public int hashCode() {
        return Objects.hash(e, d, n);
    }
}