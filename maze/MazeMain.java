package maze;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MazeMain {

	public static void main(String[] args) {
		Maze maze = new Maze(0, 0, 4096 , 2160 );
		maze.generatePath();
		BufferedImage bi = maze.generateImage(3);
		
		JFrame frame = new JFrame();
		frame.setTitle("Maze Demo Using HashMap<K, V>");
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				draw(g);
			}
			public void draw(Graphics g) {
				g.drawImage(bi, 0, 0, null);
			}
		};
		
		frame.add(panel);
		
		frame.setVisible(true);
		
	}

}
