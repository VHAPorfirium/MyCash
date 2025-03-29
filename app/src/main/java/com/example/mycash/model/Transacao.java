package com.example.mycash.model;

import java.text.NumberFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Transacao {
    private int id;
    private String descricao;
    private double valor;
    private String tipo;
    private Date data;
    private String categoria;
    private String formaPagamento;

    public Transacao() {
        // Construtor vazio para criação de novas transações
    }

    public Transacao(int id, String descricao, double valor, String tipo, Date data,
                     String categoria, String formaPagamento) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.data = data;
        this.categoria = categoria;
        this.formaPagamento = formaPagamento;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    // Métodos auxiliares
    public String getDataFormatada() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(data);
    }

    public String getValorFormatado() {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return format.format(getValor()); // Já inclui o sinal negativo se existir
    }

    public boolean isEntrada() {
        return "Entrada".equalsIgnoreCase(tipo) || valor > 0;
    }

    public boolean isSaida() {
        return "Saída".equalsIgnoreCase(tipo) || valor < 0;
    }

    public boolean isCredito() {
        return formaPagamento != null && formaPagamento.equalsIgnoreCase("Crédito");
    }

    @Override
    public String toString() {
        return "Transacao{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", valor=" + valor +
                ", tipo='" + tipo + '\'' +
                ", data=" + getDataFormatada() +
                ", categoria='" + categoria + '\'' +
                ", formaPagamento='" + formaPagamento + '\'' +
                '}';
    }
}