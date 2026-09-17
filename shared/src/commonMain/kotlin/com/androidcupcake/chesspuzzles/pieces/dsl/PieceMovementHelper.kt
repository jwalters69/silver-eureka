package com.androidcupcake.chesspuzzles.pieces.dsl

import androidx.compose.ui.unit.IntOffset
import com.androidcupcake.chesspuzzles.board.BoardXCoordinates
import com.androidcupcake.chesspuzzles.board.BoardYCoordinates
import com.androidcupcake.chesspuzzles.pieces.Piece

fun Piece.getMoves(
    pieces: List<Piece>,
    getPosition: (Int) -> IntOffset,
    maxMovements: Int,
    canCapture: Boolean,
    captureOnly: Boolean,
): Set<IntOffset> {
    val moves = mutableSetOf<IntOffset>()

    for (i in 1..maxMovements) {
        val targetPosition = getPosition(i)

        if (targetPosition.x !in BoardXCoordinates || targetPosition.y !in BoardYCoordinates)
            break

        val targetPiece = pieces.find { it.position == targetPosition }

        if (targetPiece != null) {
            if (targetPiece.color != this.color && canCapture)
                moves.add(targetPosition)

            break
        } else if (captureOnly) {
            break
        } else {
            moves.add(targetPosition)
        }
    }

    return moves
}
private val KnightOffsets = listOf(
    IntOffset(-1, -2), IntOffset(1, -2),
    IntOffset(-2, -1), IntOffset(2, -1),
    IntOffset(-2, 1), IntOffset(2, 1),
    IntOffset(-1, 2), IntOffset(1, 2),
)

fun Piece.getLMoves(
    pieces: List<Piece>,
): MutableSet<IntOffset> {
    val moves = mutableSetOf<IntOffset>()

    for (offset in KnightOffsets) {
        val targetPosition = position + offset

        if (targetPosition.x !in BoardXCoordinates || targetPosition.y !in BoardYCoordinates)
            continue

        val targetPiece = pieces.find { it.position == targetPosition }
        if (targetPiece == null || targetPiece.color != this.color)
            moves.add(targetPosition)
    }

    return moves
}