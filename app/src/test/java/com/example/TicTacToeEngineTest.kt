package com.example

import com.example.engine.TicTacToeEngine
import com.example.model.AiDifficulty
import com.example.model.PlayerMark
import com.example.model.WinningLineType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TicTacToeEngineTest {

    @Test
    fun testRowWinDetection() {
        val board = listOf(
            PlayerMark.X, PlayerMark.X, PlayerMark.X,
            PlayerMark.O, PlayerMark.NONE, PlayerMark.O,
            PlayerMark.NONE, PlayerMark.NONE, PlayerMark.NONE
        )
        val win = TicTacToeEngine.checkWinningLine(board)
        assertNotNull(win)
        assertEquals(WinningLineType.ROW_0, win?.type)
        assertEquals(0, win?.a)
        assertEquals(1, win?.b)
        assertEquals(2, win?.c)
    }

    @Test
    fun testDiagonalWinDetection() {
        val board = listOf(
            PlayerMark.O, PlayerMark.X, PlayerMark.NONE,
            PlayerMark.X, PlayerMark.O, PlayerMark.NONE,
            PlayerMark.NONE, PlayerMark.NONE, PlayerMark.O
        )
        val win = TicTacToeEngine.checkWinningLine(board)
        assertNotNull(win)
        assertEquals(WinningLineType.DIAG_MAIN, win?.type)
    }

    @Test
    fun testMinimaxBlocksWinningMove() {
        // Human X is about to win on 0 and 1, AI O should block at 2
        val board = listOf(
            PlayerMark.X, PlayerMark.X, PlayerMark.NONE,
            PlayerMark.O, PlayerMark.NONE, PlayerMark.NONE,
            PlayerMark.NONE, PlayerMark.NONE, PlayerMark.NONE
        )
        val move = TicTacToeEngine.computeAiMove(board, PlayerMark.O, AiDifficulty.UNBEATABLE)
        assertEquals(2, move)
    }

    @Test
    fun testMinimaxTakesWinningMove() {
        // AI O can win on index 6
        val board = listOf(
            PlayerMark.O, PlayerMark.X, PlayerMark.X,
            PlayerMark.O, PlayerMark.NONE, PlayerMark.NONE,
            PlayerMark.NONE, PlayerMark.NONE, PlayerMark.NONE
        )
        val move = TicTacToeEngine.computeAiMove(board, PlayerMark.O, AiDifficulty.UNBEATABLE)
        assertEquals(6, move)
    }

    @Test
    fun testFullBoardIsDetected() {
        val board = listOf(
            PlayerMark.X, PlayerMark.O, PlayerMark.X,
            PlayerMark.X, PlayerMark.O, PlayerMark.O,
            PlayerMark.O, PlayerMark.X, PlayerMark.X
        )
        assertTrue(TicTacToeEngine.isBoardFull(board))
    }
}
