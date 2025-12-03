package game;

import java.util.HashMap;
import java.util.Map;


class BoxChecker extends BaseChecker {
    public BoxChecker(SudokuBoard board, ValidationResult result, int start, int end) {
        super(board, result, start, end);
    }

    @Override
    protected void check(int boxId) {
        Map<Integer, Integer> data = new HashMap<>();
        
        int startRow = (boxId / 3) * 3;
        int startCol = (boxId % 3) * 3;
        
        // We map the 3x3 box to indices 0-8 for simplicity
        int localIndex = 0;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                data.put(localIndex++, board.getCell(r, c));
            }
        }
        validateSection("BOX", boxId, data);
    }
}