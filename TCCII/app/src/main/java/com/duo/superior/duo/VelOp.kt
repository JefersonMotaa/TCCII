package com.duo.superior.duo

import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import com.duo.superior.duo.modbus.CLP
import com.duo.com.duo.R;

class VelOp : duoActivity() {

    lateinit var velZ: SeekBar

    lateinit var label: Array<TextView>
    var pos = arrayOfNulls<TextView>(4)

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
    var selected = 1

    var memVels = arrayOf("MD119","MD120","MD121", "MD122")

    override fun inicializarUI() {
        setContentView(R.layout.activity_vel_op)
        velZ = findViewById(R.id.velVMZ) as SeekBar

//        for (i in pos.indices) {
//            pos[i] = findViewById(posLabels[i]) as TextView
//        }

        var btnVelOpVoltar = findViewById(R.id.btnVelOpVoltar) as Button
        btnVelOpVoltar.setOnClickListener {
            onBackPressed()
        }
        for (i in tvLabels.indices) {
            tvPerVels[i] = findViewById(tvLabels[i]) as TextView
        }

        for (vel in sliderLabels.indices) {
            velSlider[vel] = findViewById(sliderLabels[vel]) as SeekBar
            velSlider[vel]?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    println("Changing Progress..." + velZ.progress)
                    if (velSlider[vel]!!.progress == 0) {
                        velSlider[vel]!!.setProgress(1, false)
                    }
                    tvPerVels[vel]?.setText(velSlider[vel]?.progress.toString() + "%")
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
    }
}



