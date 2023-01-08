package net.github.douwevos.cnc.ui.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.github.douwevos.cnc.holer.CncPerspectiveBoard;
import net.github.douwevos.cnc.holer.feature.LocationFeature;
import net.github.douwevos.cnc.ui.editor.SelectionModel.Listener;
import net.github.douwevos.cnc.ui.widget.CncUIFrame;
import net.github.douwevos.cnc.ui.widget.CncUIPanel;
import net.github.douwevos.cnc.ui.widget.CncUIProperty;
import net.github.douwevos.cnc.ui.widget.CncUIPropertyGroup;
import net.github.douwevos.cnc.ui.widget.CncUIPropertyGroup.PropertyObserver;

public class SelectionPropertiesList implements Listener {

	private final SelectionModel selectionModel;
	private final CncUIPanel propertyPanel;

	private final CncUIPropertyGroup uiPropertyGroup = new CncUIPropertyGroup();
	private CncPerspectiveBoard perspectiveBoard;
	
//	private final PropertyLocationX propertyLocationX;
	
	
	private List<Holder> propertyHandlerList = new ArrayList<>();
	
	public SelectionPropertiesList(CncUIPanel menuPanel, SelectionModel selectionModel) {
		this.selectionModel = selectionModel;
		selectionModel.addListener(this);
		this.propertyPanel = menuPanel; 

		CncUIFrame frameSelection = new CncUIFrame("selection");
		propertyPanel.add(frameSelection);

		addUIFeatureProperty(new PropertyLocationX(selectionModel));
		addUIFeatureProperty(new PropertyLocationY(selectionModel));

	}

	private void addUIFeatureProperty(FeatureProperty featureProperty) {
		Holder holder = new Holder(selectionModel, featureProperty);
		featureProperty.setObserver(holder);
		
		propertyHandlerList.add(holder);
		
		CncUIProperty uiProperty = (CncUIProperty) featureProperty;
		uiProperty.setVisible(false);
		propertyPanel.add(uiProperty);
		propertyPanel.needsRepaint();
		uiPropertyGroup.addProperty(uiProperty);
		
	}

	public void setPerspectiveBoard(CncPerspectiveBoard perspectiveBoard) {
		if (perspectiveBoard == this.perspectiveBoard) {
			return;
		}
		if (this.perspectiveBoard!=null) {
			this.perspectiveBoard.setPropertiesPanel(null);
		}
		
		this.perspectiveBoard = perspectiveBoard;
		if (this.perspectiveBoard!=null) {
			this.perspectiveBoard.setPropertiesPanel(propertyPanel);
		}
		
	}

	@Override
	public void selectionChanged() {
		
		boolean updatePropertiesLayout = false;
		
		for(int idx=0; idx<propertyHandlerList.size(); idx++) {
			Holder holder = propertyHandlerList.get(idx);
			PropertyEditState propertyEditState = calculatePropertyEditState(holder);
			boolean setVisible = false;
			if (propertyEditState.editable == Boolean.TRUE) {
				setVisible = true;
				holder.setValue(propertyEditState.value);
			}
			if (holder.setVisible(setVisible)) {
				updatePropertiesLayout = true;
			}
		}
		
		if (updatePropertiesLayout) {
			propertyPanel.updateBoundaries(propertyPanel.getBoundaries());
		}
		
//		PropertyEditState combinedValues[] = new PropertyEditState[propertyHandlerList.size()];
//		for()
		
//		for(ItemGrabInfo<?> item : selectionModel.selections()) {
//			for(int idx=0; idx<combinedValues.length; idx++) {
////				selectLocationFeature(item);
//				combinedValues[idx] = combine(combinedValues[idx], propertyHandlerList.get(idx), item);
//			}
//		}
	}

	private PropertyEditState calculatePropertyEditState(Holder holder) {
		PropertyEditState result = new PropertyEditState();
		for(ItemGrabInfo<?> item : selectionModel.selections()) {
			Optional<String> optValue = holder.readValue(item);
			if (optValue == null) {
				result.editable = Boolean.FALSE;
				result.value = null;
				break;
			}
			String value = optValue.get();
			if (result.editable == null) {
				result.editable = Boolean.TRUE;
				result.value = value;
			} else {
				if (!Objects.equals(result.value, value)) {
					result.value = null;
				}
			}
		}
		return result;
	}
	
