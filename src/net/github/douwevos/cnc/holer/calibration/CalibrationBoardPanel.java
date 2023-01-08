package net.github.douwevos.cnc.holer.calibration;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.image.VolatileImage;
import java.util.Objects;

import javax.swing.JPanel;

import douwe.Point3D;
import net.github.douwevos.cnc.head.ActionMoveTo;
import net.github.douwevos.cnc.head.CncActionQueue;
import net.github.douwevos.cnc.head.CncContext;
import net.github.douwevos.cnc.head.CncHead;
import net.github.douwevos.cnc.head.CncHeadService;
import net.github.douwevos.cnc.head.CncHeadSpeed;
import net.github.douwevos.cnc.head.CncLocation;
import net.github.douwevos.cnc.head.MicroLocation;
import net.github.douwevos.cnc.holer.SourcePiece;
import net.github.douwevos.cnc.type.Distance;
import net.github.douwevos.cnc.type.DistanceUnit;

public class CalibrationBoardPanel extends JPanel implements MouseMotionListener, MouseListener, Runnable {

	private static final Color RULER_COLOR = Color.GRAY;
	private static final int INSET_X = 80;
	private static final int INSET_Y = 30;
	
	private CncHeadService cncHeadService;
	
	private VolatileImage boardImage;
	private VolatileImage controlImage;
	
	private double boardViewScale = 1;
	
	private boolean lockView = true;
	private CalibrationContext calibrationContext;
	
	private volatile boolean keepRunning = true;
	
	
	ActionMoveTo activeAction;
	
	SelectedOctant selectedOctant;
	
	public CalibrationBoardPanel() {
	}

	public void setCncHeadService(CncHeadService cncHeadService) {
		this.cncHeadService = cncHeadService;
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		addMouseListener(this);
		addMouseMotionListener(this);
		keepRunning = true;
		new Thread(this).start();
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		keepRunning = false;
		boardImage = null;
	}
	
	public void setCalibrationContext(CalibrationContext calibrationContext) {
		this.calibrationContext = calibrationContext;
	}
	
	@Override
	public void paint(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		
		Graphics2D gfx = (Graphics2D) g;

		int boardHeight = height - 300;
		
		if (boardHeight>0) {
			if (boardImage==null || width!=boardImage.getWidth(this) || height!=boardImage.getHeight(this)) {
				boardImage = createVolatileImage(width, boardHeight);
			}
			
			paintBoard(boardImage);
			
			gfx.drawImage(boardImage, 0, 300, this);
		}
		
		if (controlImage == null || width!=controlImage.getWidth()) {
			controlImage = createVolatileImage(width, 300);
		}
		paintControls(controlImage);
		gfx.drawImage(controlImage, 0, 0, this);
	}



