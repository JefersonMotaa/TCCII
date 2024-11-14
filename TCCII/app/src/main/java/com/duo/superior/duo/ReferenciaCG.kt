package com.duo.superior.duo

import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.duo.superior.duo.modbus.CLP
import com.duo.com.duo.R;

class ReferenciaCG : duoActivity() {
    lateinit var tvRefC :TextView
    lateinit var tvRefG : TextView


    var refC = 0.0f
    var refG = 0.0f

    var btnJog = arrayOfNulls<Button>(6)
    var first = false
    var escrever = false
    var escrevendo = false
    var selected = 0
    var posC = 0.0f
    var posG = 0.0f

    override fun inicializarUI() {
        setContentView(R.layout.activity_referencia_cg)

        var btnRefCHome = findViewById(R.id.btnCGHC) as Button
        btnRefCHome.setOnTouchListener(View.OnTouchListener{v:View, event:MotionEvent ->
            if(event.action == MotionEvent.ACTION_DOWN){
                CLP.sEscreverBit("MX1.3", true)
            }
            if(event.action == MotionEvent.ACTION_UP){
                CLP.sEscreverBit("MX1.3", false)
            }
            if(event.action == MotionEvent.ACTION_CANCEL){
                CLP.sEscreverBit("MX1.3", false)
            }
            true
        })

        var esqueceC = findViewById(R.id.btnREsqueceC) as Button
        esqueceC.setOnTouchListener(View.OnTouchListener{v:View, event:MotionEvent ->
            if(event.action == MotionEvent.ACTION_DOWN){
                //CLP.sEscreverFloat("MD123",0.0f)
                CLP.sEscreverFloat("MD123",0.0f)
            }
            true
        })

        var gravaC = findViewById(R.id.btnRGravarC) as Button
        gravaC.setOnTouchListener(View.OnTouchListener{v:View, event:MotionEvent ->
            if(event.action == MotionEvent.ACTION_DOWN){
                CLP.sEscreverFloat("MD123",posC)
            }
           true
        })

        var btnRefGHome = findViewById(R.id.btnCGHG) as Button
        btnRefGHome.setOnTouchListener(View.OnTouchListener{v:View, event:MotionEvent ->
            if(event.action == MotionEvent.ACTION_DOWN){
                CLP.sEscreverBit("MX1.4", true)
            }
            if(event.action == MotionEvent.ACTION_UP){
                CLP.sEscreverBit("MX1.4", false)
            }
            if(event.action == MotionEvent.ACTION_CANCEL){
                CLP.sEscreverBit("MX1.4", false)
            }
            true
        })

        var esqueceG = findViewById(R.id.btnREsqueceG) as Button
        esqueceG.setOnTouchListener(View.OnTouchListener{v:View, event:MotionEvent ->
            if(event.action == MotionEvent.ACTION_DOWN){
                //CLP.sEscreverFloat("MD123",0.0f)
                CLP.sEscreverFloat("MD124",0.0f)
            }
            true
        })

        var gravaG = findViewById(R.id.btnRGravarG) as Button
        gravaG.setOnTouchListener(View.OnTouchListener{v:View, event:MotionEvent ->
            if(event.action == MotionEvent.ACTION_DOWN){
                CLP.sEscreverFloat("MD124",posG)
            }
            true
        })

        var btnRefcVoltar = findViewById(R.id.btnRefcVoltar) as Button
        btnRefcVoltar.setOnClickListener {
            trocarTela(Configuracoes::class.java)
        }

        var btnRefCJog = findViewById(R.id.btnRefCJog) as Button
        btnRefCJog.setOnClickListener {
            trocarTela(VelRef::class.java)
        }

        tvRefC = findViewById(R.id.tvRefC) as TextView
        tvRefG = findViewById(R.id.tvRefG) as TextView

    }

    override fun atualizarCLP() {
        if (!first) {
            if (escrever) {
                escrever = false
                escrevendo = true
            }
            posC = clp.lerFloat("MD102")
            refC = clp.lerFloat("MD123")
            posG = clp.lerFloat("MD103")
            refG = clp.lerFloat("MD124")
        }
    }

    override fun atualizarUI() {

        tvRefC.setText(String.format("Referência atual C0: %.2f", refC))
        tvRefG.setText(String.format("Referência atual G0: %.2f", refG))

    }

}