	static class PropertyEditState {
		Boolean editable;
		String value;
	}
	
	
	interface FeatureProperty extends PropertyObserver {
		boolean setVisible(boolean visible);
		void setObserver(PropertyObserver observer);
		void setValue(String value);
		
		Optional<String> extractValue(ItemGrabInfo<?> grabInfo);
		boolean updateValue(ItemGrabInfo<?> grabInfo, String value);
	}
	
	class Holder implements PropertyObserver {
		
		private final SelectionModel selectionModel;
		private FeatureProperty property;
		
		public Holder(SelectionModel selectionModel, FeatureProperty property) {
			this.selectionModel = selectionModel;
			this.property = property;
		}

		public void setValue(String value) {
			property.setValue(value==null ? "" : value);
		}

		public boolean setVisible(boolean visible) {
			return property.setVisible(visible);
		}

		public Optional<String> readValue(ItemGrabInfo<?> item) {
			return property.extractValue(item);
		}
		
		public boolean writeValue(ItemGrabInfo<?> item, String value) {
			return property.updateValue(item, value);
		}

		@Override
		public boolean valueChanged(String newValue) {
			boolean result = false;
			for(ItemGrabInfo<?> grabInfo : selectionModel.selections()) {
				if (property.updateValue(grabInfo, newValue)) {
					result = true;
				}
			}
			if (result == true && perspectiveBoard!=null) {
				perspectiveBoard.repaintPropertiesPanel();
			}
			return result;
		}
		
	}
	
	
	static class PropertyLocationX extends CncUIProperty implements FeatureProperty {
		
		private final SelectionModel selectionModel;
		
		public PropertyLocationX(SelectionModel selectionModel) {
			super("X:", "");
			this.selectionModel = selectionModel;
		}

		@Override
		public boolean valueChanged(String newValue) {
			long newX = Long.parseLong(newValue);
			for(ItemGrabInfo<?> grabInfo : selectionModel.selections()) {
				if (grabInfo instanceof LocationFeature) {
					LocationFeature lf = (LocationFeature) grabInfo;
					lf.setX(newX);
				}
			}
			return false;
		}
		
		@Override
		public Optional<String> extractValue(ItemGrabInfo<?> grabInfo) {
			if (grabInfo instanceof LocationFeature) {
				LocationFeature lf = (LocationFeature) grabInfo;
				long x = lf.getX();
				return Optional.of(Long.toString(x));
			}
			return null;
		}

		@Override
		public boolean updateValue(ItemGrabInfo<?> grabInfo, String newValue) {
			if (grabInfo instanceof LocationFeature) {
				long newX = Long.parseLong(newValue);
				LocationFeature lf = (LocationFeature) grabInfo;
				if (lf.getX()==newX) {
					return false;
				}
				lf.setX(newX);
				return true;
			}
			return false;
		}
		
		
	}

	
	static class PropertyLocationY extends CncUIProperty implements FeatureProperty {
		
		private final SelectionModel selectionModel;
		
		public PropertyLocationY(SelectionModel selectionModel) {
			super("Y:", "");
			this.selectionModel = selectionModel;
		}

		@Override
		public boolean valueChanged(String newValue) {
			long newY = Long.parseLong(newValue);
			for(ItemGrabInfo<?> grabInfo : selectionModel.selections()) {
				if (grabInfo instanceof LocationFeature) {
					LocationFeature lf = (LocationFeature) grabInfo;
					lf.setY(newY);
				}
			}
			return false;
		}

		@Override
		public Optional<String> extractValue(ItemGrabInfo<?> grabInfo) {
			if (grabInfo instanceof LocationFeature) {
				LocationFeature lf = (LocationFeature) grabInfo;
				long y = lf.getY();
				return Optional.of(Long.toString(y));
			}
			return null;
		}

		@Override
		public boolean updateValue(ItemGrabInfo<?> grabInfo, String newValue) {
			if (grabInfo instanceof LocationFeature) {
				long newY = Long.parseLong(newValue);
				LocationFeature lf = (LocationFeature) grabInfo;
				if (lf.getY()==newY) {
					return false;
				}
				lf.setY(newY);
				return true;
			}
			return false;
		}

	}

	
}
