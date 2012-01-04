package org.realtors.rets.ext.retsexplorer.export;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.realtors.rets.ext.retsexplorer.util.ErrorPopupActionListener;
import org.realtors.rets.ext.retsexplorer.util.GuiUtils;
import org.realtors.rets.ext.retsexplorer.util.RetsWorker;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.io.Files;

public class ExportWizard extends JDialog {
	
	public ExportWizard(Window owner, Exportable... exportables) {
		super(owner);
		
		final LinkedListMultimap<Exportable, Exporter> exportableMap = LinkedListMultimap.create();
		if (exportables != null) {
			for (Exportable exportable : exportables) {
				if (exportable == null) {
					continue;
				}
				Exporter[] exporters = exportable.getExporters();
				if (exporters== null) {
					continue;
				}
				for (Exporter exporter : exporters) {
					exportableMap.put(exportable, exporter);
				}
			}
		}

		setTitle("Export");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // The window listener will take care of it
		
		// Clear references to exportables on close
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				exportableMap.clear();
			}
			@Override
			public void windowClosing(WindowEvent e) {
				ExportWizard.this.dispose();
			}
		});

		final JButton nextButton = new JButton("Next");
		nextButton.setEnabled(true);
		nextButton.setDoubleBuffered(true);
		
		final JLabel dirLabel = new JLabel("Enter an export directory");
		dirLabel.setToolTipText("The top level directory into which all exports will be saved");
		dirLabel.setDoubleBuffered(true);
		dirLabel.setAlignmentX(LEFT_ALIGNMENT);
		dirLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		final JTextField dirField = new JTextField(StringUtils.trimToEmpty(SystemUtils.USER_HOME), 20);
		dirField.setToolTipText(dirLabel.getText());
		dirField.setDoubleBuffered(true);
		dirField.setAlignmentX(LEFT_ALIGNMENT);
		dirField.setHorizontalAlignment(SwingConstants.LEFT);
		
		final JPanel mainPanel = new JPanel();
		mainPanel.setDoubleBuffered(true);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		
		final JButton browseButton = new JButton("Browse");
		browseButton.setDoubleBuffered(true);
		browseButton.setAlignmentX(LEFT_ALIGNMENT);
		browseButton.addActionListener(new ErrorPopupActionListener() {
			final JFileChooser dirChooser;
			{
				this.dirChooser = new JFileChooser(new File(dirField.getText()));
				this.dirChooser.setDoubleBuffered(true);
				this.dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				this.dirChooser.setToolTipText("Browse for an export directory");
			}
			@Override
			public void action() throws Exception {
				if (this.dirChooser.showSaveDialog(ExportWizard.this) == JFileChooser.APPROVE_OPTION && this.dirChooser.getSelectedFile() != null) {
					dirField.setText(this.dirChooser.getSelectedFile().getAbsolutePath());
				} else {
					this.dirChooser.setSelectedFile(new File(dirField.getText()));
				}
			}
		});
		
		JPanel dirPanel = new JPanel();
		dirPanel.setDoubleBuffered(true);
		
		GroupLayout dirLayout = new GroupLayout(dirPanel);
		dirPanel.setLayout(dirLayout);
		
		Component glue = Box.createGlue();
		
		dirLayout.setHorizontalGroup(dirLayout.createSequentialGroup()
				.addComponent(dirLabel)
				.addComponent(dirField)
				.addComponent(browseButton)
				.addComponent(glue));
		
		dirLayout.setVerticalGroup(dirLayout.createParallelGroup(Alignment.BASELINE, false)
				.addComponent(dirLabel)
				.addComponent(dirField)
				.addComponent(browseButton)
				.addComponent(glue));
		
		dirLayout.setAutoCreateContainerGaps(true);
		dirLayout.setAutoCreateGaps(true);

		mainPanel.add(dirPanel);
		
		class Update {
			private final int step;
			private final String name;
			public Update(int step, String name) {
				super();
				this.step = step;
				this.name = name;
			}
		}

		final JButton cancelButton = new JButton("Cancel");
		cancelButton.setDoubleBuffered(true);
		cancelButton.addActionListener(new ErrorPopupActionListener() {
			@Override
			public void action() throws Exception {
				ExportWizard.this.dispatchEvent(new WindowEvent(ExportWizard.this,WindowEvent.WINDOW_CLOSING));
			}
		});
		
		nextButton.addActionListener(new ErrorPopupActionListener() {
			@Override
			public void action() throws Exception {
				final File dirFile = new File(dirField.getText());
				
				final File exportDir = new File(dirFile, new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date()));
				String message;
				if (nextButton.isEnabled() && exportDir.exists()) {
					
					if (exportDir.isDirectory()) {
						message = String.format("Files within '%s' directory will be deleted.", exportDir.getAbsolutePath());
					} else {
						message = String.format("File '%s' will be deleted.", exportDir.getAbsolutePath());
					}
					
					JXLabel msgLabel = new JXLabel();
					msgLabel.setText(String.format("%s Do you want to contnue?", message));
					msgLabel.setLineWrap(true);
					msgLabel.setMaxLineSpan(msgLabel.getFontMetrics(msgLabel.getFont()).charWidth('_')*50);
					
					int confirm = JOptionPane.showConfirmDialog(ExportWizard.this, msgLabel, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					
					if (confirm != JOptionPane.YES_OPTION) {
						return;
					}
				}
				
				final Cursor savedCursor = ExportWizard.this.getCursor();
				ExportWizard.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				nextButton.removeActionListener(this);
				nextButton.setEnabled(false);
				nextButton.addActionListener(new ErrorPopupActionListener() {
					@Override
					public void action() throws Exception {
						cancelButton.setEnabled(true);
						cancelButton.doClick();
					}
				});

				final JProgressBar progress = new JProgressBar(0, exportableMap.size()+1);
				progress.setDoubleBuffered(true);
				progress.setAlignmentX(CENTER_ALIGNMENT);
				progress.setIndeterminate(true);
				progress.setStringPainted(true);

				final JLabel progressLabel = new JLabel("Preparing export...");
				progressLabel.setAlignmentX(CENTER_ALIGNMENT);
				progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
				progressLabel.setDoubleBuffered(true);
				progressLabel.setFont(progressLabel.getFont().deriveFont(Font.PLAIN));
				
				mainPanel.setVisible(false);
				mainPanel.removeAll();
				mainPanel.add(progressLabel);
				mainPanel.add(Box.createVerticalStrut(5));
				mainPanel.add(progress);
				mainPanel.setVisible(true);
				
				RetsWorker<Boolean, Update> worker = new RetsWorker<Boolean, Update>() {
					@Override
					protected void processWithPopup(List<Update> chunks) {
						for (final Update update : chunks) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									progressLabel.setText(String.format("Exporting %s...", update.name));
									progress.setValue(update.step);
								}
							});
						}
					}

					@SuppressWarnings("deprecation")
					@Override
					protected Boolean doInBackgroundWithPopup() {
						try {

							if (exportDir.exists()) {
								
								if (exportDir.isDirectory()) {
									String[] ls = exportDir.list();
									if (ls != null && ls.length > 0) {
										try {
											Files.deleteRecursively(exportDir);
										} catch (Exception e) {
											GuiUtils.exceptionPopup(e.getClass().getSimpleName(), e);
										}
									}
								} else {
									exportDir.delete();
								}
							}
							
							if (!exportDir.mkdir()) {
								publish(new Update(progress.getMaximum(), "Failed"));
								JOptionPane.showMessageDialog(ExportWizard.this, String.format("Unable to create directory '%s'", exportDir.getAbsolutePath()), "Export Failed", JOptionPane.ERROR_MESSAGE);
								return false;
							}

							try {
								SwingUtilities.invokeAndWait(new Runnable() {
									public void run() {
										progress.setIndeterminate(false);
									}
								});
							} catch (Exception e) {
								GuiUtils.exceptionPopup(e.getClass().getSimpleName(), e);
							}
							
							int i=1;
							for (Entry<Exportable, Exporter> export : exportableMap.entries() ) {
								publish(new Update(i++, export.getValue().getName()));
								File dir = new File(exportDir, export.getKey().getExportableName());

								if (!dir.exists()) {
									if (dir.mkdir() ) {
										// Keep going if one export fails
										try {
											export.getValue().export(dir);
										} catch (Exception e) {
											// TODO: Accumulate these errors and show them all in the last dialog window
											GuiUtils.exceptionPopup(e.getClass().getSimpleName(), e);
										}
									} else {
										JOptionPane.showMessageDialog(ExportWizard.this, String.format("Unable to create directory '%s'", dir.getAbsolutePath()), "Create Failed", JOptionPane.ERROR_MESSAGE);
									}
								} else {

									// Keep going if one export fails
									try {
										export.getValue().export(dir);
									} catch (Exception e) {
										// TODO: Accumulate these errors and show them all in the last dialog window
										GuiUtils.exceptionPopup(e.getClass().getSimpleName(), e);
									}

								}

							}
						} catch (Exception e) {
							GuiUtils.exceptionPopup(e.getClass().getName(), e);
						}
						return true;
					}
					
					@Override
					protected void doneWithPopup() {
						
						progress.setValue(progress.getMaximum());
						
						Boolean success = false;
						try {
							success = get();
						} catch (Exception e) {
							GuiUtils.exceptionPopup(e.getClass().getSimpleName(), e);
						}

						JLabel doneLabel = new JLabel();
						doneLabel.setFont(doneLabel.getFont().deriveFont(Font.PLAIN));
						doneLabel.setHorizontalAlignment(SwingConstants.CENTER);
						doneLabel.setAlignmentY(CENTER_ALIGNMENT);
						doneLabel.setAlignmentX(CENTER_ALIGNMENT);
						
						AbstractHyperlinkAction linkAction = new AbstractHyperlinkAction(exportDir.getAbsolutePath()) {
							public void actionPerformed(ActionEvent e) {
								try {
									Desktop.getDesktop().open(exportDir);
								} catch (IOException e1) {
									GuiUtils.exceptionPopup(e.getClass().getSimpleName(), e1);
								} finally {
									setVisited(true);
								}
							}
						};
						JXHyperlink link = new JXHyperlink(linkAction);
						link.setHorizontalAlignment(SwingConstants.CENTER);
						link.setAlignmentY(CENTER_ALIGNMENT);
						link.setAlignmentX(CENTER_ALIGNMENT);				
						
						JPanel donePanel = new JPanel(true);
						donePanel.setLayout(new BoxLayout(donePanel, BoxLayout.PAGE_AXIS));
						donePanel.setAlignmentX(CENTER_ALIGNMENT);
						donePanel.setAlignmentY(CENTER_ALIGNMENT);
						donePanel.add(doneLabel);
						donePanel.add(Box.createVerticalStrut(8));
						donePanel.add(link);						
						
						if (success != null && success) {
							doneLabel.setText("All export operations have completed. Exported files are located in:");
						} else {
							doneLabel.setText("One or more export operations failed. Exported files are located in:");
						}
						
						cancelButton.setVisible(false);
						ExportWizard.this.remove(cancelButton);
						
						mainPanel.setVisible(false);
						mainPanel.removeAll();
						mainPanel.add(donePanel);
						mainPanel.setVisible(true);

						nextButton.setText("Close");
						nextButton.setToolTipText("");
						
						ExportWizard.this.setCursor(savedCursor);
						
						nextButton.setEnabled(true);
					}
					
					};
					
					worker.execute();
					
			}
		});
		
		// Listens to textfield for changes and enables/disable next button depending on whether or
		// not entered pathname is a valid directory
		dirField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setNextButtonEnabled();				
			}
			public void insertUpdate(DocumentEvent e) {
				setNextButtonEnabled();				
			}
			public void removeUpdate(DocumentEvent e) {
				setNextButtonEnabled();
			}
			private void setNextButtonEnabled() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						File dirFile = new File(dirField.getText());
						nextButton.setEnabled(dirFile != null && dirFile.isDirectory() && dirFile.canWrite());
					}
				});
			}
		});
		
		GroupLayout layout = new GroupLayout(getContentPane());
		setLayout(layout);

		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);

		Group hGroup = layout.createParallelGroup(Alignment.CENTER)
			.addComponent(mainPanel)
			.addComponent(separator)
			.addGroup(layout.createSequentialGroup()
					.addComponent(nextButton)
					.addComponent(cancelButton));

		Group vGroup = layout.createSequentialGroup()
		.addComponent(mainPanel)
		.addComponent(separator)
		.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(nextButton)
				.addComponent(cancelButton));

		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);

		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
	}

}
