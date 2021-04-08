package com.predict_squad.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.predict_squad.ui.home.model.MatchDetailsModel
import com.predict_squad.R
import com.predict_squad.common_helper.DefaultHelper.decrypt
import com.predict_squad.databinding.RowItemMatchDetailsBinding
import java.util.*

class MatchDetailsAdapter(
    private val context: Context, private val list: ArrayList<MatchDetailsModel.Data.Prediction>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var mcontext: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemMatchDetailsBinding: RowItemMatchDetailsBinding = RowItemMatchDetailsBinding.bind(itemView)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = viewGroup.context

        val view = LayoutInflater.from(viewGroup.context).inflate(
            R.layout.row_item_match_details, viewGroup, false
        )
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {

            if (decrypt(list?.get(position)?.title.toString()).isNotEmpty()) {
                holder.itemMatchDetailsBinding.tvTitle.text = decrypt(list?.get(position)?.title.toString())
                holder.itemMatchDetailsBinding.tvTitle.visibility = View.VISIBLE
            } else {
                holder.itemMatchDetailsBinding.tvTitle.visibility = View.GONE
            }

            if (!checkNull(decrypt(list?.get(position)?.description.toString()))) {
                holder.itemMatchDetailsBinding.tvDescription.text = decrypt(list?.get(position)?.description.toString())
                holder.itemMatchDetailsBinding.tvDescription.visibility = View.VISIBLE
            } else {
                holder.itemMatchDetailsBinding.tvDescription.visibility = View.GONE
            }

            if (decrypt(list?.get(position)?.title.toString()) == "Playing Squad") {
                holder.itemMatchDetailsBinding.tvDescription.visibility = View.GONE

                if (!checkNull(decrypt(list?.get(position)?.team1_name.toString()))) {
                    holder.itemMatchDetailsBinding.tvFirstTeamName.text = decrypt(list?.get(position)?.team1_name.toString())
                    holder.itemMatchDetailsBinding.tvFirstTeamName.visibility = View.VISIBLE
                } else {
                    holder.itemMatchDetailsBinding.tvFirstTeamName.visibility = View.GONE
                }

                if (!checkNull(decrypt(list?.get(position)?.team1_description.toString()))) {
                    holder.itemMatchDetailsBinding.tvFirstTeamDescription.text = decrypt(list?.get(position)?.team1_description.toString())
                    holder.itemMatchDetailsBinding.tvFirstTeamDescription.visibility = View.VISIBLE
                } else {
                    holder.itemMatchDetailsBinding.tvFirstTeamDescription.visibility = View.GONE
                }

                if (!checkNull(decrypt(list?.get(position)?.team2_name.toString()))) {
                    holder.itemMatchDetailsBinding.tvSecondTeamName.text = decrypt(list?.get(position)?.team2_name.toString())
                    holder.itemMatchDetailsBinding.tvSecondTeamName.visibility = View.VISIBLE
                } else {
                    holder.itemMatchDetailsBinding.tvSecondTeamName.visibility = View.GONE
                }

                if (!checkNull(decrypt(list?.get(position)?.team2_description.toString()))) {
                    holder.itemMatchDetailsBinding.tvSecondTeamDescription.text = decrypt(list?.get(position)?.team2_description.toString())
                    holder.itemMatchDetailsBinding.tvSecondTeamDescription.visibility = View.VISIBLE
                } else {
                    holder.itemMatchDetailsBinding.tvSecondTeamDescription.visibility = View.GONE
                }
            }
        }
    }


    private fun checkNull(str: String): Boolean {
        if (str.isEmpty() && str == "null") {
            return true
        }
        return false
    }

    fun addData(list: List<MatchDetailsModel.Data.Prediction>?) {
        if (list != null) {
            this.list?.addAll(list)
        }
        notifyDataSetChanged()
    }
}