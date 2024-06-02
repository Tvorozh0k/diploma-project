package com.example.mymaptestapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaptestapp.data.entities.DeliveryPoint
import com.example.mymaptestapp.databinding.StartPointItemBinding
import com.example.mymaptestapp.api.utils.NewRouteUtils


class AdapterStartPointList(
    private val context: Context,
    private var points: List<DeliveryPoint>,
    private val onItemClick: () -> Unit
) : RecyclerView.Adapter<AdapterStartPointList.ItemViewHolder>() {

    private lateinit var binding: StartPointItemBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterStartPointList.ItemViewHolder {
        binding = StartPointItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemViewHolder(binding)
    }

    fun setFilteredList(filteredList: List<DeliveryPoint>) {
        points = filteredList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return points.size
    }

    override fun onBindViewHolder(holder: AdapterStartPointList.ItemViewHolder, position: Int) {
        val item = points[position]
        holder.bind(item)
    }

    inner class ItemViewHolder(private val binding: StartPointItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.chooseButton.setOnClickListener {
                val ind = points.indexOfFirst { point -> point.id == NewRouteUtils.getStartPoint() }

                if (ind != -1) {
                    notifyItemChanged(ind)
                }

                NewRouteUtils.setStartPoint(points[adapterPosition].id)
                notifyItemChanged(adapterPosition)
                onItemClick()
            }
        }

        fun bind(item: DeliveryPoint) {
            binding.apply {
                name.text = item.name
                address.text = item.address
                phoneNumber.text = item.phoneNumber
                chooseButton.isChecked = item.id == NewRouteUtils.getStartPoint()
            }
        }
    }
}