	private void paintControls(VolatileImage controlImage) {
		Graphics2D graphics = controlImage.createGraphics();
		graphics.setColor(Color.DARK_GRAY.darker());
		int width = controlImage.getWidth();
		graphics.fillRect(0, 0, width, 300);
		
		
		
		graphics.setPaint(Color.blue);
		graphics.setColor(Color.red);

		
		GeneralPath slow = new GeneralPath();
		slow.moveTo(0, -10);
		slow.lineTo(10, -10);
		slow.lineTo(10, -20);
		slow.lineTo(30, 0);
		slow.lineTo(10, 20);
		slow.lineTo(10, 10);
		slow.lineTo(0, 10);
		slow.closePath();

		
		GeneralPath fast = new GeneralPath();
		fast.moveTo(0, -10);
		fast.lineTo(10, -10);
		fast.lineTo(10, -20);
		fast.lineTo(30, 0);
		fast.lineTo(10, 20);
		fast.lineTo(10, 10);
		fast.lineTo(0, 10);
		fast.closePath();
		
		fast.moveTo(-4, -10);
		fast.lineTo(-9, -10);
		fast.lineTo(-9, 10);
		fast.lineTo(-4, 10);
		fast.closePath();

		fast.moveTo(-13, -10);
		fast.lineTo(-16, -10);
		fast.lineTo(-16, 10);
		fast.lineTo(-13, 10);
		fast.closePath();

		drawShape(graphics, slow, 200, 150, 0, Octant.RIGHT, false, false);
		drawShape(graphics, slow, 185, 185, 45, Octant.DOWN_RIGHT, false, false);
		drawShape(graphics, slow, 150, 200, 90, Octant.DOWN, false, false);
		drawShape(graphics, slow, 115, 185, 135, Octant.DOWN_LEFT, false, false);
		drawShape(graphics, slow, 100, 150, 180, Octant.LEFT, false, false);
		drawShape(graphics, slow, 115, 115, 225, Octant.UP_LEFT, false, false);
		drawShape(graphics, slow, 150, 100, 270, Octant.UP, false, false);
		drawShape(graphics, slow, 185, 115, 315, Octant.UP_RIGHT, false, false);


		drawShape(graphics, fast, 250, 150, 0, Octant.RIGHT, true, false);
		drawShape(graphics, fast, 222, 222, 45, Octant.DOWN_RIGHT, true, false);
		drawShape(graphics, fast, 150, 250, 90, Octant.DOWN, true, false);
		drawShape(graphics, fast, 78, 222, 135, Octant.DOWN_LEFT, true, false);
		drawShape(graphics, fast, 50, 150, 180, Octant.LEFT, true, false);
		drawShape(graphics, fast, 78, 78, 225, Octant.UP_LEFT, true, false);
		drawShape(graphics, fast, 150, 50, 270, Octant.UP, true, false);
		drawShape(graphics, fast, 222, 78, 315, Octant.UP_RIGHT, true, false);

		graphics.drawArc(140, 140, 20, 20, 0, 360);

		
		drawShape(graphics, slow, 350, 200, 90, Octant.DOWN, false, true);
		drawShape(graphics, fast, 350, 250, 90, Octant.DOWN, true, true);
		drawShape(graphics, slow, 350, 100, 270, Octant.UP, false, true);
		drawShape(graphics, fast, 350, 50, 270, Octant.UP, true, true);

		
	}
	
	
	
	private void drawShape(Graphics2D gfx, Shape s, int tx, int ty, int angle, Octant octant, boolean isFast, boolean isZ) {
		
		SelectedOctant lcSelectedOctant = selectedOctant;
		if (lcSelectedOctant!=null && lcSelectedOctant.octant==octant && lcSelectedOctant.fast==isFast && lcSelectedOctant.isZ==isZ) {
			gfx.setPaint(Color.green);
		} else {
			gfx.setPaint(Color.red);
		}
		gfx.translate(tx, ty);
		double theta = Math.PI*angle/180d;
		gfx.rotate(theta);
		gfx.fill(s);
		gfx.rotate(-theta);
		gfx.translate(-tx, -ty);
	}

