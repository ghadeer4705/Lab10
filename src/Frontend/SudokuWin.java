package Frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SudokuWin extends JFrame {

    private View view;
    private int[][] board;
    private JTextField[][] cells;
    private JButton btnVerify, btnSolve, btnUndo, btnBack;
    private boolean[][] originalCells;

    public SudokuWin(Controllable controller, int[][] board) {
        this.view = (View) controller;
        this.board = board;
        this.originalCells = new boolean[9][9];
        initComponents();
        loadBoard(board);
    }

    private void initComponents() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Board Panel
        JPanel boardPanel = new JPanel(new GridLayout(9, 9, 2, 2));
        boardPanel.setBackground(Color.BLACK);
        cells = new JTextField[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                JTextField cell = new JTextField();
                cell.setHorizontalAlignment(JTextField.CENTER);
                cell.setFont(new Font("Arial", Font.BOLD, 24));

                // Add thicker borders for 3x3 boxes
                int top = (i % 3 == 0) ? 3 : 1;
                int left = (j % 3 == 0) ? 3 : 1;
                int bottom = (i == 8) ? 3 : 1;
                int right = (j == 8) ? 3 : 1;
                cell.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));

                final int row = i;
                final int col = j;

                // Restrict input to single digit 1-9
                cell.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!Character.isDigit(c) || c == '0') {
                            if (c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                                e.consume();
                            }
                        }
                        String currentText = cell.getText();
                        if (currentText.length() >= 1 && Character.isDigit(c)) {
                            e.consume();
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        try {
                            String text = cell.getText().trim();
                            int val = text.isEmpty() ? 0 : Integer.parseInt(text);
                            int prev = board[row][col];

                            if (val != prev) {
                                board[row][col] = val;
                                view.logUserAction(new UserAction(row, col, val, prev));
                            }
                            updateSolveButtonState();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(SudokuWin.this,
                                    "Error logging action: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

                cells[i][j] = cell;
                boardPanel.add(cell);
            }
        }

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Top row buttons (game actions)
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        topRow.setBackground(new Color(240, 240, 240));

        btnVerify = new JButton("Verify");
        btnSolve = new JButton("Solve");
        btnUndo = new JButton("Undo");

        // Style action buttons
        for (JButton btn : new JButton[]{btnVerify, btnSolve, btnUndo}) {
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setPreferredSize(new Dimension(120, 40));
        }

        btnVerify.setBackground(new Color(100, 180, 255));
        btnSolve.setBackground(new Color(100, 255, 180));
        btnUndo.setBackground(new Color(255, 180, 100));

        topRow.add(btnVerify);
        topRow.add(btnSolve);
        topRow.add(btnUndo);

        // Bottom row (back button)
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        bottomRow.setBackground(new Color(240, 240, 240));

        btnBack = new JButton("← Back to Main Menu");
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setPreferredSize(new Dimension(250, 40));
        btnBack.setBackground(new Color(200, 200, 200));
        btnBack.setForeground(Color.DARK_GRAY);

        bottomRow.add(btnBack);

        buttonPanel.add(topRow);
        buttonPanel.add(bottomRow);

        add(boardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        btnVerify.addActionListener(e -> verifyBoard());
        btnSolve.addActionListener(e -> solveBoard());
        btnUndo.addActionListener(e -> undoAction());
        btnBack.addActionListener(e -> backToMainMenu());

        btnSolve.setEnabled(false);
    }

    private void loadBoard(int[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    cells[i][j].setText("");
                    cells[i][j].setEditable(true);
                    cells[i][j].setBackground(Color.WHITE);
                    originalCells[i][j] = false;
                } else {
                    cells[i][j].setText(String.valueOf(board[i][j]));
                    cells[i][j].setEditable(false);
                    cells[i][j].setBackground(new Color(220, 220, 220));
                    cells[i][j].setForeground(Color.DARK_GRAY);
                    originalCells[i][j] = true;
                }
            }
        }
        updateSolveButtonState();
    }

    private void verifyBoard() {
        boolean[][] result = view.verifyGame(board);
        boolean hasErrors = false;
        int incompleteCount = 0;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!originalCells[i][j]) {
                    if (board[i][j] == 0) {
                        cells[i][j].setBackground(Color.YELLOW);
                        incompleteCount++;
                    } else if (!result[i][j]) {
                        cells[i][j].setBackground(new Color(255, 100, 100));
                        hasErrors = true;
                    } else {
                        cells[i][j].setBackground(new Color(100, 255, 100));
                    }
                }
            }
        }

        if (incompleteCount > 0) {
            JOptionPane.showMessageDialog(this,
                    "Board is INCOMPLETE\n" + incompleteCount + " cells remaining.",
                    "Verification Result", JOptionPane.INFORMATION_MESSAGE);
        } else if (hasErrors) {
            JOptionPane.showMessageDialog(this,
                    "Board is INVALID\nRed cells contain errors.",
                    "Verification Result", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "🎉 Congratulations! Board is VALID!\nPuzzle completed successfully!",
                    "Victory!", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new MainGameWin().setVisible(true);
        }

        updateSolveButtonState();
    }

    private void solveBoard() {
        try {
            int[][] solved = view.solveGame(board);

            for (int[] move : solved) {
                int r = move[0];
                int c = move[1];
                int val = move[2];
                board[r][c] = val;
                cells[r][c].setText(String.valueOf(val));
                cells[r][c].setBackground(new Color(150, 200, 255));
            }

            JOptionPane.showMessageDialog(this,
                    "Puzzle solved! " + solved.length + " cells filled.",
                    "Solver", JOptionPane.INFORMATION_MESSAGE);
            updateSolveButtonState();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Cannot solve puzzle:\n" + e.getMessage(),
                    "Solver Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void undoAction() {
        try {
            view.performUndo();

            int[][] currentBoard = view.getGame('C');
            this.board = currentBoard;
            loadBoard(currentBoard);

            JOptionPane.showMessageDialog(this,
                    "Last action undone successfully!",
                    "Undo", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Cannot undo:\n" + e.getMessage(),
                    "Undo Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void backToMainMenu() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to go back?\nYour progress will be saved.",
                "Back to Main Menu",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            new MainGameWin().setVisible(true);
        }
    }

    private void updateSolveButtonState() {
        int emptyCount = 0;
        for (int[] row : board) {
            for (int val : row) {
                if (val == 0) emptyCount++;
            }
        }
        btnSolve.setEnabled(emptyCount == 5);
    }
}