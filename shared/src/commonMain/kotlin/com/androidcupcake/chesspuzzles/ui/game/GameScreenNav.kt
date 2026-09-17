package com.androidcupcake.chesspuzzles.ui.game

import androidx.compose.runtime.Composable
import com.androidcupcake.chesspuzzles.board.InitialEncodedPiecesPosition
import com.androidcupcake.chesspuzzles.board.rememberBoard
import cafe.adriel.voyager.core.screen.Screen

data class GameScreenNav(
    val encodedPieces: String = InitialEncodedPiecesPosition
): Screen {

    @Composable
    override fun Content() {
        //Initialize the board with the given encoded pieces
        val board = rememberBoard(encodedPieces = encodedPieces)

        GameScreen(
            board = board
        )
    }

}