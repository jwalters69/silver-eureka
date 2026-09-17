package com.androidcupcake.chesspuzzles.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntOffset
import com.androidcupcake.chesspuzzles.pieces.King
import com.androidcupcake.chesspuzzles.pieces.Pawn
import com.russhwolf.settings.set
import com.androidcupcake.chesspuzzles.settings.boardSettings
import com.androidcupcake.chesspuzzles.pieces.Piece
import com.androidcupcake.chesspuzzles.pieces.Rook
import kotlin.math.abs

@Composable
fun rememberBoard(
    encodedPieces: String = InitialEncodedPiecesPosition,
): Board =
    remember {
        Board(encodedPieces = encodedPieces)
    }

@Immutable
class Board(
    encodedPieces: String = InitialEncodedPiecesPosition,
) {
    private val _pieces = mutableStateListOf<Piece>()
    val pieces get() = _pieces.toList()

    var squaresAttackedByColor by mutableStateOf<Map<Piece.Color, Set<IntOffset>>>(emptyMap())
        private set

    var selectedPiece by mutableStateOf<Piece?>(null)
        private set

    var selectedPieceMoves by mutableStateOf(emptySet<IntOffset>())
        private set

    var moveIncrement by mutableIntStateOf(0)
        private set

    data class LastMove(val piece: Piece, val from: IntOffset, val to: IntOffset)
    var lastMove by mutableStateOf<LastMove?>(null)
        private set

    var playerTurn by mutableStateOf(Piece.Color.White)

    var winner by mutableStateOf<Piece.Color?>(null)
        private set
    var isDraw by mutableStateOf(false)
        private set

    init {
        _pieces.addAll(
            decodePieces(encodedPieces = encodedPieces)
        )
        updateAttackedSquares()
    }
    /**
     * User events
     */

    fun selectPiece(piece: Piece) {
        if (piece.color != playerTurn)
            return

        if (piece == selectedPiece) {
            clearSelection()
        } else {
            selectedPiece = piece
            selectedPieceMoves = piece.getAvailableMoves(
                context = Piece.MoveContext(pieces = pieces, lastMove = lastMove)
            )
            .filterNot {
                isTheKingInThreat(pieces = pieces, piece = piece, x = it.x, y = it.y, lastMove = lastMove)
            }
            .toSet()
        }
    }

    fun moveSelectedPiece(x: Int, y: Int) {
        selectedPiece?.let { piece ->
            if (!isAvailableMove(x = x, y = y))
                return

            if (piece.color != playerTurn)
                return

            val from = piece.position
            val to = IntOffset(x, y)

            movePiece(
                piece = piece,
                position = to
            )

            clearSelection()
            switchPlayerTurn()
            lastMove = LastMove(piece, from, to)
            updateAttackedSquares()
            val status = evaluateGameStatus(
                pieces = pieces,
                playerTurn = playerTurn,
                lastMove = lastMove,
                squaresAttackedByColor = squaresAttackedByColor
            )
            when {
                status.isCheckmate -> {
                    winner = if(playerTurn.isWhite) Piece.Color.Black else Piece.Color.White
                    println("Game over $winner wins!")
                }
                status.isStalemate -> { 
                    isDraw = true
                    println("Game over draw!")
                }
            }

            moveIncrement++
        }
    }

    /**
     * Public Methods
     */

    fun getPiece(x: Int, y: Int): Piece? =
        _pieces.find { it.position.x == x && it.position.y == y }

    fun isAvailableMove(x: Int, y: Int): Boolean =
        selectedPieceMoves.any { it.x == x && it.y == y }

    fun save() {
        val encodedBoard = encode()
        val now = kotlin.time.Clock.System.now()
        val millis = now.toEpochMilliseconds()

        boardSettings[BoardKeyPrefix + millis] = encodedBoard
    }

    /**
     * Private Methods
     */

    private fun movePiece(
        piece: Piece,
        position: IntOffset
    ) {
        val from = piece.position
        val targetPiece = pieces.find { it.position == position }



        if (targetPiece != null) {
            removePiece(targetPiece)
        } else if (piece is Pawn && position.x != from.x) {
            // diagonal move onto an empty square = en passant
            pieces.find { it.position == IntOffset(position.x, from.y) }
                ?.let { removePiece(it) }
        }

        if (piece is King && abs(position.x - from.x) == 2) {

            val kingSide = position.x > from.x
            val rookFromX = if (kingSide) BoardXCoordinates[7] else BoardXCoordinates[0]
            val rookToX = if (kingSide) position.x - 1 else position.x + 1
            pieces.find { it is Rook && it.position == IntOffset(rookFromX, from.y) }
                ?.let { it.position = IntOffset(rookToX, from.y) }
        }

        piece.position = position
        piece.hasMoved = true
    }

    private fun removePiece(piece: Piece) {
        _pieces.remove(piece)
    }


    private fun clearSelection() {
        selectedPiece = null
        selectedPieceMoves = emptySet()
    }

    private fun switchPlayerTurn() {
        playerTurn =
            if (playerTurn.isWhite)
                Piece.Color.Black
            else
                Piece.Color.White
    }

    private fun updateAttackedSquares() {
        squaresAttackedByColor = Piece.Color.entries.associateWith { color ->
            pieces.filter { it.color == color }
                .flatMap {
                    it.getAvailableMoves(
                        Piece.MoveContext(
                            pieces = pieces,
                            lastMove = lastMove,
                            isCheckCalculation = true
                        )
                    )
                }
                .toSet()
        }
        // TODO: DELETE
        //println("squaresAttackedByColor: $squaresAttackedByColor")
    }

    private fun encode(): String {
        return pieces.joinToString(separator = "") { piece -> piece.encode() }
    }

    companion object {
        const val BoardKeyPrefix = "board_"
    }
}

@Composable
fun Board.rememberPieceAt(x: Int, y: Int): Piece? =
    remember(x, y, moveIncrement) {
        getPiece(
            x = x,
            y = y,
        )
    }

@Composable
fun Board.rememberIsAvailableMove(x: Int, y: Int): Boolean =
    remember(x, y, selectedPieceMoves) {
        isAvailableMove(
            x = x,
            y = y,
        )
    }
