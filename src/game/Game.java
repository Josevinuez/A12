package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Game extends JFrame {
    private boolean designMode = true;
    private static final int CELL_SIZE = 30;// Size of each cell on the grid
    private JPanel boardContainer; // Container for the board
    private final JPanel originalControlPanel; // Original control panel
    private JPanel designPanel;// Panel for design mode
    private JButton backButton;// Back button for design mode
    private CellState[][] grid;// 2D array representing the grid
    private static final int DIM = 8; // Dimension of the grid (16x16) by default

    public static void main(String[] args) {
        new Game();
    }
    /**
     * Constructor for the Game class. This sets up the initial GUI components and launches the game.
     */
    public Game() {
        setTitle("Battleship Jose Vinueza and Arjunpal Singh");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(800, 600)); // Set minimum window size
        originalControlPanel = createControlPanel();// Create control panel
        add(originalControlPanel);// Create control panel
        initializeGrid(DIM); // Initialize the grid
        add(createBoardPanel(DIM));
        pack();// Pack the components in the frame
        setVisible(true);// Make the frame visible
    }
    /**
     * Creates the control panel for the game, including buttons and drop-down menus for various game options.
     * @return a JPanel containing the control panel
     */
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());

        // Logo Panel, Language Panel, Design and Random Buttons, Dimension Choice Box
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // Logo Panel
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = new ImageIcon("images/logo.png");
        logoLabel.setIcon(logoIcon);

        logoPanel.add(logoLabel);

        // Language Dropdown
        JPanel languagePanel = new JPanel();
        languagePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel languageLabel = new JLabel("Language:");
        JComboBox<String> languageComboBox = new JComboBox<>();
        languageComboBox.addItem("English");
        languageComboBox.addItem("Spanish");

        languageComboBox.addActionListener(e -> {
            String selectedLanguage = (String) languageComboBox.getSelectedItem();
            System.out.println("Selected Language: " + selectedLanguage);
        });

        languagePanel.add(languageLabel);
        languagePanel.add(languageComboBox);

        // Design and Random Buttons
        JPanel designRandomPanel = new JPanel();
        designRandomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton designButton = new JButton("Design");
        designButton.addActionListener(e -> switchToDesignMode());


        JButton randomLayoutButton = new JButton("Random Layout");
        randomLayoutButton.addActionListener(e -> {
            System.out.println("Random Layout button clicked");
            generateRandomBoats();
        });

        designRandomPanel.add(designButton);
        designRandomPanel.add(randomLayoutButton);

        // Dimension Choice Box
        JPanel dimensionPanel = new JPanel();
        dimensionPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel dimensionLabel = new JLabel("Board Dimension:");
        JComboBox<Integer> dimensionChoiceBox = new JComboBox<>();
        for (int i = 1; i <= 10; i++) {
            dimensionChoiceBox.addItem(i);
        }
        dimensionChoiceBox.addActionListener(e -> {
            Integer selectedItem = (Integer) dimensionChoiceBox.getSelectedItem();
            if (selectedItem != null) {
                int selectedDimension = selectedItem;
                System.out.println("Dimension set to: " + selectedDimension);
                if (designMode) {
                    getContentPane().remove(boardContainer);
                    boardContainer = createBoardPanel(selectedDimension); // Create a new board with the selected dimension
                    getContentPane().add(boardContainer, BorderLayout.CENTER);
                    pack();
                }
            }
        });


        dimensionPanel.add(dimensionLabel);
        dimensionPanel.add(dimensionChoiceBox);

        topPanel.add(logoPanel);
        topPanel.add(languagePanel);
        topPanel.add(designRandomPanel);
        topPanel.add(dimensionPanel);

        // History Text Area
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JTextArea historyTextArea = new JTextArea();
        JScrollPane historyScrollPane = new JScrollPane(historyTextArea);
        historyTextArea.setWrapStyleWord(true);
        historyTextArea.setLineWrap(true);

        // Reset and Play Buttons
        JPanel resetPlayPanel = new JPanel();
        resetPlayPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> System.out.println("Reset button clicked"));

        JButton playButton = new JButton("Play");
        playButton.addActionListener(e -> System.out.println("Play button clicked"));

        resetPlayPanel.add(resetButton);
        resetPlayPanel.add(playButton);

        bottomPanel.add(historyScrollPane);
        bottomPanel.add(resetPlayPanel);

        // Main Control Panel
        JPanel mainControlPanel = new JPanel();
        mainControlPanel.setLayout(new BoxLayout(mainControlPanel, BoxLayout.Y_AXIS));
        mainControlPanel.add(topPanel);
        mainControlPanel.add(bottomPanel);

        controlPanel.add(mainControlPanel, BorderLayout.CENTER);

        return controlPanel;
    }
    /**
     * Switches the game interface to design mode, where players can manually place their ships.
     */
    private void switchToDesignMode() {
        designMode = true;

        originalControlPanel.setVisible(false); // hide originalControlPanel

        // Create the design panel
        designPanel = createDesignPanel();

        // Create the back button
        backButton = new JButton("Back");
        backButton.addActionListener(e -> switchToGameMode());

        getContentPane().add(designPanel, BorderLayout.NORTH);
        getContentPane().add(backButton, BorderLayout.SOUTH);

        // Repaint and revalidate the JFrame itself
        this.repaint();
        this.revalidate();
    }
    /**
     * Switches the game interface back to the default game mode from design mode.
     */
    private void switchToGameMode() {
        designMode = false;

        // remove backButton and designPanel
        getContentPane().remove(backButton);
        getContentPane().remove(designPanel);

        // make the original control panel visible again
        originalControlPanel.setVisible(true);

        // Repaint and revalidate the JFrame
        this.repaint();
        this.revalidate();
    }
    /**
     * Creates the design panel, which allows players to choose the size and direction of their ships.
     * @return a JPanel containing the design panel
     */
    private JPanel createDesignPanel() {
        // Create a JPanel for the design panel
        JPanel designPanel = new JPanel();

        // Create a box for selecting boat size
        JComboBox<Integer> boatSizeChoiceBox = new JComboBox<>();
        for (int i = 1; i <= 10; i++) {
            boatSizeChoiceBox.addItem(i);
        }

        // Create a box for selecting boat direction
        JComboBox<String> boatDirectionChoiceBox = new JComboBox<>();
        boatDirectionChoiceBox.addItem("Horizontal");
        boatDirectionChoiceBox.addItem("Vertical");

        // Create a "Place" button
        JButton placeButton = new JButton("Place");
        placeButton.addActionListener(e -> {
            Integer selectedItem = (Integer) boatSizeChoiceBox.getSelectedItem();
            if (selectedItem != null) {
                int boatSize = selectedItem;
                System.out.println("Boat Size: " + boatSize);
            }
        });

        // Add the boat size selection, boat direction selection, and the "Place" button to the design panel
        designPanel.add(boatSizeChoiceBox);
        designPanel.add(boatDirectionChoiceBox);
        designPanel.add(placeButton);

        return designPanel;
    }
    /**
     * Initializes the 2D grid for the game. Each cell in the grid can either be empty or contain part of a ship.
     * @param dimension the number of cells in one row or column of the grid
     * @return a JPanel representing the initialized game grid
     */
    private JPanel initializeGrid(int dimension) {
        // Calculate the total size of the grid, twice the dimension
        int boardSize = 2 * dimension;
        // Initialize an empty 2D array 'grid' with dimensions as boardSize
        grid = new CellState[boardSize][boardSize];

        // Initialize the grid with empty cells
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                grid[row][col] = CellState.EMPTY;
            }
        }
        return createGridBoard(boardSize);
    }
    /**
     * Creates the board panel, which displays the 2D game grid to the user.
     * @param dimension the number of cells in one row or column of the grid
     * @return a JPanel representing the board panel
     */
    private JPanel createBoardPanel(int dimension) {
        int boardSize = 2 * dimension;

        JPanel leftBoard = createGridBoard(boardSize);
        JPanel rightBoard = createGridBoard(boardSize);
        leftBoard.setPreferredSize(new Dimension(500, 500)); // Set a fixed size for the left board
        rightBoard.setPreferredSize(new Dimension(500, 500)); // Set a fixed size for the right board

        JPanel boardContainer = new JPanel();
        boardContainer.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH; // Set the fill behavior of the component within its display area

        boardContainer.add(leftBoard, constraints);// Add the left board to the board container

        // Update the X and y coordinate for the next component
        constraints.gridx = 1;
        constraints.gridy = 0;

        JPanel controlPanel = createControlPanel(); // Create the control panel
        boardContainer.add(controlPanel, constraints); // Add the control panel to the board container

        // Update the X and y coordinate for the next component
        constraints.gridx = 2;
        constraints.gridy = 0;

        boardContainer.add(rightBoard, constraints);// Add the right board to the board container
        this.boardContainer = boardContainer;
        return boardContainer;
    }

    /**
     * Creates a grid panel to be displayed as part of the board panel. This represents a 2D grid of cells.
     * @param boardSize the number of cells in one row or column of the grid
     * @return a JPanel representing the grid
     */
    private JPanel createGridBoard(int boardSize) {
        JPanel board = new JPanel();
        // Set the layout manager for the board panel
        board.setLayout(new GridLayout(boardSize, boardSize));

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                JPanel cell = createCell(row, col);// Create a cell panel for the current row and column
                board.add(cell); // Add the cell panel to the board panel
            }
        }
        return board;
    }
    /**
     * Creates a cell to be displayed as part of the grid panel. Each cell can either be empty or contain part of a ship.
     * @param row the row number of the cell in the grid
     * @param col the column number of the cell in the grid
     * @return a JPanel representing the cell
     */
    private JPanel createCell(int row, int col) {
        JPanel cell = new JPanel();
        cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));// Set the preferred size of the cell
        cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));// Set the border color of the cell

        // Check if the cell represents a boat and set the background color to blue
        if (grid[row][col] == CellState.BOAT) {
            cell.setBackground(Color.BLUE);
        } else {
            cell.setBackground(Color.WHITE);
        }
        cell.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Cell clicked: " + cell.getName());
            }
        });
        cell.setName("cell_" + (char) ('A' + row) + (char) ('A' + col));// Set a name for the cell panel
        return cell;
    }
    /**
     * Generates a random layout of ships on the game grid.
     */
    private void generateRandomBoats() {
        for (int boatSize = 1; boatSize <= DIM; boatSize++) {
            for (int j = 1; j <= DIM - boatSize + 1; j++) {
                createRandomBoat(boatSize);// Create a random boat of the specified size
            }
        }
        updateBoard();// Update the game board after generating the random layout
    }
    /**
     * Creates a random boat of a specified size on the game grid.
     * @param boatSize the size of the boat, which is the number of consecutive cells it occupies
     */
    private void createRandomBoat(int boatSize) {
        Random rand = new Random();
        int randRow, randCol;
        boolean validPosition = false;

        do {
            randRow = rand.nextInt(2 * DIM);
            randCol = rand.nextInt(2 * DIM);

            // Check if the boat can be placed horizontally at the random position
            if (randCol + boatSize <= 2 * DIM) {
                validPosition = true;
                for (int pos = 0; pos < boatSize; pos++) {
                    if (grid[randRow][randCol + pos] != CellState.EMPTY) {
                        validPosition = false;
                        break;
                    }
                }
            }

            // Check if the boat can be placed vertically at the random position
            if (!validPosition && randRow + boatSize <= 2 * DIM) {
                validPosition = true;
                for (int pos = 0; pos < boatSize; pos++) {
                    if (grid[randRow + pos][randCol] != CellState.EMPTY) {
                        validPosition = false;
                        break;
                    }
                }
            }
        } while (!validPosition);

        // Place the boat horizontally if possible else place it vertically
        if (randCol + boatSize <= 2 * DIM) {
            for (int pos = 0; pos < boatSize; pos++) {
                grid[randRow][randCol + pos] = CellState.BOAT;
            }
        } else {
            for (int pos = 0; pos < boatSize; pos++) {
                grid[randRow + pos][randCol] = CellState.BOAT;
            }
        }
    }
    /**
     * Updates the display of the board panel to reflect the current state of the game grid.
     */
    private void updateBoard() {
        if (boardContainer != null) {
            getContentPane().remove(boardContainer);// Remove the existing board container from the content panel
        }
        boardContainer = createBoardPanel(DIM); // Create a new board container
        getContentPane().add(boardContainer, BorderLayout.CENTER); // Add the new board container to the content panel
        pack();// Pack the components in the frame
        getContentPane().revalidate(); // Revalidate the content pane after components are added or removed
        getContentPane().repaint(); // Repaint the content pane to reflect the changes
    }
    /**
     * Enumeration for the possible states of a cell in the game grid. A cell can either be empty or contain part of a ship.
     */
    private enum CellState {
        EMPTY, // Represents an empty cell
        BOAT   // Represents a cell containing part of a ship
    }
}