	private void paintBoard(VolatileImage boardImage) {

		int width = boardImage.getWidth();
		int height = boardImage.getHeight();
		
		
		
		Graphics2D gfx = boardImage.createGraphics();
		gfx.setColor(Color.BLACK);
		gfx.fillRect(0, 0, width, height);


		
		CncContext context = cncHeadService.getContext();
//		Point bounds = cncControlContext.getBounds();
		Point3D bounds = new Point3D(20000,20000,0);
		SourcePiece sourcePiece = context.getSourcePiece();
		if (sourcePiece != null) {
			bounds = new Point3D(sourcePiece.getWidth().asMicrometers(), sourcePiece.getHeight().asMicrometers(), 0);
		}
		
		if (lockView) {
			double xScale = (double) (width-INSET_X*2) / bounds.x ;
			double yScale = (double) (height-INSET_Y*2) / bounds.y;
			
			
			double scale;
			if (xScale<yScale) {
				scale = xScale;
			} else {
				scale = yScale;
			}
	
			boardViewScale = scale;
		}
		
		gfx.setColor(RULER_COLOR);
		gfx.drawRect(INSET_X, INSET_Y, (int) Math.round(bounds.x*boardViewScale), (int) Math.round(bounds.y*boardViewScale));

		Point3D currentLocation = cncHeadService.getContext().getHeadLocation();
		
		drawHorizontalRuler(height, gfx, bounds, currentLocation.x);

		drawVerticalRuler(height, gfx, bounds, currentLocation.y);

		
		gfx.setColor(Color.green);
		
		
		int x = INSET_X + (int) Math.round(currentLocation.x * boardViewScale);
		int y = height - INSET_Y - (int) Math.round(currentLocation.y * boardViewScale);
		
		gfx.drawArc(x-2, y-2, 5, 5, 0, 360);


		int yhead = height - INSET_Y - (int) Math.round(currentLocation.z * boardViewScale);

		gfx.drawLine(0, yhead, 20, yhead);
		
		
		gfx.dispose();
	}


	private void drawHorizontalRuler(int height, Graphics2D gfx, Point3D bounds, long cncCurrentX) {
		gfx.setColor(RULER_COLOR);
		long milliStep = detectMilliStep();
		long l = 0;
		int k=30;
		int rightEnd = INSET_X + (int) Math.round(bounds.x*boardViewScale);
		
		CncContext context = cncHeadService.getContext();
		CncHead cncHead = context.getHead();
		
		while(k-->0) {
			Point3D cncLocation = cncHead.toCncLocation(new MicroLocation(l, 0, 0));
			int sx = INSET_X + (int) Math.round(cncLocation.x*boardViewScale);
			boolean invalidEnd = sx>rightEnd;
			if (invalidEnd) {
				sx = rightEnd; 
				k=-1;
			}
			int sy = height - INSET_Y;
			gfx.drawLine(sx, sy, sx, sy+14);
			for(int sub=1; sub<10; sub++) {
				cncLocation = cncHead.toCncLocation(new MicroLocation(l-((sub*milliStep)/10l), 0, 0));
				int fx = INSET_X + (int) Math.round(cncLocation.x*boardViewScale);
				if (fx>INSET_X && fx<sx) {
					gfx.drawLine(fx, sy, fx, sy+5);
				}
			}

			if (!invalidEnd) {
				long mm = DistanceUnit.MICROMETER.toMilliMeter(l);
				int ascent = gfx.getFontMetrics().getMaxAscent();
				int w = gfx.getFontMetrics().stringWidth(""+mm);
				gfx.drawString(""+mm, sx - w/2, sy+16+ascent);
			}
			
			l += milliStep;
		}
		
		int sx = INSET_X + (int) Math.round(cncCurrentX*boardViewScale);
		int sy = height - INSET_Y;
		gfx.setColor(Color.green);
		gfx.drawLine(sx, sy, sx, sy+10);
		
		
	}

	

