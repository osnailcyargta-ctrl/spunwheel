package com.spunwheel.app

import android.graphics.drawable.GradientDrawable
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(
    private val items: MutableList<WheelItem>,
    private val onChanged: () -> Unit,
    private val onEditColor: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<ItemAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val colorSwatch: View = view.findViewById(R.id.swatch_color)
        val textSwatch: View = view.findViewById(R.id.swatch_text)
        val labelEdit: EditText = view.findViewById(R.id.edit_label)
        val deleteBtn: View = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_wheel_entry, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.labelEdit.setText(item.label)
        holder.labelEdit.setOnFocusChangeListener { _, _ -> }
        holder.labelEdit.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                items[holder.bindingAdapterPosition].label = s.toString()
                onChanged()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        setSwatchColor(holder.colorSwatch, item.color)
        setSwatchColor(holder.textSwatch, item.textColor)

        holder.colorSwatch.setOnClickListener {
            onEditColor(holder.bindingAdapterPosition, false)
        }
        holder.textSwatch.setOnClickListener {
            onEditColor(holder.bindingAdapterPosition, true)
        }

        holder.deleteBtn.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (items.size > 2) {
                items.removeAt(pos)
                notifyItemRemoved(pos)
                notifyItemRangeChanged(pos, items.size)
                onChanged()
            } else {
                Toast.makeText(holder.itemView.context, "Minimal 2 pilihan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setSwatchColor(view: View, color: Int) {
        val d = GradientDrawable()
        d.shape = GradientDrawable.OVAL
        d.setColor(color)
        view.background = d
    }

    override fun getItemCount() = items.size
}
