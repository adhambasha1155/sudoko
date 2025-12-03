/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

/**
 *
 * @author Eman
 */
public class CheckerFactory {
    public static BaseChecker createChecker(String type, SudokuBoard board, ValidationResult result, int start, int end) {
        switch (type.toLowerCase()) {
            case "row":
                return new RowChecker(board, result, start, end);
            case "col":
                return new ColChecker(board, result, start, end);
            case "box":
                return new BoxChecker(board, result, start, end);
            default:
                throw new IllegalArgumentException("Unknown checker type: " + type);
        }
    }
}

