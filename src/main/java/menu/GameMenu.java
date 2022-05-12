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
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class GameMenu extends Window {

  public static final int W = 1000;
  public static final int H = 800;
  public static final String BACKGROUND_IMAGE_PATH = "src/main/resources/background.png";
  public static AbstractGame theGame = null;
  public static Image i = loadImage();

  public static void main(String[] args) {
    Window.PANEL = new GameMenu();
    Window.launch();
  }

  private static Image loadImage() {
    File input = new File(BACKGROUND_IMAGE_PATH.replaceAll("[/\\\\]", "\\" + File.separator));
    Image i;
    try {
      i = ImageIO.read(input);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return i;
  }

  public GameMenu() {
    super("GameMenu", W, H);
  }

  public void paintComponent(Graphics g) {
    if (theGame != null) {
      theGame.paintComponent(g);
      return;
    }
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, 5000, 5000);
    g.drawImage(i, 0, 0, this);
    g.setColor(Color.RED);
    int x = 220, y = 100, increment = 35;
    g.setFont(new Font("Impact", Font.PLAIN, 30));
    g.drawString("Games - Press escape to return to this menu", x, y); y += increment * 3 / 2;
    g.setFont(new Font("Georgia", Font.PLAIN, 25));
    x = 280;
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
