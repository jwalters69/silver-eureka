package com.androidcupcake.chesspuzzles.pieces

import androidx.compose.ui.unit.IntOffset
import chesspuzzles.shared.generated.resources.Res
import chesspuzzles.shared.generated.resources.queen_black
import chesspuzzles.shared.generated.resources.queen_white
import org.jetbrains.compose.resources.DrawableResource
import com.androidcupcake.chesspuzzles.pieces.dsl.getPieceMoves

class Queen(
    override val color: Piece.Color,
    override var position: IntOffset,
    override var hasMoved: Boolean = false
): Piece {

    override val type: Char = Type

    override val drawable: DrawableResource =
        if (color.isWhite)
            Res.drawable.queen_white
        else
            Res.drawable.queen_black

    override fun getAvailableMoves(context: Piece.MoveContext): Set<IntOffset> =
        getPieceMoves(context.pieces) {
            straightMoves()
            diagonalMoves()
        }

    companion object {
        const val Type = 'Q'
    }

}