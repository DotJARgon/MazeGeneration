package maze;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public final class Maze {
	private final int x, y; //start and end points, I use short names cause i like short code
	private final HashMap<Node, Node> map;
	private final int W, H;
	public Maze(final int x, final int y, final int w, final int h) { //start maze x, y, then the width and height
		this.x = x;
		this.y = y;
		
		this.W = w;
		this.H = h;
		
		this.map = new HashMap<>();
	}
	
	
	public BufferedImage generateImage() {
		BufferedImage bi = new BufferedImage(W*10, H*10, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, W*10, H*10);
		g.setColor(Color.RED);
		for(Node n : map.keySet()) {
			if(!n.hasL()) {
				g.fillRect(n.l.x*10+1, n.y*10+1, 18, 8);
			}
			if(!n.hasR()) {
				g.fillRect(n.x*10+1, n.y*10+1, 18, 8);
			}
			if(!n.hasU()) {
				g.fillRect(n.x*10+1, n.y*10+1, 8, 18);
			}
			if(!n.hasD()) {
				g.fillRect(n.x*10+1, n.d.y*10+1, 8, 18);
			}
		}
		return bi;
	}
	public BufferedImage generateImage(final int thickness) {
		
		if(thickness < 3) return generateImage(3);
		
		BufferedImage bi = new BufferedImage(W*thickness, H*thickness, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, W*thickness, H*thickness);
		g.setColor(Color.RED);
		for(Node n : map.keySet()) {
			if(!n.hasL()) {
				g.fillRect(n.l.x*thickness+1, n.y*thickness+1, thickness*2-2, thickness-2);
			}
			if(!n.hasR()) {
				g.fillRect(n.x*thickness+1, n.y*thickness+1, thickness*2-2, thickness-2);
			}
			if(!n.hasU()) {
				g.fillRect(n.x*thickness+1, n.y*thickness+1, thickness-2, thickness*2-2);
			}
			if(!n.hasD()) {
				g.fillRect(n.x*thickness+1, n.d.y*thickness+1, thickness-2, thickness*2-2);
			}
		}
		return bi;
	}
	
	public void generatePath() {
		Node start = new Node(x, y);
		map.put(start, start);
		
		while(true) {
			
			//the below is if u are ever curious of the progress, just uncomment if u want it
			
			System.out.println(((map.size()) / ((double) W*H))*100 + "% finished"); 
			
			Node n; //this variable is used in a funny way below to both instantiate and store and use as a reference
			        //It's actually a fun trick where you can do func(n = new Node); which will first instantiate then pass the reference
			
			/**
			 * So to prevent concurrent modification we have to create a new list to iterate over rather than the straight up keyset
			 * this also gives us a chance to filter out nodes that are surrounded!!!!
			 * So then we don't have to check nodes that already can;t have more nodes!
			 * And below is an excuse to use functional programming in java, my fav :3
			**/
			final List<Node> nodes = map.keySet()
					.stream()                            //create a stream of the set
					.filter(node -> !node.isSurrounded())//filter for only nodes that are not surrounded
					.collect(Collectors.toList());       //collect this into a list
			
			for(Node currNode : nodes) {
				
				boolean canAdd = true;
				
				Node node = currNode;
				
				//So we basically are worming our way along the space really fast
				//Once we find a new node, we follow that node and continue, which makes this super fast too, and much more natural
				while(canAdd) {
					ArrayList<Setter> potentialSetters = new ArrayList<>();
					ArrayList<Node> potentialNodes = new ArrayList<>();
					canAdd = false;
					
					/**
					 * we are actually using lambdas to make the code simpler and easier to 
					 * pick a random side to apply a new side to! I just love lambdas, they make life so much easier :3
					**/
					if(node.x > 0 && !map.containsKey(n = new Node(node.x-1, node.y)) && node.hasL()) { 
						potentialNodes.add(n);
						potentialSetters.add(node::setL);
					}
					if(node.x < W-1 && !map.containsKey(n = new Node(node.x+1, node.y)) && node.hasR())  { 
						potentialNodes.add(n);
						potentialSetters.add(node::setR);
					}
					if(node.y > 0 && !map.containsKey(n = new Node(node.x, node.y-1)) && node.hasD()) { 
						potentialNodes.add(n);
						potentialSetters.add(node::setD);
					}
					if(node.y < H-1 && !map.containsKey(n = new Node(node.x, node.y+1)) && node.hasU()) { 
						potentialNodes.add(n);
						potentialSetters.add(node::setU);
					}
					
					if(potentialSetters.size() > 0) { //it has available slots nearby
						int rand = (int) (Math.random() * potentialSetters.size()); //pick a random one

						potentialSetters.get(rand).set(potentialNodes.get(rand)); //then give our node this new node (will be used for drawing)
						
						map.put(potentialNodes.get(rand), potentialNodes.get(rand)); //then add new node to map
						canAdd = true;
						
						//then switch node to this new node to follow path
						node = potentialNodes.get(rand);
					}
					else { //doesn't, so mark surrouned
						node.markSurrounded();
					}
				}
					
				
			}
			
			boolean freeNodes = false;
			
			for(Node node : map.keySet()) { //check to see if we have free nodes to use
				if(!node.isSurrounded()) {
					freeNodes = true;
					break;
				}
			}
			
			//If we do not have anymore free nodes, exit
			if(!freeNodes) break;
			
		}
	}
	
	//This is so we can use "functional" pointers :3
	@FunctionalInterface
	interface Setter {
		void set(Node n);
	}
	private static final class Node {
		protected Node l, r, u, d; //left, right, up, down respectively
		private final int x, y;
		private boolean surrounded = false;
		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		//check if any of the leaves are possessed by this node specifically
		public boolean hasL() {return l == null;}
		public boolean hasR() {return r == null;}
		public boolean hasU() {return u == null;}
		public boolean hasD() {return d == null;}
		
		//set the leaves
		public void setL(Node n) {this.l = n;}
		public void setR(Node n) {this.r = n;}
		public void setU(Node n) {this.u = n;}
		public void setD(Node n) {this.d = n;}
		
		//check and mark surrounded, done by the generation class
		public boolean isSurrounded() {return surrounded;}
		public void markSurrounded() {surrounded = true;}
		
		//Potentially there could be a better way to prevent collisions via this hashcode
		@Override
		public int hashCode() {
			return (x<<16)|(y);
		}
		
		//for checking if this is equal to another, we overide because 
		//two nodes of the same location are supposed to be registered as the same
		//but normally java wouldnt do that, so we override!
		@Override
		public boolean equals(Object o) {
			if(o instanceof Node) {
				if(x == ((Node)o).x) {
					if(y == ((Node)o).y) {
						return true;
					}
				}
				return false;
			}
			return super.equals(o); //just do the super method of it ;3
		}
	}
}
