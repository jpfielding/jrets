package org.realtors.rets.ext.retsexplorer.retstabbedpane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.WindowConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.realtors.rets.client.GetObjectRequest;
import org.realtors.rets.client.GetObjectResponse;
import org.realtors.rets.client.SingleObjectResponse;
import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MObject;
import org.realtors.rets.common.metadata.types.MResource;
import org.realtors.rets.common.metadata.types.MSystem;
import org.realtors.rets.ext.retsexplorer.SampleConfigs;
import org.realtors.rets.ext.retsexplorer.util.ErrorPopupActionListener;
import org.realtors.rets.ext.retsexplorer.util.GuiKeyBindings;
import org.realtors.rets.ext.retsexplorer.util.RetsWorker;
import org.realtors.rets.ext.retsexplorer.wirelog.WireLogConsole;
import org.realtors.rets.ext.util.RetsClient;
import org.realtors.rets.ext.util.RetsClientConfig;
import org.realtors.rets.ext.util.transaction.GetObject;

import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

public class RetsObjectsFrameOperator {
	
	private static final Logger LOG = Logger.getLogger(RetsObjectsFrameOperator.class);
	
	private RetsObjectsFrame retsObjectsPanel;
	
	private RetsClient client;
	private MSystem system;
	private Metadata metadata;
	private MResource[] resources;
	
	private MResource defaultResource;
	private String defaultContentId;
	
	private Map<String, MResource> resourcesMap;
	private Map<String, Vector<String>> objectTypesMap;

	private WireLogConsole wireLogConsole;

	public RetsObjectsFrameOperator(RetsClient client, MResource defaultResource,String defaultContentId, WireLogConsole wireLogConsole) {
		this(client, null, null, null, defaultResource, defaultContentId, wireLogConsole);
	}
	
	public RetsObjectsFrameOperator(RetsClient client, Metadata metadata, MSystem system, MResource[] resources, MResource defaultResource, String defaultContentId, WireLogConsole wireLogConsole) {
		this.retsObjectsPanel = new RetsObjectsFrame();
		this.wireLogConsole = wireLogConsole;
		getObjectsPanel().addContainerListener(new ContainerAdapter() {
			@Override
			public void componentAdded(ContainerEvent e) {
				getRetsObjectsFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				getRetsObjectsFrame().setVisible(false);
				getRetsObjectsFrame().pack();
				getRetsObjectsFrame().setLocationRelativeTo(null);
				getRetsObjectsFrame().setVisible(true);
			}
		});
		
		setClient(client);
		setMetadata(metadata);
		setSystem(system);
		setResources(resources);
		setDefaultResource(defaultResource);
		setDefaultContentId(defaultContentId);
	
		getContentIdField().setText(getDefaultContentId());
		setResourceComboAction();
		setObjectTypeComboModel();
		setGetButtonAction();
		GuiKeyBindings.setEnterKeyAction(getGetButton(), getRetsObjectsFrame().getRootPane());
	}
	
	
	public RetsClient getClient() {
		return this.client;
	}

	
	public void setClient(RetsClient client) {
		this.client = client;
	}
	
	
	public Metadata getMetadata() {
		if (this.metadata != null) {
			return this.metadata;
		}
		if(this.client == null) {
			return null;
		}
		this.metadata = this.client.getMetadata();
		return this.metadata;
	}


	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	

	public MSystem getSystem() {
		if (this.system != null) {
			return this.system;
		}
		Metadata meta = getMetadata();
		if (meta == null) {
			return null;
		}
		this.system = this.metadata.getSystem();
		return this.system;
	}


	public void setSystem(MSystem system) {
		this.system = system;
	}


	public MResource[] getResources() {
		if (this.resources != null) {
			return this.resources;
		}
		MSystem sys = getSystem();
		if (sys == null) {
			return null;
		}
		this.resources = sys.getMResources();
		return this.resources;
	}


	public void setResources(MResource[] resources) {
		if (this.resources != resources) {
			getResourcesMap().clear();
			getObjectTypesMap().clear();
			this.resourcesMap = null;
			this.objectTypesMap = null;
			this.resources = resources;
		}
	}


