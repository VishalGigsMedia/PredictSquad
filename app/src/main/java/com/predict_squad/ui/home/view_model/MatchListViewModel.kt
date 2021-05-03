package com.predict_squad.ui.home.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.predict_squad.retrofit.APIService
import com.predict_squad.ui.home.model.MatchDetailsModel
import com.predict_squad.ui.home.model.MatchListModel
import com.predict_squad.ui.home.repository.MatchListRepository
import com.squad_gyan.ui.home.model.TeamDetailsModel

class MatchListViewModel : ViewModel() {

    private val matchListRepository: MatchListRepository = MatchListRepository()
    private var matchListModel: LiveData<MatchListModel>? = null
    private var matchDetailsModel: LiveData<MatchDetailsModel>? = null
    private var teamDetailsModel: LiveData<TeamDetailsModel>? = null



    fun getCricketMatchList(
        context: Context, apiService: APIService, offset: Int, nextLimit: Int, fcmToken: String
    ): LiveData<MatchListModel>? {
        matchListModel = matchListRepository.getCricketMatchList(
            context, apiService, offset, nextLimit, fcmToken
        )
        return matchListModel
    }

    fun getFootballMatchList(
        context: Context, apiService: APIService, offset: Int, nextLimit: Int, fcmToken: String
    ): LiveData<MatchListModel>? {
        matchListModel = matchListRepository.getFootballMatchList(
            context, apiService, offset, nextLimit, fcmToken
        )
        return matchListModel
    }

    fun getBasketballMatchList(
        context: Context, apiService: APIService, offset: Int, nextLimit: Int, fcmToken: String
    ): LiveData<MatchListModel>? {
        matchListModel = matchListRepository.getBasketballMatchList(
            context, apiService, offset, nextLimit, fcmToken
        )
        return matchListModel
    }

    fun getMatchDetails(
        context: Context, apiService: APIService, matchId: String, matchType: String
    ): LiveData<MatchDetailsModel>? {
        matchDetailsModel = matchListRepository.getMatchDetails(context, apiService, matchId, matchType)
        return matchDetailsModel
    }

    fun getTeamDetails(context: Context, apiService: APIService, matchId: String, matchType: String): LiveData<TeamDetailsModel>? {
        teamDetailsModel = matchListRepository.getTeamDetails(context, apiService, matchId, matchType)
        return teamDetailsModel
    }



}