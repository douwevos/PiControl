package net.github.douwevos.cnc.holer;

import static net.github.douwevos.cnc.type.Distance.ofMicroMeters;
import static net.github.douwevos.cnc.type.Distance.ofMillMeters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import douwe.Point3D;
import net.github.douwevos.cnc.holer.design.SelectionModel;
import net.github.douwevos.cnc.holer.design.ViewCamera;
import net.github.douwevos.cnc.holer.item.Item;
import net.github.douwevos.cnc.holer.item.ItemCircle;
import net.github.douwevos.cnc.holer.item.ItemCircle.FillMode;
import net.github.douwevos.cnc.holer.item.ItemPolyLine;
import net.github.douwevos.cnc.holer.item.ItemRectangle;
import net.github.douwevos.cnc.poly.PolyDot;
import net.github.douwevos.cnc.poly.PolyForm;
import net.github.douwevos.cnc.tool.FraseTool;
import net.github.douwevos.cnc.tool.Tool;
import net.github.douwevos.cnc.type.Distance;
import net.github.douwevos.cnc.type.DistanceUnit;
import net.github.douwevos.justflat.values.Bounds2D;

public class HolerModel implements Iterable<Item> {

	Distance holeDiameterM5 = ofMicroMeters(3100); // m5
	Distance holeDiameterM5Loose = ofMicroMeters(3250); // m5
	Distance holeDiameterM6 = ofMicroMeters(4500); // m6

	
	
//	private SourcePiece sourcePiece = new SourcePiece(ofMillMeters(600), ofMillMeters(238), ofMillMeters(2));
	private SourcePiece sourcePiece = new SourcePiece(ofMillMeters(300), ofMillMeters(50), ofMillMeters(2));
	
//	private List<Item> items = new ArrayList<>();
	
	public List<ToolItemRun> itemRuns = new ArrayList<>();
	
	private List<Tool> toolList = new ArrayList<>();
	
	private static long millis(int k) {
		return ofMillMeters(k).asMicrometers();
	}
	
	
	int SIZE = 50;
	int CENTER = 25;
	
	
//	long depth = ofMicroMeters(2500).asMicrometers();

	
	public HolerModel(boolean isTest) {


//		createSamba2Go();


		
//		createSimpleBearingHolder();
		

		createSingleDeepening();
		
//		itemRun.add(new ItemText(textLocation, "A", textSize));
//		itemRun.add(new ItemGroup(new ItemText(textLocation, "A", textSize)));
//		items.add();

//		createXBoard();
		
		
//		create10mmAngeledLine();
//		createActMotorHolder();

//		createXSliderBackBoard(isTest);
//		createXSliderPadElevation();
//		createXSliderHeightAdapter();
//		createXSliderActMotorHolder();
		
//		createCncSpindleBoard();
//		createCncSpindleBoardElevation();
//		createCncSpindleBoardConnector();

//		createUpDownActMotorHolder();
//		createUpDownCloseToActMotorHolder();
//		createUpDownBottomPlate();


//		createMoldYBoard();
//		createYBoardActMotorHolder();

//		createSimpleBearingHolder();
//		createMultiFanBoard();


//		List<PolyDot> points = new ArrayList<>();
//		points.add(new PolyDot(millis(21), millis(0), false));
//		points.add(new PolyDot(millis(0), millis(0), false));
//		points.add(new PolyDot(millis(0), millis(41), false));
//		points.add(new PolyDot(millis(4), millis(45), false));
//		points.add(new PolyDot(millis(56), millis(45), false));
//		points.add(new PolyDot(millis(60), millis(41), false));
//		points.add(new PolyDot(millis(60), millis(0), false));
//		points.add(new PolyDot(millis(39), millis(0), false));
//		ItemPolyLine polyLine = new ItemPolyLine(new PolyForm(points, false), 20);
//		itemRun.add(polyLine);

	}
	