	public MResource getDefaultResource() {
		if (this.defaultResource != null) {
			return this.defaultResource;
		}
		MResource[] mResources = getResources();
		if (mResources == null || mResources.length <= 0) {
			return null;
		}
		return mResources[0];
	}

	
	public void setDefaultResource(MResource defaultResource) {
		this.defaultResource = defaultResource;
	}

	
	public String getDefaultContentId() {
		return this.defaultContentId;
	}

	
	public void setDefaultContentId(String defaultContentId) {
		this.defaultContentId = defaultContentId;
	}
	
	private Map<String,MResource> getResourcesMap() {
		if (this.resourcesMap == null) {
			HashMap<String,MResource> map = Maps.newHashMap();
			
			MResource[] mResources = getResources();
			if (mResources != null) {
				for (MResource resource : mResources) {
					if (resource != null) {
						map.put(resource.getId(), resource);
					}
				}
			}
			
			MResource resource = getDefaultResource();
			if (resource != null) {
				map.put(resource.getId(), resource);
			}
			
			this.resourcesMap = map;
		}
		return this.resourcesMap;
	}
	
	private Map<String,Vector<String>> getObjectTypesMap() {
		if (this.objectTypesMap == null) {
			Map<String,Vector<String>> map = Maps.newHashMap();
			for (MResource resource : getResourcesMap().values()) {
				if (resource == null) {
					continue;
				}
				MObject[] objects = resource.getMObjects();
				if(objects == null) {
					continue;
				}
				Vector<String> ids = new Vector(objects.length);
				for (MObject object : objects) {
					if (object == null) {
						continue;
					}
					ids.add(object.getId());
				}
				map.put(resource.getId(), ids);
			}
			this.objectTypesMap = map;
		}
		return this.objectTypesMap;
	}
	
	private JPanel getObjectsPanel() {
		return getRetsObjectsFrame().getObjectsPanel();
	}

	private RetsObjectsFrame getRetsObjectsFrame() {
		return this.retsObjectsPanel;
	}
	
	private JTextField getContentIdField() {
		return getRetsObjectsFrame().getContentIdField();
	}
	
	
	private JTextField getObjectIdField() {
		return getRetsObjectsFrame().getObjectIdField();
	}
	
	private JCheckBox getSavePhotoCheckBox() {
		return getRetsObjectsFrame().getSavePhotoCheckBox();
	}
	
	private JTextField getSavePhotoDirField() {
		return getRetsObjectsFrame().getSavePhotoDirField();
	}
	
	private JComboBox getResourceCombo() {
		return getRetsObjectsFrame().getResourceCombo();
	}
	
	private JComboBox getObjectTypeCombo() {
		return getRetsObjectsFrame().getObjectTypeCombo();
	}
	
	private JButton getGetButton() {
		return getRetsObjectsFrame().getGetButton();
	}
	
	private WireLogConsole getWireLogConsole() {
		return this.wireLogConsole;
	}
	
	private void setObjectTypeComboModel() {
		Object item = getResourceCombo().getSelectedItem();
		String resourceId = null;
		if (item instanceof String) {
			resourceId = StringUtils.trimToEmpty((String)item);
		} else if (item instanceof MResource) {
			resourceId = ((MResource)item).getId();
		}
		if (resourceId == null) {
			MResource resource = getDefaultResource();
			if (resource != null) {
				resourceId = resource.getId();
			}
		}
		getObjectTypeCombo().setModel(getObjectTypeModel(resourceId));
	}
	
