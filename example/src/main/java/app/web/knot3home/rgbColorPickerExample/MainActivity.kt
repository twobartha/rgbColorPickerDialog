package app.web.knot3home.rgbColorPickerExample

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.graphics.red
import androidx.core.graphics.green
import androidx.core.graphics.blue
import app.web.knot3home.rgbcolorpickerdialog.RGBColorPickerDialog

class MainActivity : AppCompatActivity() {
    var color = Color.MAGENTA
    lateinit var colorView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        colorView = findViewById(R.id.view)

        colorView.setBackgroundColor(color)
        findViewById<Button>(R.id.pickButton).setOnClickListener {

            // Call the dialog
            val dialog = RGBColorPickerDialog.create(color.red, color.green, color.blue)
            val listener: RGBColorPickerDialog.Listener = object : RGBColorPickerDialog.Listener {
                override fun onFinished(r: Int, g: Int, b: Int) {
                    color = Color.rgb(r, g, b)
                    colorView.setBackgroundColor(color)
                }
            }

            dialog.setOnFinishedListener(listener)
            dialog.show(supportFragmentManager, "dialog")
        }
    }
}