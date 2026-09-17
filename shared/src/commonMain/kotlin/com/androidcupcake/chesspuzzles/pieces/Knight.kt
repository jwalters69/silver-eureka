package com.androidcupcake.chesspuzzles.pieces

import androidx.compose.ui.unit.IntOffset
import chesspuzzles.shared.generated.resources.Res
import chesspuzzles.shared.generated.resources.knight_black
import chesspuzzles.shared.generated.resources.knight_white
import org.jetbrains.compose.resources.DrawableResource
import com.androidcupcake.chesspuzzles.pieces.dsl.getPieceMoves

class Knight(
    override val color: Piece.Color,
    override var position: IntOffset,
    override var hasMoved: Boolean = false
): Piece {

    override val type: Char = Type

    override val drawable: DrawableResource =
        if (color.isWhite)
            Res.drawable.knight_white
        else
            Res.drawable.knight_black

    override fun getAvailableMoves(context: Piece.MoveContext): Set<IntOffset> =
        getPieceMoves(context.pieces) {
            getLMoves()
        }

    companion object {
        const val Type = 'N'
    }

}