	private void setResourceComboAction() {
		JComboBox combo = getResourceCombo();
		
		MResource defaultMResource = getDefaultResource();
		if (defaultMResource != null) {
			combo.setSelectedItem(defaultMResource.getId());
		}
		
		combo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object item = getResourceCombo().getSelectedItem();
				String resourceId = null;
				if (item instanceof String) {
					resourceId = StringUtils.trimToEmpty((String)item);
				} else if (item instanceof MResource) {
					resourceId = ((MResource)item).getId();
				}
				getObjectTypeCombo().setModel(getObjectTypeModel(resourceId));
			}
		});
	}
	
	private void setGetButtonAction() {
		getGetButton().addActionListener(new ErrorPopupActionListener() {
			@Override
			public void action() throws Exception {
				final String resource = StringUtils.trimToEmpty((String)getResourceCombo().getSelectedItem());
				final String contentId = StringUtils.trimToEmpty(getContentIdField().getText());
				final String objectId = StringUtils.trimToEmpty(getObjectIdField().getText());
				final String objectType = StringUtils.trimToEmpty(String.valueOf(getObjectTypeCombo().getSelectedItem()));
				final String path = getSavePhotoDirField().getText();
				final boolean savePhotos = getSavePhotoCheckBox().isSelected();

				final DefaultListModel model = new DefaultListModel();
				final JList photosList = new JList(model);
				photosList.setVisibleRowCount(1);
				photosList.setCellRenderer(new PhotosListCellRenderer());
				photosList.setEnabled(false);
				
				final JPanel objPanel = getObjectsPanel();
				
				objPanel.removeAll();
				
				RetsWorker worker = new RetsWorker<Void, RetsObject>() {
					@Override
					protected Void doInBackgroundWithPopup() throws Exception {
						RetsClient retsClient = getClient();
						
						GetObjectRequest request = new GetObjectRequest(resource,objectType);
						request.addObject(contentId, objectId);
						request.setHeader("Accept", "*/*");
						
						final GetObject getObject = new GetObject(request);
						final GetObjectResponse response = retsClient.executeRetsTransaction(getObject, null);
						final Iterator responseIterator = response.iterator();
						final Iterable<SingleObjectResponse> objects = new Iterable(){
							public Iterator iterator() {
								return responseIterator;
							}};
						
						if (getWireLogConsole() != null && getRetsObjectsFrame().getPauseConsoleCheckBox().isSelected()) {
							getWireLogConsole().appendText("*****Pausing console while objects are being pulled...*****\n");
							getWireLogConsole().setPaused(true);
						}
						
						for(SingleObjectResponse resp : objects) {
							InputStream stream = null;
							try {
								stream = resp.getInputStream();
								Image image = null;
								if (stream != null) {
									image = ImageIO.read(stream);
								}
								if (image==null) {
									LOG.warn(String.format("Unable to parse image for object with id %s", resp.getObjectID()));
								}
								ImageIcon icon = null;
								if (image != null) {
									icon = new ImageIcon(image);
								}
								publish(new RetsObject(resp,icon));
							} catch (Exception e2) {
								publish(new RetsObject(resp,null));							
							} finally {
								Closeables.closeQuietly(stream);
							}
						}
						return null;
					}

					@Override
					protected void processWithPopup(List<RetsObject> chunks) {
						for (final RetsObject object : chunks) {
							if (path != null && savePhotos) {
								new File(path + "/"+ RetsObjectsFrameOperator.this.client.getRetsProfile().getName()+"/Photos/" + object.response.getContentID()).mkdirs();
								File saveFile = new File(path + "/Rets_Mapper_Photos/" + object.response.getContentID() + "/" + object.response.getObjectID() + ".jpg");
								try {
									ImageIO.write((RenderedImage) object.icon.getImage(), "jpg", saveFile);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							model.addElement(object);
						}
					}

					@Override
					protected void doneWithPopup() {
						if (getWireLogConsole() != null) {
							getWireLogConsole().setPaused(false);
							if (photosList.getModel().getSize() == 0) getWireLogConsole().appendText("No objects returned for query\n");
							getWireLogConsole().appendText("*****Unpausing Console*****\n\n");
						}
						if (photosList.getModel().getSize() != 0) {
							objPanel.add(new JScrollPane(photosList));
							getRetsObjectsFrame().add(objPanel, BorderLayout.SOUTH);
						}
						getRetsObjectsFrame().pack();
					}
				};
				worker.execute();
			}
		});
	}
	
	private DefaultComboBoxModel getObjectTypeModel(String resourceId) {
		if (resourceId == null) {
			return new DefaultComboBoxModel();
		}
		Vector<String> objects = getObjectTypesMap().get(resourceId);
		if (objects == null || objects.size() <= 0) {
			return new DefaultComboBoxModel();
		}
		return new DefaultComboBoxModel(objects);
	}
	
	
	private class RetsObject {
		SingleObjectResponse response;
		ImageIcon icon;
		RetsObject(SingleObjectResponse response, ImageIcon icon) {
			super();
			this.response = response;
			this.icon = icon;
		}

	}
	
	private class PhotosListCellRenderer extends JPanel implements ListCellRenderer {
		final String[] labelsText = {"Content ID:", "Object Id:", "Type:", "Size:", "Description:", "Location:" };
		
		final int CONTENT_ID = 0;
		final int OBJECT_ID = 1;
		final int TYPE = 2;
		final int OBJECT_SIZE = 3;
		final int DESCRIPTION = 4;
		final int LOCATION = 5;
		
		JLabel[] values = new JLabel[this.labelsText.length];
		
		JLabel image;
		
		PhotosListCellRenderer() {
			super();
			initialize();
		}
		void initialize() {
			setOpaque(true);
			
			JLabel[] labels = new JLabel[this.values.length]; 
			
			this.image = new JLabel();
			this.image.setOpaque(true);
			
			for (int i=0; i<this.values.length; i++) {
				labels[i] = new JLabel(this.labelsText[i]);
				this.values[i] = new JLabel();
			}
			
			
			GroupLayout layout = new GroupLayout(this);
			setLayout(layout);
			
			// x-axis
			layout.setHorizontalGroup(layout.createSequentialGroup()
					.addComponent(this.image)
					.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(Alignment.LEADING)
										.addComponent(labels[this.CONTENT_ID])
										.addComponent(labels[this.OBJECT_ID])
										.addComponent(labels[this.TYPE])
										.addComponent(labels[this.OBJECT_SIZE])
										.addComponent(labels[this.DESCRIPTION])
										.addComponent(labels[this.LOCATION])
								)
								.addGroup(layout.createParallelGroup()
										.addComponent(this.values[this.CONTENT_ID])
										.addComponent(this.values[this.OBJECT_ID])
										.addComponent(this.values[this.TYPE])
										.addComponent(this.values[this.OBJECT_SIZE])
										.addComponent(this.values[this.DESCRIPTION])
										.addComponent(this.values[this.LOCATION])
								)
						)
					)
			);
			
			// y-axis
			layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
					.addComponent(this.image)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createBaselineGroup(false,true)
										.addComponent(labels[this.CONTENT_ID])
										.addComponent(this.values[this.CONTENT_ID])
								)
								.addGroup(layout.createBaselineGroup(false,true)
										.addComponent(labels[this.OBJECT_ID])
										.addComponent(this.values[this.OBJECT_ID])
								)
								.addGroup(layout.createBaselineGroup(false,true)
										.addComponent(labels[this.TYPE])
										.addComponent(this.values[this.TYPE])
								)
								.addGroup(layout.createBaselineGroup(false,true)
										.addComponent(labels[this.OBJECT_SIZE])
										.addComponent(this.values[this.OBJECT_SIZE])
								)
								.addGroup(layout.createBaselineGroup(false,true)
										.addComponent(labels[this.DESCRIPTION])
										.addComponent(this.values[this.DESCRIPTION])
								)
								.addGroup(layout.createBaselineGroup(false,true)
										.addComponent(labels[this.LOCATION])
										.addComponent(this.values[this.LOCATION])
								)
						)
					)
			);
			
			layout.setAutoCreateContainerGaps(true);
			layout.setAutoCreateGaps(true);
		}
		
		public Component getListCellRendererComponent(JList list, Object value,int index, boolean isSelected, boolean cellHasFocus) {
			
			if (list != null) {
				this.image.setBackground(list.getBackground());
			}
			
			if (!(value instanceof RetsObject)) {
				setValuesText("");
				this.image.setIcon(null);
				this.image.setText("Unable to Load Image");
			}
			
			RetsObject rets = (RetsObject)value;
			
			if (rets.response != null) {
				this.values[this.TYPE].setText(rets.response.getType());
				this.values[this.CONTENT_ID].setText(rets.response.getContentID());
				this.values[this.OBJECT_ID].setText(rets.response.getObjectID());
				this.values[this.DESCRIPTION].setText(rets.response.getDescription());
				this.values[this.LOCATION].setText(rets.response.getLocation());
			}
			
			if (rets.icon != null) {
				this.image.setIcon(rets.icon);
				this.values[this.OBJECT_SIZE].setText(Integer.toString(rets.icon.getIconWidth()) + " x " + Integer.toString(rets.icon.getIconHeight()));
			} else {
				this.image.setText("Unable to Load Image");
			}
			
			return this;
		}
		private void setValuesText(String string) {
			for (JLabel label : this.values) {
				label.setText(string);
			}
		}
	}

	public static void main(String[] args) {
		RetsClientConfig config = new RetsClientConfig(SampleConfigs.taar(),"TAAR");
		RetsClient client = config.createClient();
		Metadata metadata = client.getMetadata();
		
		new RetsObjectsFrameOperator(client,metadata.getSystem().getMResource("Property"),"1693501", null);
	}


}