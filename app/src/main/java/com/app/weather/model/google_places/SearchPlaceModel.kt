package com.app.weather.model.google_places

data class SearchPlaceModel(
    val description: String,
    val id: String,
    val matched_substrings: MutableList<MatchedSubstring>,
    val place_id: String,
    val reference: String,
    val structured_formatting: StructuredFormatting,
    val terms: MutableList<Term>,
    val types: MutableList<String>
) {
    data class MatchedSubstring(
        val length: Int,
        val offset: Int
    )

    data class StructuredFormatting(
        val main_text: String,
        val main_text_matched_substrings: MutableList<MainTextMatchedSubstring>,
        val secondary_text: String
    ) {
        data class MainTextMatchedSubstring(
            val length: Int,
            val offset: Int
        )
    }

    data class Term(
        val offset: Int,
        val value: String
    )
}