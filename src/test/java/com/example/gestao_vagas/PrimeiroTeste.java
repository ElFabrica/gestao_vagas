package com.example.gestao_vagas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrimeiroTeste {

    @Test
    public void deveSerPossivelCalcularDoidNumeros(){
        var result = calculate(2, 3);
        assertEquals(5, result);
    }

    public static int calculate(int num1, int num2){
        return num1 + num2;
    }
}
