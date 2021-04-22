package com.food.vegtar

import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.food.vegtar.models.FoodDetail
import com.food.vegtar.models.OrderDetail

class CartAdapter(val listener:ICartAdapter):RecyclerView.Adapter<CartViewHolder>() {
    var cartItem = ArrayList<OrderDetail>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cartlist,parent,false)
        val viewHolder = CartViewHolder(view)
        val quantity = viewHolder.quantity
        viewHolder.add.setOnClickListener {
            var num: Int = Integer.valueOf(quantity.getText().toString())
            num++
            quantity.text = num.toString()
            listener.onAddClick(viewHolder.adapterPosition,num)
        }
        viewHolder.minus.setOnClickListener {
            var num: Int = Integer.valueOf(quantity.getText().toString())
            num--
            quantity.text = num.toString()
            listener.onMinusClick(viewHolder.adapterPosition,num)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        var newPrice: Int = Integer.valueOf(cartItem[position].EachFoodPrice)
        newPrice *= cartItem[position].quantity!!
        Glide.with(holder.itemImage.context ).load(cartItem[position].EachFoodUrl).into(holder.itemImage)
        holder.resname.text = cartItem[position].EachFoodName.toString()
        holder.price.text = "₹${newPrice.toString()}"
        holder.ordername.text = cartItem[position].EachShopName.toString()
        holder.quantity.text  =cartItem[position].quantity.toString()
    }

    override fun getItemCount(): Int {
        return cartItem.size
    }

    fun updateList(list:List<OrderDetail>){
        cartItem.clear()
        cartItem.addAll(list)
        notifyDataSetChanged()
    }
}

class CartViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    val resname = itemView.findViewById<TextView>(R.id.resname)
    val ordername = itemView.findViewById<TextView>(R.id.ordername)
    val price = itemView.findViewById<TextView>(R.id.price)
    val itemImage = itemView.findViewById<ImageView>(R.id.itemImage)
    val add = itemView.findViewById<Button>(R.id.add)
    val minus = itemView.findViewById<Button>(R.id.subtract)
    val quantity = itemView.findViewById<TextView>(R.id.quantity)
}

interface ICartAdapter{
    fun onAddClick(position: Int,value:Int){}
    fun onMinusClick(position:Int,value:Int){}
}