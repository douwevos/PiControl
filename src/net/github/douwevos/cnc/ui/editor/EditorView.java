package net.github.douwevos.cnc.ui.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.JPopupMenu;

import net.github.douwevos.cnc.model.Editable;
import net.github.douwevos.cnc.model.EditableLayer;
import net.github.douwevos.cnc.model.EditableModel;
import net.github.douwevos.cnc.model.ItemType;
import net.github.douwevos.cnc.ui.ModelGraphics;
import net.github.douwevos.cnc.ui.ModelMouseEvent;
import net.github.douwevos.cnc.ui.ModelViewer;
import net.github.douwevos.cnc.ui.editor.rectangle.EditableCreator;
import net.github.douwevos.cnc.ui.editor.rectangle.ItemPolyLineController;
import net.github.douwevos.cnc.ui.editor.rectangle.ItemRectangleController;
import net.github.douwevos.justflat.values.Bounds2D;
import net.github.douwevos.justflat.values.Point2D;

@SuppressWarnings("serial")
public class EditorView extends ModelViewer {

	private final static Map<ItemType, ItemController> controllers =  new EnumMap<>(ItemType.class);
	static {
		controllers.put(ItemType.RECTANGLE, new ItemRectangleController());
		controllers.put(ItemType.POLYLINE, new ItemPolyLineController());
	}
	
	private boolean didDrag = false;
	
	
	private EditableModel model = new EditableModel();
	
	private SelectionModel selectionModel = new SelectionModel();
	
	
	EditableLayer currentLayer;
	private EditableCreator creator = null;

	private final EditableModel.Listener modelLister = new EditableModel.Listener() {
		@Override
		public void onModelChanged() {
			validateSelection();
			repaintModel();
		}

	};

	private final SelectionModel.Listener selectionModelListener = new SelectionModel.Listener() {
		@Override
		public void selectionChanged() {
			repaintModel();
		}
	};

	
//	private ItemGrabInfo highligthed;
	
	public EditorView() {
		
		selectionModel.addListener(selectionModelListener);
		
		model.addListener(modelLister);
	}

	
	public void setModel(EditableModel model) {
		if (this.model == model) {
			return;
		}
		selectionModel.clear();
		creator = null;
		modelImageDirty = true;
		
		if (this.model != null) {
			this.model.removeListener(modelLister);
		}
		
		this.model = model;
		if (model != null) {
			model.addListener(modelLister);
			currentLayer = model.layerAt(0);
		}
	}
	
	

	@Override
	public void paint(Graphics g) {
		if (model == null) {
			return;
		}
		
		Graphics2D gfx = (Graphics2D) g;
		
		Image modelImage = updateModelImage();

		gfx.drawImage(modelImage, 0, 0, null);
		
		ModelGraphics modelGraphics = new ModelGraphics(gfx, camera);
		paintSelectionModel(modelGraphics);
		
		if (creator!=null) {
			creator.paint(modelGraphics, currentLayer);
		}
	}

	private void paintSelectionModel(ModelGraphics modelGraphics) {
		
		modelGraphics.colorSelection();
		for(ItemGrabInfo<?> selected : selectionModel.selections()) {
			ItemController itemController = controllers.get(selected.item.itemType);
			itemController.paintSelected(selected.item, selected, modelGraphics);
		}
		
		modelGraphics.colorHighlight();
		ItemGrabInfo<?> highlighted = selectionModel.getHighlight();
		if (highlighted != null) {
			ItemController itemController = controllers.get(highlighted.item.itemType);
			itemController.paintHighlighted(highlighted.item, highlighted, modelGraphics);
		}
	}



	@Override
	protected void drawModelImage(ModelGraphics modelGraphics) {
		for(EditableLayer layer : model) {
			paintModelLayer(modelGraphics, layer);
		}
	}


	private void paintModelLayer(ModelGraphics modelGraphics, EditableLayer layer) {
		for(Editable item : layer) {
			paintModelLayerItem(modelGraphics, layer, item);
		}
	}


	private void paintModelLayerItem(ModelGraphics modelGraphics, EditableLayer layer, Editable item) {
		ItemController itemController = controllers.get(item.itemType);
		itemController.paint(modelGraphics, layer, item);
	}

	
	public SelectionModel getSelectionModel() {
		return selectionModel;
	}
	
	private void validateSelection() {
		selectionModel.validate(model);
	}

	public void setCreator(EditableCreator editableCreator) {
		creator = editableCreator;
		repaint();
	}

	
	@Override
	public Bounds2D getModelBounds() {
		return model==null ? null : model.calculateBounds();
	}
	
