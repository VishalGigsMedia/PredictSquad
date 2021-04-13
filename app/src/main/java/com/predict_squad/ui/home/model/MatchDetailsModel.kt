package com.predict_squad.ui.home.model

data class MatchDetailsModel(
    val `data`: Data? = null,
    val force_logout: Int? = null,
    val message: Any? = null,
    val status: Int? = null
) {
    data class Data(
        val fantasy_teams: FantasyTeams? = null,
        val match_details: MatchDetails? = null,
        val prediction: List<Prediction?>? = null
    ) {
        data class FantasyTeams(
            val team1: Team1? = null,
            val team2: Team2? = null
        ) {
            data class Team1(
                val allrounders: List<Allrounder?>? = null,
                val batsmens: List<Batsmen?>? = null,
                val bowlers: List<Bowler?>? = null
            ) {
                data class Allrounder(
                    val id: String? = null,
                    val name: String? = null,
                    val profile_pic: Any? = null,
                    val type: String? = null
                )

                data class Batsmen(
                    val captain: String? = null,
                    val id: String? = null,
                    val name: String? = null,
                    val profile_pic: Any? = null,
                    val type: String? = null
                )

                data class Bowler(
                    val id: String? = null,
                    val name: String? = null,
                    val profile_pic: Any? = null,
                    val type: String? = null
                )
            }

            data class Team2(
                val allrounders: List<Allrounder?>? = null,
                val batsmens: List<Batsmen?>? = null,
                val wicket_keepers: List<WicketKeeper?>? = null
            ) {
                data class Allrounder(
                    val id: String? = null,
                    val name: String? = null,
                    val profile_pic: Any? = null,
                    val type: String? = null
                )

                data class Batsmen(
                    val id: String? = null,
                    val name: String? = null,
                    val profile_pic: Any? = null,
                    val type: String? = null
                )

                data class WicketKeeper(
                    val id: String? = null,
                    val name: String? = null,
                    val profile_pic: Any? = null,
                    val type: String? = null
                )
            }
        }

        data class MatchDetails(
            val match: String? = null,
            val match_date: String? = null,
            val title: String? = null,
            val tournament: String? = null
        )

        data class Prediction(
            val description: String? = null,
            val fantasy_game_links: List<FantasyGameLink?>? = null,
            val team1_description: String? = null,
            val team1_name: String? = null,
            val team2_description: String? = null,
            val team2_name: String? = null,
            val title: String? = null,
            val rating: String? = null
        ) {
            data class FantasyGameLink(
                val description: String? = null,
                val link: String? = null,
                val title: String? = null
            )
        }
    }
}