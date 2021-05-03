package com.predict_squad.dagger

import com.predict_squad.MainActivity
import com.predict_squad.common_helper.Application
import com.predict_squad.ui.home.BasketballMatchListFragment
import com.predict_squad.ui.home.CricketMatchListFragment
import com.predict_squad.ui.home.FootballMatchListFragment
import com.predict_squad.ui.home.HomeFragment
import com.predict_squad.ui.home.MatchDetailFragment
import com.predict_squad.ui.home.TeamDetailFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, RetrofitModule::class])
interface AppComponent {

    fun inject(application: Application)

    fun inject(homeFragment: HomeFragment)

    fun inject(matchListFragment: CricketMatchListFragment)

    fun inject(footballMatchListFragment: FootballMatchListFragment)

    fun inject(basketballMatchListFragment: BasketballMatchListFragment)

    fun inject(matchDetailFragment: MatchDetailFragment)

    fun inject(teamDetailFragment: TeamDetailFragment)

    fun inject(mainActivity: MainActivity)

}