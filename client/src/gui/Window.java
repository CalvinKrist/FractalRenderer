package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import util.Utils;

@SuppressWarnings("rawtypes")
public class Window extends JPanel implements MouseListener, MouseMotionListener {
	
	private Dimension dimension;
	private Rectangle opacityRect, gradientRect, colorRect;
	
	private List<ArrowButton<Color>> colorList;
	private List<ArrowButton<Double>> opacityList;
	private ArrowButton selectedButton;
	
	private int opacityButtonHeight, colorButtonHeight;
	
	public Window(Dimension dimension) {
		this.dimension = dimension;
		this.setPreferredSize(dimension);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		opacityButtonHeight = (int)(dimension.height * 0.2);
		colorButtonHeight = (int)(dimension.height * 0.8);
		
		opacityRect = new Rectangle(10, 0, dimension.width - 20, opacityButtonHeight);
		gradientRect = new Rectangle(10, opacityButtonHeight, dimension.width - 20, (int)(dimension.height * 0.6));
		colorRect = new Rectangle(10, colorButtonHeight, dimension.width - 20, opacityButtonHeight);
		
		colorList = new ArrayList<ArrowButton<Color>>();
		opacityList = new ArrayList<ArrowButton<Double>>();
		
		addColorButton(gradientRect.x, Color.black);
		addColorButton(gradientRect.x + gradientRect.width, Color.white);
		addOpacityButton(gradientRect.x, 1.0);
		addOpacityButton(gradientRect.x + gradientRect.width, 1.0);
	}
	
	@Override
	public void paint(Graphics g2) {
		Graphics2D g = (Graphics2D)g2;
		g.setColor(Color.white);
		g.fillRect(0, 0, dimension.width, dimension.height);
		
		for(ArrowButton b: colorList)
			b.draw(g);
		for(ArrowButton b: opacityList)
			b.draw(g);
		
		for(int x = gradientRect.x; x < gradientRect.x + gradientRect.width; x++) {
			Color c = getColorAtPoint(x);
			int opacity = (int)(255 * getOpacityAtPoint(x));
			g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), opacity));
			g.fillRect(x, gradientRect.y, 1, gradientRect.height);	
		}
		System.out.println();
		g.setColor(Color.black);
		g.drawRect(gradientRect.x, gradientRect.y, gradientRect.width, gradientRect.height);
	}
	
	private Double getOpacityAtPoint(int x) {
		if(opacityList.size() == 1)
			return opacityList.get(0).getData();
		if(x <= opacityList.get(0).getLocation().x)
			return opacityList.get(0).getData();
		for(int i = 0; i < opacityList.size() - 1; i++) {
			if(x >= opacityList.get(i).getLocation().x && x < opacityList.get(i + 1).getLocation().x)
				return Utils.interpolateDouble(opacityList.get(i).getData(), opacityList.get(i + 1).getData(), x - opacityList.get(i).getLocation().x, opacityList.get(i + 1).getLocation().x - opacityList.get(i).getLocation().x);
		}
		return opacityList.get(opacityList.size() - 1).getData();
	}
	
	private Color getColorAtPoint(int x) {
		if(colorList.size() == 1)
			return colorList.get(0).getData();
		if(x <= colorList.get(0).getLocation().x)
			return colorList.get(0).getData();
		for(int i = 0; i < colorList.size() - 1; i++) {
			if(x >= colorList.get(i).getLocation().x && x < colorList.get(i + 1).getLocation().x)
				return Utils.interpolateColors(colorList.get(i).getData(), colorList.get(i + 1).getData(), x - colorList.get(i).getLocation().x, colorList.get(i + 1).getLocation().x - colorList.get(i).getLocation().x);
		}
		return colorList.get(colorList.size() - 1).getData();
	}
	
	public void addColorButton(int x) {
		addColorButton(x, Color.black);
	}
	
	public void addColorButton(int x, Color c) {
		ArrowButton<Color> b = new ArrowButton<Color>();
		b.setLocation(new Point(x, colorButtonHeight));
		b.setData(c);
		b.setDown(false);
		b.setSelected(true);
		b.setSquareColor(c);
		colorList.add(b);
		sortButtonList(colorList);
	}
	
	public void addOpacityButton(int x) {
		addOpacityButton(x, 1.0);
	}
	
	public void addOpacityButton(int x, Double d) {
		ArrowButton<Double> b = new ArrowButton<Double>();
		b.setLocation(new Point(x, opacityButtonHeight));
		b.setData(d);
		b.setDown(true);
		b.setSelected(true);
		b.setSquareColor(Color.black);
		opacityList.add(b);
		sortButtonList(opacityList);
	}
	
	//takes a list of arrow buttons and sorts them based on x location
	@SuppressWarnings("unchecked")
	private void sortButtonList(List list) {
        for (int i = 1; i < list.size(); i++) 
            for(int j = i ; j > 0 ; j--)
                if(((ArrowButton)(list.get(j))).getLocation().x < ((ArrowButton)(list.get(j - 1))).getLocation().x) 
                    list.add(j, list.remove(j - 1));
	}
	
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		if(selectedButton == null) {
			if(colorRect.contains(p))
				addColorButton(p.x);
			else if(opacityRect.contains(p))
				addOpacityButton(p.x);
		} else {
			if(selectedButton.isSquareClicked(p)) {
				if(selectedButton.isDown()) {
					OpacityArrowMenu menu = new OpacityArrowMenu(selectedButton, opacityList);
				} else {
					ColorArrowMenu menu = new ColorArrowMenu(selectedButton, colorList);
				}
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		
	}
	
	public void mouseDragged(MouseEvent e) {
		if(selectedButton != null) {
			selectedButton.setLocation(new Point(e.getPoint().x, selectedButton.getLocation().y));
			if(selectedButton.getLocation().x < gradientRect.x)
				selectedButton.setLocation(new Point(gradientRect.x, selectedButton.getLocation().y));
			else if(selectedButton.getLocation().x > gradientRect.x + gradientRect.width) 
				selectedButton.setLocation(new Point(gradientRect.x + gradientRect.width, selectedButton.getLocation().y));
			if(colorList.contains(selectedButton))
				sortButtonList(colorList);
			else
				sortButtonList(opacityList);
		}
	}
	
	public void mousePressed(MouseEvent e) {
		Point p = e.getPoint();
		selectedButton = null;
		if(colorRect.contains(p)) {
			for(ArrowButton b : colorList) 
				if(b.isClicked(p)) {
					selectedButton = b;
					selectedButton.setSelected(true);
					return;
				} else
					b.setSelected(false);
		} else if(opacityRect.contains(p)) {
			for(ArrowButton b: opacityList)
				if(b.isClicked(p)) {
					selectedButton = b;
					selectedButton.setSelected(true);
					return;
				} else
					b.setSelected(false);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	public void mouseMoved(MouseEvent e) {}

}
