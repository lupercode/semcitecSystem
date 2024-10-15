package com.luanpereira.semcitecsystem.services;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Service
public class AccentRemover {
    public static String accentRemover(String text) {
        String textoNormalizado = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern padrao = Pattern.compile("[^\\p{ASCII}]");
        return padrao.matcher(textoNormalizado).replaceAll("");
    }
}
