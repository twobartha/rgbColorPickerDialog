package app.web.knot3home.rgbColorPickerExample

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.graphics.alpha
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

        val dialogListener = object : RGBColorPickerDialog.Listener {
            override fun onFinished(r: Int, g: Int, b: Int, a: Int) {
                color = Color.argb(a, r, g, b)
                colorView.setBackgroundColor(color)
            }
        }

        findViewById<Button>(R.id.pickButton).setOnClickListener {
            // Call the dialog
            val dialog = RGBColorPickerDialog.create(color.red, color.green, color.blue)
            dialog.setOnFinishedListener(dialogListener)
            dialog.show(supportFragmentManager, "dialog")
        }

        findViewById<Button>(R.id.pickButton2).setOnClickListener {
            val dialog = RGBColorPickerDialog.create(
                color.red, color.green, color.blue, color.alpha
            )
            dialog.setOnFinishedListener(dialogListener)
            dialog.show(supportFragmentManager, "dialog")
        }

        findViewById<Button>(R.id.pickButton3).setOnClickListener {
            val dialog = RGBColorPickerDialog.createWithHSV(color.red, color.green, color.blue)
            dialog.setOnFinishedListener(dialogListener)
            dialog.show(supportFragmentManager, "dialog")
        }

        findViewById<Button>(R.id.pickButton4).setOnClickListener {
            val dialog = RGBColorPickerDialog.createWithHSV(
                color.red, color.green, color.blue, color.alpha
            )
            dialog.setOnFinishedListener(dialogListener)
            dialog.show(supportFragmentManager, "dialog")
        }

    }
}