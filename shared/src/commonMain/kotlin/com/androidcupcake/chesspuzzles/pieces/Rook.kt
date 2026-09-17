package com.androidcupcake.chesspuzzles.pieces

import androidx.compose.ui.unit.IntOffset
import chesspuzzles.shared.generated.resources.Res
import chesspuzzles.shared.generated.resources.rook_black
import chesspuzzles.shared.generated.resources.rook_white
import org.jetbrains.compose.resources.DrawableResource
import com.androidcupcake.chesspuzzles.pieces.dsl.getPieceMoves

class Rook(
    override val color: Piece.Color,
    override var position: IntOffset,
    override var hasMoved: Boolean = false
): Piece {

    override val type: Char = Type

    override val drawable: DrawableResource =
        if (color.isWhite)
            Res.drawable.rook_white
        else
            Res.drawable.rook_black

    override fun getAvailableMoves(context: Piece.MoveContext): Set<IntOffset> =
        getPieceMoves(context.pieces) {
            straightMoves()
        }

    companion object {
        const val Type = 'R'
    }

}