	private void createMultiFanBoard() {
		sourcePiece = new SourcePiece(ofMillMeters(200), ofMillMeters(200), ofMillMeters(2));

		FraseTool tool1 = new FraseTool(new Distance(3000, DistanceUnit.MICROMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);

		
//		long s = millis(200);
//		long t = millis(4);
//		List<PolyDot> points = new ArrayList<>();
//		points.add(new PolyDot(0, t, false));
//		points.add(new PolyDot(0, 0, false));
//		points.add(new PolyDot(s, 0, false));
//		points.add(new PolyDot(s, t, false));
//		ItemPolyLine polyLine = new ItemPolyLine(new PolyForm(points, false), millis(1));
//		itemRun.add(polyLine);
		
		
		
		fanAt(itemRun, tool1, ofMillMeters(50).asMicrometers(), ofMillMeters(50).asMicrometers());
//		fanAt(itemRun, tool1, ofMillMeters(50).asMicrometers(), ofMillMeters(150).asMicrometers());
//		fanAt(itemRun, tool1, ofMillMeters(150).asMicrometers(), ofMillMeters(50).asMicrometers());
//		fanAt(itemRun, tool1, ofMillMeters(150).asMicrometers(), ofMillMeters(150).asMicrometers());
		
	}

	
	private void fanAt(ToolItemRun itemRun, FraseTool tool, long centerX, long centerY) {

		
		long s = millis(40);
		List<PolyDot> points = new ArrayList<>();
		points.add(new PolyDot(centerX -s, centerY -s, false));
		points.add(new PolyDot(centerX -s, centerY +s, false));
		points.add(new PolyDot(centerX +s, centerY +s, false));
		points.add(new PolyDot(centerX +s, centerY -s, false));
		ItemPolyLine polyLine = new ItemPolyLine(new PolyForm(points, true), millis(1));
		itemRun.add(polyLine);
		

		long fullDepth = millis(18);
		
		itemRun.add(new ItemCircle(Point3D.of(centerX, centerY, fullDepth), Distance.ofMillMeters(71), FillMode.NONE));


		long screwDepth = millis(8);
		s = millis(72)/2;
		Distance screwSize = Distance.ofMicroMeters(1300);
		itemRun.add(new ItemCircle(Point3D.of(centerX-s, centerY-s, screwDepth), screwSize, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(centerX+s, centerY-s, screwDepth), screwSize, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(centerX+s, centerY+s, screwDepth), screwSize, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(centerX-s, centerY+s, screwDepth), screwSize, FillMode.SPIRAL));

		
		
	}


	private void createYBoardActMotorHolder() {
		sourcePiece = new SourcePiece(ofMillMeters(110), ofMillMeters(80), ofMillMeters(2));
		
		FraseTool tool1 = new FraseTool(new Distance(3000, DistanceUnit.MICROMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);
		
		long shiftX = millis(5);
		long shiftY = millis(5);
		long width = millis(100) + tool1.getDiameter();
		
//		long depthFull = 20_000;
		long depthFull = 19_000;
		
		long distanceScrews = 31000/2;
				
		Distance xCenter = ofMicroMeters(shiftX + width/2);

		long lxCenter = xCenter.asMicrometers();
		long lyCenter = shiftY + 26500;

		Point3D centerLocation = Point3D.of(lxCenter, lyCenter, depthFull);
		itemRun.add(new ItemCircle(centerLocation , ofMillMeters(27), FillMode.NONE));

		
		Point3D screws[] = new Point3D[] { 
				Point3D.of(lxCenter - distanceScrews, lyCenter - distanceScrews, depthFull),
				Point3D.of(lxCenter + distanceScrews, lyCenter - distanceScrews, depthFull),
				Point3D.of(lxCenter - distanceScrews, lyCenter + distanceScrews, depthFull),
				Point3D.of(lxCenter + distanceScrews, lyCenter + distanceScrews, depthFull),
				
		};
		
		Distance screwDiameter = ofMicroMeters(1150);
		
		for(Point3D s : screws) {
			itemRun.add(new ItemCircle(s.withZ(depthFull/3), ofMicroMeters(5000), FillMode.SPIRAL));
			itemRun.add(new ItemCircle(s, screwDiameter, FillMode.SPIRAL));
		}


		List<PolyDot> points = new ArrayList<>();
		points.add(new PolyDot(shiftX+millis(0)-2000, shiftY+millis(0), false));
		points.add(new PolyDot(shiftX+width+2000, shiftY+millis(0), false));
		ItemPolyLine polyLine = new ItemPolyLine(new PolyForm(points, false), depthFull);
		itemRun.add(polyLine);

		long inset = 8_000;
		long height = millis(52);
		
		points = new ArrayList<>();
		points.add(new PolyDot(shiftX+millis(0), shiftY+millis(0), false));
		points.add(new PolyDot(shiftX+millis(0), shiftY+height-inset, false));
		points.add(new PolyDot(shiftX+inset, shiftY+height, false));
		points.add(new PolyDot(shiftX+width-inset, shiftY+height, false));
		points.add(new PolyDot(shiftX+width, shiftY+height-inset, false));
		points.add(new PolyDot(shiftX+width, shiftY+millis(0), false));
		polyLine = new ItemPolyLine(new PolyForm(points, false), depthFull);
		itemRun.add(polyLine);

	
		long helpLineOffset = millis(18);

		points = new ArrayList<>();
		points.add(new PolyDot(shiftX+helpLineOffset, shiftY+millis(0), false));
		points.add(new PolyDot(shiftX+helpLineOffset, shiftY+millis(10), false));
		polyLine = new ItemPolyLine(new PolyForm(points, false), 800);
		itemRun.add(polyLine);

		points = new ArrayList<>();
		points.add(new PolyDot(shiftX+width-helpLineOffset, shiftY+millis(0), false));
		points.add(new PolyDot(shiftX+width-helpLineOffset, shiftY+millis(10), false));
		polyLine = new ItemPolyLine(new PolyForm(points, false), 800);
		itemRun.add(polyLine);

	
	}
	

	
	private void createMoldYBoard() {
		sourcePiece = new SourcePiece(ofMillMeters(180), ofMillMeters(100), ofMillMeters(6));
		
		FraseTool tool1 = new FraseTool(new Distance(3000, DistanceUnit.MICROMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);
		
		Bounds2D bounds =new Bounds2D(0, 0, millis(150), millis(70));
		ItemRectangle rectangle = new ItemRectangle(bounds, 0, ofMillMeters(2).asMicrometers(), net.github.douwevos.cnc.holer.item.ItemRectangle.FillMode.SPIRAL);
		itemRun.add(rectangle);
	

		long shift = bounds.right - millis(135);
		
		
		
		long v1 = Distance.ofMillMeters(15).asMicrometers();
		long v2 = (v1 + Distance.ofMillMeters(35).asMicrometers());

		long h1 = shift;
		long h2 = h1 + Distance.ofMillMeters(35).asMicrometers();

		v1 = bounds.top - v1;
		v2 = bounds.top - v2;
		
		long depth = millis(19);
		
		itemRun.add(new ItemCircle(Point3D.of(h1, v1, depth), holeDiameterM5Loose, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h2, v1, depth), holeDiameterM5Loose, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h1, v2, depth), holeDiameterM5Loose, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h2, v2, depth), holeDiameterM5Loose, FillMode.SPIRAL));

		
		itemRun.add(new ItemCircle(Point3D.of(bounds.right, bounds.top, ofMillMeters(2).asMicrometers()), Distance.ofMillMeters(6), FillMode.SPIRAL));

		
	}


