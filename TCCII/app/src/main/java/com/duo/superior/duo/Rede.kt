package com.duo.superior.duo

import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.duo.com.duo.R;

class Rede : duoActivity() {
    override fun inicializarUI() {
        setContentView(R.layout.activity_rede)

        var esquecer = findViewById(R.id.btnEsquecerSerie) as Button
        var salvar = findViewById(R.id.btnRedeSalvar) as Button
        var edtRedeIP = findViewById(R.id.edtRedeIP) as EditText
        var edtPorta = findViewById(R.id.edtPorta) as EditText
        var txtNumSerie = findViewById(R.id.txtNumSerie) as TextView
        var btnRedeVoltar = findViewById(R.id.btnRedeVoltar) as Button

        //txtNumSerie.setText(String.format("Número de série: %d", sql.pegarNumeroSerie()))

        esquecer.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                sql.alterarNumeroSerie(-1)
                Toast.makeText(baseContext, "Número de série esquecido", Toast.LENGTH_SHORT).show();
                return true
            }
        })

        btnRedeVoltar.setOnClickListener {
            this.onBackPressed()
        }

        edtRedeIP.setText(Global.ip)
        salvar.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        sql.atualizarValorPreferencia("Preferencias","IP",Integer.parseInt(edtRedeIP.getText().toString().substring(10)))
                        sql.atualizarValorPreferencia("Preferencias","Porta",Integer.parseInt(edtPorta.getText().toString()))
                        Global.ip = edtRedeIP.text.toString()
                    }
                    MotionEvent.ACTION_UP ->{
                        Toast.makeText(baseContext,"Endereco registrado com sucesso",Toast.LENGTH_SHORT).show()
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })
    }

    override fun atualizarCLP() {

    }

    override fun atualizarUI() {

    }
}