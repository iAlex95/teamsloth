import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;


public class ArtGallery {
	//double galleryPoints[][] = {{5, 2}, {4.5, 1}, {4, 1}, {3.5, 2}, {3, 1}, {2.5, 1}, {2, 2}, {1.5, 1}, {1, 1}, {0.5, 2}, {0, 0}, {5, 0}};
	//double galleryPoints[][] = {{10, 5}, {9, 5}, {9, 7}, {8, 7}, {8, 5}, {6, 5}, {6, 7}, {5, 7}, {5, 3}, {4, 3}, {4, 5}, {3, 5}, {3, 3}, {2, 3}, {2, 7}, {1, 7}, {1, 6}, {0, 6}, {0, 10}, {-1, 10}, {-1, 9}, {-3, 9}, {-3, 8}, {-1, 8}, {-1, 6}, {-4, 6}, {-4, 5}, {-3, 5}, {-3, 4}, {-7, 4}, {-7, 3}, {-6, 3}, {-6, 2}, {-11, 2}, {-11, 1}, {-10, 1}, {-10, -1}, {-9, -1}, {-9, 1}, {-8, 1}, {-8, -1}, {-7, -1}, {-7, 1}, {-6, 1}, {-6, -2}, {-5, -2}, {-5, 3}, {-3, 3}, {-3, 0}, {-2, 0}, {-2, 5}, {-1, 5}, {-1, 3}, {0, 3}, {0, 5}, {1, 5}, {1, 1}, {0, 1}, {0, 0}, {1, 0}, {1, -1}, {-2, -1}, {-2, -2}, {-1, -2}, {-1, -3}, {-3, -3}, {-3, -4}, {-1, -4}, {-1, -5}, {0, -5}, {0, -2}, {1, -2}, {1, -3}, {2, -3}, {2, -2}, {3, -2}, {3, -4}, {4, -4}, {4, -2}, {5, -2}, {5, -1}, {2, -1}, {2, 0}, {3, 0}, {3, 1}, {2, 1}, {2, 2}, {7, 2}, {7, 3}, {6, 3}, {6, 4}, {10, 4}};
	ArrayList<Line2D> edges = new ArrayList<Line2D>();
	ArrayList<Node> nodes = new ArrayList<Node>();
	ArrayList<Point2D> guardPoints = new ArrayList<Point2D>();
	
	double galleryPoints[][];
	
	Reader reader = new Reader();
	
	public ArtGallery() {
		reader.readData();
		galleryPoints = reader.getData(25);
		
		createNodesAndEdges();
		calculateNodeAngleRanges();
		createConnections();
		placeGuards();
		//if (checkPossibleConnection(nodes.get(0), nodes.get(14))) System.out.println("HI");;
		
		for (Node node : nodes.get(1).getConnectedNodes()) {
			System.out.println(node.getX() + " " + node.getY());
		}
			
		/*for (Point2D point : guardPoints) {
			System.out.println(point);
		}*/
		System.out.println("Number of Guards: " + guardPoints.size());
	}
	
	public void createNodesAndEdges() {
		Line2D tempLine;
		for (int i = 0; i < galleryPoints.length; i++)
			nodes.add(new Node(galleryPoints[i][0], galleryPoints[i][1]));
		for (int i = 0; i < nodes.size(); i++) {
			if (i == nodes.size()-1) {
				tempLine = new Line2D.Double(nodes.get(i).getX(), nodes.get(i).getY(), nodes.get(0).getX(), nodes.get(0).getY());
				nodes.get(i).addConnectedNode(nodes.get(0));
				nodes.get(0).addConnectedNode(nodes.get(i));
			} else {
				tempLine = new Line2D.Double(nodes.get(i).getX(), nodes.get(i).getY(), nodes.get(i+1).getX(), nodes.get(i+1).getY());
				nodes.get(i).addConnectedNode(nodes.get(i+1));
				nodes.get(i+1).addConnectedNode(nodes.get(i));
			}
			edges.add(tempLine);
		}
	}
	
	public void calculateNodeAngleRanges() {
		for (Node node: nodes) {
			double angle1 = Math.atan2(node.getConnectedNodes().get(0).getX() - node.getX(), node.getConnectedNodes().get(0).getY() - node.getY());
			if (angle1 < 0) angle1 = angle1 + 2*Math.PI;
			if (angle1  == 0) angle1  = 2*Math.PI;
			double angle2 = Math.atan2(node.getConnectedNodes().get(1).getX() - node.getX(), node.getConnectedNodes().get(1).getY() - node.getY());
			if (angle2 < 0) angle2 = angle2 + 2*Math.PI;
			if (angle2 == 0) angle2 = 2*Math.PI;
			
			if (angle1 > angle2) {
				node.setAngleOne(angle1);
				node.setAngleTwo(angle2);
			} else {
				node.setAngleOne(angle2);
				node.setAngleTwo(angle1);
			}
		}
	}
	
