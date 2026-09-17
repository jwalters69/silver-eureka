/*
    TODO: swap the custom encode()/decode() for real FEN and represent moves in UCI instead of a bespoke format.
 */
package com.androidcupcake.chesspuzzles.board

import com.androidcupcake.chesspuzzles.pieces.Piece

/**
 * A to H
 */
val BoardXCoordinates = List(8) {
    'A'.code + it
}

/**
 * 8 to 1
 */
//PW01 - 0 is index of x and 1 is index of y
// [P=Pawn, W=White, 01=(A,2)] (65 is ASCII for A and 2 is 2 on the board)
// position.y - BoardYCoordinates.minOrNull()!! -> 2 - 1
// So instead of value 2 on board it reverts to index 1 of y which is the second position.
//[8, 7, 6, 5, 4, 3, 2, 1] min is 1
val BoardYCoordinates = List(8) {
    8 - it
}

const val InitialEncodedPiecesPosition =
    "PW01PB06PW11PB16PW21PB26PW31PB36PW41PB46PW51PB56PW61PB66PW71PB76RW00RW70RB07RB77BW20BW50BB27BB57NW10NW60NB17NB67QW30QB37KW40KB47"

fun decodePieces(
    encodedPieces: String
): List<Piece> {
    val pieces = mutableListOf<Piece>()

    var index = 0
    while (index < encodedPieces.length) {
        val encodedPiece = encodedPieces.substring(index, index + Piece.EncodedPieceLength)

        pieces.add(Piece.decode(encodedPiece))
        index += Piece.EncodedPieceLength
    }

    return pieces
}