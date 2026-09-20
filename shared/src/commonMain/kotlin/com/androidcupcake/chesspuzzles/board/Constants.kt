/*
    TODO: swap the custom encode()/decode() for real FEN and represent moves in UCI instead of a bespoke format.
 */
package com.androidcupcake.chesspuzzles.board

import androidx.compose.ui.unit.IntOffset
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

//fun decodePieces(
//    encodedPieces: String
//): List<Piece> {
//    return emptyList()
//}

// add playerTurn, castlingRights, enPassantTarget, halfmoveClock, fullmoveNumber as arguments
fun decodePieces(
    encodedPieces: String,
    applyFenMetadata: (List<String>) -> Unit
): List<Piece> {
    val pieces = mutableListOf<Piece>()
    val parts = encodedPieces.split(" ")
    if (parts.isEmpty()) throw IllegalArgumentException("Invalid Cannot be Empty")

    //_pieces.clear()
    val ranks = parts[0].split("/")
    for (rankIndex in ranks.indices) {
        val rank = ranks[rankIndex]
        val y = 8 - rankIndex
        var xOffset = 0
        for (char in rank) {
            if (char.isDigit()) {
                xOffset += char.digitToInt()
            } else {
                val x = 'A'.code + xOffset
                // Move to Constants.kt inside decode()
                pieces.add(Piece.decodeFen(
                    char,
                    IntOffset(x, y)
                ))
                xOffset++
            }
        }
    }
    applyFenMetadata(parts)

    return pieces
}