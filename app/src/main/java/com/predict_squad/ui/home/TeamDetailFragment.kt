package com.predict_squad.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson
import com.squad_gyan.ui.home.model.PlayersModel
import com.squad_gyan.ui.home.model.TeamDetailsModel
import com.predict_squad.R
import com.predict_squad.common_helper.Application
import com.predict_squad.common_helper.ConstantHelper
import com.predict_squad.common_helper.DefaultHelper
import com.predict_squad.common_helper.MarginItemDecoration
import com.predict_squad.common_helper.PreferenceHelper
import com.predict_squad.databinding.FragmentTeamDetailBinding
import com.predict_squad.retrofit.APIService
import com.predict_squad.ui.home.adapter.PlayerListAdapter
import com.predict_squad.ui.home.model.MatchDetailsModel.Data.FantasyTeams.Team1
import com.predict_squad.ui.home.model.MatchDetailsModel.Data.FantasyTeams.Team2

import com.predict_squad.ui.home.view_model.MatchListViewModel
import javax.inject.Inject

class TeamDetailFragment(private val matchId: String, val matchType: String) : Fragment() {
    @Inject
    lateinit var apiService: APIService

    private var mBinding: FragmentTeamDetailBinding? = null
    private val binding get() = mBinding!!

    private lateinit var viewModel: MatchListViewModel

    private lateinit var preferenceHelper: PreferenceHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        Application.instance?.getComponent()?.inject(this)
        viewModel = ViewModelProvider(this).get(MatchListViewModel::class.java)
        mBinding = FragmentTeamDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        /*mBinding?.clBatsmenHolder?.setPadding(70, 0, 70, 0)
        mBinding?.clFirstTeamFifthBatsmen?.visibility = View.GONE
        mBinding?.clFirstTeamSixthBatsmen?.visibility = View.GONE*/

        // mBinding?.nsv?.post(Runnable { mBinding?.nsv?.fullScroll(View.Foc) })

        mBinding?.rvWicketKeeper?.suppressLayout(true)

        /* mBinding?.rvBatsmen?.isNestedScrollingEnabled = false
         mBinding?.rvAllRound?.isNestedScrollingEnabled = false
         mBinding?.rvBowler?.isNestedScrollingEnabled = false
 */

        getTeamDetails(matchId, matchType)

