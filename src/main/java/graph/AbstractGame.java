package graph;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public abstract class AbstractGame implements MouseListener, MouseMotionListener, KeyListener {
  public String title;
  public JPanel panel;

  public AbstractGame(String title) {
    this.title = title;
  }

  public abstract void paintComponent(Graphics g);

  public abstract void endGame(); // if use timer, shut off timer.

  public void repaint() {
    if (panel != null) {panel.repaint();}
  }
}
