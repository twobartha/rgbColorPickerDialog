package app.web.knot3home.rgbcolorpickerdialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import app.web.knot3home.rgbColorPickerDialog.R
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

// Exercise the minimum supported device API and a modern API on the build JDK 17.
@Config(sdk = [26, 35])
@RunWith(RobolectricTestRunner::class)
class RGBColorPickerDialogTest {
    class HostActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            setTheme(androidx.appcompat.R.style.Theme_AppCompat)
            super.onCreate(savedInstanceState)
        }
    }

    private lateinit var activity: ActivityController<HostActivity>
    private lateinit var picker: RGBColorPickerDialog
    private lateinit var dialog: AlertDialog

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(HostActivity::class.java).setup()
    }

    @After
    fun tearDown() {
        if (::picker.isInitialized && picker.isAdded) picker.dismissNow()
        activity.pause().stop().destroy()
        shadowOf(Looper.getMainLooper()).idle()
    }

    @Test
    fun rgbFactoryInitializesControlsAndReturnsOpaqueColor() {
        show(RGBColorPickerDialog.create(12, 34, 56))
        assertRgb(12, 34, 56)
        assertPreview(12, 34, 56, 255)
        assertEquals(listOf(12, 34, 56, 255), confirm())
    }

    @Test
    fun allFactoriesRejectOutOfRangeRgbChannels() {
        for (invalid in listOf(Int.MIN_VALUE, -1, 256, Int.MAX_VALUE)) {
            for (channel in 0..2) {
                val rgb = intArrayOf(12, 34, 56)
                rgb[channel] = invalid
                val factories = listOf<() -> RGBColorPickerDialog>(
                    { RGBColorPickerDialog.create(rgb[0], rgb[1], rgb[2]) },
                    { RGBColorPickerDialog.create(rgb[0], rgb[1], rgb[2], 78) },
                    { RGBColorPickerDialog.createWithHSV(rgb[0], rgb[1], rgb[2]) },
                    { RGBColorPickerDialog.createWithHSV(rgb[0], rgb[1], rgb[2], 78) }
                )
                for ((index, factory) in factories.withIndex()) {
                    assertThrows("factory=$index channel=$channel value=$invalid",
                        IllegalArgumentException::class.java) { factory() }
                }
            }
        }
    }

    @Test
    fun alphaFactoriesRejectOutOfRangeAlpha() {
        for (invalid in listOf(Int.MIN_VALUE, -1, 256, Int.MAX_VALUE)) {
            assertThrows(IllegalArgumentException::class.java) {
                RGBColorPickerDialog.create(12, 34, 56, invalid)
            }
            assertThrows(IllegalArgumentException::class.java) {
                RGBColorPickerDialog.createWithHSV(12, 34, 56, invalid)
            }
        }
    }

    @Test
    fun allFactoriesAcceptRgbEndpoints() {
        for (value in listOf(0, 255)) {
            val factories = listOf(
                RGBColorPickerDialog.create(value, value, value),
                RGBColorPickerDialog.create(value, value, value, 255),
                RGBColorPickerDialog.createWithHSV(value, value, value),
                RGBColorPickerDialog.createWithHSV(value, value, value, 255)
            )
            for (instance in factories) {
                show(instance)
                assertRgb(value, value, value)
                assertEquals(listOf(value, value, value, 255), confirm())
            }
        }
    }

    @Test
    fun alphaFactoriesPreserveTransparentAndOpaqueEndpoints() {
        for (alpha in listOf(0, 255)) {
            for (instance in listOf(
                RGBColorPickerDialog.create(12, 34, 56, alpha),
                RGBColorPickerDialog.createWithHSV(12, 34, 56, alpha)
            )) {
                show(instance)
                assertAlpha(alpha)
                assertPreview(12, 34, 56, alpha)
                assertEquals(listOf(12, 34, 56, alpha), confirm())
            }
        }
    }

    @Test
    fun factoriesShowOnlyRequestedOptionalControls() {
        val cases = listOf(
            Triple(RGBColorPickerDialog.create(12, 34, 56), false, false),
            Triple(RGBColorPickerDialog.create(12, 34, 56, 78), true, false),
            Triple(RGBColorPickerDialog.createWithHSV(12, 34, 56), false, true),
            Triple(RGBColorPickerDialog.createWithHSV(12, 34, 56, 78), true, true)
        )
        for ((instance, alpha, hsv) in cases) {
            show(instance)
            for (id in listOf(R.id.label_a, R.id.aValueText, R.id.seekBarA)) {
                assertEquals(if (alpha) View.VISIBLE else View.GONE,
                    dialog.findViewById<View>(id)!!.visibility)
            }
            for (id in listOf(R.id.label_h, R.id.label_s, R.id.label_v,
                R.id.hValueText, R.id.sValueText, R.id.vValueText,
                R.id.seekBarH, R.id.seekBarS, R.id.seekBarV)) {
                assertEquals(if (hsv) View.VISIBLE else View.GONE,
                    dialog.findViewById<View>(id)!!.visibility)
            }
        }
    }

    @Test
    fun hsvFactoryConvertsPrimaryAndSecondaryColors() {
        for ((hue, rgb) in hueColors()) {
            if (hue == 360) continue // Both endpoints represent red; RGB maps to 0.
            show(RGBColorPickerDialog.createWithHSV(rgb[0], rgb[1], rgb[2]))
            assertHsv(hue, 100, 100)
            assertRgb(rgb[0], rgb[1], rgb[2])
            assertEquals(rgb + 255, confirm())
        }
    }

    @Test
    fun hsvFactoryHandlesBlackWhiteAndGray() {
        for ((gray, value) in listOf(0 to 0, 255 to 100, 128 to 50)) {
            show(RGBColorPickerDialog.createWithHSV(gray, gray, gray))
            assertHsv(0, 0, value)
            assertEquals(listOf(gray, gray, gray, 255), confirm())
        }
    }

    @Test
    fun hsvInitializationDoesNotQuantizeTheOriginalRgb() {
        show(RGBColorPickerDialog.createWithHSV(64, 128, 192, 96))
        assertHsv(210, 66, 75)
        assertRgb(64, 128, 192)
        assertPreview(64, 128, 192, 96)
        assertEquals(listOf(64, 128, 192, 96), confirm())
    }

    @Test
    fun rgbSliderEditsUpdateLabelsPreviewAndCallback() {
        show(RGBColorPickerDialog.create(1, 2, 3, 96))
        moveSlider(R.id.seekBarR, 24)
        moveSlider(R.id.seekBarG, 68)
        moveSlider(R.id.seekBarB, 120)
        assertRgb(24, 68, 120)
        assertPreview(24, 68, 120, 96)
        assertEquals(listOf(24, 68, 120, 96), confirm())
    }

    @Test
    fun rgbEditsSynchronizeHsvControls() {
        show(RGBColorPickerDialog.createWithHSV(0, 255, 0))
        assertHsv(120, 100, 100)
        moveSlider(R.id.seekBarR, 255)
        assertHsv(60, 100, 100)
        moveSlider(R.id.seekBarG, 0)
        assertHsv(0, 100, 100)
        moveSlider(R.id.seekBarB, 255)
        assertHsv(300, 100, 100)
        assertPreview(255, 0, 255, 255)
        assertEquals(listOf(255, 0, 255, 255), confirm())
    }

    @Test
    fun hueSliderCoversEverySectorAndWrapsAt360() {
        for ((hue, rgb) in hueColors()) {
            show(RGBColorPickerDialog.createWithHSV(255, 0, 0, 96))
            moveSlider(R.id.seekBarH, hue)
            assertHsv(hue, 100, 100)
            assertRgb(rgb[0], rgb[1], rgb[2])
            assertPreview(rgb[0], rgb[1], rgb[2], 96)
            assertEquals(rgb + 96, confirm())
        }
    }

    @Test
    fun valueSliderRoundsHalfStepsUp() {
        show(RGBColorPickerDialog.createWithHSV(255, 255, 255))
        // Exact half-byte values must round up, not down due to binary 2.55.
        for ((value, gray) in listOf(90 to 230, 50 to 128, 10 to 26, 30 to 77, 70 to 179)) {
            moveSlider(R.id.seekBarV, value)
            assertRgb(gray, gray, gray)
            assertPreview(gray, gray, gray, 255)
        }
        assertEquals(listOf(179, 179, 179, 255), confirm())
    }

    @Test
    fun hueSliderInterpolatesInsideEverySector() {
        val cases = listOf(
            30 to listOf(255, 127, 0),
            90 to listOf(127, 255, 0),
            150 to listOf(0, 255, 127),
            210 to listOf(0, 127, 255),
            270 to listOf(127, 0, 255),
            330 to listOf(255, 0, 127)
        )
        for ((hue, rgb) in cases) {
            show(RGBColorPickerDialog.createWithHSV(255, 0, 0))
            moveSlider(R.id.seekBarH, hue)
            assertHsv(hue, 100, 100)
            assertRgb(rgb[0], rgb[1], rgb[2])
            assertEquals(rgb + 255, confirm())
        }
    }

    @Test
    fun saturationAndValueSlidersHandleAchromaticAndBlackEndpoints() {
        show(RGBColorPickerDialog.createWithHSV(255, 0, 0, 96))
        moveSlider(R.id.seekBarS, 0)
        assertRgb(255, 255, 255)
        moveSlider(R.id.seekBarV, 50)
        assertRgb(128, 128, 128)
        assertHsv(0, 0, 50)
        moveSlider(R.id.seekBarV, 0)
        assertRgb(0, 0, 0)
        moveSlider(R.id.seekBarS, 100)
        assertRgb(0, 0, 0)
        moveSlider(R.id.seekBarV, 100)
        assertRgb(255, 0, 0)
        assertPreview(255, 0, 0, 96)
        assertEquals(listOf(255, 0, 0, 96), confirm())
    }

    @Test
    fun saturationSliderInterpolatesTheOtherChannels() {
        show(RGBColorPickerDialog.createWithHSV(255, 0, 0))
        // Preserve the existing truncation of the minimum RGB channel.
        for ((saturation, minimum) in listOf(50 to 127, 25 to 191, 75 to 63)) {
            moveSlider(R.id.seekBarS, saturation)
            assertHsv(0, saturation, 100)
            assertRgb(255, minimum, minimum)
            assertPreview(255, minimum, minimum, 255)
        }
        assertEquals(listOf(255, 63, 63, 255), confirm())
    }

    @Test
    fun alphaSliderChangesOpacityWithoutChangingRgb() {
        show(RGBColorPickerDialog.createWithHSV(0, 255, 255, 96))
        assertAlpha(96)
        for (alpha in listOf(0, 255, 64)) {
            moveSlider(R.id.seekBarA, alpha)
            assertAlpha(alpha)
            assertRgb(0, 255, 255)
            assertHsv(180, 100, 100)
            assertPreview(0, 255, 255, alpha)
        }
        assertEquals(listOf(0, 255, 255, 64), confirm())
    }

    @Test
    fun programmaticProgressChangesDoNotFeedBackIntoSelectedColor() {
        show(RGBColorPickerDialog.createWithHSV(255, 0, 0, 96))
        for ((id, value) in listOf(R.id.seekBarR to 10, R.id.seekBarG to 20,
            R.id.seekBarB to 30, R.id.seekBarA to 40, R.id.seekBarH to 180,
            R.id.seekBarS to 0, R.id.seekBarV to 0)) {
            seekBar(id).progress = value
        }
        assertPreview(255, 0, 0, 96)
        assertEquals(listOf(255, 0, 0, 96), confirm())
    }

    @Test
    fun editingDoesNotNotifyUntilConfirmed() {
        show(RGBColorPickerDialog.create(12, 34, 56))
        val results = recordResults()
        moveSlider(R.id.seekBarR, 78)
        assertTrue(results.isEmpty())
        clickOk()
        assertEquals(listOf(listOf(78, 34, 56, 255)), results)
    }

    @Test
    fun cancelDoesNotNotifyListener() {
        show(RGBColorPickerDialog.create(12, 34, 56))
        val results = recordResults()
        moveSlider(R.id.seekBarG, 78)
        dialog.cancel()
        shadowOf(Looper.getMainLooper()).idle()
        assertTrue(results.isEmpty())
    }

    @Test
    fun dismissDoesNotNotifyListener() {
        show(RGBColorPickerDialog.create(12, 34, 56))
        val results = recordResults()
        picker.dismissNow()
        shadowOf(Looper.getMainLooper()).idle()
        assertTrue(results.isEmpty())
    }

    @Test
    fun confirmingWithoutListenerIsSafe() {
        show(RGBColorPickerDialog.create(12, 34, 56))
        clickOk()
        assertFalse(dialog.isShowing)
    }

    @Test
    fun replacingListenerNotifiesOnlyTheLatestListener() {
        show(RGBColorPickerDialog.create(12, 34, 56))
        val oldResults = recordResults()
        val newResults = recordResults()
        clickOk()
        assertTrue(oldResults.isEmpty())
        assertEquals(listOf(listOf(12, 34, 56, 255)), newResults)
    }

    private fun hueColors(): List<Pair<Int, List<Int>>> = listOf(
        0 to listOf(255, 0, 0),
        60 to listOf(255, 255, 0),
        120 to listOf(0, 255, 0),
        180 to listOf(0, 255, 255),
        240 to listOf(0, 0, 255),
        300 to listOf(255, 0, 255),
        360 to listOf(255, 0, 0)
    )

    private fun show(value: RGBColorPickerDialog) {
        if (::picker.isInitialized && picker.isAdded) picker.dismissNow()
        picker = value
        picker.showNow(activity.get().supportFragmentManager, "picker")
        dialog = picker.requireDialog() as AlertDialog
    }

    private fun seekBar(id: Int): SeekBar = requireNotNull(dialog.findViewById(id))

    private fun moveSlider(id: Int, value: Int) {
        val bar = seekBar(id)
        bar.progress = value
        // Android dispatches this with fromUser=true for a real gesture. A plain
        // setProgress() uses false and intentionally must not change the model.
        val listener = requireNotNull(shadowOf(bar).onSeekBarChangeListener)
        listener.onProgressChanged(bar, value, true)
    }

    private fun assertHsv(h: Int, s: Int, v: Int) {
        assertEquals(h, seekBar(R.id.seekBarH).progress)
        assertEquals(s, seekBar(R.id.seekBarS).progress)
        assertEquals(v, seekBar(R.id.seekBarV).progress)
        assertEquals(h.toString(), dialog.findViewById<TextView>(R.id.hValueText)!!.text.toString())
        assertEquals(s.toString(), dialog.findViewById<TextView>(R.id.sValueText)!!.text.toString())
        assertEquals(v.toString(), dialog.findViewById<TextView>(R.id.vValueText)!!.text.toString())
    }

    private fun assertAlpha(alpha: Int) {
        assertEquals(alpha, seekBar(R.id.seekBarA).progress)
        assertEquals(alpha.toString(), dialog.findViewById<TextView>(R.id.aValueText)!!.text.toString())
    }

    private fun assertRgb(r: Int, g: Int, b: Int) {
        assertEquals(r, seekBar(R.id.seekBarR).progress)
        assertEquals(g, seekBar(R.id.seekBarG).progress)
        assertEquals(b, seekBar(R.id.seekBarB).progress)
        assertEquals(r.toString(), dialog.findViewById<TextView>(R.id.rValueText)!!.text.toString())
        assertEquals(g.toString(), dialog.findViewById<TextView>(R.id.gValueText)!!.text.toString())
        assertEquals(b.toString(), dialog.findViewById<TextView>(R.id.bValueText)!!.text.toString())
    }

    private fun assertPreview(r: Int, g: Int, b: Int, a: Int) {
        val preview = dialog.findViewById<View>(R.id.color_preview)!!
        assertEquals(Color.argb(a, r, g, b), (preview.background as ColorDrawable).color)
    }

    private fun recordResults(): MutableList<List<Int>> {
        val results = mutableListOf<List<Int>>()
        picker.setOnFinishedListener(object : RGBColorPickerDialog.Listener {
            override fun onFinished(r: Int, g: Int, b: Int, a: Int) {
                results.add(listOf(r, g, b, a))
            }
        })
        return results
    }

    private fun clickOk() {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick()
        shadowOf(Looper.getMainLooper()).idle()
    }

    private fun confirm(): List<Int> {
        val results = recordResults()
        clickOk()
        assertEquals("Confirm must notify exactly once", 1, results.size)
        return results.single()
    }
}