	private void drawVerticalRuler(int height, Graphics2D gfx, Point3D bounds, long cncCurrentY) {
		CncContext context = cncHeadService.getContext();
		CncHead cncHead = context.getHead();

		gfx.setColor(RULER_COLOR);
		long milliStep = detectMilliStep();
		long l = 0;
		int k=30;
		int baseY = height-INSET_Y;
		int rightEnd = baseY - (int) Math.round(bounds.y*boardViewScale);
		while(k-->0) {
			Point3D cncLocation = cncHead.toCncLocation(new MicroLocation(0, l, 0));
			int sy = baseY - (int) Math.round(cncLocation.y*boardViewScale);
			boolean invalidEnd = sy<rightEnd;
			if (invalidEnd) {
				sy = rightEnd; 
				k=-1;
			}
			int sx = INSET_X;
			gfx.drawLine(sx-14, sy, sx, sy);
			for(int sub=1; sub<10; sub++) {
				cncLocation = cncHead.toCncLocation(new MicroLocation(0, l-((sub*milliStep)/10l), 0));
				int fy = baseY - (int) Math.round(cncLocation.y*boardViewScale);
				if (fy<baseY && fy>sy) {
					gfx.drawLine(sx-5, fy, sx, fy);
				}
			}

			if (!invalidEnd) {
				long mm = DistanceUnit.MICROMETER.toMilliMeter(l);
				int ascent = gfx.getFontMetrics().getMaxAscent();
				String textMm = ""+mm;
				int w = gfx.getFontMetrics().stringWidth(textMm);
				int fontHeight = gfx.getFontMetrics().getHeight();
				gfx.drawString(textMm, sx-16-w , sy+ascent - fontHeight/2);
			}
			
			l += milliStep;
		}

		int sy = baseY - (int) Math.round(cncCurrentY*boardViewScale);
		gfx.setColor(Color.green);
		int sx = INSET_X;
		gfx.drawLine(sx-10, sy, sx, sy);

	}

	
	long stepTests[] = new long[] {
			Distance.ofMillMeters(1).asMicrometers(),
			Distance.ofMillMeters(2).asMicrometers(),
			Distance.ofMillMeters(5).asMicrometers(),
			Distance.ofMillMeters(10).asMicrometers(),
			Distance.ofMillMeters(20).asMicrometers(),
			Distance.ofMillMeters(50).asMicrometers(),
			Distance.ofMillMeters(100).asMicrometers(),
			Distance.ofMillMeters(200).asMicrometers(),
			Distance.ofMillMeters(500).asMicrometers(),
			Distance.ofMillMeters(1000).asMicrometers(),
	};
	
	private long detectMilliStep() {
		CncContext context = cncHeadService.getContext();
		CncHead cncHead = context.getHead();

		for(int index=0; index<stepTests.length; index++) {
			long dots = stepTests[index];
			Point3D cncLocation = cncHead.toCncLocation(new MicroLocation(dots, 0, 0));
			int nx = (int) Math.round(cncLocation.x*boardViewScale);
			if (nx>100) {
				return dots;
			}
		}
		return -1;
	}


