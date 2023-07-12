import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class SudokuSolver extends JFrame implements ActionListener {
    private JTextField[][] grid;
    private int[][] initialGrid;
    private boolean isDarkTheme = false;
    private Color backgroundColor;
    private Color gridColor;
    private Color initialGridColor;
    private Color textColor;

    private Color darkBorderColor; // New color for dark border

    public SudokuSolver() {
        setTitle("Sudoku Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

//        isDarkTheme = false;
        backgroundColor = new Color(40, 44, 52);
        gridColor = new Color(55, 59, 68);
        initialGridColor = new Color(69, 73, 82);
        textColor = new Color(255, 255, 255);
        darkBorderColor = new Color(0, 0, 0); // Define the new dark border color

        getContentPane().setBackground(backgroundColor);

        grid = new JTextField[9][9];
        initialGrid = new int[9][9];

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        Font font = new Font("Arial", Font.PLAIN, 24);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                grid[i][j] = new JTextField();
                grid[i][j].setHorizontalAlignment(JTextField.CENTER);
                grid[i][j].setFont(font);
                grid[i][j].setForeground(textColor);
                grid[i][j].setDocument(new JTextFieldLimit(1)); // Limit input to a single character
                grid[i][j].setBackground(backgroundColor);
                grid[i][j].setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(gridColor),
                        BorderFactory.createMatteBorder(
                                i % 3 == 0 ? 2 : 1, j % 3 == 0 ? 2 : 1, 1, 1, darkBorderColor))); // Add dark border
                constraints.gridx = j;
                constraints.gridy = i;
                add(grid[i][j], constraints);
            }
        }

        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(this);
        solveButton.setBackground(new Color(0, 123, 255));
        solveButton.setForeground(textColor);
        constraints.gridx = 0;
        constraints.gridy = 9;
        constraints.gridwidth = 3;
        constraints.insets = new Insets(10, 0, 10, 0);
        add(solveButton, constraints);

        JButton resetButton = new JButton("Clear");
        resetButton.addActionListener(this);
        resetButton.setBackground(new Color(245, 183, 177));
        resetButton.setForeground(textColor);
        constraints.gridx = 3;
        constraints.gridy = 9;
        constraints.gridwidth = 3;
        constraints.insets = new Insets(10, 0, 10, 0);
        add(resetButton, constraints);

        JButton generateButton = new JButton("Generate");
        generateButton.addActionListener(this);
        generateButton.setBackground(new Color(34, 139, 34));
        generateButton.setForeground(textColor);
        constraints.gridx = 6;
        constraints.gridy = 9;
        constraints.gridwidth = 3;
        constraints.insets = new Insets(10, 0, 10, 0);
        add(generateButton, constraints);

        pack();
        setLocationRelativeTo(null); // Center the frame on the screen
    }

    public static void run(){
        new SudokuSolver().setVisible(true);
    }
    // HERE IT STARTS!!!!//////////////////////////////////
    public static void main(String[] args) {
        run();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Solve")) {
            int[][] board = getGridValues();

            if (!isValidSudoku(board)) {
                JOptionPane.showMessageDialog(this, "Invalid Sudoku puzzle!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (solveSudoku(board)) {
                // Update the grid with the solved puzzle
                setGridValues(board);
                markInitialNumbers();
                setSolvedNumbersColor();
            } else {
                JOptionPane.showMessageDialog(this, "No solution found!", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (e.getActionCommand().equals("Clear")) {
            // Reset the grid to its initial state
            resetGrid();
        } else if (e.getActionCommand().equals("Generate")) {
            resetGrid();
            generateSudoku();
        }
    }

    private void resetGrid() {
        // Reset the grid to its initial state
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                grid[i][j].setText("");
                grid[i][j].setForeground(textColor); // Reset number color to default
            }
        }
    }

    private int[][] getGridValues() {
        int[][] board = new int[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String value = grid[i][j].getText();
                if (!value.isEmpty()) {
                    try {
                        int num = Integer.parseInt(value);
                        if (num < 1 || num > 9) {
                            JOptionPane.showMessageDialog(this, "Invalid input! Enter numbers from 1 to 9.", "Warning", JOptionPane.WARNING_MESSAGE);
                            return null;
                        }
                        board[i][j] = num;
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid input! Enter numbers from 1 to 9.", "Warning", JOptionPane.WARNING_MESSAGE);
                        return null;
                    }
                }
            }
        }

        return board;
    }

    private void setGridValues(int[][] board) {
        // Update the grid with the puzzle values
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != 0) {
                    grid[i][j].setText(Integer.toString(board[i][j]));
                    grid[i][j].setForeground(Color.RED);
                } else {
                    grid[i][j].setText("");
                }
            }
        }
    }

    private void markInitialNumbers() {
        // Mark the initial numbers in the grid
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!grid[i][j].getText().isEmpty()) {
                    initialGrid[i][j] = 1;
                }
            }
        }
    }

    private void setSolvedNumbersColor() {
        // Set the solved numbers to green
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (initialGrid[i][j] != 0) {
                    grid[i][j].setForeground(Color.GREEN);
                }
            }
        }
    }

    private boolean solveSudoku(int[][] board) {
        int row = -1;
        int col = -1;
        boolean isEmpty = true;

        // Find the first empty cell
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    row = i;
                    col = j;
                    isEmpty = false;
                    break;
                }
            }
            if (!isEmpty) {
                break;
            }
        }

        // If all cells are filled, the puzzle is solved
        if (isEmpty) {
            return true;
        }

        // Try numbers from 1 to 9
        for (int num = 1; num <= 9; num++) {
            if (isSafe(board, row, col, num)) {
                board[row][col] = num;
                if (solveSudoku(board)) {
                    return true;
                }
                // Backtrack if the current configuration doesn't lead to a solution
                board[row][col] = 0;
            }
        }

        return false;
    }

    private boolean isSafe(int[][] board, int row, int col, int num) {
        // Check row
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num) {
                return false;
            }
        }

        // Check column
        for (int i = 0; i < 9; i++) {
            if (board[i][col] == num) {
                return false;
            }
        }

        // Check 3x3 box
        int boxRow = row - row % 3;     // boxRow = row/3 * 3
        int boxCol = col - col % 3;     // boxCol = col%3 * 3
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[boxRow + i][boxCol + j] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isValidSudoku(int[][] board) {
        // Check rows and columns
        for (int i = 0; i < 9; i++) {
            boolean[] rowCheck = new boolean[9];
            boolean[] colCheck = new boolean[9];
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != 0) {
                    if (rowCheck[board[i][j] - 1]) {
                        return false;
                    }
                    rowCheck[board[i][j] - 1] = true;
                    initialGrid[i][j] = 1; // Mark initial numbers
                }
                if (board[j][i] != 0) {
                    if (colCheck[board[j][i] - 1]) {
                        return false;
                    }
                    colCheck[board[j][i] - 1] = true;
                }
            }
        }

        // Check 3x3 boxes
        for (int box = 0; box < 9; box++) {
            boolean[] boxCheck = new boolean[9];
            int startRow = box / 3 * 3;
            int startCol = box % 3 * 3;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[startRow + i][startCol + j] != 0) {
                        if (boxCheck[board[startRow + i][startCol + j] - 1]) {
                            return false;
                        }
                        boxCheck[board[startRow + i][startCol + j] - 1] = true;
                    }
                }
            }
        }

        return true;
    }


    private void generateSudoku() {
        int[][] solvedBoard = new int[9][9];

        // Generate a complete, solved Sudoku puzzle
        if (generateSudokuHelper(solvedBoard, 0, 0)) {
            // Remove numbers from the solved puzzle to create a playable puzzle
            int numToRemove = getDifficultyLevel();
            Random rand = new Random();
            while (numToRemove > 0) {
                int row = rand.nextInt(9);
                int col = rand.nextInt(9);
                if (solvedBoard[row][col] != 0) {
                    grid[row][col].setText(Integer.toString(solvedBoard[row][col])); // Set the generated number
                    grid[row][col].setForeground(Color.RED); // Set the color to red
                    solvedBoard[row][col] = 0;
                    numToRemove--;
                }
            }
            setGridValues(solvedBoard);
//            markInitialNumbers();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to generate a Sudoku puzzle!", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean generateSudokuHelper(int[][] board, int row, int col) {
        if (col == 9) {
            col = 0;
            row++;
            if (row == 9) {
                return true;
            }
        }

        // Try numbers from 1 to 9
        for (int num = 1; num <= 9; num++) {
            if (isSafe(board, row, col, num)) {
                board[row][col] = num;
                if (generateSudokuHelper(board, row, col + 1)) {
                    return true;
                }
                // Backtrack if the current configuration doesn't lead to a solution
                board[row][col] = 0;
            }
        }

        return false;
    }

    private int getDifficultyLevel() {
        String[] options = {"Easy", "Medium", "Hard"};
        int choice = JOptionPane.showOptionDialog(this, "Select the difficulty level:", "Difficulty Level",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0:
                return 40; // Easy: 40 numbers will be removed
            case 1:
                return 50; // Medium: 50 numbers will be removed
            case 2:
                return 60; // Hard: 60 numbers will be removed
            default:
                return 40;
        }
    }

    class JTextFieldLimit extends PlainDocument {
        private int limit;

        JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }

        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null) {
                return;
            }
            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }
}
