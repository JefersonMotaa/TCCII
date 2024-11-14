package com.duo.superior.duo

import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.duo.superior.duo.modbus.CLP
import com.duo.com.duo.R;

class ManutencaoActivity : duoActivity() {

    var selected = false

    lateinit var myLayout: LinearLayout
    lateinit var activators: LinearLayout

    var Entradas = arrayOf("In:00 - Emergência", "In:01 - Pressostato", "In:02 - Transdutor", "In:03 - Fluxostato", "In:04 - Disjuntor da Bomba d'água",
        "In:05 - Comando Ligado", "In:06 - Eletrodo Extraído", "In:07 - Tampa do Magazine", "In:10 -Bico Saída Recuado",
        "In:11 - Bico Saída Avançado", "In:12 - Empurrador Externo Avançado", "In:13 - Empurrador Externo Recuado", "In:14 - Empurrador Interno Recuado", "In:15: Empurrador Interno Avançado",
        "In:16 - Index Magazine Avançado", "In:17 - Posicionador Avançado")

    var Saidas = arrayOf("Out:00 - Máquina de Solda", "Out:01 - Giroflex", "Out:02 - Bomba d'Água", "Out:03 - Index", "Out:04 - Empurra Eletrodo Externa", "Out:05 - Empurra Eletrodo Interno",
                         "Out:06 - Posicionador", "Out:07 - Catraca")

    var inStatus = arrayOfNulls<ImageView>(Entradas.size)
    var outStatus = arrayOfNulls<ImageView>(Saidas.size)

    lateinit var switch:Switch
    var entradas = BooleanArray(Entradas.size)
    var saidas = BooleanArray(Entradas.size)

    var first = false
    var firstOut = false
    var editavel = false

    fun MostrarEntradas(){

        myLayout.removeAllViews()

        val HL0 = LinearLayout(this)
        HL0.orientation = LinearLayout.HORIZONTAL
        HL0.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        )

        HL0.layoutParams.height= 50
        myLayout.addView(HL0)
            for(i in 0..(Entradas.size)-1){
                val textView = TextView(this)
                inStatus[i] = ImageView(this)

                inStatus[i]!!.setImageResource(if (entradas[i]){R.drawable.btn_verde}else{R.drawable.led_off})
                inStatus[i]!!.setLayoutParams(
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT)
                )

                inStatus[i]!!.layoutParams.width =25
                inStatus[i]!!.layoutParams.height = 50

                val HL = LinearLayout(this)
                HL.orientation = LinearLayout.HORIZONTAL
                textView.setText("  "+Entradas[i])
                textView.setTextColor(resources.getColor(R.color.white))
                textView.textSize = 25.5f

