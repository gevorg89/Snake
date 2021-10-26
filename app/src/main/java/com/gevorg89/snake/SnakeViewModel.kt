package com.gevorg89.snake

import android.graphics.Point
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class SnakeViewModel : ViewModel() {

    private var blockHeight: Int = 0
    private var blockWidth: Int = 0

    private enum class MoveDirection {
        NONE, LEFT, TOP, RIGHT, DOWN
    }

    enum class GameState {
        NONE, NEW_GAME, GAME_OVER
    }

    private val timeDraw = 300L
    private val drawFrame = timeDraw / 60L
    private var previewTime = System.currentTimeMillis()

    private var moveDirection = MoveDirection.TOP

    private val colors = arrayOf(Color.Black, Color.Cyan, Color.Red, Color.Green)
    private fun randomColor(): Color {
        val random = Random.nextInt(colors.size)
        return colors[random]
    }

    private val _snakePath = MutableStateFlow<List<Box>>(emptyList())
    val snakePath = _snakePath.asStateFlow()

    private val _eat = MutableStateFlow<Box?>(null)
    val eat = _eat.asStateFlow()

    private val _gameState = MutableStateFlow<GameState>(GameState.NONE)
    val gameState = _gameState.asStateFlow()


    private var testPath: MutableList<Box> = mutableListOf(
        Box(Point(4, 10), Color.Black, 0),
        Box(Point(4, 11), Color.Black, 1),
        Box(Point(4, 12), Color.Black, 2),
        Box(Point(4, 13), Color.Black, 3),
        Box(Point(5, 13), Color.Black, 4),
        Box(Point(5, 14), Color.Black, 5),
        Box(Point(5, 15), Color.Black, 6),
        Box(Point(5, 16), Color.Black, 7),
        Box(Point(5, 17), Color.Black, 8),
        Box(Point(5, 18), Color.Black, 9),
        Box(Point(5, 19), Color.Black, 10),
        Box(Point(6, 19), Color.Black, 11),
        Box(Point(7, 19), Color.Black, 12),
    )

    fun newGame() {
        Log.d("runGame", "runGame")
        _snakePath.value = testPath
        _gameState.value = GameState.NEW_GAME
        setMoveDirection(MoveDirection.TOP)
        newItem()
        viewModelScope.launch {
            while (true) {
                if (isActive){
                    delay(drawFrame)
                    val elapsedTime = System.currentTimeMillis() - previewTime
                    if (elapsedTime >= timeDraw) {
                        checkMove()
                        previewTime = System.currentTimeMillis()
                    }
                }
            }
        }
    }

    fun gameData(blockWidth: Int, blockHeight: Int) {
        this.blockWidth = blockWidth
        this.blockHeight = blockHeight
    }

    private fun checkMove() {
        val path = sortedSnakePath()
        val newItems = mutableListOf<Box>()
        var preview: Box? = null
        path.onEachIndexed { index, box ->
            val boxX = box.position.x
            val boxY = box.position.y

            var newPositionX = boxX
            var newPositionY = boxY

            if (preview == null) {
                when (moveDirection) {
                    MoveDirection.LEFT -> newPositionX -= 1
                    MoveDirection.TOP -> newPositionY -= 1
                    MoveDirection.RIGHT -> newPositionX += 1
                    MoveDirection.DOWN -> newPositionY += 1
                    else -> {
                    }
                }
            } else {
                newPositionX = preview!!.position.x
                newPositionY = preview!!.position.y
            }
            if (newPositionX >= blockWidth) {
                newPositionX = 0
            } else if (newPositionX < 0) {
                newPositionX = blockWidth - 1
            }
            if (newPositionY >= blockHeight) {
                newPositionY = 0
            } else if (newPositionY < 0) {
                newPositionY = blockHeight - 1
            }
            preview = box.copy()
            val position = Point(newPositionX, newPositionY)
            if (index == 0 && checkEat(position)) {
                return
            }
            val newBox = Box(position = position, color = Color.Black, index = box.index)
            newItems.add(newBox)
        }
        if (checkCollision()) {
            _gameState.value = GameState.GAME_OVER
        }
        _snakePath.value = newItems
    }

    private fun checkCollision(): Boolean {
        val path = sortedSnakePath().asSequence()
        val keys = path.groupBy { it.position }.keys
        if (path.count() != keys.size) {
            return true
        }
        return false
    }

    private fun checkEat(point: Point): Boolean {
        if (point == _eat.value?.position) {
            val boxEat = _eat.value
            val positionEat = _eat.value?.position
            val newBoxEat = Box(position = positionEat!!, color = boxEat!!.color, index = 0)
            val newItems = mutableListOf<Box>()
            snakePath().onEachIndexed { index, box ->
                val position = box.position
                val newBox = Box(position = position, color = box.color, index = index + 1)
                newItems.add(newBox)
            }
            newItems.add(0, newBoxEat)
            _snakePath.value = newItems
            newItem()
            return true
        }
        return false
    }

    private fun snakePath(): List<Box> {
        return _snakePath.value
    }

    private fun sortedSnakePath(): List<Box> {
        return snakePath().sortedBy { it.index }
    }

    private fun newItem() {
        var point: Point? = null
        while (point == null) {
            val randomPoint = randomPoint()
            if (!pointNotEmpty(randomPoint)) {
                point = randomPoint
            }
        }
        _eat.value = Box(position = point, Color.Red, index = 100)
    }

    private fun randomPoint(): Point {
        val randomX = Random.nextInt(0, blockWidth)
        val randomY = Random.nextInt(0, blockHeight)
        return Point(randomX, randomY)
    }

    private fun pointNotEmpty(point: Point): Boolean {
        return snakePath().any { it.position == point }
    }

    private val horizontal: Boolean
        get() = moveDirection == MoveDirection.LEFT || moveDirection == MoveDirection.RIGHT

    private val vertical: Boolean
        get() = moveDirection == MoveDirection.TOP || moveDirection == MoveDirection.DOWN

    fun moveLeftClick() {
        if (vertical) {
            setMoveDirection(MoveDirection.LEFT)
        }
    }

    fun moveTopClick() {
        if (horizontal) {
            setMoveDirection(MoveDirection.TOP)
        }
    }

    fun moveRightClick() {
        if (vertical) {
            setMoveDirection(MoveDirection.RIGHT)
        }
    }

    fun moveDownClick() {
        if (horizontal) {
            setMoveDirection(MoveDirection.DOWN)
        }
    }

    private fun setMoveDirection(moveDirection: MoveDirection) {
        this.moveDirection = moveDirection
    }


}