        /*preferenceHelper = PreferenceHelper(requireContext())
        val teamFirst = preferenceHelper.getString("team1")
        val teamSecond = preferenceHelper.getString("team2")
        if (teamFirst.isNotEmpty()) {
            setTeamFirst()
        }
        if (teamSecond.isNotEmpty()) {
            setTeamSecond()
        }*/

    }

    private fun getTeamDetails(matchId: String, matchType: String) {
        viewModel.getTeamDetails(context as FragmentActivity, apiService, matchId, matchType)?.observe(viewLifecycleOwner, { teamDetailsModel ->
            if (teamDetailsModel != null) {
                if (teamDetailsModel.force_logout != ConstantHelper.forceLogout) {
                    DefaultHelper.forceLogout(context as FragmentActivity, "")
                }
                when (teamDetailsModel.status) {
                    ConstantHelper.success -> {

                        //wicket keeper
                        setFirstTeamWicketKeeper(teamDetailsModel.data?.team1?.wicket_keepers!!)

                        //batsmen
                        setFirstTeamBatsmen(teamDetailsModel.data?.team1?.batsmens!!)

                        //all rounder
                        setFirstTeamAllRound(teamDetailsModel.data.team1.allrounders!!)

                        //bowler
                        setFirstTeamBowler(teamDetailsModel.data.team1.bowlers!!)

                        //second team wicket keeper
                        setSecondTeamWicketKeeper(teamDetailsModel.data.team2?.wicket_keepers!!)

                        //second team batsmen
                        setSecondTeamBatsmen(teamDetailsModel.data.team2.batsmens)

                        //second team all rounder
                        setSecondTeamAllRounder(teamDetailsModel.data.team2.allrounders)

                        //second team bowler
                        setSecondTeamBowler(teamDetailsModel.data.team2.bowlers)

                    }
                    ConstantHelper.failed -> {
                        setNoDataLayout(DefaultHelper.decrypt(teamDetailsModel.message.toString()))
                    }
                    ConstantHelper.authorizationFailed -> {
                        setNoDataLayout(DefaultHelper.decrypt(teamDetailsModel.message.toString()))
                    }
                    ConstantHelper.apiFailed -> {
                        DefaultHelper.showToast(context, DefaultHelper.decrypt(teamDetailsModel.message.toString()))
                    }
                    ConstantHelper.noInternet -> {
                        setNoDataLayout(DefaultHelper.decrypt(teamDetailsModel.message.toString()))
                    }
                }
            }
        })
    }

    private fun setFirstTeamWicketKeeper(wicketKeepers: List<TeamDetailsModel.Data.Team1.WicketKeeper?>) {
        val wicketKeeper: ArrayList<PlayersModel> = ArrayList()
        for (i in wicketKeepers) {
            val playerModel = PlayersModel(i?.id.toString(), i?.name.toString(), i?.profile_pic.toString(), i?.type.toString(), i?.captain.toString(), i?.vice_captain.toString(), i?.team.toString())
            wicketKeeper.add(playerModel)
        }
        val playerListAdapter = PlayerListAdapter(requireActivity(), wicketKeeper)
        mBinding?.rvWicketKeeper?.adapter = playerListAdapter
        /*mBinding?.rvWicketKeeper?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        */
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        //layoutManager.alignItems = AlignItems.CENTER
        //layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.scrollToPosition(0)
        mBinding?.rvWicketKeeper?.layoutManager = layoutManager
        mBinding?.rvWicketKeeper?.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen._10sdp).toInt()
            )
        )
        playerListAdapter.notifyDataSetChanged()
    }

    private fun setFirstTeamBatsmen(batsmens: List<TeamDetailsModel.Data.Team1.Batsmen?>) {

        val batsmen: ArrayList<PlayersModel> = ArrayList()
        for (i in batsmens) {
            val playerModel = PlayersModel(i?.id.toString(), i?.name.toString(), i?.profile_pic.toString(), i?.type.toString(), i?.captain.toString(), i?.vice_captain.toString(), i?.team.toString())
            batsmen.add(playerModel)
        }

        val playerListBatsmenAdapter = PlayerListAdapter(requireActivity(), batsmen)
        mBinding?.rvBatsmen?.adapter = playerListBatsmenAdapter

        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.justifyContent = JustifyContent.CENTER
        //layoutManager.alignItems = AlignItems.CENTER
        layoutManager.flexDirection = FlexDirection.ROW
        // layoutManager.flexWrap = FlexWrap.WRAP
        mBinding?.rvBatsmen?.layoutManager = layoutManager
        mBinding?.rvBatsmen?.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen._10sdp).toInt()
            )
        )
        playerListBatsmenAdapter.notifyDataSetChanged()

        /*mBinding?.rvBatsmen?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        */


        /* if(batsmen.size >2){
             mBinding?.rvBatsmen?.layoutManager = GridLayoutManager(context, 2,GridLayoutManager.HORIZONTAL,false)
         }
         else{
             mBinding?.rvBatsmen?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
         }*/
    }


    private fun setFirstTeamAllRound(allrounders: List<TeamDetailsModel.Data.Team1.Allrounder?>) {
        if (allrounders.isNotEmpty()) {
            val allRounders: ArrayList<PlayersModel> = ArrayList()
            for (i in allrounders) {
                val playerModel = PlayersModel(i?.id.toString(), i?.name.toString(), i?.profile_pic.toString(), i?.type.toString(), i?.captain.toString(), i?.vice_captain.toString(), i?.team.toString())
                allRounders.add(playerModel)
            }

            val playerListAdapter = PlayerListAdapter(requireActivity(), allRounders)
            mBinding?.rvAllRound?.adapter = playerListAdapter
            /*mBinding?.rvAllRound?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
           */
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.justifyContent = JustifyContent.CENTER
            //layoutManager.alignItems = AlignItems.CENTER
            layoutManager.flexDirection = FlexDirection.ROW
            //layoutManager.flexWrap = FlexWrap.WRAP
            mBinding?.rvAllRound?.layoutManager = layoutManager
            mBinding?.rvAllRound?.addItemDecoration(
                MarginItemDecoration(
                    resources.getDimension(R.dimen._10sdp).toInt()
                )
            )
            playerListAdapter.notifyDataSetChanged()
        }
    }

    private fun setFirstTeamBowler(bowlers: List<TeamDetailsModel.Data.Team1.Bowler?>) {
        if (bowlers.isNotEmpty()) {
            val bowler: ArrayList<PlayersModel> = ArrayList()
            for (i in bowlers) {
                val playerModel = PlayersModel(i?.id.toString(), i?.name.toString(), i?.profile_pic.toString(), i?.type.toString(), i?.captain.toString(), i?.vice_captain.toString(), i?.team.toString())
                bowler.add(playerModel)
            }

            val playerListAdapter = PlayerListAdapter(requireActivity(), bowler)
            mBinding?.rvBowler?.adapter = playerListAdapter
            /*mBinding?.rvBowler?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            */
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.justifyContent = JustifyContent.CENTER
            //layoutManager.alignItems = AlignItems.CENTER
            layoutManager.flexDirection = FlexDirection.ROW
            // layoutManager.flexWrap = FlexWrap.WRAP
            mBinding?.rvBowler?.layoutManager = layoutManager
            mBinding?.rvBowler?.addItemDecoration(
                MarginItemDecoration(
                    resources.getDimension(R.dimen._10sdp).toInt()
                )
            )
            playerListAdapter.notifyDataSetChanged()

        }
    }

    private fun setSecondTeamWicketKeeper(wicketKeepers: List<TeamDetailsModel.Data.Team2.WicketKeeper?>) {
        if (wicketKeepers.isNotEmpty()) {
            val wicketKeeper: ArrayList<PlayersModel> = ArrayList()
            for (i in wicketKeepers) {
                val playerModel = PlayersModel(i?.id.toString(), i?.name.toString(), i?.profile_pic.toString(), i?.type.toString(), i?.captain.toString(), i?.vice_captain.toString(), i?.team.toString())
                wicketKeeper.add(playerModel)
            }

            val playerListAdapter = PlayerListAdapter(requireActivity(), wicketKeeper)
            mBinding?.rvTeamSecondWicketKeeper?.adapter = playerListAdapter
            //mBinding?.rvTeamSecondWicketKeeper?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.justifyContent = JustifyContent.CENTER
            //layoutManager.alignItems = AlignItems.CENTER
            layoutManager.flexDirection = FlexDirection.ROW
            //layoutManager.flexWrap = FlexWrap.WRAP
            mBinding?.rvTeamSecondWicketKeeper?.layoutManager = layoutManager
            mBinding?.rvTeamSecondWicketKeeper?.addItemDecoration(
                MarginItemDecoration(
                    resources.getDimension(R.dimen._10sdp).toInt()
                )
            )
            playerListAdapter.notifyDataSetChanged()
        }
    }

    private fun setSecondTeamBatsmen(batsmens: List<TeamDetailsModel.Data.Team2.Batsmen?>?) {
        if (batsmens?.isNotEmpty()!!) {
            val batsmen: ArrayList<PlayersModel> = ArrayList()
            for (i in batsmens) {
                val playerModel = PlayersModel(i?.id.toString(), i?.name.toString(), i?.profile_pic.toString(), i?.type.toString(), i?.captain.toString(), i?.vice_captain.toString(), i?.team.toString())
                batsmen.add(playerModel)
            }

            val playerListAdapter = PlayerListAdapter(requireActivity(), batsmen)
            mBinding?.rvTeamSecondBatsmen?.adapter = playerListAdapter
            //mBinding?.rvTeamSecondBatsmen?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.justifyContent = JustifyContent.CENTER
            //layoutManager.alignItems = AlignItems.CENTER
            layoutManager.flexDirection = FlexDirection.ROW
            //layoutManager.flexWrap = FlexWrap.WRAP
            mBinding?.rvTeamSecondBatsmen?.layoutManager = layoutManager
            mBinding?.rvTeamSecondBatsmen?.addItemDecoration(
                MarginItemDecoration(
                    resources.getDimension(R.dimen._10sdp).toInt()
                )
            )
            playerListAdapter.notifyDataSetChanged()

        }
    }

    private fun setSecondTeamAllRounder(allrounders: List<TeamDetailsModel.Data.Team2.Allrounder?>?) {
        if (allrounders?.isNotEmpty()!!) {
            val allRounders: ArrayList<PlayersModel> = ArrayList()
            for (i in allrounders) {
                val playerModel = PlayersModel(i?.id.toString(), i?.name.toString(), i?.profile_pic.toString(), i?.type.toString(), i?.captain.toString(), i?.vice_captain.toString(), i?.team.toString())
                allRounders.add(playerModel)
            }

            val playerListAdapter = PlayerListAdapter(requireActivity(), allRounders)
            mBinding?.rvTeamSecondAllRound?.adapter = playerListAdapter
            // mBinding?.rvTeamSecondAllRound?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.justifyContent = JustifyContent.CENTER
            //layoutManager.alignItems = AlignItems.CENTER
            layoutManager.flexDirection = FlexDirection.ROW
            //layoutManager.flexWrap = FlexWrap.WRAP
            mBinding?.rvTeamSecondAllRound?.layoutManager = layoutManager
            mBinding?.rvTeamSecondAllRound?.addItemDecoration(
                MarginItemDecoration(
                    resources.getDimension(R.dimen._10sdp).toInt()
                )
            )
            playerListAdapter.notifyDataSetChanged()

        }
    }

    private fun setSecondTeamBowler(bowlers: List<TeamDetailsModel.Data.Team2.Bowler?>?) {
        if (bowlers?.isNotEmpty()!!) {
            val bowler: ArrayList<PlayersModel> = ArrayList()
            for (i in bowlers) {
                val playerModel = PlayersModel(i?.id.toString(), i?.name.toString(), i?.profile_pic.toString(), i?.type.toString(), i?.captain.toString(), i?.vice_captain.toString(), i?.team.toString())
                bowler.add(playerModel)
            }

            val playerListAdapter = PlayerListAdapter(requireActivity(), bowler)
            mBinding?.rvTeamSecondBowler?.adapter = playerListAdapter
            // mBinding?.rvTeamSecondBowler?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.justifyContent = JustifyContent.CENTER
            //layoutManager.alignItems = AlignItems.CENTER
            layoutManager.flexDirection = FlexDirection.ROW
            //layoutManager.flexWrap = FlexWrap.WRAP
            mBinding?.rvTeamSecondBowler?.layoutManager = layoutManager
            mBinding?.rvTeamSecondBowler?.addItemDecoration(
                MarginItemDecoration(
                    resources.getDimension(R.dimen._10sdp).toInt()
                )
            )
            playerListAdapter.notifyDataSetChanged()

        }
    }

    private fun setNoDataLayout(msg: String) {
        if (msg.isNotEmpty()) {
            mBinding?.clNoData?.tvNoDataLayout?.text = msg
            mBinding?.clNoData?.clNoDataParent?.visibility = View.VISIBLE
            mBinding?.clContentHolder?.visibility = View.GONE
        }
    }


    private fun setTeamFirst() {
        val gson = Gson()
        val teamFirst = preferenceHelper.getString("team1")
        val teamFirstObject: Team1 = gson.fromJson(teamFirst, Team1::class.java)
    }


    private fun setTeamSecond() {
        val gson = Gson()
        val teamSecond = preferenceHelper.getString("team2")
        val teamSecondObject: Team2 = gson.fromJson(teamSecond, Team2::class.java)

    }


}