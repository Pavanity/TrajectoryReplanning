package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

class State implements Comparable<State>{
	
	State treeP;
	int gvalue;
	int hvalue;
	int fvalue;
	int searchval;
	int x;
	int y;
	boolean visible;
	int cost;
	
	public State() {
		this.treeP = null;
		this.gvalue = 0;
		this.hvalue = 0;
		this.fvalue = 0;
		this.searchval = 0;
		this.visible = false;
		this.cost = 1;
	}
	
	public int getFvalue() {
		return fvalue;
	}
	
	public int getGvalue() {
		return gvalue;
	}

	@Override
	public int compareTo(State s) {
		return Comparator.comparingInt(State::getFvalue)
				.thenComparing(Comparator.comparing(State::getGvalue).reversed())
				.compare(this, s);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if(!(obj instanceof State)) {
			return false;
		}
		State s = (State) obj;
		
		return s.x == this.x && s.y == this.y;
	}
	
	@Override
	public String toString() {
		return "("+ x +", " + y + ")";
	}
}

public class Application {

	static GridMap grid = new GridMap(10, 10);
	static State[][] statemap;
	static MinHeap<State> stateQueue;
	static LinkedList<State> closedList;
	static State sgoal;
	static State sstart;
	static int counter;
	static int expansions;
	
	private static final String storeDir = "data";
	public static final String storeFile = "GridMaps.dat";
	
	private static ArrayList<GridMap> gridMaps;
	
	public static void main(String[] args) {
		
		try {
			readGridMaps();
		} catch(FileNotFoundException e) {
			gridMaps = generateGridMaps(50, 101, 101);
			writeGridMaps();
		} catch(IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 50; i++) {
			grid = gridMaps.get(i);
//			grid.printGrid();
//			repeatedA(false);
			repeatedAreverse();
		}
		
//		repeatedAreverse();
	}
	
	public static void repeatedA(boolean adaptive) {
		statemap = new State[grid.height][grid.width];
		if (adaptive) {
			closedList = new LinkedList<State>();
		}
		
		for (int i = 0; i < grid.height; i++) {
			for (int j = 0; j < grid.width; j++) {
				statemap[i][j] = new State();
				statemap[i][j].x = j;
				statemap[i][j].y = i;
				statemap[i][j].hvalue = Math.abs(j - grid.targx) + Math.abs(i - grid.targy); 
			}
		}
		sgoal = statemap[grid.targy][grid.targx];
		sgoal.x = grid.targx;
		sgoal.y = grid.targy;
		counter = 0;
		expansions = 0;
		while (grid.agentx != grid.targx || grid.agenty != grid.targy) {
			counter++;
			sgoal.gvalue = (grid.width * grid.height) * (grid.width * grid.height);
			sgoal.searchval = counter;
			
			sstart = statemap[grid.agenty][grid.agentx];
			sstart.x = grid.agentx;
			sstart.y = grid.agenty;
			sstart.gvalue = 0;
			sstart.searchval = counter;
//			sstart.hvalue = Math.abs(sstart.x - grid.targx) + Math.abs(sstart.y - grid.targy);
			sstart.fvalue = sstart.gvalue + sstart.hvalue;
			sstart.treeP = null;
			
			setVisibility();
			
			stateQueue = new MinHeap<State>();
			stateQueue.insert(sstart);
			computePath(adaptive);
			
			if (stateQueue.isEmpty()) {
				System.out.println("Cannot reach target");
				return;
			}
			
			if (adaptive) {
				while (true) {
					try {
						State s = closedList.removeFirst();
						s.hvalue = sgoal.gvalue - statemap[s.y][s.x].gvalue;
					}catch (NoSuchElementException e) {
						break;
					}
				}
			}
			
			String path = "Path from goal: ";
			State pathtree = sgoal;
			while (pathtree != null) {
//				System.out.println("in pathtree");
				path = path.concat("("+(pathtree.x+1)+", "+(pathtree.y+1)+"); ");
				pathtree = pathtree.treeP;
			}
			System.out.println(path);
			
			pathtree = sgoal;
			move(pathtree);
//			System.out.println(counter);
//			System.out.println("agentx = " + sstart.x);
//			System.out.println("agenty = " + sstart.y);
		}
//		grid.printGrid();
		System.out.println("Reached target.");
		System.out.println("agent x: " + grid.agentx + " agent y: " + grid.agenty + " targ x: " + grid.targx + " targ y: " + grid.targy);
		System.out.println("Total expansions: " + expansions);
	}
	
