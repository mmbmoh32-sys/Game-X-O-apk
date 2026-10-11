package com.example.engine

import com.example.model.AiDifficulty
import com.example.model.PlayerMark
import com.example.model.WinningLine
import com.example.model.WinningLineType
import kotlin.random.Random

object TicTacToeEngine {

    private val WIN_PATTERNS = listOf(
        Triple(0, 1, 2) to WinningLineType.ROW_0,
        Triple(3, 4, 5) to WinningLineType.ROW_1,
        Triple(6, 7, 8) to WinningLineType.ROW_2,
        Triple(0, 3, 6) to WinningLineType.COL_0,
        Triple(1, 4, 7) to WinningLineType.COL_1,
        Triple(2, 5, 8) to WinningLineType.COL_2,
        Triple(0, 4, 8) to WinningLineType.DIAG_MAIN,
        Triple(2, 4, 6) to WinningLineType.DIAG_ANTI
    )

    fun checkWinningLine(board: List<PlayerMark>): WinningLine? {
        for ((triple, type) in WIN_PATTERNS) {
            val (a, b, c) = triple
            val mark = board[a]
            if (mark != PlayerMark.NONE && mark == board[b] && mark == board[c]) {
                return WinningLine(a, b, c, type)
            }
        }
        return null
    }

    fun isBoardFull(board: List<PlayerMark>): Boolean {
        return board.none { it == PlayerMark.NONE }
    }

    fun getAvailableMoves(board: List<PlayerMark>): List<Int> {
        return board.indices.filter { board[it] == PlayerMark.NONE }
    }

    /**
     * Calculates the best move for AI based on difficulty.
     */
    fun computeAiMove(board: List<PlayerMark>, aiMark: PlayerMark, difficulty: AiDifficulty): Int {
        val available = getAvailableMoves(board)
        if (available.isEmpty()) return -1
        if (available.size == 9) {
            // First move: center or corner
            val goodFirstMoves = listOf(4, 0, 2, 6, 8)
            return goodFirstMoves.random()
        }

        return when (difficulty) {
            AiDifficulty.EASY -> computeEasyMove(board, aiMark, available)
            AiDifficulty.MEDIUM -> computeMediumMove(board, aiMark, available)
            AiDifficulty.UNBEATABLE -> computeMinimaxMove(board, aiMark)
        }
    }

    private fun computeEasyMove(
        board: List<PlayerMark>,
        aiMark: PlayerMark,
        available: List<Int>
    ): Int {
        // 20% chance of making smart winning move, 80% random
        if (Random.nextFloat() < 0.25f) {
            val winMove = findWinningMove(board, aiMark, available)
            if (winMove != null) return winMove
        }
        return available.random()
    }

    private fun computeMediumMove(
        board: List<PlayerMark>,
        aiMark: PlayerMark,
        available: List<Int>
    ): Int {
        // 1. Take win if available
        findWinningMove(board, aiMark, available)?.let { return it }

        // 2. Block opponent's immediate win
        val humanMark = aiMark.opponent
        findWinningMove(board, humanMark, available)?.let { return it }

        // 3. Take center if free
        if (board[4] == PlayerMark.NONE && Random.nextFloat() < 0.7f) return 4

        // 4. Take corner if free
        val corners = listOf(0, 2, 6, 8).filter { board[it] == PlayerMark.NONE }
        if (corners.isNotEmpty() && Random.nextFloat() < 0.6f) {
            return corners.random()
        }

        // 5. Otherwise random
        return available.random()
    }

    private fun findWinningMove(
        board: List<PlayerMark>,
        mark: PlayerMark,
        available: List<Int>
    ): Int? {
        val mutable = board.toMutableList()
        for (move in available) {
            mutable[move] = mark
            if (checkWinningLine(mutable) != null) {
                return move
            }
            mutable[move] = PlayerMark.NONE
        }
        return null
    }

    /**
     * Minimax algorithm with depth weighting.
     * Guarantees unbeatable play (optimal response every single turn).
     */
    private fun computeMinimaxMove(board: List<PlayerMark>, aiMark: PlayerMark): Int {
        val available = getAvailableMoves(board)
        val mutable = board.toMutableList()
        var bestScore = Int.MIN_VALUE
        var bestMove = available.first()

        for (move in available) {
            mutable[move] = aiMark
            val score = minimax(
                board = mutable,
                depth = 0,
                isMaximizing = false,
                aiMark = aiMark,
                humanMark = aiMark.opponent,
                alpha = Int.MIN_VALUE,
                beta = Int.MAX_VALUE
            )
            mutable[move] = PlayerMark.NONE

            if (score > bestScore) {
                bestScore = score
                bestMove = move
            }
        }
        return bestMove
    }

    private fun minimax(
        board: MutableList<PlayerMark>,
        depth: Int,
        isMaximizing: Boolean,
        aiMark: PlayerMark,
        humanMark: PlayerMark,
        alpha: Int,
        beta: Int
    ): Int {
        val winLine = checkWinningLine(board)
        if (winLine != null) {
            val winner = board[winLine.a]
            return if (winner == aiMark) 10 - depth else depth - 10
        }
        if (isBoardFull(board)) return 0

        var curAlpha = alpha
        var curBeta = beta

        if (isMaximizing) {
            var maxScore = Int.MIN_VALUE
            for (i in board.indices) {
                if (board[i] == PlayerMark.NONE) {
                    board[i] = aiMark
                    val score = minimax(board, depth + 1, false, aiMark, humanMark, curAlpha, curBeta)
                    board[i] = PlayerMark.NONE
                    maxScore = maxOf(maxScore, score)
                    curAlpha = maxOf(curAlpha, score)
                    if (curBeta <= curAlpha) break
                }
            }
            return maxScore
        } else {
            var minScore = Int.MAX_VALUE
            for (i in board.indices) {
                if (board[i] == PlayerMark.NONE) {
                    board[i] = humanMark
                    val score = minimax(board, depth + 1, true, aiMark, humanMark, curAlpha, curBeta)
                    board[i] = PlayerMark.NONE
                    minScore = minOf(minScore, score)
                    curBeta = minOf(curBeta, score)
                    if (curBeta <= curAlpha) break
                }
            }
            return minScore
        }
    }
}
