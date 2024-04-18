package com.example.eduenvi.models

class Board(
    var id: Int,
    var rows: Int,
    var columns: Int,
    var taskId: Int,
    var wallImageId: Int,
    var freeImageId: Int,
    var startImageId: Int,
    var itemImageId: Int,
    var tiles: MutableList<Tile>? //TODO nastav. nech toto neberie do api, zatial nastavovat na null pri posielani, nullove hodnoty neserializuje
)