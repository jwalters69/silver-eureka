package com.androidcupcake.chesspuzzles.pieces

import androidx.compose.ui.unit.IntOffset
import com.androidcupcake.chesspuzzles.board.Board
import com.androidcupcake.chesspuzzles.board.BoardXCoordinates
import com.androidcupcake.chesspuzzles.board.BoardYCoordinates
import com.androidcupcake.chesspuzzles.pieces.Piece.Color
import org.jetbrains.compose.resources.DrawableResource

interface Piece {

    val color: Color

    enum class Color {
        White,
        Black;

        val isWhite: Boolean
            get() = this == White

        val isBlack: Boolean
            get() = this == Black
    }

    val type: Char

    val fenChar: Char
        get() = if (color.isWhite) type.uppercaseChar() else type.lowercaseChar()

    val drawable: DrawableResource

    var position: IntOffset

    var hasMoved: Boolean

    //fun getAvailableMoves(pieces: List<Piece>, lastMove: Board.LastMove? = null): Set<IntOffset>

    data class MoveContext(
        val pieces: List<Piece>,
        val lastMove: Board.LastMove? = null,
        val isCheckCalculation: Boolean = false,
        val enPassantTarget: IntOffset? = null,
        val castlingRights: String = ""
    )
    fun getAvailableMoves(context: MoveContext): Set<IntOffset>

    companion object {
        fun decodeFen(fenChar: Char, position: IntOffset): Piece {
            val color = if (fenChar.isUpperCase()) Color.White else Color.Black
            val type = fenChar.uppercaseChar()
            return when (type) {
                Pawn.Type -> Pawn(color, position)
                King.Type -> King(color, position)
                Queen.Type -> Queen(color, position)
                Knight.Type -> Knight(color, position)
                Rook.Type -> Rook(color, position)
                Bishop.Type -> Bishop(color, position)
                else -> throw IllegalArgumentException("Invalid FEN character: $fenChar")
            }
        }
    }
}


