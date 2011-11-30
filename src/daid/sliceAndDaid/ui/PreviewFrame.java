package daid.sliceAndDaid.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import daid.sliceAndDaid.Layer;
import daid.sliceAndDaid.Segment2D;
import daid.sliceAndDaid.util.Vector2;

public class PreviewFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	private Vector<Layer> layers;
	
	public class PreviewPanel extends JPanel implements MouseMotionListener
	{
		private static final long serialVersionUID = 1L;
		
		public int showLayer = 0;
		public double drawScale = 5.0;
		public double viewOffsetX, viewOffsetY;
		
		private int oldX, oldY;
		
		public PreviewPanel()
		{
			addMouseMotionListener(this);
		}
		
		public void paint(Graphics g)
		{
			super.paint(g);
			for (Segment2D s : layers.get(showLayer).modelSegmentList)
			{
				drawSegment(g, s);
			}
			for (Segment2D s = layers.get(showLayer).pathStart; s != null; s = s.getNext())
			{
				drawSegment(g, s);
			}
		}
		
		private void drawSegment(Graphics g, Segment2D s)
		{
			switch (s.getType())
			{
			case Segment2D.TYPE_MODEL_SLICE:
				g.setColor(Color.GREEN);
				break;
			case Segment2D.TYPE_PERIMETER:
				g.setColor(Color.BLACK);
				break;
			case Segment2D.TYPE_FILL:
				g.setColor(Color.YELLOW);
				break;
			case Segment2D.TYPE_MOVE:
				g.setColor(Color.BLUE);
				break;
			default:
				g.setColor(Color.RED);
				break;
			}
			drawModelLine(g, s.start, s.end);
			Vector2 center = s.start.add(s.end).div(2);
			Vector2 normal = center.add(s.getNormal().div(drawScale / 5));
			drawModelLine(g, center, normal);
			drawModelLine(g, s.start, normal);
			if (s.getPrev() == null)
				drawModelCircle(g, s.start, 10);
			if (s.getNext() == null)
				drawModelCircle(g, s.end, 10);
		}
		
		private void drawModelLine(Graphics g, Vector2 start, Vector2 end)
		{
			g.drawLine((int) ((start.x + viewOffsetX) * drawScale) + this.getWidth() / 2, (int) ((start.y + viewOffsetY) * drawScale) + this.getHeight() / 2, (int) ((end.x + viewOffsetX) * drawScale) + this.getWidth() / 2, (int) ((end.y + viewOffsetY) * drawScale) + this.getHeight() / 2);
		}
		
		private void drawModelCircle(Graphics g, Vector2 center, int radius)
		{
			g.drawOval((int) ((center.x + viewOffsetX) * drawScale) + this.getWidth() / 2 - radius / 2, (int) ((center.y + viewOffsetY) * drawScale) + this.getHeight() / 2 - radius / 2, radius, radius);
		}
		
		public void mouseDragged(MouseEvent e)
		{
			viewOffsetX += (double) (e.getX() - oldX) / drawScale;
			viewOffsetY += (double) (e.getY() - oldY) / drawScale;
			repaint();
			oldX = e.getX();
			oldY = e.getY();
		}
		
		public void mouseMoved(MouseEvent e)
		{
			oldX = e.getX();
			oldY = e.getY();
		}
	}
	
	public PreviewFrame(Vector<Layer> layers)
	{
		final PreviewPanel viewPanel = new PreviewPanel();
		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
		this.setTitle("Preview");
		this.layers = layers;
		
		final JSpinner layerSpinner = new JSpinner(new SpinnerNumberModel(viewPanel.showLayer, 0, layers.size() - 1, 1));
		layerSpinner.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				viewPanel.showLayer = ((Integer) layerSpinner.getValue()).intValue();
				viewPanel.repaint();
			}
		});
		final JSpinner zoomSpinner = new JSpinner(new SpinnerNumberModel(viewPanel.drawScale, 1.0, 200.0, 1.0));
		zoomSpinner.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				viewPanel.drawScale = ((Double) zoomSpinner.getValue()).doubleValue();
				viewPanel.repaint();
			}
		});
		
		actionPanel.add(new JLabel("Layer:"));
		actionPanel.add(layerSpinner);
		actionPanel.add(new JLabel("Zoom:"));
		actionPanel.add(zoomSpinner);
		
		this.setLayout(new BorderLayout());
		this.add(viewPanel, BorderLayout.CENTER);
		this.add(actionPanel, BorderLayout.SOUTH);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setSize(600, 600);
		this.setVisible(true);
	}
}
