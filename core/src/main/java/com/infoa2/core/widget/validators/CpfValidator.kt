package com.infoa2.core.widget.validators

/**
 * Created by caio on 20/03/17.
 */

import com.google.android.material.textfield.TextInputEditText
import com.infoa2.core.widget.textwatchers.CpfMaksTextWatcher

/**
 * Validação de CPF.
 *
 * @author Pablo Nóbrega
 */
object CpfValidator {

    fun isCpf(cpf: TextInputEditText): Boolean {
        return isCpf(CpfMaksTextWatcher.unmask(cpf.text!!.toString()))

    }

    /**
     * Valida CPF do usuário. Não aceita CPF's padrões como
     * 11111111111 ou 22222222222
     *
     * @param cpf String valor com 11 dígitos
     */
    fun isCpf(cpf: String?): Boolean {
        if (cpf == null || cpf.length != 11 || isCPFPadrao(cpf))
            return false

        try {
            java.lang.Long.parseLong(cpf)
        } catch (e: NumberFormatException) { // CPF não possui somente números
            return false
        }

        return calcDigVerif(cpf.substring(0, 9)) == cpf.substring(9, 11)
    }

    /**
     * @param cpf String valor a ser testado
     * @return boolean indicando se o usuário entrou com um CPF padrão
     */
    private fun isCPFPadrao(cpf: String): Boolean {
        return if (cpf == "11111111111" || cpf == "22222222222"
            || cpf == "33333333333"
            || cpf == "44444444444"
            || cpf == "55555555555"
            || cpf == "66666666666"
            || cpf == "77777777777"
            || cpf == "88888888888"
            || cpf == "99999999999"
        ) {

            true
        } else false

    }

    private fun calcDigVerif(num: String): String {
        val primDig: Int?
        val segDig: Int?
        var soma = 0
        var peso = 10
        for (i in 0 until num.length)
            soma += Integer.parseInt(num.substring(i, i + 1)) * peso--

        if ((soma % 11 == 0) or (soma % 11 == 1))
            primDig = 0
        else
            primDig = 11 - soma % 11

        soma = 0
        peso = 11
        for (i in 0 until num.length)
            soma += Integer.parseInt(num.substring(i, i + 1)) * peso--

        soma += primDig.toInt() * 2
        if ((soma % 11 == 0) or (soma % 11 == 1))
            segDig = 0
        else
            segDig = 11 - soma % 11

        return primDig.toString() + segDig.toString()
    }
}
