package com.spunwheel.app

import android.app.Dialog
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.graphics.toColorInt

class ColorPickerDialog(
    context: Context,
    private val initialColor: Int,
    private val onColorSelected: (Int) -> Unit
) : Dialog(context, R.style.BottomSheetStyle) {

    private val presets = listOf(
        "#EF5350", "#EC407A", "#AB47BC", "#7E57C2",
        "#5C6BC0", "#42A5F5", "#26C6DA", "#26A69A",
        "#66BB6A", "#D4E157", "#FFCA28", "#FFA726",
        "#FF7043", "#FFFFFF", "#BDBDBD", "#616161",
        "#212121", "#1A1A1E", "#000000", "#FF5252",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_color_picker)

        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawableResource(android.R.color.transparent)
        }

        val hexInput = findViewById<EditText>(R.id.edit_hex)
        val preview = findViewById<View>(R.id.color_preview)
        val grid = findViewById<GridLayout>(R.id.color_grid)
        val btnOk = findViewById<TextView>(R.id.btn_ok)

        var currentColor = initialColor
        preview.setBackgroundColor(currentColor)
        hexInput.setText(String.format("#%06X", 0xFFFFFF and currentColor))

        // Preset grid
        presets.forEach { hex ->
            val swatch = View(context).apply {
                val size = (context.resources.displayMetrics.density * 40).toInt()
                layoutParams = GridLayout.LayoutParams().apply {
                    width = size; height = size
                    setMargins(6, 6, 6, 6)
                }
                val d = android.graphics.drawable.GradientDrawable()
                d.shape = android.graphics.drawable.GradientDrawable.OVAL
                d.setColor(Color.parseColor(hex))
                background = d
                setOnClickListener {
                    currentColor = Color.parseColor(hex)
                    preview.setBackgroundColor(currentColor)
                    hexInput.setText(hex)
                }
            }
            grid.addView(swatch)
        }

        hexInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                try {
                    val hex = s.toString()
                    if (hex.length == 7) {
                        currentColor = Color.parseColor(hex)
                        preview.setBackgroundColor(currentColor)
                    }
                } catch (_: Exception) {}
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnOk.setOnClickListener {
            onColorSelected(currentColor)
            dismiss()
        }

        findViewById<View>(R.id.btn_cancel).setOnClickListener { dismiss() }
    }
}
