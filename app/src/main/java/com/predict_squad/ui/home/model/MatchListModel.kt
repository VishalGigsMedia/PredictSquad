package com.predict_squad.ui.home.model

data class MatchListModel(
    val data: Data?, val force_logout: Int, val message: String?, val status: Int
) {
    data class Data(
        val match_list: List<Match>
    ) {
        data class Match(
            val id: String, val match_date: String, val team1: Team1, val team2: Team2, val title: String, val venue: String, val match_details_available: String, val rating: String
        ) {
            data class Team1(
                val full_name: String, val id: String, val logo: String, val short_name: String
            )

            data class Team2(
                val full_name: String, val id: String, val logo: String, val short_name: String
            )
        }
    }
}