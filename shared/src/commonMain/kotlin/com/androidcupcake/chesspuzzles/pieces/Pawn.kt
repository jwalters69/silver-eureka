package com.androidcupcake.chesspuzzles.pieces

import androidx.compose.ui.unit.IntOffset
import chesspuzzles.shared.generated.resources.Res
import chesspuzzles.shared.generated.resources.pawn_black
import chesspuzzles.shared.generated.resources.pawn_white
import com.androidcupcake.chesspuzzles.board.Board
import org.jetbrains.compose.resources.DrawableResource
import com.androidcupcake.chesspuzzles.pieces.dsl.DiagonalMovement
import com.androidcupcake.chesspuzzles.pieces.dsl.StraightMovement
import com.androidcupcake.chesspuzzles.pieces.dsl.getPieceMoves
import kotlin.math.abs

//Piece:color: Color,type:Char,drawable:DrawableResource, position: IntOffset,
// getAvailableMoves(pieces: List<Piece>): Set<IntOffset>
class Pawn(
    override val color: Piece.Color,
    override var position: IntOffset,
    override var hasMoved: Boolean = false

): Piece {

    override val type: Char = Type

    override val drawable: DrawableResource =
        if (color.isWhite)
            Res.drawable.pawn_white
        else
            Res.drawable.pawn_black

    override fun getAvailableMoves(context: Piece.MoveContext): Set<IntOffset> {
        val (pieces, lastMove, _, enPassantTarget, _) = context
        val isFirstMove =
            position.y == 2 && color.isWhite ||
            position.y == 7 && color.isBlack

        return getPieceMoves(pieces) {
            straightMoves(
                movement = if (color.isWhite) StraightMovement.Up else StraightMovement.Down,
                maxMovements = if (isFirstMove) 2 else 1,
                canCapture = false,
            )

            diagonalMoves(
                movement = if (color.isWhite) DiagonalMovement.UpRight else DiagonalMovement.DownRight,
                maxMovements = 1,
                captureOnly = true,
            )

            diagonalMoves(
                movement = if (color.isWhite) DiagonalMovement.UpLeft else DiagonalMovement.DownLeft,
                maxMovements = 1,
                captureOnly = true,
            )
            addMove(enPassantTarget ?: enPassantTarget(lastMove))
        }
    }
    private fun enPassantTarget(lastMove: Board.LastMove?): IntOffset? {
        val last = lastMove ?: return null
        val lastPawn = last.piece as? Pawn ?: return null
        if (lastPawn.color == color) return null
        if (abs(last.to.y - last.from.y) != 2) return null      // was a double-step
        if (last.to.y != position.y) return null                // landed beside me
        if (abs(last.to.x - position.x) != 1) return null        // adjacent file
        val dir = if (color.isWhite) 1 else -1
        return IntOffset(last.to.x, position.y + dir)
    }

    companion object {
        const val Type = 'P'
    }

}