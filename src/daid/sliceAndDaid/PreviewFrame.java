package daid.sliceAndDaid;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import daid.sliceAndDaid.util.Vector2;

public class PreviewFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	private Vector<Layer> layers;
	private int showLayer = 6;
	private double drawScale = 5.0;
	
	public PreviewFrame(Vector<Layer> layers)
	{
		final PreviewFrame self = this;
		this.layers = layers;
		
		final SpinnerModel model = new SpinnerNumberModel(showLayer, 0, layers.size() - 1, 1);
		final JSpinner spinner = new JSpinner(model);
		spinner.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				showLayer = ((Integer) spinner.getValue()).intValue();
				self.repaint();
			}
		});
		this.setLayout(new BorderLayout());
		this.add(spinner, BorderLayout.SOUTH);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setSize(600, 600);
		this.setVisible(true);
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		for (Segment2D s : layers.get(showLayer).segmentList)
		{
			switch (s.type)
			{
			case Segment2D.TYPE_MODEL_SLICE:
				g.setColor(Color.GREEN);
				break;
			case Segment2D.TYPE_OUTLINE:
				g.setColor(Color.BLACK);
				break;
			case Segment2D.TYPE_FILL:
				g.setColor(Color.YELLOW);
				break;
			default:
				g.setColor(Color.RED);
				break;
			}
			drawModelLine(g, s.start, s.end);
			Vector2 center = s.start.add(s.end).div(2);
			Vector2 normal = center.add(s.normal);
			drawModelLine(g, center, normal);
			drawModelLine(g, s.start, normal);
			if (s.prev == null)
				drawModelCircle(g, s.start, 10);
			if (s.next == null)
				drawModelCircle(g, s.end, 10);
		}
	}
	
	private void drawModelLine(Graphics g, Vector2 start, Vector2 end)
	{
		g.drawLine((int) (start.x * drawScale) + this.getWidth() / 2, (int) (start.y * drawScale) + this.getHeight() / 2, (int) (end.x * drawScale) + this.getWidth() / 2, (int) (end.y * drawScale) + this.getHeight() / 2);
	}
	
	private void drawModelCircle(Graphics g, Vector2 center, int radius)
	{
		g.drawOval((int) (center.x * drawScale) + this.getWidth() / 2 - radius / 2, (int) (center.y * drawScale) + this.getHeight() / 2 - radius / 2, radius, radius);
	}
}
