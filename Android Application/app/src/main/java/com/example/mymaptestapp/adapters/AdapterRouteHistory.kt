package com.example.mymaptestapp.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaptestapp.R
import com.example.mymaptestapp.activities.HomeActivity
import com.example.mymaptestapp.data.entities.UserRoute
import com.example.mymaptestapp.databinding.RouteHistoryItemBinding
import java.text.SimpleDateFormat
import java.util.Locale


class AdapterRouteHistory(
    private val context: Context,
    private var points: List<UserRoute>,
    private val onButtonClick: (Int) -> Unit
) : RecyclerView.Adapter<AdapterRouteHistory.ItemViewHolder>() {

    private lateinit var binding: RouteHistoryItemBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterRouteHistory.ItemViewHolder {
        binding = RouteHistoryItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return points.size
    }

    override fun onBindViewHolder(holder: AdapterRouteHistory.ItemViewHolder, position: Int) {
        val item = points[position]
        holder.bind(item)
    }

    inner class ItemViewHolder(private val binding: RouteHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.moreInfoButton.setOnClickListener {
                onButtonClick(points[adapterPosition].id)
            }
        }

        fun bind(item: UserRoute) {
            val dateFormat = SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.getDefault())

            binding.apply {
                routeId.text = HtmlCompat.fromHtml(String.format("<b># %d</b>", item.id), HtmlCompat.FROM_HTML_MODE_LEGACY)
                createdAt.text = dateFormat.format(item.createdAt)
                routeLength.text = HtmlCompat.fromHtml(String.format("<i>Длина маршрута: <b>%.2f км.</b></i>", item.routeLength), HtmlCompat.FROM_HTML_MODE_LEGACY)
                routePoints.text = HtmlCompat.fromHtml(String.format("<i>Число точек в маршруте: <b>%d</b></i>", item.routePoints.size - 1), HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        }
    }
}