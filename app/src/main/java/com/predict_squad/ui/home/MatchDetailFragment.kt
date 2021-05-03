package com.predict_squad.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.predict_squad.MainActivity
import com.predict_squad.common_helper.Application
import com.predict_squad.common_helper.ConstantHelper
import com.predict_squad.common_helper.DefaultHelper.decrypt
import com.predict_squad.common_helper.DefaultHelper.forceLogout
import com.predict_squad.common_helper.DefaultHelper.showToast
import com.predict_squad.common_helper.OnCurrentFragmentVisibleListener
import com.predict_squad.common_helper.PreferenceHelper
import com.predict_squad.databinding.FragmentMatchDetailsBinding
import com.predict_squad.retrofit.APIService
import com.predict_squad.ui.home.adapter.MatchDetailsAdapter
import com.predict_squad.ui.home.model.MatchDetailsModel
import com.predict_squad.ui.home.view_model.MatchListViewModel
import javax.inject.Inject

class MatchDetailFragment(private val matchId: String, val matchType: String) : Fragment() {
    @Inject
    lateinit var apiService: APIService

    private var callback: OnCurrentFragmentVisibleListener? = null

    private lateinit var layoutManager: LinearLayoutManager
    private var list: ArrayList<MatchDetailsModel.Data.Prediction> = ArrayList()
    private var adapter: MatchDetailsAdapter? = null

    private lateinit var viewModel: MatchListViewModel
    private var mBinding: FragmentMatchDetailsBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        Application.instance?.getComponent()?.inject(this)
        viewModel = ViewModelProvider(this).get(MatchListViewModel::class.java)
        mBinding = FragmentMatchDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        // callback?.onSetToolbarTitle(true, MatchDetailFragment::class.java.simpleName,"")
        setAdapter()
        getMatchDetails(matchId, matchType)
    }

    fun setOnCurrentFragmentVisibleListener(activity: MainActivity) {
        callback = activity
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun setAdapter() {
        layoutManager = LinearLayoutManager(context)
        mBinding?.rvMatchDetails?.layoutManager = layoutManager
        adapter = MatchDetailsAdapter(
            context as FragmentActivity, list
        )
        mBinding?.rvMatchDetails?.adapter = adapter
        adapter?.notifyDataSetChanged()
    }

    private fun getMatchDetails(matchId: String, matchType: String) {
        viewModel.getMatchDetails(context as FragmentActivity, apiService, matchId, matchType)?.observe(viewLifecycleOwner, { matchDetailsModel ->
            if (matchDetailsModel != null) {
                if (matchDetailsModel.force_logout != ConstantHelper.forceLogout) {
                    forceLogout(context as FragmentActivity, "")
                }
                when (matchDetailsModel.status) {
                    ConstantHelper.success -> {
                        setMatchDetails(matchDetailsModel.data?.match_details)

                        if (matchDetailsModel.data?.prediction?.isNotEmpty() == true) {
                            mBinding?.rvMatchDetails?.visibility = View.VISIBLE
                            this.list = matchDetailsModel.data.prediction as ArrayList<MatchDetailsModel.Data.Prediction>
                            adapter?.addData(list)
                        }

                        //teamDetails(matchDetailsModel.data?.fantasy_teams!!)

                    }
                    ConstantHelper.failed -> {
                        setNoDataLayout(decrypt(matchDetailsModel.message.toString()))
                    }
                    ConstantHelper.authorizationFailed -> {
                        setNoDataLayout(decrypt(matchDetailsModel.message.toString()))
                    }
                    ConstantHelper.apiFailed -> {
                        showToast(context, decrypt(matchDetailsModel.message.toString()))
                    }
                    ConstantHelper.noInternet -> {
                        setNoDataLayout(decrypt(matchDetailsModel.message.toString()))
                    }
                }
            }
        })
    }

    private fun setNoDataLayout(msg: String) {
        if (msg.isNotEmpty()) {
            mBinding?.clNoData?.tvNoDataLayout?.text = msg
            mBinding?.clNoData?.clNoDataParent?.visibility = View.VISIBLE
        }
    }

    // private fun setMatchDetails(matchDetails: MatchDetailsModel1.Data.MatchDetails?) {
    private fun setMatchDetails(matchDetails: MatchDetailsModel.Data.MatchDetails?) {
        if (matchDetails != null) {
            mBinding?.clMatchDetail?.visibility = View.VISIBLE
            mBinding?.cvMatchDetails?.visibility = View.VISIBLE
            mBinding?.tvTitle?.visibility = View.VISIBLE
            if (decrypt(matchDetails.title.toString()).isNotEmpty()) {
                mBinding?.tvTitle?.text = decrypt(matchDetails.title.toString())
            }
            if (decrypt(matchDetails.match.toString()).isNotEmpty()) {
                mBinding?.tvMatchValue?.text = decrypt(matchDetails.match.toString())
            }
            if (decrypt(matchDetails.tournament.toString()).isNotEmpty()) {
                mBinding?.tvTournamentValue?.text = decrypt(matchDetails.tournament.toString())
            }
            if (decrypt(matchDetails.match_date.toString()).isNotEmpty()) {
                val value = decrypt(matchDetails.match_date.toString()).split(" ")
                val date = value[0]
                val time = value[1]
                mBinding?.tvDateValue?.text = date
                mBinding?.tvTimeValue?.text = time
            }
        }
    }

}