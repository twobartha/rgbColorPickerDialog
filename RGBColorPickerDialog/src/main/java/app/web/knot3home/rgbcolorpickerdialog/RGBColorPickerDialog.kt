package app.web.knot3home.rgbcolorpickerdialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import app.web.knot3home.rgbColorPickerDialog.R
import kotlin.math.roundToInt

/**
 * Use the [RGBColorPickerDialog.create] or [RGBColorPickerDialog.createWithHSV] to
 * create an instance of this DialogFragment.
 */
class RGBColorPickerDialog : DialogFragment() {
    private var _listener: Listener? = null

    interface Listener {
        fun onFinished(r: Int, g: Int, b: Int, a: Int)
    }

    companion object {
        /**
         * Creates an instance of RGBColorPickerDialog.
         * Specify the default color with [r], [g], and [b].
         *
         * @return A new instance of DialogFragment RGBColorPickerDialog.
         */
        fun create(r: Int, g: Int, b: Int): RGBColorPickerDialog {
            if(r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255){
                throw IllegalArgumentException("r/g/b must be 0..255")
            }

            return RGBColorPickerDialog().apply {
                val args = Bundle()
                args.putInt("key_r", r)
                args.putInt("key_g", g)
                args.putInt("key_b", b)
                args.putBoolean("key_alpha", false)
                this.arguments = args
            }
        }
        /**
         * Creates an instance of RGBColorPickerDialog.
         * Specify the default color with [r], [g], [b], and [a].
         *
         * @return A new instance of DialogFragment RGBColorPickerDialog.
         */
        fun create(r: Int, g: Int, b: Int, a: Int): RGBColorPickerDialog {
            if(r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255 || a < 0 || a > 255){
                throw IllegalArgumentException("r/g/b/a must be 0..255")
            }

            return RGBColorPickerDialog().apply {
                val args = Bundle()
                args.putInt("key_r", r)
                args.putInt("key_g", g)
                args.putInt("key_b", b)
                args.putBoolean("key_alpha", true)
                args.putInt("key_a", a)
                this.arguments = args
            }
        }
        /**
         * Creates an instance of RGBColorPickerDialog.
         * Show seekbars for HSV.
         * Specify the default color with [r], [g], and [b].
         *
         * @return A new instance of DialogFragment RGBColorPickerDialog.
         */
        fun createWithHSV(r: Int, g: Int, b: Int): RGBColorPickerDialog {
            if(r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255){
                throw IllegalArgumentException("r/g/b must be 0..255")
            }

            return RGBColorPickerDialog().apply {
                val args = Bundle()
                args.putInt("key_r", r)
                args.putInt("key_g", g)
                args.putInt("key_b", b)
                args.putBoolean("key_alpha", false)
                args.putBoolean("key_hsv", true)
                this.arguments = args
            }
        }
        /**
         * Creates an instance of RGBColorPickerDialog.
         * Show seekbars for HSV.
         * Specify the default color with [r], [g], [b], and [a].
         *
         * @return A new instance of DialogFragment RGBColorPickerDialog.
         */
        fun createWithHSV(r: Int, g: Int, b: Int, a: Int): RGBColorPickerDialog {
            if(r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255 || a < 0 || a > 255){
                throw IllegalArgumentException("r/g/b/a must be 0..255")
            }

            return RGBColorPickerDialog().apply {
                val args = Bundle()
                args.putInt("key_r", r)
                args.putInt("key_g", g)
                args.putInt("key_b", b)
                args.putBoolean("key_alpha", true)
                args.putBoolean("key_hsv", true)
                args.putInt("key_a", a)
                this.arguments = args
            }
        }
    }