	@Override
	protected boolean onModelMouseEvent(ModelMouseEvent modelEvent) {
		if (creator != null) {
			return onModelMouseEventForCreator(modelEvent);
		}
		switch(modelEvent.type) {
			case MOVED : 
				ItemGrabInfo<?> grabInfo = resolveItemGrabInfo(modelEvent);
				selectionModel.setHighlight(grabInfo);
				repaint();
				return true;
			case PRESSED : {
				didDrag = false;
				MouseEvent event = modelEvent.event;
				dragX = event.getX();
				dragY = event.getY();

				ItemGrabInfo<?> grabInfoP = resolveItemGrabInfo(modelEvent);
				if (grabInfoP == null) {
					selectionModel.clear();
					break;
				}
				int modifiersEx = modelEvent.event.getModifiersEx();
				boolean ctrlDown = (modifiersEx & InputEvent.CTRL_DOWN_MASK) != 0;
				if (!selectionModel.hasSelected(grabInfoP)) {
					selectionModel.addSelection(grabInfoP, ctrlDown);
				}
				selectionModel.startDrag(dragX, dragY);
				repaintModel();
				return true;
			}
			case DRAGGED : {
				didDrag = true;
				if (!selectionModel.isEmpty()) {
					MouseEvent event = modelEvent.event;
					int dragToX = event.getX();
					int dragToY = event.getY();
					selectionModel.doDrag(camera, dragToX, dragToY);
					repaintModel();
					return true;
				}
			} break;
			case RELEASED : {
				ItemGrabInfo<?> grabInfoP = resolveItemGrabInfo(modelEvent);
				if (grabInfoP == null) {
					System.out.println("clear on release");
					selectionModel.clear();
					break;
				}
				int modifiersEx = modelEvent.event.getModifiersEx();
				boolean ctrlDown = (modifiersEx & InputEvent.CTRL_DOWN_MASK) != 0;
				System.out.println("addSelection on release: ctrlDown="+ctrlDown+", didDrag="+didDrag);
				selectionModel.addSelection(grabInfoP, ctrlDown || didDrag);
				updateViewCamera();
				break;
			}
			case CLICKED : {
				if (modelEvent.event.getButton() == 3) {
					Point2D modelClickPoint = new Point2D(Math.round(modelEvent.modelX), Math.round(modelEvent.modelY));
					showPopupMenu(modelEvent.event.getX(), modelEvent.event.getY(), modelClickPoint);
				}
			}
			default :
				break;
		}
		return super.onModelMouseEvent(modelEvent);
	}


	private boolean onModelMouseEventForCreator(ModelMouseEvent modelEvent) {
		switch(modelEvent.type) {
			case MOVED :
				creator.moveLocationTo(modelEvent.createModelPoint());
				repaint();
				break;
			case CLICKED : {
				if (modelEvent.event.getButton()==3) {
					Editable editable = creator.createEditable();
					currentLayer.addItem(editable);
					creator = null;
					repaintModel();
					return true;
				} else if (creator.clicked(modelEvent)) {
					repaintModel();
					return true;
				}
			} break;
		
		}
		return super.onModelMouseEvent(modelEvent);
	}



	private void showPopupMenu(int x, int y, Point2D modelPoint) {

		JPopupMenu p = new JPopupMenu();
		
		ItemGrabInfo<?> highlight = selectionModel.getHighlight();
		if (highlight != null) {
			Editable item = highlight.getItem();
			ItemController controller = controllers.get(item.itemType);
			controller.addPopupItems(item, highlight, p, modelPoint);
		}
		
//		if (highlighted instanceof ContourDotSelection) {
//			ContourDotSelection s = (ContourDotSelection) highlighted;
//			Action actDeletePoint = new AbstractAction("Delete point") {
//				
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					s.contour.removeAt(s.dotIndex);
//					layerImage = null;
//					repaint();
//				}
//			};
//			p.add(actDeletePoint);
//		}
//
//		if (highlighted instanceof LineSelection) {
//			LineSelection l = (LineSelection) highlighted;
//			Action actDummy = new AbstractAction("Add point") {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					l.contour.addAt(new Point2D(Math.round(modelEvent.modelX), Math.round(modelEvent.modelY)), l.dotIndex+1);
//					layerImage = null;
//					repaint();
//				}
//			};
//			p.add(actDummy);
//		}
//		
		p.show(this, x, y);
	}



	private void repaintModel() {
		modelImageDirty = true;
		repaint();
	}

	private ItemGrabInfo<?> resolveItemGrabInfo(ModelMouseEvent modelEvent) {
		double minSnapSize = camera.toModelSize(50*50);
//		System.out.println("minSnapSize="+minSnapSize);
		ItemGrabInfo<?> result = null;
		for(EditableLayer itemLayer : model) {
			for(Editable item : itemLayer) {
				ItemController itemController = controllers.get(item.itemType);
				ItemGrabInfo<?> grabInfo = itemController.getGrabInfo(item, modelEvent, minSnapSize);
				if (grabInfo == null) {
					continue;
				}
				if (result == null || grabInfo.getSquareDistance()<result.getSquareDistance()) {
					result = grabInfo;
				}
			}
		}
		
//		long snapSize = camera.toModelSize(20);
//		snapSize = snapSize*snapSize;
//		if (result!=null && result.getSquareDistance()>snapSize) {
//			result = null;
//		}

		return result;
	}
	
	@Override
	protected void updateViewCamera() {
		super.updateViewCamera();
		modelImageDirty = true;
	}



}
