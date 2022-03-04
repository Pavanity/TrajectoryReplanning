package app;

import java.io.Serializable;
import java.util.Random;

public class GridMap implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -184435062536246970L;
	
	String[][] grid;
	int width;
	int height;
	int agentx;
	int agenty;
	int targx;
	int targy;
	
	public GridMap(int width, int height) {
		this.width = width;
		this.height = height;
		this.grid = new String[height][width];
		fillGrid();
	}
	
	private void fillGrid() {
		Random rand = new Random();
		for (int i=0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				if (rand.nextInt(10) > 6) {
					this.grid[i][j] = "B";
				}
			}
		}
		int xagent = rand.nextInt(width);
		int yagent = rand.nextInt(height);
		while (grid[yagent][xagent]!= null && grid[yagent][xagent].equals("B")) {
			xagent = rand.nextInt(height);
			yagent = rand.nextInt(width);
		}
		grid[yagent][xagent] = "A";
		this.agentx = xagent;
		this.agenty = yagent;
		
		int xtarg = rand.nextInt(width);
		int ytarg = rand.nextInt(height);
		while (grid[ytarg][xtarg]!= null && (grid[ytarg][xtarg].equals("B") || grid[ytarg][xtarg].equals("A"))) {
			xtarg = rand.nextInt(width);
			ytarg = rand.nextInt(height);
		}
		grid[ytarg][xtarg] = "T";
		this.targx = xtarg;
		this.targy = ytarg;
	}
	
	public void printGrid() {
		for (int i=0; i < this.width; i++) {
			for (int j=0; j < this.height; j++) {
				if (grid[i][j] == null) System.out.print("  ");
				else System.out.print(grid[i][j] + " ");
				if (j==width-1) System.out.print(i+1+"");
			}
			System.out.println();
		}
		for (int j = 1; j < this.height+1; j++) {
			System.out.print(j + " ");
		}
		System.out.println();
		System.out.println();
	}
}