	@Override
	public void mouseDragged(MouseEvent e) {
	}

	
	static double LOWER = Math.tan(Math.PI*22.5d/180d);
	static double UPPER = Math.tan(Math.PI*67.5d/180d);

	
	@Override
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if ((x>0 && x<300) && 
			(y>0 && y<300)) {
			
			
			int rx = x-150;
			int ry = y-150;

			
			this.selectedOctant = extractOctant(rx, ry, false);
			repaint();
		} else if ((x>300 && x<400) && 
					(y>0 && y<300)) {

			this.selectedOctant = extractOctant(x-350, y-150, true);
			repaint();
		} else {
			this.selectedOctant = null;
			repaint();
		}
	}
	
	
	private SelectedOctant extractOctant(int rx, int ry, boolean ISz) {
		boolean left = false;
		
		if (rx<0) {
			rx = -rx;
			left = true;
		}
		boolean down = true;
		if (ry<0) {
			ry = -ry;
			down = false;
		}
		
		Octant octant = null;
		
		
		if (rx==0) {
			octant = Octant.ofUpper(left, down);
		} else {
			double tan = (double) ry / rx;
			if (tan<LOWER) {
				octant = Octant.ofLower(left, down);
			} else if (tan<UPPER) {
				octant = Octant.ofMiddle(left, down);
			} else {
				octant = Octant.ofUpper(left, down);
			}
		}

		long s = rx*rx+ry*ry;
		boolean fast = s>80*80;
		
		SelectedOctant selectedOctant = new SelectedOctant();
		selectedOctant.fast = fast;
		selectedOctant.isZ = ISz;
		selectedOctant.octant = octant;
		return selectedOctant;
	}


	static class SelectedOctant {
		Octant octant;
		boolean fast;
		boolean isZ;
	}
	
	enum Octant {
		UP(0, 1),
		UP_RIGHT(1, 1),
		RIGHT(1, 0),
		DOWN_RIGHT(1, -1),
		DOWN(0, -1),
		DOWN_LEFT(-1, -1),
		LEFT(-1, 0),
		UP_LEFT(-1, 1),
		;
		
		
		final int dx;
		final int dy;
		Octant(int dx, int dy ){
			this.dx = dx;
			this.dy = dy;
		}
		
		
		static Octant ofMiddle(boolean left, boolean down) {
			if (left) {
				if (down) {
					return Octant.DOWN_LEFT;
				}
				return Octant.UP_LEFT;
			} else {
				if (down) {
					return Octant.DOWN_RIGHT;
					
				}
				return Octant.UP_RIGHT;
			}
		}


		static Octant ofLower(boolean left, boolean down) {
			if (left) {
				return Octant.LEFT;
			} else {
				return Octant.RIGHT;
			}
		}

		static Octant ofUpper(boolean left, boolean down) {
			if (down) {
				return Octant.DOWN;
			} else {
				return Octant.UP;
			}
		}

	}


	@Override
	public void mouseClicked(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();

		mouseY = mouseY-300;
		if (boardImage==null || mouseY<0) {
			return;
		}
		
		CncContext context = cncHeadService.getContext();

		int height = boardImage.getHeight();
		int px = (int) Math.round((mouseX-INSET_X) / boardViewScale);
		int py = (int) Math.round((height-mouseY-INSET_Y) / boardViewScale);

		MicroLocation currentLocation = context.getHeadLocation();
		MicroLocation newLocation = currentLocation.withX(px).withY(py);

		System.out.println("newLocation="+newLocation);
		
		CncActionQueue actionQueue = calibrationContext.getActionQueue();
		if (actionQueue != null) {
			actionQueue.resetTo(newLocation, CncHeadSpeed.FAST);
		}
//		cncControlContext.resetAndForceTo(newLocation);
		repaint();
	}


	@Override
	public void mousePressed(MouseEvent e) {
		SelectedOctant lcSelectedOctant = selectedOctant;
		if (lcSelectedOctant != null) {
			System.err.println("octan = "+ lcSelectedOctant);
			CncActionQueue actionQueue = calibrationContext.getActionQueue();
			if (actionQueue == null) {
				return;
			}
			System.err.println("actionQueue= "+ actionQueue);
			
			MicroLocation headLocation = cncHeadService.getContext().getHeadLocation();
			
			long ex = 0L;
			long ey = 0L;
			long ez = 0L;
			if (lcSelectedOctant.isZ) {
				ez = lcSelectedOctant.octant.dy * -100_000_000L;
			} else {
				ex = lcSelectedOctant.octant.dx * 100_000_000L;
				ey = lcSelectedOctant.octant.dy * 100_000_000L;
			}
			MicroLocation newLocation = new MicroLocation(headLocation.x + ex, headLocation.y + ey, headLocation.z + ez);
			activeAction = actionQueue.resetTo(newLocation, lcSelectedOctant.fast ? CncHeadSpeed.FAST : CncHeadSpeed.SLOW);
			System.err.println("activeAction= "+ activeAction+ ", newLocation="+newLocation+", headLocation="+headLocation);
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		ActionMoveTo act = activeAction;
		if (act!=null) {
			act.markFinished();
			activeAction = null;
		}
	}


	@Override
	public void mouseEntered(MouseEvent e) {
	}


	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void run() {
		CncLocation lastLocation = null; 
		while(keepRunning) {
			try {
				Thread.sleep(100l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (cncHeadService == null) {
				continue;
			}
			CncContext context = cncHeadService.getContext();
			CncHead cncHead = context.getHead();
			CncLocation location = cncHead.getLocation();
			if (!Objects.equals(location, lastLocation)) {
				repaint();
				lastLocation = location;
			}
		}
	}
	
}
