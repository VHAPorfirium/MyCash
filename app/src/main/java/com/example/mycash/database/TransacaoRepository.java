package com.example.mycash.database;

import com.example.mycash.model.Transacao;
import java.util.ArrayList;
import java.util.List;

public class TransacaoRepository {
    private static final List<Transacao> transacoes = new ArrayList<>();
    private static int currentId = 1;

    public static List<Transacao> getTransacoes() {
        return new ArrayList<>(transacoes); // Retorna cópia para evitar modificações externas
    }

    public static void addTransacao(Transacao transacao) {
        transacao.setId(currentId++);
        transacoes.add(transacao);
    }

    public static Transacao getTransacaoById(int id) {
        for (Transacao t : transacoes) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public static void removeTransacao(Transacao transacao) {
        if (transacao != null) {
            transacoes.removeIf(t -> t.getId() == transacao.getId());
        }
    }

    public static void atualizarTransacao(Transacao transacao) {
        for (int i = 0; i < transacoes.size(); i++) {
            if (transacoes.get(i).getId() == transacao.getId()) {
                transacoes.set(i, transacao);
                return;
            }
        }
        addTransacao(transacao); // Se não encontrou, adiciona como nova
    }

    // Substitua os métodos existentes por:
    public static double getTotalEntradas() {
        return transacoes.stream()
                .filter(t -> t.getValor() > 0) // Entradas são valores positivos
                .mapToDouble(Transacao::getValor)
                .sum();
    }

    public static double getTotalSaidas() {
        return transacoes.stream()
                .filter(t -> t.getValor() < 0) // Saídas são valores negativos
                .mapToDouble(t -> Math.abs(t.getValor())) // Converte para positivo
                .sum();
    }

    public static double getSaldo() {
        return getTotalEntradas() - getTotalSaidas();
    }
}