	public static void computePath(boolean adaptive) {
		while (!stateQueue.isEmpty() && sgoal.gvalue > stateQueue.peek().fvalue) {
			expansions++;
//			System.out.println("in compute path iteration:" + x);
			State s = stateQueue.extractMin();
			if (adaptive)
				closedList.add(s);
			State[] sActs = actions(s);
			for (int i = 0; i < 4; i++) {
				State a = sActs[i];
				if (a != null) {
					if (a.searchval < counter) {
						a.gvalue = (grid.width * grid.height) * (grid.width * grid.height);
						a.searchval = counter;
					}
					int cost = a.cost;
					if (a.gvalue > s.gvalue + cost) {
						a.gvalue = s.gvalue + cost;
//						a.hvalue = Math.abs(a.x - grid.targx) + Math.abs(a.y - grid.targy);
						a.treeP = s;
						stateQueue.remove(a);
						a.fvalue = a.gvalue + a.hvalue;
						stateQueue.insert(a);
//						System.out.println("enqueued" + a);
					}
				}
			}
		}
	}
	
	public static void repeatedAreverse() {
		statemap = new State[grid.height][grid.width];
		
		for (int i = 0; i < grid.height; i++) {
			for (int j = 0; j < grid.width; j++) {
				statemap[i][j] = new State();
				statemap[i][j].x = j;
				statemap[i][j].y = i;
			}
		}
		sgoal = statemap[grid.targy][grid.targx];
		sgoal.x = grid.targx;
		sgoal.y = grid.targy;
		counter = 0;
		expansions = 0;
		while (grid.agentx != grid.targx || grid.agenty != grid.targy) {
			counter++;
			
			sstart = statemap[grid.agenty][grid.agentx];
			sstart.x = grid.agentx;
			sstart.y = grid.agenty;
			sstart.gvalue = (grid.width * grid.height) * (grid.width * grid.height);
			sstart.searchval = counter;
			sstart.treeP = null;
			
			sgoal.gvalue = 0;
			sgoal.hvalue = Math.abs(sstart.x - grid.targx) + Math.abs(sstart.y - grid.targy);
			sgoal.searchval = counter;
			sgoal.fvalue = sgoal.gvalue + sgoal.hvalue;
			
			
			setVisibility();
			
			stateQueue = new MinHeap<State>();
			stateQueue.insert(sgoal);
			computePathReverse();
			
			if (stateQueue.isEmpty()) {
				System.out.println("Cannot reach target");
				return;
			}
			
			String path = "Path from target: ";
			State pathtree = sstart;
			while (pathtree != null) {
//				System.out.println("in pathtree");
				path = path.concat("("+(pathtree.x+1)+", "+(pathtree.y+1)+"); ");
				pathtree = pathtree.treeP;
			}
			System.out.println(path);
			
			pathtree = sstart;
			moveReverse(pathtree);
//			System.out.println(counter);
//			System.out.println("agentx = " + sstart.x);
//			System.out.println("agenty = " + sstart.y);
		}
		System.out.println("Reached target.");
		System.out.println("agent x: " + grid.agentx + " agent y: " + grid.agenty + " targ x: " + grid.targx + "targ y: " + grid.targy);
		System.out.println("Total expansions: " + expansions);
	}
	
	public static void computePathReverse() {
		while (!stateQueue.isEmpty() && sstart.gvalue > stateQueue.peek().fvalue) {
			expansions++;
//			System.out.println("in compute path iteration:" + x);
			State s = stateQueue.extractMin();
			State[] sActs = actions(s);
			for (int i = 0; i < 4; i++) {
				State a = sActs[i];
				if (a != null) {
					if (a.searchval < counter) {
						a.gvalue = (grid.width * grid.height) * (grid.width * grid.height);
						a.searchval = counter;
					}
					int cost = a.cost;
					if (a.gvalue > s.gvalue + cost) {
						a.gvalue = s.gvalue + cost;
						a.hvalue = Math.abs(a.x - grid.agentx) + Math.abs(a.y - grid.agenty);
						a.treeP = s;
						stateQueue.remove(a);
						a.fvalue = a.gvalue + a.hvalue;
						stateQueue.insert(a);
//						System.out.println("enqueued" + a);
					}
				}
			}
		}
	}
	
	
	
	
	public static int move(State tree) {
		if (tree.treeP == null) {
			return 1;
		}
		if (move(tree.treeP) == -1) {
			return -1;
		} 
		else {
			if (tree.visible && tree.cost > 1) {
				return -1;
			}
			else {
				grid.grid[tree.treeP.y][tree.treeP.x] = null;
				grid.grid[tree.y][tree.x] = "A";
				grid.agentx = tree.x;
				grid.agenty = tree.y;
				sstart = statemap[grid.agenty][grid.agentx];
//				sstart.x = tree.x;
//				sstart.y = tree.y;
				setVisibility();
				
//				grid.printGrid();
				return 1;
			}
		}
		
	}
	
