package com.duo.superior.duo

import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import com.duo.com.duo.R
import com.duo.superior.duo.modbus.CLP



class VelRef : duoActivity() {
//Ativa JOG quando inicia a atividade
    override fun onStart() {
        CLP.sEscreverBit("MX0.0", true)
        CLP.sEscreverBit("MX3.2", true)
        super.onStart()
    }
//Desativa JOG quando para a atividade
    override fun onBackPressed() {
    CLP.sEscreverBit("MX0.0", false)
    CLP.sEscreverBit("MX3.2", false)
    super.onBackPressed()
    }

    //btnVelRefVoltar : Button = findViewById(R.id.btnVelRefVoltar)
    lateinit var velZ: SeekBar

    lateinit var label: Array<TextView>
    var pos = arrayOfNulls<TextView>(4)

    var posEixos = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)

    var eixosNome = arrayOf("Z", "X", "C", "G")
    var unidades = arrayOf("mm", "mm", "º", "º")
    var memJog = arrayOf("MX0.2", "MX0.1", "MX0.4", "MX0.3", "MX0.6", "MX0.5", "MX1.0", "MX0.7")
    var jogLabels = intArrayOf(R.id.btnPZ, R.id.btnPX, R.id.btnPC, R.id.btnPG, R.id.btnPPlus, R.id.btnPMinus)
    var posLabels = intArrayOf(R.id.tvPPosZ, R.id.tvPPosX, R.id.tvPPosC, R.id.tvPPosG)
    var edtAxis = intArrayOf(0, 0, 0, 0, 2, 2, 2, 2, 3, 3, 3)


    var btnJog = arrayOfNulls<Button>(6)
    var tvPerVels = arrayOfNulls<TextView>(4)
    var velSlider = arrayOfNulls<SeekBar>(4);
    var tvLabels = arrayOf(R.id.tvPerMZ,R.id.tvPerMX, R.id.tvPerMC, R.id.tvPerMG)
    var sliderLabels = arrayOf(R.id.velVMZ, R.id.velVMX, R.id.velVMC, R.id.velVMG)
    var first = false
    var escrever = false
    var escrevendo = false
    var atual = intArrayOf(0,0,0,0)
    var selected = 0

    var memVels = arrayOf("MD115","MD116","MD117","MD118")

    var buttonLabel = arrayOf(R.id.btnVelRefVoltar)

    override fun inicializarUI() {
        setContentView(R.layout.activity_vel_ref)
        velZ = findViewById(R.id.velVMZ) as SeekBar

        for(i in pos.indices){
            pos[i] = findViewById(posLabels[i]) as TextView
        }

        for(i in tvLabels.indices){
            tvPerVels[i] = findViewById(tvLabels[i]) as TextView
        }


        for(vel in sliderLabels.indices){
            velSlider[vel] = findViewById(sliderLabels[vel]) as SeekBar
            velSlider[vel]?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    println("Changing Progress..."+velZ.progress)

                    if(velSlider[vel]!!.progress ==0 ){
                        velSlider[vel]!!.setProgress(1,false)
                    }
                    tvPerVels[vel]?.setText(velSlider[vel]?.progress.toString()+"%")
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    println("Started Tracking...")
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    println("Stoooping....")
                    atual[vel] = velSlider[vel]!!.progress
                    escrever = true
                }

            })
        }

        for (i in 0..5) {
            if (i >= 4) {
                btnJog[i] = findViewById(jogLabels[i]) as Button
                btnJog[i]!!.setOnTouchListener(OnTouchListener { view, motionEvent ->
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            btnJog[i]!!.setTextColor(resources.getColor(R.color.white))
                            pos[selected]!!.setBackgroundColor(resources.getColor(R.color.green))
                            CLP.sEscreverBit(
                                if (i == 4) memJog[selected * 2 + 1] else memJog[selected * 2],
                                true
                            )
                        }
                        MotionEvent.ACTION_UP -> {
                            btnJog[i]!!.setTextColor(resources.getColor(R.color.colorPrimary))
                            pos[selected]!!.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                            CLP.sEscreverBit(
                                if (i == 4) memJog[selected * 2 + 1] else memJog[selected * 2],
                                false
                            )
                        }
                        MotionEvent.ACTION_CANCEL -> {
                            btnJog[i]!!.setTextColor(resources.getColor(R.color.colorPrimary))
                            CLP.sEscreverBit(
                                if (i == 4) memJog[selected * 2] else memJog[selected * 2 + 1],
                                false
                            )
                        }
                    }
                   true
                })
                continue
            } else {
                btnJog[i] = findViewById(jogLabels[i]) as Button
                btnJog[i]!!.setBackgroundColor(resources.getColor(if (selected == i) R.color.green else R.color.gray))
                btnJog[i]!!.setOnTouchListener(OnTouchListener { view, motionEvent ->
                    btnJog[selected]!!.setBackgroundColor(resources.getColor(R.color.gray))
                    btnJog[i]!!.setBackgroundColor(resources.getColor(R.color.green))
                    selected = i
                   true

                })
            }
        }
        var btnVelRefVoltar: View  = findViewById(R.id.btnVelRefVoltar)
        btnVelRefVoltar.setOnClickListener {
            this.onBackPressed()
        }
    }

    override fun atualizarCLP() {
        if(!first){
           for(i in memVels.indices)
           {
               atual[i] = clp.lerInteiro(memVels[i])
           }
        }

        if(escrever){
           for(i in atual.indices){
               clp.escreverInteiro(memVels[i],atual[i])
           }
            escrever = false
            escrevendo = true
        }


        posEixos[0] = clp.lerInteiro("MD100").toFloat()
        posEixos[1] = clp.lerInteiro("MD101").toFloat()
        posEixos[2] = clp.lerFloat("MD102")
        posEixos[3] = clp.lerFloat("MD103")

    }

    override fun atualizarUI() {
        if(!first){
            for(i in velSlider.indices){
                velSlider[i]!!.setProgress(atual[i], false)
            }
            first = true
        }

        if(escrevendo){

            escrevendo = false
        }

        for (i in 0..3) {
            pos[i] = findViewById(posLabels[i]) as TextView
            pos[i]!!.text =
                String.format(" %s ->  %.2f %s", eixosNome[i], posEixos[i], unidades[i])
        }
    }
}



