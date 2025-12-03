package game;

import java.util.HashMap;
import java.util.Map;


class ColChecker extends BaseChecker {
    
    public ColChecker(SudokuBoard board, ValidationResult result, int start, int end) {
        super(board, result, start, end);
    }

    @Override
    protected void check(int col) {
        Map<Integer, Integer> data = new HashMap<>();
        for (int row = 0; row < 9; row++) {
            data.put(row, board.getCell(row, col));
        }
        validateSection("COL", col, data);
    }
}
