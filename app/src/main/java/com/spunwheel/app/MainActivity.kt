package com.spunwheel.app

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var wheelView: WheelView
    private lateinit var spinBtn: TextView
    private lateinit var resultText: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var addBtn: TextView
    private lateinit var editPanel: View
    private lateinit var toggleEdit: TextView

    private val defaultColors = listOf(
        0xFFEF5350.toInt(), 0xFF42A5F5.toInt(), 0xFF66BB6A.toInt(),
        0xFFFFCA28.toInt(), 0xFFAB47BC.toInt(), 0xFFFF7043.toInt(),
        0xFF26C6DA.toInt(), 0xFFEC407A.toInt()
    )

    private val items = mutableListOf(
        WheelItem(label = "Option 1", color = defaultColors[0], textColor = Color.WHITE),
        WheelItem(label = "Option 2", color = defaultColors[1], textColor = Color.WHITE),
        WheelItem(label = "Option 3", color = defaultColors[2], textColor = Color.WHITE),
        WheelItem(label = "Option 4", color = defaultColors[3], textColor = Color.BLACK),
        WheelItem(label = "Option 5", color = defaultColors[4], textColor = Color.WHITE),
        WheelItem(label = "Option 6", color = defaultColors[5], textColor = Color.WHITE),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#0A0A0C")
        setContentView(R.layout.activity_main)

        wheelView = findViewById(R.id.wheel_view)
        spinBtn = findViewById(R.id.btn_spin)
        resultText = findViewById(R.id.result_text)
        recycler = findViewById(R.id.recycler_items)
        addBtn = findViewById(R.id.btn_add)
        editPanel = findViewById(R.id.edit_panel)
        toggleEdit = findViewById(R.id.toggle_edit)

        adapter = ItemAdapter(items, ::refreshWheel, ::onEditColor)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        wheelView.items = items.toList()

        wheelView.onSpinEnd = { winner ->
            resultText.text = winner.label
            resultText.setTextColor(winner.color)
            resultText.animate().alpha(0f).setDuration(0).withEndAction {
                resultText.animate().alpha(1f).setDuration(400).start()
            }.start()
            spinBtn.isEnabled = true
            spinBtn.text = "SPIN"
        }

        spinBtn.setOnClickListener {
            if (items.size < 2) {
                Toast.makeText(this, "Tambah minimal 2 pilihan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            resultText.text = ""
            spinBtn.isEnabled = false
            spinBtn.text = "..."
            wheelView.spin()
        }

        addBtn.setOnClickListener {
            val nextColor = defaultColors[items.size % defaultColors.size]
            val newItem = WheelItem(
                label = "Option ${items.size + 1}",
                color = nextColor,
                textColor = Color.WHITE
            )
            items.add(newItem)
            adapter.notifyItemInserted(items.size - 1)
            recycler.smoothScrollToPosition(items.size - 1)
            refreshWheel()
        }

        toggleEdit.setOnClickListener {
            if (editPanel.visibility == View.VISIBLE) {
                editPanel.visibility = View.GONE
                toggleEdit.text = "Edit"
            } else {
                editPanel.visibility = View.VISIBLE
                toggleEdit.text = "Done"
            }
        }
    }

    private fun refreshWheel() {
        wheelView.items = items.toList()
    }

    private fun onEditColor(position: Int, isText: Boolean) {
        val item = items[position]
        val current = if (isText) item.textColor else item.color
        ColorPickerDialog(this, current) { selectedColor ->
            if (isText) items[position].textColor = selectedColor
            else items[position].color = selectedColor
            adapter.notifyItemChanged(position)
            refreshWheel()
        }.show()
    }
}
