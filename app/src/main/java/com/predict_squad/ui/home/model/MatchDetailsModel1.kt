package com.predict_squad.ui.home.model

data class MatchDetailsModel1(
    val data: Data?, val force_logout: Int, val message: String, val status: Int
) {
    data class Data(
        val match_details: MatchDetails, val prediction: List<Prediction>
    ) {
        data class MatchDetails(
            val match: String, val match_date: String, val title: String, val tournament: String
        )

        data class Prediction(
            val description: String, val rating: String, val team1_description: String, val team1_name: String, val team2_description: String, val team2_name: String, val title: String
        )
    }
}