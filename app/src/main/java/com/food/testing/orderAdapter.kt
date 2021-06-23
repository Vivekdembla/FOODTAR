package com.food.testing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.food.testing.models.Message

class orderAdapter(options: FirestoreRecyclerOptions<Message>, val listener: IOrderAdapter) :
        FirestoreRecyclerAdapter<Message, orderAdapter.orderViewHolder>(options) {
    class orderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shopoforder = itemView.findViewById<TextView>(R.id.shopoforder)
        val orderImage = itemView.findViewById<ImageView>(R.id.orderImage2)
        val money = itemView.findViewById<TextView>(R.id.orderprice)
        val time = itemView.findViewById<TextView>(R.id.dateofOrder)
        val orderStatus = itemView.findViewById<TextView>(R.id.orderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): orderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.orderlayout, parent, false)
        val viewHolder = orderViewHolder(view)
        view.setOnClickListener {
            listener.onOrderClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: orderViewHolder, position: Int, model: Message) {
        Glide.with(holder.orderImage.context ).
        load(model.image).apply(RequestOptions.circleCropTransform()).
        into(holder.orderImage)
        holder.shopoforder.text = model.shopname
        holder.money.text = "₹${model.amount}"
        holder.time.text = model.timeOfMessage
        holder.orderStatus.text = model.status
    }
}

interface IOrderAdapter {
    fun onOrderClicked(orderid: String) {
    }
}