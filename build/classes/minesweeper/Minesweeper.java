/*
Authors: Maxime Mahdavian and Omar Merhi
*/ 
package minesweeper;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
//import static minesweeper.MainWindow.gamesWon;


public class Minesweeper extends JFrame implements KeyListener{
    
    protected static int defaultSize = 8;
    protected static int defaultMineCount = 10;
    protected static int defaultTextSize = 60;
    protected boolean ctrlPressed = false;
    protected int flagCount = 0;
    protected int filledCount = 0;
    protected int[][] board = new int[defaultSize][defaultSize];
    protected JButton[][] buttonBoard;
    
    public Minesweeper(){
        super("Minesweeper");
        
        setFocusable(true);
        addKeyListener(this);
        
        setSize(1000,1000);
        
        
        Container pane = getContentPane();
        pane.setLayout(new GridLayout(defaultSize,defaultSize));
        
        
        buttonBoard = new JButton[defaultSize][defaultSize];
        for(int i = 0; i < defaultSize; i++){
            for(int j = 0; j < defaultSize; j++){
                
                buttonBoard[i][j] = new JButton();
                buttonBoard[i][j].setFont(new Font("Tahoma", Font.BOLD, defaultTextSize));
                buttonBoard[i][j].addKeyListener(this);
                
                final int x = i;
                final int y = j;
                
                
                buttonBoard[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        if(ctrlPressed){
                            if(board[x][y] < 19 ){
                                board[x][y] += 20;                                
                                buttonBoard[x][y].setText("F");
                                flagCount++;
                                isGameFinished();
                            }
                           
                            else{
                                board[x][y] -=20;
                                buttonBoard[x][y].setText("");
                                flagCount--;
                            }
                        }
                        else{
                            if(board[x][y] == -1){
                                try{
                                File wavFile = new File("sound/explosion.wav");
                                AudioInputStream ais = AudioSystem.getAudioInputStream(wavFile);
                                Clip clip = AudioSystem.getClip();
                                clip.open(ais);
                                clip.start();
                                } catch (UnsupportedAudioFileException ex) {
                                    Logger.getLogger(Minesweeper.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(Minesweeper.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (LineUnavailableException ex) {
                                    Logger.getLogger(Minesweeper.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                
                                gameWon(false);
                                buttonBoard[x][y].setEnabled(false);
                        
                            }
                           
                            else if (board[x][y] > 0 && board[x][y] < 19){
                                buttonBoard[x][y].setEnabled(false);
                                filledCount++;
                                isGameFinished();
                                buttonBoard[x][y].setText(String.valueOf(board[x][y]));
                            }
                            else if (board[x][y] == 0){
                                checkSurrounding(x, y);
                            }
                        }
                            
                    }
                } );
                pane.add(buttonBoard[i][j]);
                
            }
            addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                MainWindow.newGameButton.setEnabled(true);
                MainWindow.messageLabel.setText("Press New Game to start");
            }   
            });
        }
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    protected void initializeBoard(){
        
        Random rand = new Random();
        
        int z = 0;
        int x = rand.nextInt(defaultSize);
        int y = rand.nextInt(defaultSize);
        while(z < defaultMineCount){
            
            if(board[x][y] != -1){
                board[x][y] = -1;
                z++;
                initializeSurrounding(x,y);
    
            }
            x = rand.nextInt(defaultSize);
            y = rand.nextInt(defaultSize);
        }
         
    } 
    
    
    protected void initializeSurrounding(int x, int y){
        for(int i = x-1; i < x+2; i++){
            for(int j = y-1; j < y+2; j++){
                if(i > -1 && j > -1 && j < defaultSize && i < defaultSize){
                    if(board[i][j] != -1){
                        board[i][j]++;
                    }
                }
            }
        }
    }
    
    protected void checkSurrounding(int x, int y){
        if(x == -1 || x == defaultSize || y == -1 || y == defaultSize || board[x][y] >= 19){
            return;
        }
        
        if(board[x][y] == 0 && buttonBoard[x][y].isEnabled()){
            buttonBoard[x][y].setEnabled(false);
            filledCount++;
            isGameFinished();
            checkSurrounding(x+1,y);
            checkSurrounding(x-1,y);
            checkSurrounding(x,y+1);
            checkSurrounding(x,y-1);
            checkSurrounding(x+1,y+1);
            checkSurrounding(x+1,y-1);
            checkSurrounding(x-1,y+1);
            checkSurrounding(x-1,y-1);
        }
        
        else if(board[x][y] > 0 && buttonBoard[x][y].isEnabled()){
            buttonBoard[x][y].setEnabled(false);
            buttonBoard[x][y].setText(String.valueOf(board[x][y]));
            filledCount++;
            isGameFinished();
            return;
        }
        
        else 
            return;
    }
    
    protected void isGameFinished(){
        if(filledCount == (defaultSize*defaultSize - defaultMineCount))
            gameWon(true);
    }
    
    protected void gameWon(boolean win){
        if(win){
            MainWindow.gamesWon++;
            MainWindow.messageLabel.setText("You won! Press New Game to play again");
            for(int i = 0; i < defaultSize; i++){
                for(int j = 0; j < defaultSize; j++){
                    buttonBoard[i][j].setEnabled(false);
                }
                
                    
            }
            MainWindow.gamesWonLabel.setText("Games Won: " + MainWindow.gamesWon);
        }
        else{
            for(int i = 0; i < defaultSize; i++){
                for(int j = 0; j < defaultSize; j++){
                    buttonBoard[i][j].setEnabled(false);
                    if(board[i][j] == -1){
                        buttonBoard[i][j].setText("*");
                    }
                    else if(board[i][j] > 0 && board[i][j] < 19)
                        buttonBoard[i][j].setText(String.valueOf(board[i][j]));
                    else if(board[i][j] > 19)
                        buttonBoard[i][j].setText("f");
                }
                
                    
            }
            MainWindow.gamesLost++;
            MainWindow.gamesLostLabel.setText("Games Lost: " + MainWindow.gamesLost);
            MainWindow.messageLabel.setText("You lost. Press New Game to play again.");
        }
        MainWindow.newGameButton.setEnabled(true);
    }

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    @Override
    public void keyPressed(KeyEvent ke) {
        ctrlPressed = ke.isControlDown();
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        ctrlPressed = ke.isControlDown();
    }
    
}
