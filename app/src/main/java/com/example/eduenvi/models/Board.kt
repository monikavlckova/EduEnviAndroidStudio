package com.example.eduenvi.models

class Board(
    var id: Int,
    var rows: Int,
    var columns: Int,
    var index: Int,
    var taskId: Int,
    var startIndex: Int?,
    var wallImageId: Int?,
    var freeImageId: Int?,
    var startImageId: Int?,
    var itemImageId: Int?,
    var tiles: MutableList<Tile>? //toto neposielam
)