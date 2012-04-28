package org.opennms.features.vaadin.topology;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.terminal.KeyMapper;


public class Graph{
	
	public static final String PROP_X = "x";
	public static final String PROP_Y = "y";
	public static final String PROP_ICON = "icon";
	
	private abstract class ElementHolder<T> {
		Container m_itemContainer;
		List<T> m_graphElements;
		KeyMapper m_elementKey2ItemId = new KeyMapper();
		Map<String, T> m_keyToElementMap = new HashMap<String, T>();
		
		ElementHolder(Container container) {
			
			m_itemContainer = container;
			
			update();
		}

		public void update() {
			Collection<?> itemIds = m_itemContainer.getItemIds();
			m_elementKey2ItemId.removeAll();
			m_keyToElementMap.clear();
			
			m_graphElements = new ArrayList<T>(itemIds.size());
			
			for(Object itemId : itemIds) {
			    String key = m_elementKey2ItemId.key(itemId);
			    
				if(!m_keyToElementMap.containsKey(key)) {

					T v = make(key, itemId, m_itemContainer.getItem(itemId));
					System.err.println("make v: " + v);
					m_graphElements.add(v);

					m_keyToElementMap.put(key, v);
				}
			}
		}
		
		List<T> getElements(){
			return m_graphElements;
		}

		protected abstract T make(String key, Object itemId, Item item);

		public T getElementByKey(String key) {
			return m_keyToElementMap.get(key);
		}
		
		public T getElementByItemId(Object itemId) {
			return getElementByKey(m_elementKey2ItemId.key(itemId));
		}
		
		public List<T> getElementsByItemIds(Collection<?> itemIds) {
			List<T> elements = new ArrayList<T>(itemIds.size());
			
			for(Object itemId : itemIds) {
				elements.add(getElementByItemId(itemId));
			}
			
			return elements;
		}

		
		
	}

	
	private GraphContainer m_dataSource;
	private int m_counter = 0;
	private LayoutAlgorithm m_layoutAlgorithm = new SimpleLayoutAlgorithm();
	private ElementHolder<Vertex> m_vertexHolder;
	private ElementHolder<Edge> m_edgeHolder;

	
	public Graph(GraphContainer dataSource){
		
		if(dataSource == null) {
			throw new NullPointerException("dataSource may not be null");
		}
		setDataSource(dataSource);
		
	}
	
	public void setDataSource(GraphContainer dataSource) {
		if(dataSource == m_dataSource) {
			return;
		}
		
		m_dataSource = dataSource;
		
		m_vertexHolder = new ElementHolder<Vertex>(m_dataSource.getVertexContainer()) {

			@Override
			protected Vertex make(String key, Object itemId, Item item) {
				return new Vertex(key, itemId, item);
			}

		};
		
		m_edgeHolder = new ElementHolder<Edge>(m_dataSource.getEdgeContainer()) {

			@Override
			protected Edge make(String key, Object itemId, Item item) {

				List<Object> endPoints = new ArrayList<Object>(m_dataSource.getEndPointIdsForEdge(itemId));

				Object sourceId = endPoints.get(0);
				Object targetId = endPoints.get(1);
				
				Vertex source = m_vertexHolder.getElementByItemId(sourceId);
				Vertex target = m_vertexHolder.getElementByItemId(targetId);

				return new Edge(key, itemId, item, source, target);
			}

		};
		
		
	}
	public void update() {
		m_vertexHolder.update();
		m_edgeHolder.update();
	}

	public GraphContainer getDataSource() {
		return m_dataSource;
	}

	public List<Vertex> getVertices(){
		return m_vertexHolder.getElements();
	}
	
	public List<Edge> getEdges(){
		return m_edgeHolder.getElements();
	}
	
	public Vertex getVertexByKey(String key) {
		return m_vertexHolder.getElementByKey(key);
	}
	
	public List<Edge> getEdgesForVertex(Vertex vertex){
		return m_edgeHolder.getElementsByItemIds(m_dataSource.getEdgeIdsForVertex(vertex.getItemId()));
	}
	
	void updateLayout() {
        getLayoutAlgorithm().updateLayout(this);
    }
	
	public String getNextId() {
		return "" + m_counter ++;
	}
    
    public LayoutAlgorithm getLayoutAlgorithm() {
        return m_layoutAlgorithm;
    }
    public void setLayoutAlgorithm(LayoutAlgorithm layoutAlgorithm) {
        m_layoutAlgorithm = layoutAlgorithm;
        updateLayout();
    }

       
	
}