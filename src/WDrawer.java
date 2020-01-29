import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Stack;


public class WDrawer extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final int OFFSET = 10;
    private static final int SIZE = 10;
    private Panel panel;
    private RobotController controller;

    private double maxX, maxY;
    private double scaleX, scaleY;

    /**
     * Construct the WindowTSP and draw the cities to the screen.
     *
     * @param robotController the cities to draw to the screen
     */
    public WDrawer(RobotController robotController) {
        this.controller = robotController;
        setScale();
        panel = createPanel();
        setWindowProperties();
    }

    /**
     * Draw a path through the maze.
     *
     * @param
     */
    public void draw() {
        panel.repaint();
    }

    private Panel createPanel() {
        Panel panel = new Panel();
        Container cp = getContentPane();
        cp.add(panel);
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        return panel;
    }

    private void setWindowProperties() {
        int sWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2;
        int sHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2;
        int x = sWidth - (WIDTH / 2);
        int y = sHeight - (HEIGHT / 2);
        setLocation(x, y);
        setResizable(false);
        pack();
        setTitle("Path Finding Sim");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Sets the scale for the drawing so that all the cities
     * are drawn inside the window.
     */
    private void setScale() {
/*        for (Pair<Point2D, Float> c : controller.noise_arr) {
            if (c.getKey().getX()  > maxX) {
                maxX =  (c.getKey().getX() );
            }
            if (c.getKey().getY() > maxY) {
                maxY =  (c.getKey().getY());
            }
        }*/
        maxX = 1;
        maxY = 1;
        scaleX = ((double) maxX) / ((double) WIDTH);
        //     scaleX =500 ;
        scaleY = ((double) maxY) / ((double) HEIGHT);
        //     scaleY = 500;
    }

    /**
     * All the drawing is done here.
     */
    private class Panel extends JPanel {

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            drawFindPath((Graphics2D) graphics);
            //        drawNoise((Graphics2D) graphics);
            //      paintTravelingSalesman((Graphics2D)graphics);
        }


        private void drawFindPath(Graphics2D graphics) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawNoise(graphics);
            drawPath(graphics);
            drawBorders(graphics);
        }

        private void drawBorders(Graphics2D graphics) {
            graphics.setColor(Color.BLACK);
            int x_left = 0;
            int y_left = 0;
            int x1_left = 0;
            int y1_left = (int) (1 * scaleY);
            int x_right = (int) (1 * scaleX);
            int y_right = 0;
            graphics.drawLine(x_left, y_left, x_right, y_right);
            graphics.drawLine(x_left, y_left, x1_left, y1_left);
            graphics.drawLine(x1_left, y1_left, x_right, y1_left);
            graphics.drawLine(x_right, y1_left, x_right, y_right);


        }


        private void drawPath(Graphics2D graphics) {

            graphics.setColor(Color.darkGray);
            ArrayList<Point2D> array = controller.elite.get(0).getThisPath();

            for (int i = 8; i < controller.elite.get(0).ind; i++) {
                int x1 = (int) (array.get(i - 1).getX() / scaleX);
                int y1 = (int) (array.get(i - 1).getY() / scaleY);
                int x2 = (int) (array.get(i).getX() / scaleX);
                int y2 = (int) (array.get(i).getY() / scaleY);
                graphics.drawLine(x1, y1, x2, y2);
            }


            controller.elite.get(0).writeData();
        }

        private void drawExpectedPath(Graphics2D graphics) {
            graphics.setColor(Color.blue);
            Stack<Point2D> array = controller.elite.get(0).true_path;
            for (int i = 1; i < array.size(); i++) {
                int x1 = (int) (array.get(i - 1).getX() / scaleX) + 200;
                int y1 = (int) (array.get(i - 1).getY() / scaleY) + 200;
                int x2 = (int) (array.get(i).getX() / scaleX) + 200;
                int y2 = (int) (array.get(i).getY() / scaleY) + 200;
                graphics.drawLine(x1, y1, x2, y2);
            }
        }

        private void drawNoise(Graphics2D graphics) {
            graphics.setColor(Color.darkGray);
            for (Pair<Point2D, Float> c : controller.noise_arr) {
                int x = (int) ((c.getKey().getX()) / scaleX - (c.getValue() / scaleX / 2));
                int y = (int) ((c.getKey().getY()) / scaleY);
                graphics.fillOval(x, y, (int) (c.getValue() / scaleX), (int) (c.getValue() / scaleY));
            }

//            graphics.fillOval(x,y,10,10);
        }

        private void drawCoords(Graphics2D graphics) {
            graphics.setColor(Color.darkGray);
            ArrayList<Point2D> array = controller.elite.get(0).getThisPath();
            for (int i = 0; i < controller.elite.get(0).ind; i++) {
                int x = (int) (array.get(i).getX() * scaleX);
                int y = (int) (array.get(i).getY() * scaleY);
                String string = "(" + x + ";" + y + " )";
                int fontOffset = getFontMetrics(graphics.getFont()).stringWidth(string) / 2 - 2;
                graphics.drawString(string, x - fontOffset, y - 3);
            }
        }


    }
}
