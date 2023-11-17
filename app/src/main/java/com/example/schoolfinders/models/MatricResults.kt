package com.example.schoolfinders.models

data class MatricResults(
    val idImageUri: String = "",
    val matricImageUri: String = ""
) {
    constructor() : this("", "")
}
