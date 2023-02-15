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

class RGBColorPickerDialog : DialogFragment() {
    private var r = 0
    private var g = 0
    private var b = 0
    private var a = 255
    private var isAlphaEnabled = false
    private var _listener: Listener? = null

    interface Listener {
        fun onFinished(r: Int, g: Int, b: Int, a: Int)
    }

    companion object {
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
    }

    fun setOnFinishedListener(listener: Listener) {
        _listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        arguments?.let {
            r = it.getInt("key_r", 0)
            g = it.getInt("key_g", 0)
            b = it.getInt("key_b", 0)
            isAlphaEnabled = it.getBoolean("key_alpha", false)
            if(isAlphaEnabled){
                a = it.getInt("key_a", 0)
            }
        }
        val factory = LayoutInflater.from(requireContext())
        val dialogView = factory.inflate(R.layout.rgb_color_picker_dialog, null)

        val alphaValueTextView = dialogView.findViewById<TextView>(R.id.alpha_value)
        val alphaSeekBar = dialogView.findViewById<SeekBar>(R.id.seekBarA)
        if(isAlphaEnabled){
            dialogView.findViewById<TextView>(R.id.label_a).visibility = View.VISIBLE
            alphaValueTextView.visibility = View.VISIBLE
            alphaSeekBar.visibility = View.VISIBLE
        }

        fun refresh() {
            dialogView.findViewById<TextView>(R.id.red_value).text = r.toString()
            dialogView.findViewById<TextView>(R.id.green_value).text = g.toString()
            dialogView.findViewById<TextView>(R.id.blue_value).text = b.toString()
            alphaValueTextView.text = a.toString()

            dialogView.findViewById<View>(R.id.color_preview).setBackgroundColor(
                Color.argb(a,r,g,b)
            )
        }

        dialogView.findViewById<SeekBar>(R.id.seekBarR).progress = r
        dialogView.findViewById<SeekBar>(R.id.seekBarG).progress = g
        dialogView.findViewById<SeekBar>(R.id.seekBarB).progress = b
        alphaSeekBar.progress = a
        refresh()

        val listener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when (seekBar?.id) {
                    R.id.seekBarR -> {
                        r = progress
                    }
                    R.id.seekBarG -> {
                        g = progress
                    }
                    R.id.seekBarB -> {
                        b = progress
                    }
                    R.id.seekBarA -> {
                        a = progress
                    }
                }
                refresh()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        }
        dialogView.findViewById<SeekBar>(R.id.seekBarR).setOnSeekBarChangeListener(listener)
        dialogView.findViewById<SeekBar>(R.id.seekBarG).setOnSeekBarChangeListener(listener)
        dialogView.findViewById<SeekBar>(R.id.seekBarB).setOnSeekBarChangeListener(listener)
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