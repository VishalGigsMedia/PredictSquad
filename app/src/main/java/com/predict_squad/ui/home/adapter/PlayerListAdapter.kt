package com.predict_squad.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squad_gyan.ui.home.model.PlayersModel
import com.predict_squad.R
import com.predict_squad.common_helper.DefaultHelper.decrypt
import com.predict_squad.databinding.RowItemPlayerListBinding

class PlayerListAdapter(
    private val context: Context, private val list: ArrayList<PlayersModel>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var mcontext: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemPlayerListBinding: RowItemPlayerListBinding = RowItemPlayerListBinding.bind(itemView)
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = viewGroup.context

        val view = LayoutInflater.from(viewGroup.context).inflate(
            R.layout.row_item_player_list, viewGroup, false
        )
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            try {

                if (decrypt(list!![position].profilePic).isNotEmpty()) {
                    Glide.with(context).load(decrypt(list[position].profilePic)).centerCrop().into(holder.itemPlayerListBinding.ivPlayer)
                }
                if (decrypt(list[position].name).isNotEmpty()) {
                    holder.itemPlayerListBinding.tvPlayerName.text = decrypt(list[position].name)
                }

                if (decrypt(list[position].teamType).isNotEmpty()) {
                    if (decrypt(list[position].teamType) == "t1") {
                        holder.itemPlayerListBinding.tvPlayerName.setTextColor(ContextCompat.getColor(context, R.color.white))
                        holder.itemPlayerListBinding.tvPlayerName.background = ContextCompat.getDrawable(context, R.drawable.curve_black)
                    } else {
                        holder.itemPlayerListBinding.tvPlayerName.setTextColor(ContextCompat.getColor(context, R.color.black))
                        holder.itemPlayerListBinding.tvPlayerName.background = ContextCompat.getDrawable(context, R.drawable.curve_white)
                    }
                }

                if (decrypt(list[position].isCaptain).isNotEmpty()) {
                    if (decrypt(list[position].isCaptain) == "1") {
                        holder.itemPlayerListBinding.tvType.background = ContextCompat.getDrawable(context, R.drawable.captain_background)
                        holder.itemPlayerListBinding.tvType.visibility = View.VISIBLE
                        holder.itemPlayerListBinding.tvType.text = context.getString(R.string.captain)
                        holder.itemPlayerListBinding.tvType.setTextColor(ContextCompat.getColor(context, R.color.white))
                    }
                }

                if (decrypt(list[position].isViceCaptain).isNotEmpty()) {
                    if (decrypt(list[position].isViceCaptain) == "1") {
                        holder.itemPlayerListBinding.tvType.background = ContextCompat.getDrawable(context, R.drawable.vice_captain_background)
                        holder.itemPlayerListBinding.tvType.visibility = View.VISIBLE
                        holder.itemPlayerListBinding.tvType.text = context.getString(R.string.vice_captain)
                        holder.itemPlayerListBinding.tvType.setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    interface MatchListClickListener {
        fun onMatchClick(id: String, matchType: String)
        fun onShowErrorDialog()
    }

}