	private void createXSliderActMotorHolder() {
		sourcePiece = new SourcePiece(ofMillMeters(80), ofMillMeters(80), ofMillMeters(2));
		
		FraseTool tool1 = new FraseTool(new Distance(3000, DistanceUnit.MICROMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);
		
		long shiftX = millis(5);
		long shiftY = millis(5);
		long width = millis(70) + tool1.getDiameter();
		
//		long depthFull = 20_000;
		long depthFull = 20_000;
		
		long distanceScrews = 31000/2;
				
		Distance xCenter = ofMicroMeters(shiftX + width/2);

		long lxCenter = xCenter.asMicrometers();
		long lyCenter = shiftY + 26500;
		Point3D centerLocation = Point3D.of(lxCenter, lyCenter, depthFull);
		itemRun.add(new ItemCircle(centerLocation , ofMillMeters(27), FillMode.NONE));

		Point3D screws[] = new Point3D[] { 
				Point3D.of(lxCenter - distanceScrews, lyCenter - distanceScrews, depthFull),
				Point3D.of(lxCenter + distanceScrews, lyCenter - distanceScrews, depthFull),
				Point3D.of(lxCenter - distanceScrews, lyCenter + distanceScrews, depthFull),
				Point3D.of(lxCenter + distanceScrews, lyCenter + distanceScrews, depthFull),
				
		};
		
		Distance screwDiameter = ofMicroMeters(1050);
		
		for(Point3D s : screws) {
			itemRun.add(new ItemCircle(s.withZ(depthFull/3), holeDiameterM5, FillMode.SPIRAL));
		}

		for(Point3D s : screws) {
			itemRun.add(new ItemCircle(s, screwDiameter, FillMode.SPIRAL));
		}

		List<PolyDot> points = new ArrayList<>();
		points.add(new PolyDot(shiftX+millis(0)-2000, shiftY+millis(0), false));
		points.add(new PolyDot(shiftX+width+2000, shiftY+millis(0), false));
		ItemPolyLine polyLine = new ItemPolyLine(new PolyForm(points, false), depthFull);
		itemRun.add(polyLine);

		long inset = 5_000;
		long height = millis(52);
		
		points = new ArrayList<>();
		points.add(new PolyDot(shiftX+millis(0), shiftY+millis(0), false));
		points.add(new PolyDot(shiftX+millis(0), shiftY+height-inset, false));
		points.add(new PolyDot(shiftX+inset, shiftY+height, false));
		points.add(new PolyDot(shiftX+width-inset, shiftY+height, false));
		points.add(new PolyDot(shiftX+width, shiftY+height-inset, false));
		points.add(new PolyDot(shiftX+width, shiftY+millis(0), false));
		polyLine = new ItemPolyLine(new PolyForm(points, false), 7_000, depthFull);
		itemRun.add(polyLine);
	}


	private void createCncSpindleBoardConnector() {

		sourcePiece = new SourcePiece(ofMillMeters(80), ofMillMeters(50), ofMillMeters(2));
		
		FraseTool tool1 = new FraseTool(new Distance(3, DistanceUnit.MILLIMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);

		long depth = 19_000L;
		long depthLowering = 2_000L;

		
		
		int hMain = 5_000;
		long vMain = 3_000;
		
		int width = 44;
		
		int longSlope=4;
		int shortSlope = 2;
		
		int height = 44;

		long ha1 = hMain +ofMillMeters(width).asMicrometers() - 30_000;
		long va1 = vMain + ofMillMeters(height/2).asMicrometers(); 
		itemRun.add(new ItemCircle(Point3D.of(ha1, va1, depth), ofMicroMeters(8_900), FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(ha1, va1, depthLowering), ofMicroMeters(21_500), FillMode.SPIRAL));

		
		List<PolyDot> asList = Arrays.asList(
				new PolyDot(hMain +ofMillMeters(width).asMicrometers(), vMain , false),
				new PolyDot(hMain+ofMillMeters(width).asMicrometers(), vMain+ofMillMeters(height).asMicrometers(), false),
				new PolyDot(hMain+ofMillMeters(longSlope).asMicrometers(), vMain+ofMillMeters(height).asMicrometers(), false),
				new PolyDot(hMain, vMain+ofMillMeters(height-shortSlope).asMicrometers(), false),
				new PolyDot(hMain-ofMillMeters(8).asMicrometers(), vMain+ofMillMeters(height/2).asMicrometers(), true),
				new PolyDot(hMain, vMain+ofMillMeters(shortSlope).asMicrometers(), false),
				new PolyDot(hMain+ofMillMeters(longSlope).asMicrometers(), vMain, false)
				);

		itemRun.add(new ItemPolyLine(new PolyForm(asList , true) , depth));



		

	}

	
	
	private void createCncSpindleBoard() {

		sourcePiece = new SourcePiece(ofMillMeters(150), ofMillMeters(150), ofMillMeters(2));

		long totalWidth = 150_000L;
		long totalHeight = 150_000L;
		
		long blockHeight = 36000l;
		long blockWidth = 32000l;
		long centerGlider = 25_000l;
		long insets = 6000l;
		
		long va1 = centerGlider - ((blockHeight/2) - insets);
		long va2 = centerGlider + ((blockHeight/2) - insets);

		long vb1 = totalHeight - centerGlider - ((blockHeight/2) - insets);
		long vb2 = totalHeight - centerGlider + ((blockHeight/2) - insets);

		
		long ha1 = insets;
		long ha2 = blockWidth - insets;

		long hb1 = totalWidth - insets;
		long hb2 = totalWidth - (blockWidth - insets);

		
		FraseTool tool1 = new FraseTool(new Distance(3, DistanceUnit.MILLIMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);

		long depth = 11_500L;

		Distance holeDiameterM = holeDiameterM5;

		// slider left-bottom
		itemRun.add(new ItemCircle(Point3D.of(ha1, va1, depth), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(ha2, va1, depth), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(ha2, va2, depth), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(ha1, va2, depth), holeDiameterM, FillMode.SPIRAL));

		
		// slider left-top
		itemRun.add(new ItemCircle(Point3D.of(ha1, vb1, depth), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(ha2, vb1, depth), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(ha2, vb2, depth), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(ha1, vb2, depth), holeDiameterM, FillMode.SPIRAL));

		// slider right-top
		itemRun.add(new ItemCircle(Point3D.of(hb1, vb1, depth), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(hb2, vb1, depth), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(hb2, vb2, depth), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(hb1, vb2, depth), holeDiameterM, FillMode.SPIRAL));

		// slider right-bottom
		itemRun.add(new ItemCircle(Point3D.of(hb1, va1, depth), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(hb2, va1, depth), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(hb2, va2, depth), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(hb1, va2, depth), holeDiameterM, FillMode.SPIRAL));

	}

	
	private void createCncSpindleBoardElevation() {

		sourcePiece = new SourcePiece(ofMillMeters(200), ofMillMeters(50), ofMillMeters(2));

		long totalWidth = 150_000L;
		long totalHeight = 150_000L;
		
		long spindleHoleWidth = 95_500L;
		long spindleHoleHeight =35_000L;
		
		long blockHeight = 36000l;
		long blockWidth = 32000l;
		long centerGlider = 25_000l;
		long insets = 6000l;
		
		long va1 = centerGlider - ((blockHeight/2) - insets);
		long va2 = centerGlider + ((blockHeight/2) - insets);

		
		long shiftX =  4_000L;
		
		long ha1 = shiftX+insets;
		long ha2 = shiftX+blockWidth - insets;

		long hb1 = shiftX+totalWidth - insets;
		long hb2 = shiftX+totalWidth - (blockWidth - insets);

		
		FraseTool tool1 = new FraseTool(new Distance(3, DistanceUnit.MILLIMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);

//		long depth = 11_500L;
//		long depthSenkung = 7_000L;

		long depth = 11_500L;
		long depthSenkung = 4_600L;

		Distance holeDiameterM = ofMillMeters(7);

		// slider left-bottom
		itemRun.add(new ItemCircle(Point3D.of(ha1, va1, depthSenkung), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(ha2, va1, depthSenkung), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(ha2, va2, depthSenkung), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(ha1, va2, depthSenkung), holeDiameterM, FillMode.SPIRAL));


		// slider right-bottom
		itemRun.add(new ItemCircle(Point3D.of(hb1, va1, depthSenkung), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(hb2, va1, depthSenkung), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(hb2, va2, depthSenkung), holeDiameterM, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(hb1, va2, depthSenkung), holeDiameterM, FillMode.SPIRAL));

		
//		75_000L - 47_750L; 
		
		
		
		long s = totalHeight/2;
		
		
		
//		va1 = 50_000l -( (totalHeight-spindleHoleWidth)/2);
		va1 = ( (totalHeight-spindleHoleWidth)/2);
		ha1 = shiftX+60_000L;
		
		System.out.println("va1="+va1);

		itemRun.add(new ItemCircle(Point3D.of(ha1, va1, depth), holeDiameterM6, FillMode.NONE));
		itemRun.add(new ItemCircle(Point3D.of(ha1 + spindleHoleHeight, va1, depth), holeDiameterM6, FillMode.NONE));
		itemRun.add(new ItemCircle(Point3D.of(ha1 + spindleHoleHeight*3, va1, depth), holeDiameterM6, FillMode.NONE));

		
	}
	
	private void createSamba2Go() {
		FraseTool tool1 = new FraseTool(new Distance(3, DistanceUnit.MILLIMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);

		long centerX = ofMillMeters(100).asMicrometers();
		long centerY = ofMillMeters(100).asMicrometers();
		long depth = ofMillMeters(16).asMicrometers();
//		long depth = ofMicroMeters(150).asMicrometers();
		
		
		
		int longSlope = 20;
		int shortSlope = 4;
		
		
		int vLocation = 18+1;
		int hLocation = 26+1;
		int width = 79-3;
		int height = 26-3;
		
		long vMain = centerY + ofMillMeters(vLocation).asMicrometers();
		long hMain = centerX - ofMillMeters(hLocation).asMicrometers();
		
		List<PolyDot> asList = Arrays.asList(
				new PolyDot(hMain+ofMillMeters(width-longSlope).asMicrometers(), vMain, false),
				new PolyDot(hMain+ofMillMeters(width).asMicrometers(), vMain+ofMillMeters(shortSlope).asMicrometers(), false),
				new PolyDot(hMain+ofMillMeters(width+8).asMicrometers(), vMain+ofMillMeters(height/2).asMicrometers(), true),
				new PolyDot(hMain+ofMillMeters(width).asMicrometers(), vMain+ofMillMeters(height-shortSlope).asMicrometers(), false),
				new PolyDot(hMain+ofMillMeters(width-longSlope).asMicrometers(), vMain+ofMillMeters(height).asMicrometers(), false),
				new PolyDot(hMain+ofMillMeters(longSlope).asMicrometers(), vMain+ofMillMeters(height).asMicrometers(), false),
				new PolyDot(hMain, vMain+ofMillMeters(height-shortSlope).asMicrometers(), false),
				new PolyDot(hMain-ofMillMeters(8).asMicrometers(), vMain+ofMillMeters(height/2).asMicrometers(), true),
				new PolyDot(hMain, vMain+ofMillMeters(shortSlope).asMicrometers(), false),
				new PolyDot(hMain+ofMillMeters(longSlope).asMicrometers(), vMain, false)
				);

//		itemRun.add(new ItemPolyLine(new PolyForm(asList , true) , depth));

		
		
//		List<PolyDot> rotate = rotate(asList, centerX, centerY, 120*Math.PI/180d);
//		itemRun.add(new ItemPolyLine(new PolyForm(rotate , true) , depth));

//		List<PolyDot> rotate = rotate(asList, centerX, centerY, 240*Math.PI/180d);
//		itemRun.add(new ItemPolyLine(new PolyForm(rotate , true) , depth));

		
		itemRun.add(new ItemCircle(Point3D.of(centerX, centerY, depth), ofMillMeters(200), FillMode.NONE));
		
		
		//
//		
//		
//		long depth2 = ofMillMeters(6).asMicrometers();
//		Distance holeDiameter1 = ofMicroMeters(3300);
//
//		Distance holeDiameter2 = ofMicroMeters(12500);
//		
//		// piece needs to be moved 1 cm from nul-point
//		
//		long shift = Distance.ofMillMeters(8).asMicrometers();
//
//		long v1 = Distance.ofMillMeters(10).asMicrometers();
//		long v2 = v1 + Distance.ofMillMeters(35).asMicrometers();
//
//		long h1 = shift;
//		long h2 = h1 + Distance.ofMillMeters(35).asMicrometers();
//		long h3 = h1 + new Distance(18, DistanceUnit.CM).asMicrometers();
//		long h4 = h3 + Distance.ofMillMeters(35).asMicrometers();
//
//		long ho1 = h1-Distance.ofMillMeters(8).asMicrometers();
//		long ho2 = (h2 + h3)/2;
//		long vo1 = (v1 + v2)/2;
//
//		long ho3 = (h3 + h4)/2;
//
//		
//
//		itemRun.add(new ItemCircle(Point3D.of(h2, v1, depth2), holeDiameter2, FillMode.SPIRAL));
//		itemRun.add(new ItemCircle(Point3D.of(h1, v1, depth2), holeDiameter2, FillMode.SPIRAL));
//		
//		itemRun.add(new ItemCircle(Point3D.of(ho1, vo1, depth), holeDiameter1, FillMode.SPIRAL));
//		
//		itemRun.add(new ItemCircle(Point3D.of(h1, v2, depth2), holeDiameter2, FillMode.SPIRAL));
//		itemRun.add(new ItemCircle(Point3D.of(h2, v2, depth2), holeDiameter2, FillMode.SPIRAL));
////
//		itemRun.add(new ItemCircle(Point3D.of(h3, v2, depth2), holeDiameter2, FillMode.SPIRAL));
//		itemRun.add(new ItemCircle(Point3D.of(h3, v1, depth2), holeDiameter2, FillMode.SPIRAL));
//		itemRun.add(new ItemCircle(Point3D.of(h4, v1, depth2), holeDiameter2, FillMode.SPIRAL));
//		itemRun.add(new ItemCircle(Point3D.of(h4, v2, depth2), holeDiameter2, FillMode.SPIRAL));
//		
//		
//
//		
//		itemRun.add(new ItemCircle(Point3D.of(ho2, vo1, depth), holeDiameter1, FillMode.SPIRAL));
//
//		long bottom = 0;
//		long top = Distance.ofMillMeters(53).asMicrometers();
//		
//		long fw = Distance.ofMicroMeters(2000).asMicrometers();;
//
//		
//		for(int r = 0; r<5; r++) {
//			
//			long sub = r*fw - Distance.ofMillMeters(5).asMicrometers();
//			
//			List<PolyDot> asList = Arrays.asList(
//					new PolyDot(ho3+sub, bottom, false),
//					new PolyDot(ho3+sub, top, false)
//				);
//			itemRun.add(new ItemPolyLine(new PolyForm(asList , false) , depth/2));
//		}

	}
	
	private List<PolyDot> rotate(List<PolyDot> source, long centerX, long centerY, double angle) {
		List<PolyDot> result = new ArrayList<>();
		double sin = Math.sin(angle);
		double cos = Math.cos(angle);
		for(PolyDot dotIn : source) {
			double x = dotIn.x-centerX;
			double y = dotIn.y-centerY;
			
			double nx = sin*y + cos*x;
			double ny = -sin*x + cos*y;
			PolyDot dotOut = new PolyDot(centerX+Math.round(nx), centerY+Math.round(ny), dotIn.isCurve);
			result.add(dotOut);
		}
		return result;
	}

	private void createSingleDeepening() {

//		FraseTool tool1 = new FraseTool(new Distance(3, DistanceUnit.MILLIMETER));
		FraseTool tool1 = new FraseTool(new Distance(3, DistanceUnit.MILLIMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);
		
//		long depth2 = ofMicroMeters(500).asMicrometers();
		long depth2 = ofMillMeters(5).asMicrometers();
		Distance holeDiameter2 = ofMicroMeters(25000);
		

//		itemRun.add(new ItemCircle(Point3D.of(0, 0, depth2), holeDiameter2, FillMode.SPIRAL));

//		itemRun.add(new ItemCircle(Point3D.of(0, 0, depth2), Distance.ofMicroMeters(1300), FillMode.SPIRAL));
//
//		itemRun.add(new ItemCircle(Point3D.of(millis(10), 0, depth2), Distance.ofMicroMeters(1800), FillMode.SPIRAL));
//		itemRun.add(new ItemCircle(Point3D.of(millis(20), 0, depth2), Distance.ofMicroMeters(2300), FillMode.SPIRAL));
//

		Bounds2D bounds = new Bounds2D(0, 0, 12_000, 12_000);
		itemRun.add(new ItemRectangle(bounds , 0, 1_000, net.github.douwevos.cnc.holer.item.ItemRectangle.FillMode.SPIRAL));

		
//		itemRun.add(new ItemCircle(Point3D.of(0, 0, ofMillMeters(10).asMicrometers()), 8_000, FillMode.SPIRAL));


	}

	
	private void createXSliderHeightAdapter() {
		
		sourcePiece = new SourcePiece(ofMillMeters(300), ofMillMeters(50), ofMillMeters(2));
		
		FraseTool tool1 = new FraseTool(new Distance(3, DistanceUnit.MILLIMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);
		
		long depth = ofMillMeters(10).asMicrometers();
		long depth2 = ofMillMeters(4).asMicrometers();
		Distance holeDiameter1 = ofMicroMeters(3300);

		Distance holeDiameter2 = ofMicroMeters(13500);
		
		// piece needs to be moved 1 cm from nul-point
		
		long shiftX = Distance.ofMillMeters(8).asMicrometers();

		long v1 = Distance.ofMillMeters(10).asMicrometers();
		long v2 = Distance.ofMillMeters(45).asMicrometers();

		long h1 = shiftX;
		long h2 = h1 + Distance.ofMillMeters(35).asMicrometers();
		long h3 = h1 + new Distance(18, DistanceUnit.CM).asMicrometers();
		long h4 = h3 + Distance.ofMillMeters(35).asMicrometers();


		itemRun.add(new ItemCircle(Point3D.of(h2, v1, depth2), holeDiameter2, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h1, v1, depth2), holeDiameter2, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h1, v2, depth2), holeDiameter2, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h2, v2, depth2), holeDiameter2, FillMode.SPIRAL));

		itemRun.add(new ItemCircle(Point3D.of(h3, v2, depth2), holeDiameter2, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h3, v1, depth2), holeDiameter2, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h4, v1, depth2), holeDiameter2, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h4, v2, depth2), holeDiameter2, FillMode.SPIRAL));

//		long ho1 = h1-Distance.ofMillMeters(8).asMicrometers();
		long ho1 = (h2*3 + h3)/4;
		long ho2 = (h2 + h3*3)/4;
		long vo1 = (v1 + v2)/2;
		long ho3 = (h3 + h4)/2;

		itemRun.add(new ItemCircle(Point3D.of(ho1, vo1, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(ho2, vo1, depth), holeDiameter1, FillMode.SPIRAL));

	}

	

	private void createXSliderPadElevation() {

		sourcePiece = new SourcePiece(ofMillMeters(300), ofMillMeters(150), ofMillMeters(2));

		sourcePiece = new SourcePiece(ofMillMeters(300), ofMillMeters(150), ofMillMeters(2));

		FraseTool tool1 = new FraseTool(new Distance(3, DistanceUnit.MILLIMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);

		Distance holeDiameter1 = ofMicroMeters(3200);
		long depth = ofMillMeters(7).asMicrometers();
		long depthFull = ofMillMeters(11).asMicrometers();
		
		
		long v1 = Distance.ofMillMeters(10).asMicrometers();
		long v2 = v1 + Distance.ofMillMeters(35).asMicrometers();
		long v3 = Distance.ofMillMeters(140).asMicrometers();
		long v4 = v3 - Distance.ofMillMeters(35).asMicrometers();

		long h2 = Distance.ofMillMeters(35).asMicrometers();
		long h3 = new Distance(18, DistanceUnit.CM).asMicrometers();
		long h4 = h3 + Distance.ofMillMeters(35).asMicrometers();

		long ho1 = (h2*3 + h3)/4;
		long ho2 = (h2 + h3*3)/4;
		long vo1 = (v1 + v2)/2;
		long vo2 = (v3 + v4)/2;

		System.out.println("ho1="+ho1);
		System.out.println("ho2="+ho2);
		System.out.println("vo1="+vo1);
		System.out.println("vo2="+vo2);
		
//		itemRun.add(new ItemCircle(Point3D.of(ho1, vo1, depth), holeDiameter1, FillMode.SPIRAL));
//		itemRun.add(new ItemCircle(Point3D.of(ho2, vo1, depth), holeDiameter1, FillMode.SPIRAL));
//
//		itemRun.add(new ItemCircle(Point3D.of(ho1, vo2, depth), holeDiameter1, FillMode.SPIRAL));
//		itemRun.add(new ItemCircle(Point3D.of(ho2, vo2, depth), holeDiameter1, FillMode.SPIRAL));

		
		
		
		// XSpindel-gewinde connector

		long hcenter = (h2 + h3)/2;
		long vcenter = (v3 + v1)/2;

		long connectorXDist = ofMillMeters(40).asMicrometers()/2;
		long connectorYDist = ofMillMeters(25).asMicrometers()/2;
		
		long shiftX = -(hcenter-connectorXDist);
		long shiftY = -(vcenter-connectorYDist);
		
		itemRun.add(new ItemCircle(Point3D.of(shiftX + hcenter-connectorXDist, shiftY + vcenter-connectorYDist, depthFull), holeDiameterM5, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(shiftX + hcenter-connectorXDist, shiftY + vcenter+connectorYDist, depthFull), holeDiameterM5, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(shiftX + hcenter+connectorXDist, shiftY + vcenter-connectorYDist, depthFull), holeDiameterM5, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(shiftX + hcenter+connectorXDist, shiftY + vcenter+connectorYDist, depthFull), holeDiameterM5, FillMode.SPIRAL));

		long connectorWidth = ofMillMeters(51).asMicrometers();
		long connectorHeight = ofMillMeters(40).asMicrometers();
		
//		List<PolyDot> asList = Arrays.asList(
//				new PolyDot(hcenter-connectorWidth/2, vcenter-connectorHeight/2, false),
//				new PolyDot(hcenter+connectorWidth/2, vcenter-connectorHeight/2, false),
//				new PolyDot(hcenter+connectorWidth/2, vcenter+connectorHeight/2, false),
//				new PolyDot(hcenter-connectorWidth/2, vcenter+connectorHeight/2, false)
//				);
//
//		itemRun.add(new ItemPolyLine(new PolyForm(asList , true) , 200));

		
	
	}
	
	private void createXSliderBackBoard(boolean isTest) {

		sourcePiece = new SourcePiece(ofMillMeters(300), ofMillMeters(150), ofMillMeters(2));

		FraseTool tool1 = new FraseTool(new Distance(3, isTest ? DistanceUnit.MICROMETER : DistanceUnit.MILLIMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);

		Distance holeDiameter1 = ofMicroMeters(3200);
		long depth = ofMillMeters(11).asMicrometers();
//		long depth = ofMicroMeters(200).asMicrometers();

		
		long shift = Distance.ofMillMeters(18).asMicrometers();

		shift = 0L; // TODO if new board the remove this.
		
		long v1 = Distance.ofMillMeters(10).asMicrometers();
		long v2 = v1 + Distance.ofMillMeters(35).asMicrometers();
		long v3 = Distance.ofMillMeters(140).asMicrometers();
		long v4 = v3 - Distance.ofMillMeters(35).asMicrometers();

		long h1 = shift;
		long h2 = h1 + Distance.ofMillMeters(35).asMicrometers();
		long h3 = h1 + new Distance(18, DistanceUnit.CM).asMicrometers();
		long h4 = h3 + Distance.ofMillMeters(35).asMicrometers();

		itemRun.add(new ItemCircle(Point3D.of(h1, v1, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h2, v1, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h1, v2, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h2, v2, depth), holeDiameter1, FillMode.SPIRAL));

		itemRun.add(new ItemCircle(Point3D.of(h1, v3, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h2, v3, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h1, v4, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h2, v4, depth), holeDiameter1, FillMode.SPIRAL));

		itemRun.add(new ItemCircle(Point3D.of(h3, v3, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h4, v3, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h3, v4, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h4, v4, depth), holeDiameter1, FillMode.SPIRAL));


		itemRun.add(new ItemCircle(Point3D.of(h3, v1, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h4, v1, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h3, v2, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h4, v2, depth), holeDiameter1, FillMode.SPIRAL));


		long ho1 = (h2*3 + h3)/4;
		long ho2 = (h2 + h3*3)/4;
		long vo1 = (v1 + v2)/2;
		long vo2 = (v3 + v4)/2;

		itemRun.add(new ItemCircle(Point3D.of(ho1, vo1, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(ho2, vo1, depth), holeDiameter1, FillMode.SPIRAL));

		itemRun.add(new ItemCircle(Point3D.of(ho1, vo2, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(ho2, vo2, depth), holeDiameter1, FillMode.SPIRAL));

		
		
		
		// XSpindel-gewinde connector

		long hcenter = (h2 + h3)/2;
		long vcenter = (v3 + v1)/2;

		long connectorXDist = ofMillMeters(40).asMicrometers()/2;
		long connectorYDist = ofMillMeters(25).asMicrometers()/2;
		
		itemRun.add(new ItemCircle(Point3D.of(hcenter-connectorXDist, vcenter-connectorYDist, depth), holeDiameterM5, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(hcenter-connectorXDist, vcenter+connectorYDist, depth), holeDiameterM5, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(hcenter+connectorXDist, vcenter-connectorYDist, depth), holeDiameterM5, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(hcenter+connectorXDist, vcenter+connectorYDist, depth), holeDiameterM5, FillMode.SPIRAL));

		long connectorWidth = ofMillMeters(51).asMicrometers();
		long connectorHeight = ofMillMeters(40).asMicrometers();
		
		List<PolyDot> asList = Arrays.asList(
				new PolyDot(hcenter-connectorWidth/2, vcenter-connectorHeight/2, false),
				new PolyDot(hcenter+connectorWidth/2, vcenter-connectorHeight/2, false),
				new PolyDot(hcenter+connectorWidth/2, vcenter+connectorHeight/2, false),
				new PolyDot(hcenter-connectorWidth/2, vcenter+connectorHeight/2, false)
				);

		itemRun.add(new ItemPolyLine(new PolyForm(asList , true) , 200));

		
	
	}

	private void createXBoard() {
		long depth = ofMillMeters(5).asMicrometers();
		FraseTool tool1 = new FraseTool(new Distance(2970, DistanceUnit.MICROMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);
		
		
		long sourceHeight = sourcePiece.getHeight().asMicrometers();

		long v1 = Distance.ofMillMeters(15).asMicrometers();
		long v2 = v1 + Distance.ofMillMeters(30).asMicrometers();
		
		long v3 = sourceHeight-v2;
		long v4 = sourceHeight-v1;
		
		long h1 = Distance.ofMillMeters(26).asMicrometers();
		long h2 = h1 + new Distance(15, DistanceUnit.CM).asMicrometers();
		long h3 = h2 + new Distance(15, DistanceUnit.CM).asMicrometers();
		
		
		Distance holeDiameter1 = ofMillMeters(3);

		long block1Height = 19_800;
		long block1CenterDistance  = 46_000/2l;
		
		long vcenter = sourceHeight/2;

//		items.add(new ItemCircle(Point.of(0, 0, depth), Distance.ofMillMeters(6), FillMode.SPIRAL));

		int toolRad = tool1.getDiameter()/2;
		
		
		List<PolyDot> asList = Arrays.asList(
				new PolyDot(-toolRad, 0, false),
				new PolyDot(-toolRad, ofMillMeters(15).asMicrometers(), false));
		
		itemRun.add(new ItemPolyLine(new PolyForm(asList , false) , depth/2));

		asList = Arrays.asList(
				new PolyDot(-toolRad, sourceHeight-ofMillMeters(15).asMicrometers(), false),
				new PolyDot(-toolRad, sourceHeight-ofMillMeters(30).asMicrometers(), false)
				);

		itemRun.add(new ItemPolyLine(new PolyForm(asList , false) , depth/2));

		
		itemRun.add(new ItemCircle(Point3D.of(block1Height/2, vcenter+block1CenterDistance, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(block1Height/2, vcenter-block1CenterDistance, depth), holeDiameter1, FillMode.SPIRAL));
		
		itemRun.add(new ItemCircle(Point3D.of(h1, v1, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h1, v2, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h1, v3, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h1, v4, depth), holeDiameter1, FillMode.SPIRAL));

		itemRun.add(new ItemCircle(Point3D.of(h2, v1, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h2, v2, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h2, v3, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h2, v4, depth), holeDiameter1, FillMode.SPIRAL));

		itemRun.add(new ItemCircle(Point3D.of(h2, vcenter+block1CenterDistance, depth), holeDiameter1, FillMode.SPIRAL));
		itemRun.add(new ItemCircle(Point3D.of(h2, vcenter-block1CenterDistance, depth), holeDiameter1, FillMode.SPIRAL));

		
//		itemRun.add(new ItemCircle(Point.of(h3, v1, depth), holeDiameter1, FillMode.SPIRAL));
//		itemRun.add(new ItemCircle(Point.of(h3, v2, depth), holeDiameter1, FillMode.SPIRAL));
//		itemRun.add(new ItemCircle(Point.of(h3, v3, depth), holeDiameter1, FillMode.SPIRAL));
//		itemRun.add(new ItemCircle(Point.of(h3, v4, depth), holeDiameter1, FillMode.SPIRAL));


	}

	private void createSimpleBearingHolder() {
		
		sourcePiece = new SourcePiece(ofMillMeters(60), ofMillMeters(60), ofMillMeters(2));

		long depth = ofMillMeters(21).asMicrometers();
//		long depth2 = ofMillMeters(5).asMicrometers();
//		long depth2 = ofMicroMeters(300).asMicrometers();

		
		FraseTool tool1 = new FraseTool(new Distance(3175, DistanceUnit.MICROMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);

		Point3D centerLocation = Point3D.of(ofMillMeters(30), ofMillMeters(25), ofMicroMeters(depth));
//		itemRuns.add(new ItemCircle(centerLocation.withZ(depth2), ofMillMeters(24), true));
		itemRun.add(new ItemCircle(centerLocation.withZ(depth), ofMillMeters(11), FillMode.NONE));


		long distanceHoles = ofMillMeters(37).asMicrometers();
		
		itemRun.add(new ItemCircle(centerLocation.addX(-distanceHoles/2), ofMicroMeters(3400), FillMode.NONE));
		itemRun.add(new ItemCircle(centerLocation.addX(distanceHoles/2), ofMicroMeters(3400), FillMode.NONE));

		
		List<PolyDot> points = new ArrayList<>();
		points.add(new PolyDot(millis(21), millis(0), false));
		points.add(new PolyDot(millis(0), millis(0), false));
		points.add(new PolyDot(millis(0), millis(41), false));
		points.add(new PolyDot(millis(4), millis(45), false));
		points.add(new PolyDot(millis(56), millis(45), false));
		points.add(new PolyDot(millis(60), millis(41), false));
		points.add(new PolyDot(millis(60), millis(0), false));
		points.add(new PolyDot(millis(39), millis(0), false));
		ItemPolyLine polyLine = new ItemPolyLine(new PolyForm(points, false), depth);
		itemRun.add(polyLine);
	}

	private void create10mmAngeledLine() {
		long depth = ofMillMeters(11).asMicrometers();

		FraseTool tool1 = new FraseTool(new Distance(2970, DistanceUnit.MICROMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);

		List<PolyDot> points = new ArrayList<>();
		points.add(new PolyDot(millis(0), millis(0), false));
		points.add(new PolyDot(millis(10), millis(10), false));
		ItemPolyLine polyLine = new ItemPolyLine(new PolyForm(points, false), depth);
		itemRun.add(polyLine);
	}

	
	private void createUpDownActMotorHolder() {
		//		FraseTool tool1 = new FraseTool(new Distance(1620, DistanceUnit.MICROMETER));
				FraseTool tool1 = new FraseTool(new Distance(3000, DistanceUnit.MICROMETER));
				toolList.add(tool1);
				ToolItemRun itemRun = new ToolItemRun(tool1);
				itemRuns.add(itemRun);
//				Point textLocation = Point.of(ofMillMeters(10), ofMillMeters(36), ofMillMeters(6));
		//		itemRun.add(new ItemCircle(textLocation, ofMillMeters(15)));
//				int textSize = (int) (ofMillMeters(45).asMicrometers());
		//		itemRun.add(new ItemText(textLocation, "", textSize));
		
				
				long width = millis(50) + tool1.getDiameter();
				
				long depthFull = ofMillMeters(20).asMicrometers();
				long depthBigHole = ofMillMeters(20).asMicrometers();

		
				long distanceScrews = 31000/2;
				
				Distance xCenter = ofMicroMeters(width/2);
				Point3D centerLocation = Point3D.of(xCenter, ofMillMeters(25), ofMicroMeters(depthBigHole));
				
//				itemRun.add(new ItemCircle(centerLocation , ofMillMeters(25), FillMode.NONE));
		
				long lxCenter = xCenter.asMicrometers();
		
				Point3D screws[] = new Point3D[] { 
						Point3D.of(lxCenter - distanceScrews, 25000 - distanceScrews, depthFull),
						Point3D.of(lxCenter + distanceScrews, 25000 - distanceScrews, depthFull),
						Point3D.of(lxCenter - distanceScrews, 25000 + distanceScrews, depthFull),
						Point3D.of(lxCenter + distanceScrews, 25000 + distanceScrews, depthFull),
						
				};
//				
//				for(Point3D s : screws) {
//					itemRun.add(new ItemCircle(s.withZ(depthFull/2), ofMicroMeters(900), FillMode.SPIRAL));
//				}
//
//				for(Point3D s : screws) {
//					itemRun.add(new ItemCircle(s, ofMicroMeters(900), FillMode.SPIRAL));
//				}

				
				List<PolyDot> points = new ArrayList<>();
				points.add(new PolyDot(millis(0), millis(0), false));
				points.add(new PolyDot(millis(0), millis(44), false));
				points.add(new PolyDot(millis(4), millis(48), false));
				points.add(new PolyDot(width-millis(4), millis(48), false));
				points.add(new PolyDot(width, millis(44), false));
				points.add(new PolyDot(width, millis(0), false));
				ItemPolyLine polyLine = new ItemPolyLine(new PolyForm(points, false), depthFull);
				itemRun.add(polyLine);
	}

	
	private void createUpDownCloseToActMotorHolder() {
		sourcePiece = new SourcePiece(ofMillMeters(60), ofMillMeters(60), ofMillMeters(10));
		//		FraseTool tool1 = new FraseTool(new Distance(1620, DistanceUnit.MICROMETER));
				FraseTool tool1 = new FraseTool(new Distance(3000, DistanceUnit.MICROMETER));
				toolList.add(tool1);
				ToolItemRun itemRun = new ToolItemRun(tool1);
				itemRuns.add(itemRun);
//				Point textLocation = Point.of(ofMillMeters(10), ofMillMeters(36), ofMillMeters(6));
		//		itemRun.add(new ItemCircle(textLocation, ofMillMeters(15)));
//				int textSize = (int) (ofMillMeters(45).asMicrometers());
		//		itemRun.add(new ItemText(textLocation, "", textSize));
		
				
				long width = millis(50) + tool1.getDiameter();
				
				long depthFull = ofMillMeters(10).asMicrometers();
				long depthBigHole = ofMillMeters(10).asMicrometers();

		
				
				Distance xCenter = ofMicroMeters(width/2);
				Point3D centerLocation = Point3D.of(xCenter, ofMillMeters(25), ofMicroMeters(depthBigHole));
				
				itemRun.add(new ItemCircle(centerLocation , ofMillMeters(23), FillMode.NONE));

		

				long distanceHoles = ofMillMeters(37).asMicrometers();
				
				itemRun.add(new ItemCircle(centerLocation.addX(-distanceHoles/2), ofMicroMeters(3600), FillMode.NONE));
				itemRun.add(new ItemCircle(centerLocation.addX(distanceHoles/2), ofMicroMeters(3600), FillMode.NONE));


				
				List<PolyDot> points = new ArrayList<>();
				points.add(new PolyDot(millis(0), millis(0), false));
				points.add(new PolyDot(millis(0), millis(44), false));
				points.add(new PolyDot(millis(4), millis(48), false));
				points.add(new PolyDot(width-millis(4), millis(48), false));
				points.add(new PolyDot(width, millis(44), false));
				points.add(new PolyDot(width, millis(0), false));
				ItemPolyLine polyLine = new ItemPolyLine(new PolyForm(points, false), depthFull);
				itemRun.add(polyLine);
	}

	
	private void createUpDownBottomPlate() {
		
		sourcePiece = new SourcePiece(ofMillMeters(150), ofMillMeters(60), ofMillMeters(10));
		
		FraseTool tool1 = new FraseTool(new Distance(3000, DistanceUnit.MICROMETER));
		toolList.add(tool1);
		ToolItemRun itemRun = new ToolItemRun(tool1);
		itemRuns.add(itemRun);
		
		long depth = ofMillMeters(11).asMicrometers();

		long bottomPlateDepth = 10;
		
		Point3D centerLocation = Point3D.of(ofMillMeters(75), ofMillMeters(25 + bottomPlateDepth), ofMicroMeters(depth));

		long distanceHoles = ofMillMeters(37).asMicrometers();
		
		itemRun.add(new ItemCircle(centerLocation.addX(-distanceHoles/2), ofMicroMeters(3400), FillMode.NONE));
		itemRun.add(new ItemCircle(centerLocation.addX(distanceHoles/2), ofMicroMeters(3400), FillMode.NONE));


		long lowering2 = ofMillMeters(2).asMicrometers();
		itemRun.add(new ItemCircle(centerLocation.withZ(lowering2), ofMillMeters(20), FillMode.SPIRAL));


		
		Point3D leftHighLocationA = Point3D.of(ofMillMeters(15), ofMillMeters(9 + bottomPlateDepth), ofMicroMeters(depth));
		Point3D leftHighLocationB = Point3D.of(ofMillMeters(50-15), ofMillMeters(9 + bottomPlateDepth), ofMicroMeters(depth));

		Point3D rightHighLocationA = Point3D.of(ofMillMeters(150-15), ofMillMeters(9 + bottomPlateDepth), ofMicroMeters(depth));
		Point3D rightHighLocationB = Point3D.of(ofMillMeters(150-(50-15)), ofMillMeters(9 + bottomPlateDepth), ofMicroMeters(depth));


		long lowering = ofMillMeters(3).asMicrometers();

		itemRun.add(new ItemCircle(leftHighLocationA.withZ(lowering), ofMillMeters(7), FillMode.SPIRAL));
		itemRun.add(new ItemCircle(leftHighLocationB.withZ(lowering), ofMillMeters(7), FillMode.SPIRAL));

		itemRun.add(new ItemCircle(rightHighLocationA.withZ(lowering), ofMillMeters(7), FillMode.SPIRAL));
		itemRun.add(new ItemCircle(rightHighLocationB.withZ(lowering), ofMillMeters(7), FillMode.SPIRAL));


		itemRun.add(new ItemCircle(leftHighLocationA, lowering, ofMicroMeters(1200), FillMode.SPIRAL));
		itemRun.add(new ItemCircle(leftHighLocationB, lowering, ofMicroMeters(1200), FillMode.SPIRAL));

		itemRun.add(new ItemCircle(rightHighLocationA, lowering, ofMicroMeters(1200), FillMode.SPIRAL));
		itemRun.add(new ItemCircle(rightHighLocationB, lowering, ofMicroMeters(1200), FillMode.SPIRAL));

		
		
	}

	
	private void createActMotorHolder() {
				FraseTool tool1 = new FraseTool(new Distance(2970, DistanceUnit.MICROMETER));
				toolList.add(tool1);
				ToolItemRun itemRun = new ToolItemRun(tool1);
				itemRuns.add(itemRun);
		
				
				long depth = ofMicroMeters(2400).asMicrometers();
				long depth2 = ofMicroMeters(2400).asMicrometers();

				
				
				long width = millis(65);
				long centerX = width/2;
		
				long distanceScrews = 31000/2;
				
//				Point3D centerLocation = Point3D.of(ofMicroMeters(centerX), ofMillMeters(25), ofMicroMeters(depth));
//				itemRun.add(new ItemCircle(centerLocation , ofMillMeters(26), FillMode.NONE));
//		
//		
//				Point3D screws[] = new Point3D[] { 
//						Point3D.of(centerX - distanceScrews, 25000 - distanceScrews, depth),
//						Point3D.of(centerX + distanceScrews, 25000 - distanceScrews, depth),
//						Point3D.of(centerX - distanceScrews, 25000 + distanceScrews, depth),
//						Point3D.of(centerX + distanceScrews, 25000 + distanceScrews, depth),
//						
//				};
//				
//				for(Point3D s : screws) {
//					itemRun.add(new ItemCircle(s, ofMicroMeters(500), FillMode.NONE));
//				}
//
//				List<PolyDot> points = new ArrayList<>();
//				points.add(new PolyDot(millis(0), millis(0), false));
//				points.add(new PolyDot(millis(0), millis(44), false));
//				points.add(new PolyDot(millis(4), millis(48), false));
//				points.add(new PolyDot(width - millis(4), millis(48), false));
//				points.add(new PolyDot(width, millis(44), false));
//				points.add(new PolyDot(width, millis(0), false));
//				ItemPolyLine polyLine = new ItemPolyLine(new PolyForm(points, false), depth2);
//				itemRun.add(polyLine);

				
				
				List<PolyDot> points = new ArrayList<>();
				points.add(new PolyDot(millis(0), millis(0), false));
				points.add(new PolyDot(millis(0), millis(25), false));
				ItemPolyLine polyLine = new ItemPolyLine(new PolyForm(points, false), depth2);
				itemRun.add(polyLine);

				points = new ArrayList<>();
				points.add(new PolyDot(width, millis(0), false));
				points.add(new PolyDot(width, millis(25), false));
				polyLine = new ItemPolyLine(new PolyForm(points, false), depth2);
				itemRun.add(polyLine);

				
	}
	
	public Bounds2D bounds() {
		Bounds2D result = null;
		for(ToolItemRun run : itemRuns) {
			Bounds2D runBounds = run.bounds();
			if (runBounds == null) {
				continue;
			}
			
			int diameter = run.tool.getDiameter();
			long r = (diameter*5)/8;
			runBounds = new Bounds2D(runBounds.left-r, runBounds.bottom-r, runBounds.right+r, runBounds.top+r);
			
			if (result == null) {
				result = runBounds;
			} else {
				result = result.union(runBounds);
			}
		}
		return result;
	}
	
	public Distance getWidth() {
		return sourcePiece.getWidth();
	}
	
	public Distance getHeight() {
		return sourcePiece.getHeight();
	}

	public Distance getDeptth() {
		return sourcePiece.getDepth();
	}
	
	public List<Tool> getToolList() {
		return toolList;
	}
	
	public List<ToolItemRun> getToolItemRuns() {
		return itemRuns;
	}

	@Override
	public Iterator<Item> iterator() {
		return itemRuns.get(0).items.iterator();
	}
	
	public void add(Item item) {
		itemRuns.get(0).add(item);
	}
	
	public void remove(Item item) {
		itemRuns.get(0).remove(item);
	}

	
	public void paint(Graphics2D gfx, ViewCamera designView, List<Color> itemColors, SelectionModel selectionModel) {
		gfx.setColor(Color.red);

		sourcePiece.paint(gfx);
		ToolItemRun itemRun = itemRuns.get(0);
		
		for(Item item : itemRun.items) {
			Tool tool = itemRun.getTool();
			int colorIndex = toolList.indexOf(tool);
			if (colorIndex<0) {
				gfx.setColor(Color.red);
			} else {
				Color c = itemColors.get(colorIndex % itemColors.size());
				gfx.setColor(c);
			}
			item.paint(gfx, itemRun, designView, selectionModel);
		}
	}

	public ItemGrabInfo findNearestGrabInfo(int mouseX, int mouseY) {
		ItemGrabInfo result = null;
		ToolItemRun itemRun = itemRuns.get(0);
		for(Item item : itemRun.items) {
			ItemGrabInfo grabInfo = item.createGrabInfo(mouseX, mouseY);
			if (grabInfo == null) {
				continue;
			}
			if (result==null || result.getSquareDistance()>grabInfo.getSquareDistance()) {
				result = grabInfo;
			}
		}
		return result;
	}

	
	public static class ToolItemRun implements Iterable<Item> {
		public final Tool tool;
		private List<Item> items = new ArrayList<>();
		
		private Bounds2D bounds;
		
		public ToolItemRun(Tool tool) {
			this.tool = tool;
		}
		
		public Tool getTool() {
			return tool;
		}

		public void add(Item item) {
			items.add(item);
			bounds = null;
		}

		public void remove(Item item) {
			items.remove(item);
			bounds = null;
		}
		
		public Bounds2D bounds() {
			if (bounds == null) {
				Bounds2D result = null;
				
				for(Item item : items) {
					Bounds2D itemBounds = item.bounds();
					if (result == null) {
						result = itemBounds;
					} else {
						result = result.union(itemBounds);
					}
				}
				bounds = result;
			}
			return bounds;
		}
		
		@Override
		public Iterator<Item> iterator() {
			return items.iterator();
		}

	}
	
	
	public SourcePiece getSourcePiece() {
		return sourcePiece;
	}
}