	public void createConnections() {
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.size(); j++) {
				if (!nodes.get(i).getConnectedNodes().contains(nodes.get(j)) && nodes.get(i) != nodes.get(j)) {
					if (checkPossibleConnection(nodes.get(i), nodes.get(j))) {
						nodes.get(i).addConnectedNode(nodes.get(j));
						nodes.get(j).addConnectedNode(nodes.get(i));
					}
				}
			}
		}
	}
	
	public boolean checkPossibleConnection(Node node1, Node node2) {
		Line2D tempLine = new Line2D.Double(node1.getX(), node1.getY(), node2.getX(), node2.getY());
		
		double tempAngle = Math.atan2(node2.getX() - node1.getX(), node2.getY() - node1.getY());
		if (tempAngle < 0) tempAngle = tempAngle + 2*Math.PI;
		if (tempAngle == 0) tempAngle = 2*Math.PI;
		//System.out.println(tempAngle + " " + node1.getAngleOne() + " " + node1.getAngleTwo());
		if (tempAngle > node1.getAngleOne() || tempAngle < node1.getAngleTwo()) return false;
		
		int intersectCounter = 0;
		for (int i = 0; i < edges.size(); i++) {
			if (testIntersection(tempLine, edges.get(i))) {
				if (!checkIfNodeIntersect(tempLine, edges.get(i), node1, node2)) intersectCounter++;
			}
		}
		//System.out.println(intersectCounter);
		if (intersectCounter > 0) return false;
		
		return true;
	}
	
	public boolean checkIfParallel(Line2D line, Double angle) {
		double angle1 = Math.atan2(line.getX1() - line.getX2(), line.getY1() - line.getY2());
		if (angle1 < 0) angle1 = angle1 + 2*Math.PI;
		double angle2 = Math.atan2(line.getX2() - line.getX1(), line.getY2() - line.getY1());
		if (angle2 < 0) angle2 = angle2 + 2*Math.PI;
		
		if (angle == angle1 || angle == angle2) return true;
		else return false;
	}
	
	public boolean checkAngle(Node node1, Node node2) {
		double tempAngle = Math.atan2(node2.getX() - node1.getX(), node2.getY() - node1.getY());
		if (tempAngle < 0) tempAngle = tempAngle + 2*Math.PI;
		if (tempAngle > node1.getAngleOne() || tempAngle < node1.getAngleTwo()) return false;
		else return true;
	}
		
	
	public boolean checkIfNodeIntersect(Line2D line1, Line2D line2, Node node1, Node node2) {
		for (int j = 0; j < nodes.size(); j++) {
			Point2D tempPoint = getIntersectionPoint(line1, line2);
			//System.out.println(tempPoint);
			if (tempPoint.getX() == nodes.get(j).getX() && tempPoint.getY() == nodes.get(j).getY() || Double.isNaN(tempPoint.getX())) {
				if (nodes.get(j) == node1) return true;
				if (nodes.get(j) == node2) return true;
				if (checkAngle(nodes.get(j), node2)) {
					return true;
				}
			}
		}
		return false;
	}
		
	public boolean testIntersection(Line2D line1, Line2D line2) {
		return line1.intersectsLine(line2);
	}
	
	public void placeGuards() {
		/*for (Node node : nodes) {
			if (!node.isVisible()) {
				Node tempNode = node;
				int biggestSize = node.getConnectedNodes().size();
				for (int i = 0; i < node.getConnectedNodes().size(); i++) {
					if (node.getConnectedNodes().get(i).getConnectedNodes().size() > biggestSize && !node.getConnectedNodes().get(i).isVisible()) {
						biggestSize = node.getConnectedNodes().get(i).getConnectedNodes().size();
						tempNode = node.getConnectedNodes().get(i);
					}
				}
				tempNode.setVisible(true);
				System.out.println(tempNode.getX() + " " + tempNode.getY());
				for (int i = 0; i < tempNode.getConnectedNodes().size(); i++) {
					tempNode.getConnectedNodes().get(i).setVisible(true);
				}
				
				guardPoints.add(new Point2D.Double(tempNode.getX(), tempNode.getY()));
			}
		}*/
		Node tempNode = null;
		int biggestSize = 0;
		
		for (Node node : nodes) {
			if (node.getConnectedNodes().size() > biggestSize && !node.isVisible()) {
				biggestSize = node.getConnectedNodes().size();
				tempNode = node;
			}
		}
		
		if (tempNode != null) {
			tempNode.setVisible(true);
			for (int i = 0; i < tempNode.getConnectedNodes().size(); i++) {
				tempNode.getConnectedNodes().get(i).setVisible(true);
			}
			guardPoints.add(new Point2D.Double(tempNode.getX(), tempNode.getY()));
			
			placeGuards();
		}
		
	}
					
				
	
	public Point2D getIntersectionPoint(Line2D line1, Line2D line2) {

        final double x1,y1, x2,y2, x3,y3, x4,y4;
        x1 = line1.getX1(); y1 = line1.getY1(); x2 = line1.getX2(); y2 = line1.getY2();
        x3 = line2.getX1(); y3 = line2.getY1(); x4 = line2.getX2(); y4 = line2.getY2();
        final double x = ((x2 - x1)*(x3*y4 - x4*y3) - (x4 - x3)*(x1*y2 - x2*y1)) /
                ((x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4));
        final double y = ((y3 - y4)*(x1*y2 - x2*y1) - (y1 - y2)*(x3*y4 - x4*y3)) /
                ((x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4));

        return new Point2D.Double(x, y);

    }
	
	public static void main(String args[]) {
		new ArtGallery();
	}
}
