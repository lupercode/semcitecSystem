package com.luanpereira.semcitecsystem.utils;

import java.util.*;

public class BrazilianStates {

    private static final Map<String, String> states = new TreeMap<>();

    static {
        states.put("AC", "Acre");
        states.put("AL", "Alagoas");
        states.put("AP", "Amapá");
        states.put("AM", "Amazonas");
        states.put("BA", "Bahia");
        states.put("CE", "Ceará");
        states.put("DF", "Distrito Federal");
        states.put("ES", "Espírito Santo");
        states.put("GO", "Goiás");
        states.put("MA", "Maranhão");
        states.put("MT", "Mato Grosso");
        states.put("MS", "Mato Grosso do Sul");
        states.put("MG", "Minas Gerais");
        states.put("PA", "Pará");
        states.put("PB", "Paraíba");
        states.put("PR", "Paraná");
        states.put("PE", "Pernambuco");
        states.put("PI", "Piauí");
        states.put("RJ", "Rio de Janeiro");
        states.put("RN", "Rio Grande do Norte");
        states.put("RS", "Rio Grande do Sul");
        states.put("RO", "Rondônia");
        states.put("RR", "Roraima");
        states.put("SC", "Santa Catarina");
        states.put("SP", "São Paulo");
        states.put("SE", "Sergipe");
        states.put("TO", "Tocantins");
    }

    public static List<Map.Entry<String, String>> getStates() {
        // // Convertendo as entradas do HashMap para uma lista e ordenando por nome
        List<Map.Entry<String, String>> sortedStates = new ArrayList<>(states.entrySet());
        // List sortedStates = new ArrayList<>(states.entrySet());
        // sortedStates.sort(Comparator.comparing(Map.Entry::getValue));
        return sortedStates;
    }
}
