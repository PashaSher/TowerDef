package com.example.WarOfDrawing.characters

import android.graphics.Bitmap

open class Character(
    val id: String,
    val name: String,
    var x: Float,
    var y: Float,
    val bitmap: Bitmap,                // Изображение персонажа
    val attributes: CharacterAttributes // Характеристики персонажа
)

class Gunner(id: String, name: String, x: Float, y: Float, bitmap: Bitmap, attributes: CharacterAttributes) :
    Character(id, name, x, y, bitmap, attributes)

class Rocketman(id: String, name: String, x: Float, y: Float, bitmap: Bitmap, attributes: CharacterAttributes) :
    Character(id, name, x, y, bitmap, attributes)
