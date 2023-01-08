package net.github.douwevos.cnc.holer.item;

import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.List;

import douwe.Point3D;
import net.github.douwevos.cnc.head.CncHeadSpeed;
import net.github.douwevos.cnc.head.MicroLocation;
import net.github.douwevos.cnc.holer.ItemGrabInfo;
import net.github.douwevos.cnc.holer.HolerModel.ToolItemRun;
import net.github.douwevos.cnc.holer.HolerModelRun.RunContext;
import net.github.douwevos.cnc.holer.design.ViewCamera;
import net.github.douwevos.cnc.holer.design.SelectionModel;
import net.github.douwevos.cnc.holer.item.ItemCircle.Context;
import net.github.douwevos.cnc.layer.Layer;
import net.github.douwevos.cnc.layer.LayerDescription;
import net.github.douwevos.cnc.layer.StartStopLayer;
import net.github.douwevos.cnc.run.Chain;
import net.github.douwevos.cnc.run.LayeredCncContext;
import net.github.douwevos.cnc.ttf.TextLayout;
import net.github.douwevos.cnc.ttf.TextLayoutPainter;
import net.github.douwevos.cnc.ttf.TextLayoutToCncLayer;
import net.github.douwevos.justflat.ttf.format.Ttf;
import net.github.douwevos.justflat.ttf.reader.TrueTypeFontParser;
import net.github.douwevos.justflat.types.values.Bounds2D;

public class ItemText implements Item {

	private static final long DROP_SPEED = 70*4;
	private static final long PUSH_UP = 100;

	private final Point3D location;
	private final String text;
	private final int size;
	private final TextLayout textLayout;
	private final Bounds2D bounds;
	private Layer layer;
	
	
	private static Ttf ttf2;
	
	
	static {
		TrueTypeFontParser ttfParser = new TrueTypeFontParser();
		try {
			ttf2 = ttfParser.parse(new File("/usr/share/fonts/truetype/freefont/FreeSans.ttf"));
//			ttf2 = ttfParser.parse(new File("./src/Purisa.ttf"));
			
		} catch (IOException e) {
		}
		
	}
	
	
	public ItemText(Point3D location, String text, int size) {
		this.location = location;
		this.text = text;
		this.size = size;
		textLayout = new TextLayout(ttf2, text);
		bounds = textLayout.calculateBounds();
	}
	
	
	public TextLayout getTextLayout() {
		return textLayout;
	}
	
	
	public int getTextSize() {
		return size;
	}
	
	@Override
	public void paint(Graphics2D gfx, ToolItemRun toolItemRun, ViewCamera designView, SelectionModel selectionModel) {
		TextLayoutPainter textLayoutPainter = new TextLayoutPainter(textLayout, size);
		textLayoutPainter.draw(gfx, (int) location.x, (int) location.y);
	}
	

	@Override
	public void run(RunContext runContext) {
		runContext.setFloating(true);
		Context context = new Context();
		boolean keepGoing = true;
		for(long z=DROP_SPEED/2; keepGoing; z+=DROP_SPEED) {
			if (z>=location.z) {
				z = location.z;
				keepGoing = false;
			}
			context.z = z;
//			innerFill(controlContext, context);
			outerLine(runContext, context);
			
			context.moveForward = !context.moveForward; 
		}
		runContext.setFloating(true);

//		context.moveHeadDown();
	}

	
	@Override
	public Layer produceLayer(LayerDescription description) {
		if (layer == null) {
			Bounds2D calculateBounds = textLayout.calculateBounds();
			double scalar = (double) size/textLayout.getMaxHeight();
			Bounds2D microBounds = calculateBounds.scale(scalar);
			layer = new StartStopLayer(microBounds.bottom+location.y, (int) (1+microBounds.top-microBounds.bottom));
			TextLayoutToCncLayer textLayoutToCncLayer = new TextLayoutToCncLayer(textLayout, size);
			textLayoutToCncLayer.produceLayer(layer, location.x, location.y);
		}
		return layer;
	}
	
	

	private void outerLine(RunContext runContext, Context context) {
		
		long useZ = context.z;
		
		TextLayout textLayout = new TextLayout(ttf2, text);
		
		TextLayoutToCncLayer textLayoutToCncLayer = new TextLayoutToCncLayer(textLayout, size);
		
		Bounds2D calculateBounds = textLayout.calculateBounds();
		double scalar = (double) size/textLayout.getMaxHeight();
		Bounds2D microBounds = calculateBounds.scale(scalar);

		LayeredCncContext layeredCncContext = new LayeredCncContext(runContext.getSelectedTool(), useZ, null);
		
		layeredCncContext.realModelLayer = new StartStopLayer(microBounds.bottom+location.y, (int) (microBounds.top-microBounds.bottom));
		textLayoutToCncLayer.produceLayer(layeredCncContext.realModelLayer, location.x, location.y);

		List<Chain> chains = layeredCncContext.calculateModelAndCnc(runContext, System.nanoTime());
		
		for(Chain chain : chains) {
			doCncChain(runContext, chain);
		}
	}
	
	private void doCncChain(RunContext runContext, Chain chain) {
		runContext.setFloating(true);
		for(MicroLocation location : chain.locations) {
			runContext.lineTo(location, CncHeadSpeed.NORMAL);
			runContext.setFloating(false);
		}
		runContext.setFloating(true);
	}


	public TextLayout createTextLayout() {
		return new TextLayout(ttf2, text);
	}

	
	@Override
	public ItemGrabInfo createGrabInfo(int mouseX, int mouseY) {
		return null;
	}
	
	@Override
	public Bounds2D bounds() {
		return bounds;
	}

}
