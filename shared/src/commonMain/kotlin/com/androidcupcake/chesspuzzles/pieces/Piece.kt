/*
    TODO: swap the custom encode()/decode() for real FEN and represent moves in UCI instead of a bespoke format.

 */
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

    val drawable: DrawableResource

    var position: IntOffset

    var hasMoved: Boolean

    //fun getAvailableMoves(pieces: List<Piece>, lastMove: Board.LastMove? = null): Set<IntOffset>

    data class MoveContext(
        val pieces: List<Piece>,
        val lastMove: Board.LastMove? = null,
        val isCheckCalculation: Boolean = false
    )
    fun getAvailableMoves(context: MoveContext): Set<IntOffset>

    fun encode(): String {
        // W, B
        val colorCode = color.name.first()

        return StringBuilder()
            .append(type)
            .append(colorCode)
            .append(position.x - BoardXCoordinates.minOrNull()!!)
            .append(position.y - BoardYCoordinates.minOrNull()!!)
            .toString()
    }

    companion object {
        fun decode(encodedPiece: String): Piece {
            val (type, color, x, y) = encodedPiece.toCharArray()
            val pieceColor =
                Color.entries
                    .find { it.name.first() == color }
                    ?: throw IllegalArgumentException("Invalid piece color!")
            val position =
                IntOffset(
                    x = x.digitToInt() + BoardXCoordinates.minOrNull()!!,
                    y = y.digitToInt() + BoardYCoordinates.minOrNull()!!
                )
            return when (type) {
                Pawn.Type ->
                    Pawn(pieceColor, position)
                King.Type ->
                    King(pieceColor, position)
                Queen.Type ->
                    Queen(pieceColor, position)
                Knight.Type ->
                    Knight(pieceColor, position)
                Rook.Type ->
                    Rook(pieceColor, position)
                Bishop.Type ->
                    Bishop(pieceColor, position)
                else ->
                    throw IllegalArgumentException("Invalid piece type!")
            }
        }
        const val EncodedPieceLength = 4
    }

}