    fun setOnFinishedListener(listener: Listener) {
        _listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var r = 0
        var g = 0
        var b = 0
        var a = 255
        var h = 0
        var s = 0
        var v = 0
        var isAlphaEnabled = false
        var isHSVEnabled = false

        val factory = LayoutInflater.from(requireContext())
        val dialogView = factory.inflate(R.layout.rgb_color_picker_dialog, null)

        val rValueTextView = dialogView.findViewById<TextView>(R.id.rValueText)
        val gValueTextView = dialogView.findViewById<TextView>(R.id.gValueText)
        val bValueTextView = dialogView.findViewById<TextView>(R.id.bValueText)
        val alphaValueTextView = dialogView.findViewById<TextView>(R.id.aValueText)
        val hValueTextView = dialogView.findViewById<TextView>(R.id.hValueText)
        val sValueTextView = dialogView.findViewById<TextView>(R.id.sValueText)
        val vValueTextView = dialogView.findViewById<TextView>(R.id.vValueText)
        val rSeekBar = dialogView.findViewById<SeekBar>(R.id.seekBarR)
        val gSeekBar = dialogView.findViewById<SeekBar>(R.id.seekBarG)
        val bSeekBar = dialogView.findViewById<SeekBar>(R.id.seekBarB)
        val alphaSeekBar = dialogView.findViewById<SeekBar>(R.id.seekBarA)
        val hSeekBar = dialogView.findViewById<SeekBar>(R.id.seekBarH)
        val sSeekBar = dialogView.findViewById<SeekBar>(R.id.seekBarS)
        val vSeekBar = dialogView.findViewById<SeekBar>(R.id.seekBarV)

        arguments?.let {
            r = it.getInt("key_r", 0)
            g = it.getInt("key_g", 0)
            b = it.getInt("key_b", 0)
            isAlphaEnabled = it.getBoolean("key_alpha", false)
            if(isAlphaEnabled){
                a = it.getInt("key_a", 0)
            }
            isHSVEnabled = it.getBoolean("key_hsv", false)
        }

        if(isAlphaEnabled){
            dialogView.findViewById<TextView>(R.id.label_a).visibility = View.VISIBLE
            alphaValueTextView.visibility = View.VISIBLE
            alphaSeekBar.visibility = View.VISIBLE
        }

        if(isHSVEnabled){
            dialogView.findViewById<TextView>(R.id.label_h).visibility = View.VISIBLE
            dialogView.findViewById<TextView>(R.id.label_s).visibility = View.VISIBLE
            dialogView.findViewById<TextView>(R.id.label_v).visibility = View.VISIBLE
            hValueTextView.visibility = View.VISIBLE
            sValueTextView.visibility = View.VISIBLE
            vValueTextView.visibility = View.VISIBLE
            hSeekBar.visibility = View.VISIBLE
            sSeekBar.visibility = View.VISIBLE
            vSeekBar.visibility = View.VISIBLE
        }

        fun setHSVByRGB(){
            val max = (r.coerceAtLeast(g.coerceAtLeast(b)))
            val min = (r.coerceAtMost(g.coerceAtMost(b)))
            v = (max / 2.55).toInt()
            s = (((max - min) / max.toDouble()) * 100.0).toInt()
            if(max != min){
                h = max - min
                h = when(min){
                    r -> {
                        (60.0 * ((b - g) / h.toDouble()) + 180).toInt()
                    }
                    g -> {
                        (60.0 * ((r - b) / h.toDouble()) + 300).toInt()
                    }
                    else -> {
                        (60.0 * ((g - r) / h.toDouble()) + 60).toInt()
                    }
                }
                if(h < 0){
                    h += 360
                }
                if(h > 360){
                    h -= 360
                }
                if(h == 360){
                    h = 0
                }
            }
        }

        fun setRGBByHSV(){
            val max = (v * 2.55).roundToInt()
            val min = (max - ((s / 100.toDouble()) * max)).toInt()
            when(h){
                in 0 until 60 -> {
                    r = max
                    g = ((h / 60.0) * (max - min) + min).toInt()
                    b = min
                }
                in 60 until 120 -> {
                    r = (((120 - h) / 60.0) * (max - min) + min).toInt()
                    g = max
                    b = min
                }
                in 120  until 180 -> {
                    r = min
                    g = max
                    b = (((h - 120) / 60.0) * (max - min) + min).toInt()
                }
                in 180 until 240 -> {
                    r = min
                    g = (((240 - h) / 60.0) * (max - min) + min).toInt()
                    b = max
                }
                in 240 until 300 -> {
                    r = (((h - 240) / 60.0) * (max - min) + min).toInt()
                    g = min
                    b = max
                }
                in 300 .. 360 -> {
                    r = max
                    g = min
                    b = (((360 - h) / 60.0) * (max - min) + min).toInt()
                }
            }
        }

        fun refresh() {
            rValueTextView.text = r.toString()
            gValueTextView.text = g.toString()
            bValueTextView.text = b.toString()
            alphaValueTextView.text = a.toString()

            dialogView.findViewById<View>(R.id.color_preview).setBackgroundColor(
                Color.argb(a,r,g,b)
            )
            rSeekBar.progress = r
            gSeekBar.progress = g
            bSeekBar.progress = b

            if(isHSVEnabled){
                hValueTextView.text = h.toString()
                sValueTextView.text = s.toString()
                vValueTextView.text = v.toString()
                hSeekBar.progress = h
                sSeekBar.progress = s
                vSeekBar.progress = v
            }
        }

        alphaSeekBar.progress = a
        setHSVByRGB()
        refresh()

        val listener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(!fromUser){ return }
                when (seekBar?.id) {
                    R.id.seekBarR -> {
                        r = progress
                        setHSVByRGB()
                    }
                    R.id.seekBarG -> {
                        g = progress
                        setHSVByRGB()
                    }
                    R.id.seekBarB -> {
                        b = progress
                        setHSVByRGB()
                    }
                    R.id.seekBarA -> {
                        a = progress
                        setHSVByRGB()
                    }
                    R.id.seekBarH -> {
                        h = progress
                        setRGBByHSV()
                    }
                    R.id.seekBarS -> {
                        s = progress
                        setRGBByHSV()
                    }
                    R.id.seekBarV -> {
                        v = progress
                        setRGBByHSV()
                    }
                }
                refresh()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        }
        rSeekBar.setOnSeekBarChangeListener(listener)
        gSeekBar.setOnSeekBarChangeListener(listener)
        bSeekBar.setOnSeekBarChangeListener(listener)
        hSeekBar.setOnSeekBarChangeListener(listener)
        sSeekBar.setOnSeekBarChangeListener(listener)
        vSeekBar.setOnSeekBarChangeListener(listener)
        alphaSeekBar.setOnSeekBarChangeListener(listener)

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                _listener?.onFinished(r, g, b, a)
            }
            .create()

        return dialog
    }
}