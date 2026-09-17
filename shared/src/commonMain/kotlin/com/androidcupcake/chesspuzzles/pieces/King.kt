package com.androidcupcake.chesspuzzles.pieces

import androidx.compose.ui.unit.IntOffset
import chesspuzzles.shared.generated.resources.Res
import chesspuzzles.shared.generated.resources.king_black
import chesspuzzles.shared.generated.resources.king_white
import com.androidcupcake.chesspuzzles.board.BoardXCoordinates
import com.androidcupcake.chesspuzzles.board.isTheKingInThreat
import org.jetbrains.compose.resources.DrawableResource
import com.androidcupcake.chesspuzzles.pieces.dsl.getPieceMoves

class King(
    override val color: Piece.Color,
    override var position: IntOffset,
    override var hasMoved: Boolean = false
): Piece {

    override val type: Char = Type

    override val drawable: DrawableResource =
        if (color.isWhite)
            Res.drawable.king_white
        else
            Res.drawable.king_black

    override fun getAvailableMoves(context: Piece.MoveContext): Set<IntOffset> {
        val baseMoves = getPieceMoves(context.pieces) {
            straightMoves(
                maxMovements = 1,
            )
            diagonalMoves(
                maxMovements = 1,
            )
        }
        val castling = buildSet {
            if (!context.isCheckCalculation) {
                if (canCastle(context.pieces, rookX = BoardXCoordinates[7])) add(IntOffset(position.x + 2, position.y))
                if (canCastle(context.pieces, rookX = BoardXCoordinates[0])) add(IntOffset(position.x - 2, position.y))
            }
        }
        return baseMoves + castling
    }

    private fun canCastle(pieces: List<Piece>, rookX: Int): Boolean {
        if (hasMoved) return false
        val rook = pieces.find { it is Rook && it.color == color && it.position == IntOffset(rookX, position.y) }
            ?: return false
        if (rook.hasMoved) return false

        val step = if (rookX > position.x) 1 else -1
        val start = if (step == 1) position.x + 1 else rookX + 1
        val end = if (step == 1) rookX else position.x
        val between = start until end

        if (between.any { x -> pieces.any { it.position == IntOffset(x, position.y) } })
            return false

        val path = listOf(0, step, step * 2).map {
            IntOffset(position.x + it, position.y)
        }
        return path.none { isTheKingInThreat(pieces, this, it.x, it.y) }
    }

    companion object {
        const val Type = 'K'
    }

}