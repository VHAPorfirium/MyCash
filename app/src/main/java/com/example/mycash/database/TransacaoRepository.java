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
        transacoes.remove(transacao);
    }

    public static void atualizarTransacao(Transacao transacaoAtualizada) {
        for (int i = 0; i < transacoes.size(); i++) {
            if (transacoes.get(i).getId() == transacaoAtualizada.getId()) {
                transacoes.set(i, transacaoAtualizada);
                break;
            }
        }
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