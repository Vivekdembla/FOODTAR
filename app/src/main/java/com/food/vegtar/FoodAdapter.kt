package com.food.vegtar

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.food.vegtar.models.FoodDetail

class FoodAdapter(val listener: IFoodAdapter): RecyclerView.Adapter<FoodViewHolder>() {

    var foodListArray=ArrayList<FoodDetail>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        Log.e("Check","OnCreate me")
        val view= LayoutInflater.from(parent.context).inflate(R.layout.foodlist, parent, false)
        val viewHolder = FoodViewHolder(view)
        view.setOnClickListener{
            listener.onClickFood(viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        Log.e("Check","position is ${position}")
        Glide.with(holder.FoodImage.context ).load(foodListArray[position].EachFoodUrl).
        apply(RequestOptions.circleCropTransform()).into(holder.FoodImage)
        holder.name.text = foodListArray[position].EachFoodName
        holder.price.text = "₹${foodListArray[position].EachFoodPrice}"
        holder.description.text = foodListArray[position].EachFoodDes
    }

    override fun getItemCount(): Int {
        return foodListArray.size
    }
    fun updateList(list:List<FoodDetail>){
        Log.e("Check","Update k andr")
        foodListArray.clear()
        foodListArray.addAll(list)
        notifyDataSetChanged()
        Log.e("Check","Update k bahar jaate hue")
    }
}

class FoodViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
    val name = itemView.findViewById<TextView>(R.id.FoodName)
    val price = itemView.findViewById<TextView>(R.id.price)
    val FoodImage = itemView.findViewById<ImageView>(R.id.FoodImage)
    val description = itemView.findViewById<TextView>(R.id.desc)
}

interface IFoodAdapter{
    fun onClickFood(position: Int){

    }
}
