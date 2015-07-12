/*
 * Copyright (c) 2011-2015, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.gui;

import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.ScaleOptions;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of items and their respective data.
 *
 * @author Peter Abeles
 */
public class ListDisplayPanel extends JPanel implements ListSelectionListener  {

	List<JPanel> panels = new ArrayList<JPanel>();
	private JPanel bodyPanel;
	private JList listPanel;

	DefaultListModel listModel = new DefaultListModel();

	JScrollPane scroll;

	public ListDisplayPanel() {
		setLayout(new BorderLayout());
		listPanel = new JList(listModel);

		listPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPanel.setSelectedIndex(0);
		listPanel.addListSelectionListener(this);

		scroll = new JScrollPane(listPanel);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		bodyPanel = new JPanel();
		bodyPanel.setLayout(new BorderLayout());
		bodyPanel.add(scroll,BorderLayout.WEST);

		add(bodyPanel);
	}

	public synchronized void automaticPreferredSize() {
		double width = 0;
		double height = 0;

		for (int i = 0; i < panels.size(); i++) {
			Dimension d = panels.get(i).getPreferredSize();
			width = Math.max(width,d.getWidth());
			height = Math.max(height,d.getHeight());
		}
		bodyPanel.setPreferredSize(new Dimension((int) (width + scroll.getPreferredSize().getWidth()) + 10, (int) height));
	}

	public synchronized void reset() {
		if( SwingUtilities.isEventDispatchThread() ) {
			panels.clear();
			listModel.removeAllElements();
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						panels.clear();
						listModel.removeAllElements();
					}
				});
			} catch (InterruptedException e) {
			} catch (InvocationTargetException e) {
			}
		}
//		bodyPanel.requestFocus();
	}

	public int getListWidth(){
		return (int)listPanel.getPreferredSize().getWidth();
	}

	/**
	 * Displays a new image in the list.
	 *
	 * @param image The image being displayed
	 * @param name Name of the image.  Shown in the list.
	 */
	public void addImage( BufferedImage image , String name ) {
		addImage(image, name,ScaleOptions.DOWN );
	}

	public void addImage( BufferedImage image , String name , ScaleOptions scaling ) {
		addItem(new ImagePanel(image, scaling), name );
	}

	/**
	 * Displays a new JPanel in the list.
	 *
	 * @param panel The panel being displayed
	 * @param name Name of the image.  Shown in the list.
	 */
	public synchronized void addItem( final JPanel panel , final String name ) {
		panels.add(panel);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				listModel.addElement(name);
				if( listModel.size() == 1 ) {
					listPanel.setSelectedIndex(0);
				}
				Dimension d = listPanel.getMinimumSize();
				listPanel.setPreferredSize(new Dimension(d.width + scroll.getVerticalScrollBar().getWidth(), d.height));
				validate();
			}
		});
	}

	@Override
	public synchronized void valueChanged(ListSelectionEvent e) {
		if( e.getValueIsAdjusting() )
			return;

		final int index = listPanel.getSelectedIndex();
		if( index >= 0 ) {
			removeCenterBody();
			bodyPanel.add(panels.get(index), BorderLayout.CENTER);
			bodyPanel.validate();
			bodyPanel.repaint();
		}
	}

	private void removeCenterBody() {
		Component old = ((BorderLayout)bodyPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
		if( old != null ) {
			bodyPanel.remove(old);
		}
	}
}
