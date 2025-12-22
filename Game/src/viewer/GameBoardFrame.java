/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package viewer;

import controller.DifficultyEnum;
import game.InvalidGameException;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Eman
 */
public class GameBoardFrame extends javax.swing.JFrame {

    private int[][] board;
    private int[][] originalBoard;
    private ControllerFacade controller;
    private DifficultyEnum difficulty;
    private JTextField[][] cells;

    /**
     * Creates new form GameBoardFrame
     */
    public GameBoardFrame(int[][] board, ControllerFacade controller, DifficultyEnum difficulty) {
        this.board = deepCopy(board);
        this.originalBoard = deepCopy(board);
        this.controller = controller;
        this.difficulty = difficulty;

        initComponents();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Sudoku Game - " + difficulty.name());
        // ✅ Add window listener to handle proper cleanup
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                handleWindowClose();
            }
        });

        createSudokuGrid();
        displayBoard();
        
    }


    private void handleWindowClose() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Do you want to save your progress?",
                "Exit Game",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (choice == JOptionPane.CANCEL_OPTION) {
            // Don't close
            return;
        } else if (choice == JOptionPane.YES_OPTION) {
            // Progress is already saved automatically in handleCellChange
            // Just exit normally
            System.exit(0);
        } else {
            // NO - Delete the current game (don't save progress)
            try {
                controller.deleteCompletedGame(DifficultyEnum.CURRENT);
            } catch (Exception ex) {
                System.err.println("Error deleting game: " + ex.getMessage());
            }
            System.exit(0);
        }
    }

    private void createSudokuGrid() {
        // Remove any existing components
        gridPanel.removeAll();

        // Set layout
        gridPanel.setLayout(new GridLayout(9, 9, 0, 0));

        // Create 81 text fields
        cells = new JTextField[9][9];

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                JTextField cell = new JTextField();
                cell.setHorizontalAlignment(JTextField.CENTER);
                cell.setFont(new Font("Arial", Font.BOLD, 20));

                // Borders for 3x3 boxes
                int top = (row % 3 == 0) ? 2 : 1;
                int left = (col % 3 == 0) ? 2 : 1;
                int bottom = (row == 8) ? 2 : ((row % 3 == 2) ? 2 : 1);
                int right = (col == 8) ? 2 : ((col % 3 == 2) ? 2 : 1);

                cell.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));

                // Original cells are read-only
                if (originalBoard[row][col] != 0) {
                    cell.setEditable(false);
                    cell.setBackground(new Color(230, 230, 230));
                    cell.setForeground(Color.BLACK);
                } else {
                    cell.setBackground(Color.WHITE);
                    cell.setForeground(new Color(0, 0, 200));
                }

                final int r = row;
                final int c = col;

                // Input validation
                cell.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char ch = e.getKeyChar();
                        if (ch == KeyEvent.VK_BACK_SPACE || ch == KeyEvent.VK_DELETE) {
                            return;
                        }
                        if (ch < '1' || ch > '9') {
                            e.consume();
                            return;
                        }
                        if (cell.getText().length() >= 1) {
                            e.consume();
                        }
                    }
                });

                // Save on focus lost
                cell.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        handleCellChange(r, c, cell);
                    }
                });

                cells[row][col] = cell;
                gridPanel.add(cell);
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void displayBoard() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] != 0) {
                    cells[row][col].setText(String.valueOf(board[row][col]));
                } else {
                    cells[row][col].setText("");
                }
            }
        }
    }

    private int[][] deepCopy(int[][] original) {
        int[][] copy = new int[9][9];
        for (int r = 0; r < 9; r++) {
            System.arraycopy(original[r], 0, copy[r], 0, 9);
        }
        return copy;
    }

    private void handleCellChange(int row, int col, JTextField cell) {
        String text = cell.getText().trim();
        int newValue = text.isEmpty() ? 0 : Integer.parseInt(text);
        int previousValue = board[row][col];

        if (newValue != previousValue) {
            board[row][col] = newValue;

            try {
                UserAction action = new UserAction(row, col, newValue, previousValue);
                controller.logUserAction(action);
                controller.saveCurrentGame(board);
            } catch (IOException | InvalidGameException ex) {
                System.err.println("Error: " + ex.getMessage());
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblStatus = new javax.swing.JLabel();
        btnVerify = new javax.swing.JButton();
        btnSolve = new javax.swing.JButton();
        btnUndo = new javax.swing.JButton();
        btnNewGame = new javax.swing.JButton();
        gridPanel = new javax.swing.JPanel();
        BtnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblStatus.setText("    Status: Ready");

        btnVerify.setText("Verify");
        btnVerify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerifyActionPerformed(evt);
            }
        });

        btnSolve.setText("Solve");
        btnSolve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolveActionPerformed(evt);
            }
        });

        btnUndo.setText("Undo");
        btnUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUndoActionPerformed(evt);
            }
        });

        btnNewGame.setText("New Game");
        btnNewGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewGameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout gridPanelLayout = new javax.swing.GroupLayout(gridPanel);
        gridPanel.setLayout(gridPanelLayout);
        gridPanelLayout.setHorizontalGroup(
            gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 387, Short.MAX_VALUE)
        );
        gridPanelLayout.setVerticalGroup(
            gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 241, Short.MAX_VALUE)
        );

        BtnBack.setBackground(new java.awt.Color(255, 255, 0));
        BtnBack.setText("Back");
        BtnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnVerify)
                        .addGap(27, 27, 27)
                        .addComponent(btnSolve)
                        .addGap(28, 28, 28)
                        .addComponent(btnUndo)
                        .addGap(18, 18, 18)
                        .addComponent(btnNewGame))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BtnBack)
                        .addGap(43, 43, 43)
                        .addComponent(lblStatus)))
                .addContainerGap(16, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(gridPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(BtnBack)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 262, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVerify)
                    .addComponent(btnSolve)
                    .addComponent(btnUndo)
                    .addComponent(btnNewGame))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(43, Short.MAX_VALUE)
                    .addComponent(gridPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(47, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVerifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerifyActionPerformed

        boolean[][] validity = controller.verifyGame(board);

        boolean hasInvalid = false;

        // IMPORTANT: Reset ALL cells to their default colors first
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (originalBoard[r][c] == 0) {
                    // Editable cells -> white background
                    cells[r][c].setBackground(Color.WHITE);
                } else {
                    // Original cells -> gray background
                    cells[r][c].setBackground(new Color(230, 230, 230));
                }
            }
        }

        // Now highlight ONLY the currently invalid cells in red
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                // If the cell is invalid AND not empty, mark it red
                if (!validity[r][c] && board[r][c] != 0) {
                    cells[r][c].setBackground(new Color(255, 200, 200));
                    hasInvalid = true;
                }
            }
        }

        if (hasInvalid) {
            lblStatus.setText("Status: Invalid - Duplicates found!");
            lblStatus.setForeground(Color.RED);
        } else {
            // Check if board is complete
            boolean isComplete = true;
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    if (board[r][c] == 0) {
                        isComplete = false;
                        break;
                    }
                }
                if (!isComplete) {
                    break;
                }
            }

            if (isComplete) {
                lblStatus.setText("Status: Valid and Complete! 🎉");
                lblStatus.setForeground(new Color(0, 150, 0));
                JOptionPane.showMessageDialog(this,
                        "Congratulations! You've completed the puzzle!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Delete the finished game
                try {
                    controller.deleteCompletedGame(difficulty);

                    // Return to difficulty selection
                    this.dispose();
                    new WelcomeFrame().setVisible(true);
                } catch (Exception ex) {
                    System.err.println("Error deleting completed game: " + ex.getMessage());
                }
            } else {
                lblStatus.setText("Status: Valid but Incomplete");
                lblStatus.setForeground(new Color(200, 100, 0));
            }
        }    // TODO add your handling code here:
    }//GEN-LAST:event_btnVerifyActionPerformed

    private void btnSolveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolveActionPerformed
        try {
            // Count empty cells
            int emptyCount = 0;
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    if (board[r][c] == 0) {
                        emptyCount++;
                    }
                }
            }

            if (emptyCount != 5) {
                JOptionPane.showMessageDialog(this,
                        "Solve feature requires exactly 5 empty cells.\nCurrent empty cells: " + emptyCount,
                        "Cannot Solve",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Call solver
            int[][] solution = controller.solveGame(board);

            // Apply solution
            for (int i = 0; i < solution.length; i++) {
                int row = solution[i][0];
                int col = solution[i][1];
                int value = solution[i][2];

                board[row][col] = value;
                cells[row][col].setText(String.valueOf(value));
                cells[row][col].setForeground(new Color(0, 150, 0)); // Green for solved
            }

            lblStatus.setText("Status: Puzzle Solved!");
            lblStatus.setForeground(new Color(0, 150, 0));

            JOptionPane.showMessageDialog(this,
                    "Puzzle solved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (InvalidGameException ex) {
            JOptionPane.showMessageDialog(this,
                    "Cannot solve: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_btnSolveActionPerformed

    private void btnUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUndoActionPerformed
        try {
            UserAction lastAction = controller.undo();

            if (lastAction == null) {
                JOptionPane.showMessageDialog(this,
                        "Nothing to undo",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Revert the cell
            int row = lastAction.getRow();
            int col = lastAction.getCol();
            int prevValue = lastAction.getPreviousValue();

            board[row][col] = prevValue;
            cells[row][col].setText(prevValue == 0 ? "" : String.valueOf(prevValue));

            // Save the reverted state
            controller.saveCurrentGame(board);

            lblStatus.setText("Status: Undo successful");
            lblStatus.setForeground(Color.BLUE);

        } catch (IOException | InvalidGameException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error performing undo: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_btnUndoActionPerformed

    private void btnNewGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewGameActionPerformed
        int choice = JOptionPane.showConfirmDialog(this,
                "Start a new game? Current progress will be lost.",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            new DifficultySelectionFrame(controller).setVisible(true);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_btnNewGameActionPerformed

    private void BtnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBackActionPerformed
  int choice = JOptionPane.showConfirmDialog(this,
        "Go back to difficulty selection? Current progress will not be saved.",
        "Confirm Back",
        JOptionPane.YES_NO_OPTION);
    
    if (choice == JOptionPane.YES_OPTION) {
        // Progress is already auto-saved in handleCellChange
        this.dispose();
        new DifficultySelectionFrame(controller).setVisible(true);
    }        // TODO add your handling code here:
    }//GEN-LAST:event_BtnBackActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnBack;
    private javax.swing.JButton btnNewGame;
    private javax.swing.JButton btnSolve;
    private javax.swing.JButton btnUndo;
    private javax.swing.JButton btnVerify;
    private javax.swing.JPanel gridPanel;
    private javax.swing.JLabel lblStatus;
    // End of variables declaration//GEN-END:variables
}
