package com.duo.superior.duo

import android.app.AlertDialog
import android.content.Intent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.duo.com.duo.R

class Configuracoes : duoActivity() {

    var ids = intArrayOf(
        R.id.btnInputOutput,
        R.id.btnReferenciaCG,
        R.id.btnParametros,
        R.id.btnRede,
        R.id.btnPassoRotinas,
        R.id.btnConfigVoltar
    )
    var referenciaCG: Boolean = false
    var parametros: Boolean = false
    override fun inicializarUI() {
        setContentView(R.layout.activity_configuracoes)
        var btnIO: View = findViewById(ids[0])
        btnIO.setOnClickListener(View.OnClickListener {
            trocarTela(
                ManutencaoActivity::class.java
            )
        })

        var btnRefCG: View = findViewById(ids[1])
        btnRefCG.setOnClickListener(View.OnClickListener {
            referenciaCG = true
            exibirDialogSenha()
        })

        var btnParametros: View = findViewById(ids[2])
        btnParametros.setOnClickListener(View.OnClickListener {
            parametros = true
            exibirDialogSenha()
        })


        var btnNet: View = findViewById(ids[3])
        btnNet.setOnClickListener(View.OnClickListener {
            trocarTela(
                Rede::class.java
            )
        })

        var btnPassoRotina: View = findViewById(ids[4])
        btnPassoRotina.setOnClickListener(View.OnClickListener {
            trocarTela(
                PassoRotinaActivity::class.java
            )
        })

        var btnConfigVoltar: View = findViewById(ids[5])
        btnConfigVoltar.setOnClickListener {
            this.onBackPressed()
        }
    }

    override fun atualizarCLP() {

    }

    override fun onBackPressed() {
        timer.cancel()
        val it = Intent(baseContext, MainActivity::class.java)
        startActivity(it)
    }

    override fun atualizarUI() {

    }

    private fun exibirDialogSenha() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Digite a senha")

        // Inflar o layout personalizado para o AlertDialog
        val view = layoutInflater.inflate(R.layout.layout_dialog_senha, null)
        builder.setView(view)

        val edtSenha = view.findViewById<EditText>(R.id.edtSenha)

        builder.setPositiveButton("OK") { dialog, which ->
            val senhaDigitada = edtSenha.text.toString()
            // Verificar a senha aqui
            if (senhaCorreta(senhaDigitada)) {
                // Senha correta, chame a próxima tela
                abrirProximaTela()
            } else {
                // Senha incorreta, exiba uma mensagem ou tome a ação apropriada
                Toast.makeText(this, "Senha incorreta", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, which -> dialog.dismiss() }

        builder.show()
    }

    private fun senhaCorreta(senha: String): Boolean {
        // Lógica para verificar se a senha está correta
        // Substitua por sua própria lógica de verificação
        return senha == "2017dc"
    }

    private fun abrirProximaTela() {
        // Lógica para abrir a próxima tela aqui
        if (referenciaCG) {
            trocarTela(
                ReferenciaCG::class.java
            )
            referenciaCG = false
        }
        if (parametros) {
            trocarTela(
                ParametrosActivity::class.java
            )
            parametros = false
        }
    }
}
