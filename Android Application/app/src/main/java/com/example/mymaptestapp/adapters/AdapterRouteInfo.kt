package com.example.mymaptestapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaptestapp.adapters.models.RouteInfoItem
import com.example.mymaptestapp.adapters.models.RouteInfoItemType
import com.example.mymaptestapp.databinding.RouteInfoItemBinding
import com.example.mymaptestapp.databinding.RouteInfoStartItemBinding
import com.example.mymaptestapp.databinding.RouteInfoFinalItemBinding

class AdapterRouteInfo(private val context: Context, private val items: List<RouteInfoItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            RouteInfoItemType.START_ITEM.ordinal -> {
                val binding = RouteInfoStartItemBinding.inflate(LayoutInflater.from(context), parent, false)
                StartViewHolder(binding)
            }
            RouteInfoItemType.LIST_ITEM.ordinal -> {
                val binding = RouteInfoItemBinding.inflate(LayoutInflater.from(context), parent, false)
                ItemViewHolder(binding)
            }
            RouteInfoItemType.FINAL_ITEM.ordinal -> {
                val binding = RouteInfoFinalItemBinding.inflate(LayoutInflater.from(context), parent, false)
                FinalViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is StartViewHolder -> {
                holder.bind(item)
            }
            is ItemViewHolder -> {
                holder.bind(item)
            }
            is FinalViewHolder -> {
                holder.bind(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type.ordinal
    }

    inner class StartViewHolder(private val binding: RouteInfoStartItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RouteInfoItem) {

            binding.name.text = item.point.name
            binding.address.text = item.point.address
            binding.phoneNumber.text = item.point.phoneNumber
        }
    }

    inner class ItemViewHolder(private val binding: RouteInfoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RouteInfoItem) {

            binding.number.text = item.index.toString()

            binding.name.text = item.point.name
            binding.address.text = item.point.address
            binding.phoneNumber.text = item.point.phoneNumber
        }
    }

    inner class FinalViewHolder(private val binding: RouteInfoFinalItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RouteInfoItem) {

            binding.name.text = item.point.name
            binding.address.text = item.point.address
            binding.phoneNumber.text = item.point.phoneNumber
        }
    }
}