	public static int moveReverse(State tree) {
		if (tree.treeP == null) {
			return -1;
		}
		if (tree.treeP.visible && tree.treeP.cost == 1) {
			grid.grid[tree.treeP.y][tree.treeP.x] = "A";
			grid.grid[tree.y][tree.x] = null;
			grid.agentx = tree.treeP.x;
			grid.agenty = tree.treeP.y;
			sstart = statemap[grid.agenty][grid.agentx];
			setVisibility();
			
//			grid.printGrid();
			return moveReverse(tree.treeP);
		}
		else {
			return -1;
		}
	}
	
	public static void setVisibility() {
		if (sstart.x-1 >= 0) {
			statemap[sstart.y][sstart.x-1].visible = true;
			if (grid.grid[sstart.y][sstart.x-1] != null && grid.grid[sstart.y][sstart.x-1].equals("B")) {
				statemap[sstart.y][sstart.x-1].cost = (grid.width * grid.height) * (grid.width * grid.height);
			}
		}
		if (sstart.x+1 < grid.width) {
			statemap[sstart.y][sstart.x+1].visible = true;
			if (grid.grid[sstart.y][sstart.x+1] != null && grid.grid[sstart.y][sstart.x+1].equals("B")) {
				statemap[sstart.y][sstart.x+1].cost = (grid.width * grid.height) * (grid.width * grid.height);
			}
		}
		if (sstart.y-1 >= 0) {
			statemap[sstart.y-1][sstart.x].visible = true;
			if (grid.grid[sstart.y-1][sstart.x] != null && grid.grid[sstart.y-1][sstart.x].equals("B")) {
				statemap[sstart.y-1][sstart.x].cost = (grid.width * grid.height) * (grid.width * grid.height);
			}
		}
		if (sstart.y+1 < grid.height) {
			statemap[sstart.y+1][sstart.x].visible = true;
			if (grid.grid[sstart.y+1][sstart.x] != null && grid.grid[sstart.y+1][sstart.x].equals("B")) {
				statemap[sstart.y+1][sstart.x].cost = (grid.width * grid.height) * (grid.width * grid.height);
			}
		}
	}
	
	public static State[] actions(State s) {
		State[] acts = new State[4];
		if (s.x-1 >= 0) acts[0] = statemap[s.y][s.x-1];
		if (s.x+1 < grid.width) acts[1] = statemap[s.y][s.x+1];
		if (s.y-1 >= 0) acts[2] = statemap[s.y-1][s.x];
		if (s.y+1 < grid.height) acts[3] = statemap[s.y+1][s.x];
		return acts;
	}
	
	public static ArrayList<GridMap> generateGridMaps(int noOfMaps, int width, int height) {
		ArrayList<GridMap> maps = new ArrayList<GridMap>();
		for (int i = 0; i < noOfMaps; i++) {
			GridMap map = new GridMap(width, height);
			maps.add(map);
		}
		return maps;
	}
	
	@SuppressWarnings("unchecked")
	public static void readGridMaps() throws FileNotFoundException, IOException {
		FileInputStream fs = new FileInputStream(storeDir + File.separator + storeFile);
		ObjectInputStream ois = new ObjectInputStream(fs);
		try {
			gridMaps = (ArrayList<GridMap>) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		fs.close();
		ois.close();
			
			
	}
	
	public static void writeGridMaps() {
		try {
			FileOutputStream fs = new FileOutputStream(storeDir + File.separator + storeFile, false);
			ObjectOutputStream oos = new ObjectOutputStream(fs);
			oos.writeObject(gridMaps);
			fs.close();
			oos.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
