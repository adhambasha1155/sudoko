package game;

import java.util.HashMap;
import java.util.Map;


class RowChecker extends BaseChecker {
    public RowChecker(SudokuBoard board, ValidationResult result, int start, int end) {
        super(board, result, start, end);
    }

    @Override
    protected void check(int row) {
        Map<Integer, Integer> data = new HashMap<>();
        for (int col = 0; col < 9; col++) {
            data.put(col, board.getCell(row, col));
        }
        validateSection("ROW", row, data);
    }
}