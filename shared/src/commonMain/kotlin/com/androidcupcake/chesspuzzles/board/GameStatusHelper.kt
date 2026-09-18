package com.androidcupcake.chesspuzzles.board

import androidx.compose.ui.unit.IntOffset
import com.androidcupcake.chesspuzzles.pieces.King
import com.androidcupcake.chesspuzzles.pieces.Piece
import kotlin.collections.emptySet


data class GameStatus(val isInCheck: Boolean, val hasLegalMove: Boolean) {
    val isCheckmate get() = isInCheck && !hasLegalMove
    val isStalemate get() = !isInCheck && !hasLegalMove
}
fun evaluateGameStatus(
    pieces: List<Piece>,
    playerTurn: Piece.Color,
    lastMove: Board.LastMove? = null,
    squaresAttackedByColor: Map<Piece.Color, Set<IntOffset>> = emptyMap(),
    enPassantTarget: IntOffset? = null,
    castlingRights: String = ""
): GameStatus {
    val inCheck = isKingInCheck(pieces, playerTurn, lastMove, squaresAttackedByColor)
    val legalMove = hasAnyLegalMove(pieces, playerTurn, lastMove, enPassantTarget, castlingRights)
    return GameStatus(inCheck, legalMove)
}

fun isTheKingInThreat(
    pieces: List<Piece>,
    piece: Piece,
    x: Int,
    y: Int,
    lastMove: Board.LastMove? = null,
    enPassantTarget: IntOffset? = null,
    castlingRights: String = ""
): Boolean {
    val piecePosition = piece.position
    val targetPosition = IntOffset(x = x, y = y)

    // Move the piece to the new position temporarily to check if the king is in threat
    piece.position = targetPosition

    // Simulated pieces: include the moved piece itself, but exclude any enemy piece captured at the target position
    val simulatedPieces = pieces.filter { it === piece || it.position != targetPosition }

    val king = simulatedPieces.firstOrNull { it is King && it.color == piece.color }
    val isThreatened = if (king == null) false else {
        simulatedPieces.filter { it.color != piece.color }
            .any { enemy ->
                enemy.getAvailableMoves(Piece.MoveContext(
                    pieces = simulatedPieces,
                    lastMove = lastMove,
                    isCheckCalculation = true,
                    enPassantTarget = enPassantTarget,
                    castlingRights = castlingRights
                ))
                    .any { it == king.position }
            }
    }
    // Move the piece back to its original position
    piece.position = piecePosition

    return isThreatened
}

private fun isKingInCheck(
    pieces: List<Piece>,
    playerTurn: Piece.Color,
    lastMove: Board.LastMove?,
    squaresAttackedByColor: Map<Piece.Color, Set<IntOffset>> = emptyMap(),
): Boolean {
    val king = pieces.firstOrNull { it is King && it.color == playerTurn } ?: return false
    val enemyColor = if (playerTurn.isWhite) Piece.Color.Black else Piece.Color.White
    return king.position in (squaresAttackedByColor[enemyColor] ?: emptySet())
}

private fun hasAnyLegalMove(
    pieces: List<Piece>,
    playerTurn: Piece.Color,
    lastMove: Board.LastMove?,
    enPassantTarget: IntOffset? = null,
    castlingRights: String = ""
): Boolean {
    val friendlyPieces = pieces.filter { it.color == playerTurn }
    return friendlyPieces.any { friendlyPiece ->
        friendlyPiece.getAvailableMoves(Piece.MoveContext(
            pieces = pieces,
            lastMove = lastMove,
            enPassantTarget = enPassantTarget,
            castlingRights = castlingRights
        ))
        .any { move -> !isTheKingInThreat(
            pieces = pieces,
            piece = friendlyPiece,
            x = move.x,
            y = move.y,
            lastMove = lastMove,
            enPassantTarget = enPassantTarget,
            castlingRights = castlingRights
        ) }
    }
}

fun isCheckmate(
    pieces: List<Piece>,
    playerTurn: Piece.Color,
    lastMove: Board.LastMove? = null,
    enPassantTarget: IntOffset? = null,
    castlingRights: String = ""
): Boolean =
    isKingInCheck(pieces, playerTurn, lastMove) && !hasAnyLegalMove(pieces, playerTurn, lastMove, enPassantTarget, castlingRights)

fun isStalemate(
    pieces: List<Piece>,
    playerTurn: Piece.Color,
    lastMove: Board.LastMove? = null,
    enPassantTarget: IntOffset? = null,
    castlingRights: String = ""
): Boolean =
    !isKingInCheck(pieces, playerTurn, lastMove) && !hasAnyLegalMove(pieces, playerTurn, lastMove, enPassantTarget, castlingRights)
