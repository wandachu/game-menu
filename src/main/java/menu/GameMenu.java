package menu;

import game.Breakout;
import game.Destructo;
import game.Maze;
import game.Skunk;
import game.Snake;
import game.Sokoban;
import game.Tetris;
import game.XEd;
import graph.AbstractGame;
import graph.Window;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


public class GameMenu extends Window {

  public static final int W = 1000;
  public static final int H = 800;
  public static Color bkColor = new Color(200, 255, 200);
  public static AbstractGame theGame = null;


  public GameMenu() {
    super("GameMenu", W, H);
  }

  public static void main(String[] args) {
    Window.PANEL = new GameMenu();
    Window.launch();
  }

  public void paintComponent(Graphics g) {
    if (theGame != null) {
      theGame.paintComponent(g);
      return;
    }
    g.setColor(bkColor);
    g.fillRect(0, 0, 5000, 5000);
    g.setColor(Color.BLACK);
    int x = 350, y = 200, increment = 25;
    g.setFont(new Font("Verdana", Font.PLAIN, 20));
    g.drawString("Games - Press escape to return to this menu", x, y); y += increment * 2;
    g.setFont(new Font("Arial ", Font.PLAIN, 15));
    g.drawString("(B)reakout - Press B to play Breakout", x, y); y += increment;
    g.drawString("(D)estructo - Press D to play Destructo", x, y); y += increment;
    g.drawString("(E)xpressionEditor - Press E to play XEd", x, y); y += increment;
    g.drawString("(M)aze - Press M to play Maze", x, y); y += increment;
    g.drawString("S(n)ake - Press N to play Snake", x, y); y += increment;
    g.drawString("S(k)unk - Press K to play Skunk", x, y); y += increment;
    g.drawString("(S)okoban - Press S to play Sokoban", x, y); y += increment;
    g.drawString("(T)etris - Press T to play Tetris", x, y);
  }

  @Override
  public void keyPressed(KeyEvent ke) {
    char ch = ke.getKeyChar();
    if (ch == 27) {stopGame();} // 27 is escape
    if (theGame != null) {theGame.keyPressed(ke); return;}
    if (ch == 'B' || ch == 'b') {theGame = new Breakout();}
    if (ch == 'D' || ch == 'd') {theGame = new Destructo();}
    if (ch == 'E' || ch == 'e') {theGame = new XEd();}
    if (ch == 'M' || ch == 'm') {theGame = new Maze();}
    if (ch == 'N' || ch == 'n') {theGame = new Snake();}
    if (ch == 'S' || ch == 's') {theGame = new Sokoban();}
    if (ch == 'K' || ch == 'k') {theGame = new Skunk();}
    if (ch == 'T' || ch == 't') {theGame = new Tetris();}
    if (theGame != null) {theGame.panel = PANEL;}
    repaint();
  }

  public static void stopGame() {
    if (theGame != null) {
      theGame.endGame();
      theGame.panel = null;
      theGame = null;
    }
  }

  @Override
  public void mouseClicked(MouseEvent me){if (theGame != null) {theGame.mouseClicked(me);}}
  @Override
  public void mousePressed(MouseEvent me) {if (theGame != null) {theGame.mousePressed(me);}}
  @Override
  public void mouseReleased(MouseEvent me) {if (theGame != null) {theGame.mouseReleased(me);}}
  @Override
  public void mouseEntered(MouseEvent me) {if (theGame != null) {theGame.mouseEntered(me);}}
  @Override
  public void mouseExited(MouseEvent me) {if (theGame != null) {theGame.mouseExited(me);}}
  @Override
  public void mouseDragged(MouseEvent me) {if (theGame != null) {theGame.mouseDragged(me);}}
  @Override
  public void mouseMoved(MouseEvent me) {if (theGame != null) {theGame.mouseMoved(me);}}
  @Override
  public void keyTyped(KeyEvent ke) {if (theGame != null) {theGame.keyTyped(ke);}}
  @Override
  public void keyReleased(KeyEvent ke) {if (theGame != null) {theGame.keyReleased(ke);}}
}
