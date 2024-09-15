package com.example.WarOfDrawing.characters

// Класс для характеристик персонажей
data class CharacterAttributes(
    val width: Float,       // Ширина персонажа
    val height: Float,      // Высота персонажа
    var health: Int,        // Здоровье персонажа
    val attackPower: Int,   // Сила атаки персонажа
    val accuracy: Float,    // Точность персонажа
    val speed: Float        // Скорость персонажа
)

// Объекты с хардкорными характеристиками для каждого типа персонажа
object CharacterPresets {

    // Характеристики для Gunner
    fun getGunnerAttributes(): CharacterAttributes {
        return CharacterAttributes(
            width = 50f,
            height = 50f,
            health = 100,
            attackPower = 15,
            accuracy = 0.8f,
            speed = 2f
        )
    }

    // Характеристики для Rocketman
    fun getRocketmanAttributes(): CharacterAttributes {
        return CharacterAttributes(
            width = 60f,
            height = 60f,
            health = 120,
            attackPower = 20,
            accuracy = 0.75f,
            speed = 1.8f
        )
    }

    // Вы можете добавить характеристики для других типов персонажей, если нужно
}