                var sp = Space(this)
                sp!!.setLayoutParams(
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT)
                )

                sp!!.layoutParams.width =10

                HL.addView(sp)
                HL.addView(inStatus[i])
                HL.addView(textView)
                myLayout.addView(HL)

                var footer = Space(this)
                footer.setLayoutParams(
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT)
                )
                footer.layoutParams.height = 50
                myLayout.addView(footer)
            }
    }

    fun MostrarSaidas(){

        myLayout.removeAllViews()

        val HL0 = LinearLayout(this)
        HL0.orientation = LinearLayout.HORIZONTAL
        HL0.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        )

        HL0.layoutParams.height= 30
        myLayout.addView(HL0)
        switch = Switch(this)
        switch.setChecked(if(editavel){true}else{false})
        switch.setText("  Ativar Edição")
        switch.setTextColor(resources.getColor(R.color.white))
        switch.highlightColor = resources.getColor(R.color.green)
        switch.setBackgroundColor(resources.getColor(R.color.green))
        switch.gravity = Gravity.CENTER_VERTICAL
        switch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
          editavel = switch.isChecked
            MostrarSaidas()
        })

        val HL1 = LinearLayout(this)
        HL1.orientation = LinearLayout.HORIZONTAL
        HL1.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        )
        HL1.layoutParams.height= 50
        HL1.setBackgroundColor(resources.getColor(R.color.green))
        HL1.addView(switch)

        myLayout.addView(HL1)

        val HL2 = LinearLayout(this)
        HL2.orientation = LinearLayout.HORIZONTAL
        HL2.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        )
        HL2.layoutParams.height=10
        myLayout.addView(HL2)

        for(i in 0..(Saidas.size)-1){
            val textView = TextView(this)
            outStatus[i] = ImageView(this)
            outStatus[i]!!.setImageResource(if (saidas[i]){R.drawable.btn_verde}else{R.drawable.led_off})
            outStatus[i]!!.setImageResource(R.drawable.led_off)
            outStatus[i]!!.setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            )

            outStatus[i]!!.layoutParams.width = 25
            outStatus[i]!!.layoutParams.height = 50

            firstOut = true

            val HL = LinearLayout(this)
            HL.orientation = LinearLayout.HORIZONTAL
            textView.setText("  " + Saidas[i])
            textView.setTextColor(resources.getColor(R.color.white))
            textView.textSize = 25.5f
            textView.setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            )

            textView.layoutParams.width = 1020

            var sp = Space(this)
            sp!!.setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            )
            sp!!.layoutParams.width =10
            HL.addView(sp)
            if(editavel){

                var button = Button(this)

                button.setText("MUDAR")
                button.setLayoutParams(
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT)
                )
                button.layoutParams.width = 100
                button.layoutParams.height = 50

                button.setOnTouchListener(object : View.OnTouchListener {
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        when (event?.action) {
                            MotionEvent.ACTION_DOWN -> {
                                button.setTextColor(resources.getColor(R.color.white))
                                CLP.sEscreverBit("QX0."+i,!saidas[i])
                            }
                            MotionEvent.ACTION_UP ->{
                                button.setTextColor(resources.getColor(R.color.colorPrimary))
                            }
                        }

                        return v?.onTouchEvent(event) ?: true
                    }
                })
                HL.addView(button)
            }

            HL.addView(outStatus[i])
            HL.addView(textView)

            HL.setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            )
            HL.layoutParams.height = 80
            myLayout.addView(HL)

            var footer = Space(this)
            footer.setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            )
            footer.layoutParams.height = 50
            myLayout.addView(footer)
        }

    }

    override fun inicializarUI() {
        setContentView(R.layout.activity_manutencao)
        myLayout = findViewById(R.id.lLIO) as LinearLayout
        var btnEntradas = findViewById(R.id.btnMEntradas) as Button
        var btnSaidas = findViewById(R.id.btnMSaidas) as Button
        var btnManutVoltar = findViewById(R.id.btnManutVoltar) as Button

        btnEntradas.setBackgroundColor(resources.getColor(R.color.green))
        btnEntradas.setTextColor(resources.getColor(R.color.white))

        btnEntradas.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        btnEntradas.setBackgroundColor(resources.getColor(R.color.green))
                        btnEntradas.setTextColor(resources.getColor(R.color.white))
                        btnSaidas.setBackgroundColor(resources.getColor(R.color.gray))
                        btnSaidas.setTextColor(resources.getColor(R.color.colorPrimary))

                        MostrarEntradas()
                        selected = false
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        btnSaidas.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        btnSaidas.setBackgroundColor(resources.getColor(R.color.green))
                        btnSaidas.setTextColor(resources.getColor(R.color.white))
                        btnEntradas.setBackgroundColor(resources.getColor(R.color.gray))
                        btnEntradas.setTextColor(resources.getColor(R.color.colorPrimary))
                        MostrarSaidas()
                        selected = true
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        btnManutVoltar.setOnClickListener {
            onBackPressed()
        }

        btnSaidas.setBackgroundColor(resources.getColor(R.color.gray))
        btnSaidas.setTextColor(resources.getColor(R.color.colorPrimary))
        MostrarEntradas()
    }

    override fun atualizarCLP() {

        if(!first){
            first = true
        }

        if(!selected){
            for(i in 0 until Entradas.size){
               entradas = clp.lerSequencia("IX0",Entradas.size)
            }
        }else{
            saidas = clp.lerSequencia("QX0",Saidas.size)
        }
    }

    override fun atualizarUI() {
        if(first){
            for(i in 0 until Entradas.size){
                inStatus[i]!!.setImageResource(if (entradas[i]){R.drawable.btn_verde}else{R.drawable.led_off})
            }

        }

        if(firstOut){
            for(i in 0 until Saidas.size){
                outStatus[i]!!.setImageResource(if (saidas[i]){R.drawable.btn_verde}else{R.drawable.led_off})
            }
        }
    }

    override fun onStart() {
        CLP.sEscreverBit("MX7.7", true)
        super.onStart()
    }

    override fun onStop() {
        CLP.sEscreverBit("MX7.7", false)
        super.onStop()
    }

}