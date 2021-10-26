package com.gevorg89.snake

import android.graphics.Point
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.gevorg89.snake.ui.theme.SnakeTheme

class MainActivity : ComponentActivity() {

    private val snakeViewModel: SnakeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val blockWidth = 10
        val blockHeight = 20
        snakeViewModel.gameData(blockWidth, blockHeight)
        setContent {
            SnakeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val gameState by snakeViewModel.gameState.collectAsState()
                    when (gameState) {
                        SnakeViewModel.GameState.GAME_OVER -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "GAME OVER!!!")
                                Button(onClick = { snakeViewModel.newGame() }) {
                                    Text(text = "New game")
                                }
                            }
                        }
                        SnakeViewModel.GameState.NONE -> {
                            Button(onClick = { snakeViewModel.newGame() }) {
                                Text(text = "New game")
                            }
                        }
                        else -> {
                            val snakePath by snakeViewModel.snakePath.collectAsState()
                            val eat by snakeViewModel.eat.collectAsState()
                            Column() {
                                Row() {
                                    Button(
                                        onClick = { snakeViewModel.moveLeftClick() },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Left")
                                    }
                                    Button(
                                        onClick = { snakeViewModel.moveTopClick() },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Top")
                                    }
                                    Button(
                                        onClick = { snakeViewModel.moveRightClick() },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Right")
                                    }
                                    Button(
                                        onClick = { snakeViewModel.moveDownClick() },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Down")
                                    }

                                }
                                PlayZone(blockWidth, blockHeight, snakePath, eat)

                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun PlayZone(blockWidth: Int, blockHeight: Int, snakePath: List<Box>, eat: Box?) {
        BoxWithConstraints(modifier = Modifier
            .padding(16.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consumeAllChanges()
                    val (x, y) = dragAmount
                    when {
                        x > 0 -> { /* right */
                            snakeViewModel.moveRightClick()
                        }
                        x < 0 -> { /* left */
                            snakeViewModel.moveLeftClick()
                        }
                        y > 0 -> { /* down */
                            snakeViewModel.moveDownClick()
                        }
                        y < 0 -> { /* up */
                            snakeViewModel.moveTopClick()
                        }
                    }
                }
            }) {
            //var drawState by remember { mutableStateOf(-1L) }
            val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
            val heightPx = with(LocalDensity.current) { maxHeight.toPx() }
            val blockSizeHeight = heightPx / blockHeight
            val blockSizeWidth = widthPx / blockWidth
            DrawPlayZone(
                maxWidth,
                maxHeight,
                blockWidth,
                blockHeight,
                widthPx,
                heightPx,
                blockSizeHeight,
                blockSizeWidth
            ) {
                snakePath.onEach { box ->
                    val x = box.position.x
                    val y = box.position.y
                    drawRect(
                        color = box.color!!,
                        topLeft = Offset(
                            (x) * blockSizeWidth,
                            (y) * blockSizeHeight
                        ),
                        size = Size(blockSizeWidth, blockSizeHeight)
                    )
                }
                eat?.let { box ->
                    val x = box.position.x
                    val y = box.position.y
                    drawRect(
                        color = box.color!!,
                        topLeft = Offset(
                            (x) * blockSizeWidth,
                            (y) * blockSizeHeight
                        ),
                        size = Size(blockSizeWidth, blockSizeHeight)
                    )
                }
            }
        }
    }
}

data class Box(
    var position: Point,
    var color: Color? = null,
    var index: Int
)