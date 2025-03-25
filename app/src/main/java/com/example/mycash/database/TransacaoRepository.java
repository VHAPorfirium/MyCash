package com.example.mycash.database;

import com.example.mycash.model.Transacao;
import java.util.ArrayList;
import java.util.List;

public class TransacaoRepository {
    private static final List<Transacao> transacoes = new ArrayList<>();
    private static int currentId = 1;

    public static List<Transacao> getTransacoes() {
        return transacoes;
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
}
