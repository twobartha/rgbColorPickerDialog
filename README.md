# rgbColorPickerDialog
Yet another simple color picker dialog

## Screen shot
![Screen Shot](screen_shot.png)

## Usage
    val dialog = RGBColorPickerDialog.create(255, 0, 0)
    val listener: RGBColorPickerDialog.Listener = object : RGBColorPickerDialog.Listener {
        override fun onFinished(r: Int, g: Int, b: Int) {
            // TODO: use values
        }
    }

    dialog.setOnFinishedListener(listener)
    dialog.show(supportFragmentManager, "dialog")

