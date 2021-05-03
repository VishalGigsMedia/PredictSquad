package com.squad_gyan.ui.home.model

data class TeamDetailsModel(
    val `data`: Data? = null, val force_logout: Int? = null, val message: Any? = null, val status: Int? = null
) {
    data class Data(
        val team1: Team1? = null, val team2: Team2? = null
    ) {
        data class Team1(
            val allrounders: List<Allrounder?>? = null, val batsmens: List<Batsmen?>? = null, val bowlers: List<Bowler?>? = null, val wicket_keepers: List<WicketKeeper?>? = null
        ) {
            data class Allrounder(
                val id: String? = null, val name: String? = null, val profile_pic: String? = null, val type: String? = null, val vice_captain: String? = null, val captain: String? = null, val team: String? = null
            )

            data class Batsmen(
                val id: String? = null, val name: String? = null, val profile_pic: String? = null, val type: String? = null, val vice_captain: String? = null, val captain: String? = null, val team: String? = null
            )

            data class Bowler(
                val id: String? = null, val name: String? = null, val profile_pic: String? = null, val type: String? = null, val vice_captain: String? = null, val captain: String? = null, val team: String? = null
            )

            data class WicketKeeper(
                val id: String? = null, val name: String? = null, val profile_pic: String? = null, val type: String? = null, val vice_captain: String? = null, val captain: String? = null, val team: String? = null
            )
        }

        data class Team2(
            val allrounders: List<Allrounder?>? = null, val batsmens: List<Batsmen?>? = null, val bowlers: List<Bowler?>? = null, val wicket_keepers: List<WicketKeeper?>? = null
        ) {
            data class Allrounder(
                val id: String? = null, val name: String? = null, val profile_pic: String? = null, val type: String? = null, val vice_captain: String? = null, val captain: String? = null, val team: String? = null
            )

            data class Batsmen(
                val id: String? = null, val name: String? = null, val profile_pic: String? = null, val type: String? = null, val vice_captain: String? = null, val captain: String? = null, val team: String? = null
            )

            data class Bowler(
                val id: String? = null, val name: String? = null, val profile_pic: String? = null, val type: String? = null, val vice_captain: String? = null, val captain: String? = null, val team: String? = null
            )

            data class WicketKeeper(
                val id: String? = null, val name: String? = null, val profile_pic: String? = null, val type: String? = null, val vice_captain: String? = null, val captain: String? = null, val team: String? = null
            )
        }
    }
}