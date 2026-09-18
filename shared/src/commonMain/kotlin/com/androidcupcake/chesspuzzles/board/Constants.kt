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
val BoardYCoordinates = List(8) {
    8 - it
}

const val InitialEncodedPiecesPosition =
    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

fun decodePieces(
    encodedPieces: String
): List<Piece> {
    return emptyList()
}
