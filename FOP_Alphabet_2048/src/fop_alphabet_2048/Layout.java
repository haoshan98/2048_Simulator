package fop_alphabet_2048;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;
import java.util.Scanner;
import java.awt.event.*;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.System.exit;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;


public class Layout extends JPanel{
    private int row;
    private int col;
    private String name;  //user name
    private int score;
    private int checkScore;
    private int undoScore;
    private char[][] arr;
    private char[][] undo;
    private char[][] check;
    private int[] topScore;
    private String[] nameList;
    boolean end = false;
    String file; //get file name
    private Color gridColor = new Color(0xBBADA0);  //16777215
    private Color startColor = Color.CYAN;//new Color(0xFFEBCD);
    private Color emptyColor = Color.LIGHT_GRAY;//new Color(0xCDC1B4);
    boolean END = false;
    boolean won = false;
    
    
    public Layout(int row, int col, String file, String name){
        setPreferredSize(new Dimension(1200, 800));
        setBackground(Color.darkGray);  //0xFAF8EF  window color
        setFocusable(true);
        this.row=row;
        this.col=col;
        this.file = file;
        this.name = name;
        score = 0;
        topScore = new int[10];
        nameList = new String[10];
        System.out.println("[u - undo ; 0 - exit]]");
        readFile();
        arr = new char[row][col];
        undo = new char[row][col];
        check = new char[row][col];
        arr = sizeOfScale(arr);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if(!END)
                            moveUp();
                        break;
                    case KeyEvent.VK_DOWN:
                        if(!END)
                            moveDown();
                        break;
                    case KeyEvent.VK_LEFT:
                        if(!END)
                            moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(!END)
                            moveRight();
                        break;
                    case KeyEvent.VK_U:
                        if(!END)
                            undo();
                        break;
                }
                repaint();
            }
        });
    }
    
    public static synchronized void playSound(){ //String fileName
        //String directory = System.getProperty("user.dir") + "/snd/" + fileName;
        new Thread(new Runnable(){
            public void run(){
                try{
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("C:\\Users\\Hp\\Documents\\NetBeansProjects\\FOP_Alphabet_2048\\Jump2.wav"));
                    clip.open(inputStream);
                    clip.start();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
 
        drawGrid(g);
        playSound();
    }
    
    public void drawGrid(Graphics g){
        g.setColor(new Color(0xfd5555));
        g.fillRoundRect(200, 100, 499, 499, 15, 15);  //game board
        
        g.fillRoundRect(800, 300, 195, 300, 15, 15);  //name board
        g.fillRoundRect(1000, 300, 100, 300, 15, 15); //score board
        StringBuilder f = new StringBuilder();  //title of board
        f.append("Top 10 high score for ");
        f.append(Integer.toString(row));
        f.append(" X ");
        f.append(Integer.toString(col));
        String scoreBoard = f.toString();
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString(scoreBoard,800,295);
        
        g.setColor(Color.white);
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        g.drawString("Player : ", 800, 80);
        g.drawString(name, 920, 80);
        g.drawString("Your score : ", 800, 130);
        g.drawString(Integer.toString(score), 1000, 130);
        
        for(int i=1; i<=10; i++){  //display 10 high scores
            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.PLAIN, 20));
            g.drawString(Integer.toString(i), 810, 315 + 25*i);
            g.drawString(nameList[i-1],835, 315 + 25*i);
            g.drawString(Integer.toString(topScore[i-1]), 1020,310 + 25*i);
        }
        g.setColor(Color.white);
        g.drawString("Press [u] for undo",200,650);
        if (!end) {
            for (int r = 0; r < row; r++) {
                for (int c = 0; c < col; c++) {
                    if (arr[r][c] == '-') {
                        g.setColor(emptyColor);
                        int p, q=8, l=2;  //p = size; q = border
                        if(row>col){
                            p = row;
                        }else{
                            p = col;
                        }
                        //g.fillRoundRect(215 + c * 121, 115 + r * 121, 106, 106, 7, 7);
                        g.fillRoundRect(215-(15-q) + c * ((499-q)/p), 115-(15-q)+ r * ((499-q)/p), ((499-q)/p)-q, ((499-q)/p)-q, l, l);
                    } else {
                        drawTile(g, r, c);
                    }
                }
            }
            if(won){
                g.setFont(new Font("SansSerif", Font.BOLD, 40));
                g.setColor(Color.cyan);
                g.drawString("You Made It!", 800, 220);
            }
        } else {
            g.setColor(startColor);
            g.fillRoundRect(215, 115, 469, 469, 7, 7);
 
            //g.setColor(gridColor.darker());
            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 128));
            g.drawString("2048", 310, 270);
 
            g.setFont(new Font("SansSerif", Font.BOLD, 50));
 
            if (end) {  
                g.drawString("Game Over", 310, 400);
            } 
        }
    }
    
    
    
    public void drawTile(Graphics g, int r, int c) {
        //int value = tiles[r][c].getValue();
        char value= (arr[r][c]);
 
        //g.setColor(colorTable[(int)value - 65]);
        int p, q=8, l=2, f;  //p = size; q = border; f = font
        double a=0, b=0;  //(a,b)
        if(row>col){
            p = row;
            
        }else{
            p = col;
        }
        //g.fillRoundRect(215 + c * 121, 115 + r * 121, 106, 106, 7, 7);
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toString(value));
        sb.append(Character.toString(value));
        sb.append(Integer.toString(p));
        sb.append("e");
        sb.append(Character.toString(value));
        sb.append("a");
        //sb.toString();
        String HexColor = sb.toString();
        int co = (int)(((int)value - 64) * 800 % 255 * 750 % Math.pow(256, 2) * 900 % Math.pow(256, 3));  //color of grid
        g.setColor(new Color(co));
        g.fillRoundRect(215-(15-q) + c * ((499-q)/p), 115-(15-q) + r * ((499-q)/p), ((499-q)/p)-q, ((499-q)/p)-q, l, l);
        String s = String.valueOf(value);
 
        //g.setColor(new Color(Integer.parseInt(HexColor, 16)));
       
        FontMetrics fm = g.getFontMetrics();
        int asc = fm.getAscent();
        int dec = fm.getDescent();
        
        if(p>6){
            f = 50;
            a = 0.10; b = 0.45;
        }else if(p==6){
            f = 60;
            a = 0.15; b = 0.55;
        }else if(p==5){
            f = 80;
            a = 0.15; b = 0.70;
        }else if(p==4){
            f = 90;
            a = 0.2; b = 0.85;
        }else{
            f = 130;
            a = 0.25; b = 1.15;
        }
        
        int x = 215-(15-q) + c * ((499-q)/p) + (int)(106*a);  
        int y = 115-(15-q) + r * ((499-q)/p) + (int)(106*b);  
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, f)); 
            g.drawString(s, x, y);
    }
 
    public void readFile(){  //read to display top ten score
        try{  //read from
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            try{
                while(true){
                    for(int i=1; i<=10; i++){
                        String x = in.readUTF();
                        nameList[i-1] = x;
                        int y = in.readInt();
                        System.out.print(i+". "+x+"\t"+y+"\n");
                        topScore[i-1] = y;
                    }
                }
            }catch(EOFException e){}
            
            System.out.println();
            in.close();
        }catch(FileNotFoundException e){
            System.out.println("File was not found");
        }catch(IOException e){
            System.out.println("Problem with file input");
        }
    }
    
    public  char[][] sizeOfScale(char[][]arr){
        Random r = new Random();
        int place1 = r.nextInt(row);
        int place2 = r.nextInt(col);
        int place4 = r.nextInt(row);
        int place5 = r.nextInt(col);
        //String [][] arr = new String[row][col];
        for(int i=0; i<(row); i++){  //random generate first two 
            for(int j=0; j<(col); j++){
                arr[i][j] = '-';
                if(place1 !=place4){
                    if(place1%2==0 || place5%2==0){
                        arr[place1][place2]='A';
                        arr[place4][place5]='B';
                    }else{
                        arr[place1][place2]='A';
                        arr[place4][place5]='A';
                    }
                    System.out.print(arr[i][j]);
                }
            }
            System.out.println();
        }
        return arr;
    }
    
        
     public  void moveLeft(){
         saveCheck();
         //if got '-', exchange to move left
         for(int i=0; i<row; i++){
            for(int j=0; j<col-1; j++){
                for(int k = 0; k <col-1; k++)
                if(arr[i][k]=='-'){
                    char temp = arr[i][k+1];
                    arr[i][k+1] = arr[i][k];
                    arr[i][k] = temp;
                }
            }
        }
        //if same but not '-', combine
        for(int i=0; i<row; i++){
            for(int j=0; j<col-1; j++){
                if(arr[i][j]==arr[i][j+1] && arr[i][j] != '-'){
                    arr[i][j]++;
                    score += ((int)arr[i][j] -64);  //sum up the score
                    arr[i][j+1] = '-';
                }
                
            }
        }
        //move all to left
        for(int i=0; i<row; i++){
            for(int j=0; j<col-1; j++){
                for(int k = 0; k <col-1; k++){  //bubble sort
                    if(arr[i][k]=='-'){
                        char temp = arr[i][k+1];
                        arr[i][k+1] = arr[i][k];
                        arr[i][k] = temp;
                    }
                }
            }
        }
        newNumber();  
                
     }
     
     public void moveRight(){
         saveCheck();
         for(int i=row-1; i>=0; i--){
            for(int j=col-1; j>0; j--){
                for(int k =col-1; k>0; k--){
                    if(arr[i][k]=='-'){
                        char temp = arr[i][k-1];
                        arr[i][k-1] = arr[i][k];
                        arr[i][k] = temp;
                    }
                }
            }
        }
        
        for(int i=row-1; i>=0; i--){
            for(int j=col-1; j>0; j--){
                if(arr[i][j]==arr[i][j-1] && arr[i][j] != '-'){
                    arr[i][j]++;
                    score += ((int)arr[i][j] -64);
                    arr[i][j-1] = '-';
                }
                
            }
        }
        
        for(int i=row-1; i>=0; i--){
            for(int j=col-1; j>0; j--){
                for(int k =col-1; k>0; k--){
                    if(arr[i][k]=='-'){
                        char temp = arr[i][k-1];
                        arr[i][k-1] = arr[i][k];
                        arr[i][k] = temp;
                    }
                }
            }
        }
        newNumber();
                
     }
     
     public void moveUp(){
         saveCheck();
         for(int i=0; i<col; i++){
            for(int j=0; j<row; j++){
                for(int k = 0; k <row-1; k++)
                if(arr[k][i]=='-'){
                    char temp = arr[k+1][i];
                    arr[k+1][i] = arr[k][i];
                    arr[k][i] = temp;
                }
            }
        }
        
        for(int i=0; i<col; i++){
            for(int j=0; j<row-1; j++){
                if(arr[j][i]==arr[j+1][i] && arr[j][i] != '-'){
                    arr[j][i]++;
                    score += ((int)arr[j][i] -64);
                    arr[j+1][i] = '-';
                }
            }
        }
        
        for(int i=0; i<col; i++){
            for(int j=0; j<row; j++){
                for(int k = 0; k <row-1; k++){
                    if(arr[k][i]=='-'){
                        char temp = arr[k+1][i];
                        arr[k+1][i] = arr[k][i];
                        arr[k][i] = temp;
                    }
                }
            }
        }
        newNumber();
     }
     
     public void moveDown(){
         saveCheck();
         for(int i=col-1; i>=0; i--){
            for(int j=row-1; j>0; j--){
                for(int k =row-1; k>0; k--){
                    if(arr[k][i]=='-'){
                        char temp = arr[k-1][i];
                        arr[k-1][i] = arr[k][i];
                        arr[k][i] = temp;
                    }
                }
            }
        }
        
        for(int i=col-1; i>=0; i--){
            for(int j=row-1; j>0; j--){
                if(arr[j][i]==arr[j-1][i] && arr[j][i] != '-'){
                    arr[j][i]++;
                    score += ((int)arr[j][i] -64);
                    arr[j-1][i] = '-';
                }
            }
        }
        
        for(int i=col-1; i>=0; i--){
            for(int j=row-1; j>0; j--){
                for(int k =row-1; k>0; k--){
                    if(arr[k][i]=='-'){
                        char temp = arr[k-1][i];
                        arr[k-1][i] = arr[k][i];
                        arr[k][i] = temp;
                    }
                }
            }
        }
        newNumber();
     }
     
     public void newNumber(){  //generate new num(char) only when still got space
         boolean move = false;  
         for(int i=0; i<row; i++){
             for(int j=0; j<col; j++){
                 if(check[i][j] != arr[i][j]){
                     move = true;
                     for(int m=0; m<row; m++){
                         for(int n=0; n<col; n++){
                             undo[m][n] = check[m][n];  //for undo
                         }
                     }
                     undoScore = checkScore;  
                     break;
                 }
             }
             if(move)
                 break;
         }
         
            boolean hasSpace = false;
            for(int i=0; i<row; i++){
                for(int j=0; j<col; j++){
                    if(arr[i][j]=='-'){                       
                        hasSpace = true;
                        break;
                    }
                 }
                if(hasSpace){
                    break;
                }
            }
            
            if(move && hasSpace)
                rand();
            else if(!hasSpace){
                boolean hasMove = hasMove();
                if(!hasMove)
                {
                    end = true;
                }
            }
            
            //display won, but still can continue the game
            for(int i=0; i<row; i++){
                for(int j=0; j<col; j++){
                    if(arr[i][j]=='K'){
                        won = true;
                        break;
                    }
                }
            }
         
         for(int i=0; i<row; i++){
             for(int j=0; j<col; j++){
                 System.out.print(arr[i][j]);
             }
             System.out.println();
         }
         System.out.println("Score : "+score);
         if(end){
             System.out.println("Game over");
             
             try{  //write score to binary file
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
               int position = 10,temp1=0, temp2; String temp1n, temp2n;
               //determine position
                for(int i=topScore.length-1; i>=0; i--){
                   if(score>topScore[i]){
                        position = i;
                    }else{
                       break;
                   }
                }
              //rearrange
              temp1 = score;
              for(int i=position; i<10; i++){
                  temp2 = topScore[i];
                  topScore[i] = temp1;
                  temp1 = temp2;
                  //System.out.printf("score[%d] = %d     temp1=%d   temp2=%d\n",i,topScore[i], temp1, temp2);
              }                
              temp1n = name;
              for(int i=position; i<10; i++){
                  temp2n = nameList[i];
                  nameList[i] = temp1n;
                  temp1n = temp2n;
              }
              //write to
              for(int i=0; i<topScore.length; i++){
                   out.writeUTF(nameList[i]);
                   out.writeInt(topScore[i]);
               }
                    
                    out.close();
                    System.out.println("Score recorded");
                    //System.out.println("Successfully write score to the binary file");

            }catch(IOException e){
                System.out.println("Problem with file output");
            }
        
         }
         
         if(end)
             END = true;
         
     }
     
     public void rand(){
         Random r = new Random();
         int place3 = r.nextInt(row);
         int place4 = r.nextInt(col);
         if(arr[place3][place4]!='-'){
             rand();
         }else if(place3%2==0 && score>=20){
             arr[place3][place4] = 'B';
         }else{
             arr[place3][place4] = 'A';
         }
     }
     
     public void saveCheck(){
         for(int i=0; i<row; i++){
             for(int j=0; j<col; j++){
                 check[i][j] = arr[i][j];
             }
         }
         checkScore = score;
     }
     
     public void undo(){
         for(int m=0; m<row; m++){
            for(int n=0; n<col; n++){
                arr[m][n] = undo[m][n];
                System.out.print(arr[m][n]);
            }
             System.out.println();
         }
         score = undoScore;
         System.out.println("Score : "+score);
     }
     
     public boolean hasMove(){  //still can add(combine)
         boolean hasMove = false;
         for(int i=0; i<row; i++){
             for(int j=0; j<col; j++){
                 if(i==0 && j==0){
                    if(arr[i][j]==arr[i+1][j] || arr[i][j]==arr[i][j+1]){  //down & right
                            hasMove = true;
                    }
                }else if(i==0 && j==col-1){
                    if(arr[i][j]==arr[i+1][j] || arr[i][j]==arr[i][j-1]){  //down & left
                            hasMove = true;
                    }
                }else if(i==row-1 && j==0){
                    if(arr[i][j]==arr[i-1][j] || arr[i][j]==arr[i][j+1]){  
                            hasMove = true;
                    }
                }else if(i==row-1 && j==col-1){
                    if(arr[i][j]==arr[i-1][j] || arr[i][j]==arr[i][j-1]){  
                            hasMove = true;
                    }
                }else if(j==0){  //left edge
                    if(arr[i][j]==arr[i-1][j] || arr[i][j]==arr[i][j+1] || arr[i][j]==arr[i+1][j]){  //up & right & down
                            hasMove = true;
                    }
                }else if(i==0){  //up
                    if(arr[i][j]==arr[i][j+1] || arr[i][j]==arr[i][j-1] ||arr[i][j]==arr[i+1][j]){  //right & left & down
                            hasMove = true;
                    }
                }else if(j==col-1){  //right
                    if(arr[i][j]==arr[i][j-1] ||arr[i][j]==arr[i+1][j]  || arr[i][j]==arr[i-1][j]){  
                            hasMove = true;
                    }
                }else if(i==row-1){  //down
                    if(arr[i][j]==arr[i][j-1] || arr[i][j]==arr[i-1][j] || arr[i][j]==arr[i][j+1]){  
                            hasMove = true;
                    }
                }else{  //middle
                    if(arr[i][j]==arr[i][j-1] || arr[i][j]==arr[i-1][j] || arr[i][j]==arr[i][j+1] || arr[i][j]==arr[i+1][j]){  
                            hasMove = true;
                    }
                }
                 if(hasMove)
                     break;
            }
             if(hasMove)
                 break;
         }
         return hasMove;
     }
     
    
}



