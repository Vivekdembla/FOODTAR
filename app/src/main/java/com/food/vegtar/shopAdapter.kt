package com.food.vegtar

import android.content.Context
import android.graphics.Color
import android.net.MailTo.parse
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.food.vegtar.models.Shop
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class shopAdapter(options: FirestoreRecyclerOptions<Shop>, val listener: IShopAdapter) :
    FirestoreRecyclerAdapter<Shop, shopAdapter.shopViewHolder>(options) {
    class shopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shopName = itemView.findViewById<TextView>(R.id.shopName)
        val shopImage = itemView.findViewById<ImageView>(R.id.shopImage)
        val description = itemView.findViewById<TextView>(R.id.shopDetail)
        val address = itemView.findViewById<TextView>(R.id.shopAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): shopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shoplist, parent, false)
        val viewHolder = shopViewHolder(view)
        view.setOnClickListener {
            view.startAnimation(AnimationUtils.loadAnimation(parent.context, R.anim.animation))
            listener.onShopClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: shopViewHolder, position: Int, model: Shop) {
        Glide.with(holder.shopImage.context).load(model.shopImage).into(holder.shopImage)
        holder.shopName.text = model.NameOfShop
        holder.description.text = model.ShopDescription
        holder.address.text = model.Address
    }
}

interface IShopAdapter {
    fun onShopClicked(shopid: String) {
    }
}