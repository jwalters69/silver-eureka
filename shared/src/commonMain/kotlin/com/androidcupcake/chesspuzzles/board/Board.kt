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
        private set

    var castlingRights by mutableStateOf("KQkq")
        private set

    var enPassantTarget by mutableStateOf<IntOffset?>(null)
        private set

    var halfmoveClock by mutableIntStateOf(0)
        private set

    var fullmoveNumber by mutableIntStateOf(1)
        private set

    var winner by mutableStateOf<Piece.Color?>(null)
        private set
    var isDraw by mutableStateOf(false)
        private set

    init {
        //TODO: If fromFen is moved to Constants.kt
        //Then: Constants.decode(encodedPieces)
        //Then: updateAttackedSquares (or maybe just leave in decode?)
        fromFEN(encodedPieces)
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
                context = Piece.MoveContext(
                    pieces = pieces,
                    lastMove = lastMove,
                    enPassantTarget = enPassantTarget,
                    castlingRights = castlingRights
                )
            )
            .filterNot {
                isTheKingInThreat(
                    pieces = pieces,
                    piece = piece,
                    x = it.x,
                    y = it.y,
                    lastMove = lastMove,
                    enPassantTarget = enPassantTarget,
                    castlingRights = castlingRights
                )
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
            lastMove = LastMove(piece, from, to)
            updateAttackedSquares()
            val status = evaluateGameStatus(
                pieces = pieces,
                playerTurn = playerTurn,
                lastMove = lastMove,
                squaresAttackedByColor = squaresAttackedByColor,
                enPassantTarget = enPassantTarget,
                castlingRights = castlingRights
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

    fun toFEN(): String {
        val fen = StringBuilder()
        // 1. Piece placement
        for (rankIndex in 0 until 8) {
            val y = 8 - rankIndex
            var emptySquares = 0
            for (fileIndex in 0 until 8) {
                val x = 'A'.code + fileIndex
                val piece = getPiece(x, y)
                if (piece == null) {
                    emptySquares++
                } else {
                    if (emptySquares > 0) {
                        fen.append(emptySquares)
                        emptySquares = 0
                    }
                    fen.append(piece.fenChar)
                }
            }
            if (emptySquares > 0) {
                fen.append(emptySquares)
            }
            if (rankIndex < 7) {
                fen.append("/")
            }
        }

        // 2. Active color
        fen.append(if (playerTurn.isWhite) " w " else " b ")

        // 3. Castling rights
        fen.append(if (castlingRights.isEmpty()) "-" else castlingRights)

        // 4. En passant target
        fen.append(" ")
        fen.append(enPassantTarget?.let { toUCI(it) } ?: "-")

        // 5. Halfmove clock
        fen.append(" $halfmoveClock")

        // 6. Fullmove number
        fen.append(" $fullmoveNumber")

        return fen.toString()
    }

    // TODO: Move to constants.kt this replaced decode()
    fun fromFEN(fen: String) {
        val parts = fen.split(" ")
        if (parts.isEmpty()) return

        _pieces.clear()
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
                    _pieces.add(Piece.fromFenChar(char, IntOffset(x, y)))
                    xOffset++
                }
            }
        }

        if (parts.size > 1) {
            playerTurn = if (parts[1] == "w") Piece.Color.White else Piece.Color.Black
        }
        if (parts.size > 2) {
            castlingRights = parts[2]
        }
        if (parts.size > 3) {
            enPassantTarget = if (parts[3] == "-") null else fromUCI(parts[3])
        }
        if (parts.size > 4) {
            halfmoveClock = parts[4].toIntOrNull() ?: 0
        }
        if (parts.size > 5) {
            fullmoveNumber = parts[5].toIntOrNull() ?: 1
        }

        updateAttackedSquares()
    }

    fun toUCI(offset: IntOffset): String {
        val file = ('a'.code + (offset.x - 'A'.code)).toChar()
        val rank = offset.y.toString()
        return "$file$rank"
    }

    fun fromUCI(square: String): IntOffset {
        val file = square[0] - 'a'
        val rank = square.substring(rankIndex(square)).toInt()
        return IntOffset('A'.code + file, rank)
    }

    private fun rankIndex(square: String): Int = if (square.length > 1 && square[1].isDigit()) 1 else 0

    fun save() {
        val encodedBoard = toFEN()
        val now = kotlin.time.Clock.System.now()
        val millis = now.toEpochMilliseconds()

        boardSettings[BoardKeyPrefix + millis] = encodedBoard
    }

    /**
     * Private Methods
     */

    fun moveUCI(uci: String) {
        if (uci.length < 4) return
        val from = fromUCI(uci.substring(0, 2))
        val to = fromUCI(uci.substring(2, 4))
        val piece = getPiece(from.x, from.y) ?: return

        movePiece(piece, to)
    }

    private fun movePiece(
        piece: Piece,
        position: IntOffset
    ) {
        val from = piece.position
        val targetPiece = getPiece(position.x, position.y)

        // FEN updates: Halfmove clock
        if (piece is Pawn || targetPiece != null) {
            halfmoveClock = 0
        } else {
            halfmoveClock++
        }

        // FEN updates: Fullmove number
        if (playerTurn.isBlack) {
            fullmoveNumber++
        }

        // FEN updates: En passant target
        enPassantTarget = if (piece is Pawn && abs(position.y - from.y) == 2) {
            IntOffset(from.x, if (playerTurn.isWhite) from.y + 1 else from.y - 1)
        } else {
            null
        }

        // Handle capture
        if (targetPiece != null) {
            removePiece(targetPiece)
            updateCastlingRightsOnCapture(position)
        } else if (piece is Pawn && position.x != from.x) {
            // diagonal move onto an empty square = en passant
            val capturedPawnY = from.y
            getPiece(position.x, capturedPawnY)?.let { removePiece(it) }
        }

        // Handle Castling move
        if (piece is King && abs(position.x - from.x) == 2) {
            val kingSide = position.x > from.x
            val rookFromX = if (kingSide) BoardXCoordinates[7] else BoardXCoordinates[0]
            val rookToX = if (kingSide) position.x - 1 else position.x + 1
            getPiece(rookFromX, from.y)?.let { it.position = IntOffset(rookToX, from.y) }
        }

        // Update castling rights on King/Rook move
        updateCastlingRightsOnMove(piece, from)

        piece.position = position
        piece.hasMoved = true

        // Switch turn
        playerTurn = if (playerTurn.isWhite) Piece.Color.Black else Piece.Color.White
    }

    private fun updateCastlingRightsOnMove(piece: Piece, from: IntOffset) {
        when (piece) {
            is King -> {
                if (piece.color.isWhite) {
                    castlingRights = castlingRights.replace("K", "").replace("Q", "")
                } else {
                    castlingRights = castlingRights.replace("k", "").replace("q", "")
                }
            }
            is Rook -> {
                val file = from.x
                if (piece.color.isWhite) {
                    if (file == BoardXCoordinates[7]) castlingRights = castlingRights.replace("K", "")
                    if (file == BoardXCoordinates[0]) castlingRights = castlingRights.replace("Q", "")
                } else {
                    if (file == BoardXCoordinates[7]) castlingRights = castlingRights.replace("k", "")
                    if (file == BoardXCoordinates[0]) castlingRights = castlingRights.replace("q", "")
                }
            }
        }
        if (castlingRights.isEmpty()) castlingRights = "-"
    }

    private fun updateCastlingRightsOnCapture(position: IntOffset) {
        val x = position.x
        val y = position.y
        when(y){
            1 -> {
                if (x == BoardXCoordinates[7]) castlingRights = castlingRights.replace("K", "")
                if (x == BoardXCoordinates[0]) castlingRights = castlingRights.replace("Q", "")
            }
            8 -> {
                if (x == BoardXCoordinates[7]) castlingRights = castlingRights.replace("k", "")
                if (x == BoardXCoordinates[0]) castlingRights = castlingRights.replace("q", "")
            }
        }
        if (castlingRights.isEmpty()) castlingRights = "-"
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
                            isCheckCalculation = true,
                            enPassantTarget = enPassantTarget,
                            castlingRights = castlingRights
                        )
                    )
                }
                .toSet()
        }
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
