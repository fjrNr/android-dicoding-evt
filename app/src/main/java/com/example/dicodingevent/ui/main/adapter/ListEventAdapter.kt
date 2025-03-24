package com.example.dicodingevent.ui.main.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.dicodingevent.R
import com.example.dicodingevent.data.local.entity.FavoriteEventEntity
import com.example.dicodingevent.data.remote.response.EventItem
import com.example.dicodingevent.databinding.ItemRowEventLargeBinding
import com.example.dicodingevent.databinding.ItemRowEventMediumBinding
import com.example.dicodingevent.ui.detail.DetailActivity

class ListEventAdapter<T, VB : ViewBinding>(
    private var items: List<T>,
    private val inflateBinding: (LayoutInflater, ViewGroup, Boolean) -> VB
) : RecyclerView.Adapter<ListEventAdapter<T, VB>.ViewHolder>() {

    inner class ViewHolder(private val binding: VB) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T) {
            if(item is EventItem) {
                when(binding) {
                    is ItemRowEventLargeBinding -> {
                        with(binding) {
                            Glide.with(root.context)
                                .load(item.mediaCover)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(ivItemCover)
                            tvItemName.text = item.name
                            tvItemDate.text = item.beginTime.toString()
                        }
                    }
                    is ItemRowEventMediumBinding -> {
                        with(binding) {
                            Glide.with(root.context)
                                .load(item.imageLogo)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(ivItemCover)
                            tvItemName.text = item.name
                            tvItemDate.text = item.beginTime.toString()
                        }
                    }
                }

                with(binding.root) {
                    setOnClickListener{
                        val intentDetail = Intent(context, DetailActivity::class.java)
                        intentDetail.putExtra(DetailActivity.EXTRA_INDEX, item.id)
                        intentDetail.putExtra(DetailActivity.EXTRA_TITLE, item.name)
                        context.startActivity(intentDetail)
                    }
                }
            }else if(item is FavoriteEventEntity && binding is ItemRowEventLargeBinding) {
                with(binding) {
                    Glide.with(root.context)
                        .load(item.mediaCover)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivItemCover)
                    tvItemName.text = item.name
                    tvItemDate.text = item.beginTime.toString()
                    root.setOnClickListener{
                        val intentDetail = Intent(root.context, DetailActivity::class.java)
                        intentDetail.putExtra(DetailActivity.EXTRA_INDEX, item.id)
                        intentDetail.putExtra(DetailActivity.EXTRA_TITLE, item.name)
                        root.context.startActivity(intentDetail)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflateBinding(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

