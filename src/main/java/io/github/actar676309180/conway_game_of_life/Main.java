package io.github.actar676309180.conway_game_of_life;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    private static JFrame jFrame;

    private static boolean[][] cells;
    private static JButton[][] cellJButtons;
    private static JButton playButton;
    private static JButton nextButton;
    private static JButton clearButton;
    private static JTextField inputTextField;
    private static JButton randomButton;

    private static int cellWidth = 25;
    private static int cellCount = 20;

    private static long interval = 250;
    private static boolean play = false;

    public static void main(String[] args) {
        init();
    }

    private static void init() {
        jFrame = new JFrame("Conway's Game of Life");
        jFrame.setBounds(100, 100, 650, 550);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(null);

        cells = new boolean[cellCount][cellCount];
        cellJButtons = new JButton[cellCount][cellCount];
        setCell();
        setPlayButton();
        setNextButton();
        setClearButton();
        setRandomButton();

        initPlay();

        jFrame.setVisible(true);
    }

    private static void initPlay() {
        new Thread(new Runnable() {
            public void run() {
                //noinspection InfiniteLoopStatement
                while (true) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException ignored) {
                    }
                    if (play) {
                        next();
                    }
                }
            }
        }).start();
    }

    private static void setPlayButton() {
        playButton = new JButton("play");

        playButton.setBounds(cellWidth * cellCount + 25, 25, 100, 50);
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                play = !play;
                playButton.setText(play ? "pause" : "play");
            }
        });

        jFrame.add(playButton);
    }

    private static void pause(){
        play = false;
        playButton.setText("play");
    }

    private static void setNextButton() {
        nextButton = new JButton("next");

        nextButton.setBounds(cellWidth * cellCount + 25, 100, 100, 50);
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                next();
            }
        });

        jFrame.add(nextButton);
    }

    private static void setClearButton() {
        clearButton = new JButton("clear");

        clearButton.setBounds(cellWidth * cellCount + 25, 175, 100, 50);
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                clear();
            }
        });

        jFrame.add(clearButton);
    }

    private static void setRandomButton() {
        inputTextField = new JTextField("37.5");
        inputTextField.setBounds(cellWidth * cellCount + 25, 250, 75, 25);
        jFrame.add(inputTextField);
        JLabel label = new JLabel(" % ");
        label.setBounds(cellWidth * cellCount + 100, 250, 25, 25);
        jFrame.add(label);
        randomButton = new JButton("random");
        randomButton.setBounds(cellWidth * cellCount + 25, 300, 100, 50);
        randomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                random();
            }
        });
        jFrame.add(randomButton);
    }

    private static void random() {
        pause();

        String text = inputTextField.getText();

        try {
            double num = Double.valueOf(text);

            if (num>100){
                JOptionPane.showMessageDialog(jFrame, "Input value should be less than 100", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (num<0){
                JOptionPane.showMessageDialog(jFrame, "Input value should be greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            clear();

            int count = (int) (cellCount * cellCount * (num / 100.0));

            while (count > 0) {
                int x = (int) (Math.random() * cellCount);
                int y = (int) (Math.random() * cellCount);
                if (cells[x][y]) {
                    continue;
                }
                cells[x][y] = true;
                count--;
            }

            refresh();

        } catch (NumberFormatException ignored) {
            JOptionPane.showMessageDialog(jFrame, "The input value should be a number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void clear() {
        cells = new boolean[20][20];
        refresh();
    }

    private static void setCell() {
        for (int x = 0; x < cellCount; x++) {
            for (int y = 0; y < cellCount; y++) {
                JButton button = new JButton();
                button.setBackground(Color.white);
                button.setBounds(x * cellWidth, y * cellWidth, cellWidth, cellWidth);
                cells[x][y] = false;
                button.addActionListener(getOnClick(x, y));
                jFrame.add(button);
                cellJButtons[x][y] = button;
            }
        }
    }

    private static ActionListener getOnClick(final int x, final int y) {
        return new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                cells[x][y] = !cells[x][y];
                refresh();
            }
        };
    }

    private static void refresh() {
        for (int x = 0; x < cellCount; x++) {
            for (int y = 0; y < cellCount; y++) {
                boolean cell = cells[x][y];
                JButton button = cellJButtons[x][y];
                if (cell) {
                    button.setBackground(Color.GREEN);
                } else {
                    button.setBackground(Color.white);
                }
                jFrame.repaint();
            }
        }
    }

    private static void next() {
        boolean[][] next = new boolean[cellCount][cellCount];
        for (int x = 0; x < cellCount; x++) {
            for (int y = 0; y < cellCount; y++) {
                next[x][y] = checkCellAround(x, y);
            }
        }
        cells = next;
        refresh();
        if (!checkCells()) {
            play = false;
            playButton.setText("play");
        }
    }

    private static boolean checkCellAround(int x, int y) {
        boolean current = cells[x][y];
        int around = 0;

        if (checkCell(x - 1, y - 1)) around++;
        if (checkCell(x - 1, y)) around++;
        if (checkCell(x - 1, y + 1)) around++;
        if (checkCell(x, y - 1)) around++;
        if (checkCell(x, y + 1)) around++;
        if (checkCell(x + 1, y - 1)) around++;
        if (checkCell(x + 1, y)) around++;
        if (checkCell(x + 1, y + 1)) around++;

        if (current) {
            if (around < 2) {
                return false;
            }
            return around <= 3;
        } else {
            return around == 3;
        }

    }

    private static boolean checkCell(int x, int y) {
        if (!existCell(x, y)) {
            if (x < 0) x = cellCount - 1;
            if (x >= cellCount) x = 0;
            if (y < 0) y = cellCount - 1;
            if (y >= cellCount) y = 0;

        }
        return cells[x][y];
    }

    private static boolean existCell(int x, int y) {
        return ((x < 0) && (x >= cellCount) && (y < 0) && (y >= cellCount));
    }

    private static boolean checkCells() {
        for (boolean[] cells : cells) {
            for (boolean cell : cells) {
                if (cell) return true;
            }
        }
        return false;
    }

}