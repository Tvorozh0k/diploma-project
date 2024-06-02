package com.example.mymaptestapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaptestapp.data.entities.DeliveryPoint
import com.example.mymaptestapp.databinding.PointItemBinding
import com.example.mymaptestapp.api.utils.NewRouteUtils

class AdapterRoutePointsList(
    private val context: Context,
    private var points: List<DeliveryPoint>,
    private val onItemClick: () -> Unit
) : RecyclerView.Adapter<AdapterRoutePointsList.ItemViewHolder>() {

    private lateinit var binding: PointItemBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterRoutePointsList.ItemViewHolder {
        binding = PointItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemViewHolder(binding)
    }

    fun setFilteredList(filteredList: List<DeliveryPoint>) {
        points = filteredList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return points.size
    }

    override fun onBindViewHolder(holder: AdapterRoutePointsList.ItemViewHolder, position: Int) {
        val item = points[position]
        holder.bind(item)
    }

    inner class ItemViewHolder(private val binding: PointItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.chooseButton.setOnClickListener {
                if (NewRouteUtils.routePointsContains(points[adapterPosition].id)) {
                    NewRouteUtils.removeRoutePoint(points[adapterPosition].id)
                } else {
                    NewRouteUtils.addRoutePoint(points[adapterPosition].id)
                }

                notifyItemChanged(adapterPosition)
                onItemClick()
            }
        }

        fun bind(item: DeliveryPoint) {
            binding.apply {
                name.text = item.name
                address.text = item.address
                phoneNumber.text = item.phoneNumber
                chooseButton.isChecked = NewRouteUtils.routePointsContains(item.id)
            }
        }
    }
}