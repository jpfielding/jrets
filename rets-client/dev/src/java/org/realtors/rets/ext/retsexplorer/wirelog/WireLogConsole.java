package org.realtors.rets.ext.retsexplorer.wirelog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.StringContent;

import org.apache.commons.lang.StringUtils;
import org.realtors.rets.ext.retsexplorer.util.GuiUtils;


public class WireLogConsole extends JPanel {
	
	private static final int DEFAULT_SIZE_LIMIT = 10*1024*1024;

	private volatile int maximumTextLength = DEFAULT_SIZE_LIMIT;
	private AtomicBoolean scrollLocked = new AtomicBoolean(false);
	
	private JTextArea wireTextArea = initWireTextArea(null);
	
	private JScrollPane scrollPane = new JScrollPane(this.wireTextArea) {
		@Override
		public void scrollRectToVisible(Rectangle aRect) {
			if (WireLogConsole.this.scrollLocked.get()) return;
			super.scrollRectToVisible(aRect);
		}
	};
	
	private JPanel toolBarPanel = getToolBarPanel();
	
	private boolean paused = false;

	public WireLogConsole() {
		initialize();
	}
	
	public boolean isPaused() {
		return this.paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	private void initialize() {
		setDoubleBuffered(true);

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup())
				.addComponent(this.toolBarPanel)
				.addComponent(this.scrollPane));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.toolBarPanel)
				.addComponent(this.scrollPane));
		
		this.setBorder(BorderFactory.createTitledBorder(""));

	}
	
	private JPanel getToolBarPanel() {
		if (this.toolBarPanel == null) {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			
			JButton clear = new JButton("Clear");
			clear.setToolTipText(clear.getText());
			clear.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					clearAllText();
				}
			});
			
			final JToggleButton lock = new JToggleButton("Scroll Lock");
			lock.setToolTipText(lock.getText());
			lock.setSelected(isScrollLocked());
			lock.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean locked = !WireLogConsole.this.scrollLocked.get();
					setScrollLocked(locked);
					lock.setSelected(locked);
				}
			});

			final JToggleButton pause = new JToggleButton("Pause");
			pause.setToolTipText(pause.getText());
			pause.setSelected(isPaused());
			pause.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean nowPaused = !WireLogConsole.this.paused;
					setPaused(nowPaused);
					pause.setSelected(nowPaused);
				}
			});
			
			Component glue = Box.createHorizontalGlue();
			
			clear.setPreferredSize(lock.getPreferredSize());
			pause.setPreferredSize(lock.getPreferredSize());
			
			panel.add(glue);
			panel.add(pause);
			panel.add(clear);
			panel.add(lock);
			
			panel.setMinimumSize(new Dimension(panel.getMinimumSize().width, panel.getPreferredSize().height));
			panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getPreferredSize().height));
			
			this.toolBarPanel = panel;
		}
		return this.toolBarPanel;
	}
	
	private JTextArea initWireTextArea(JTextArea textArea) {
		JTextArea area = (textArea==null) ? new JTextArea() : textArea;
		area.setDoubleBuffered(true);
		area.setRows(10);
		area.setTabSize(4);
		area.setEditable(false);
		area.setLineWrap(false);
		area.setWrapStyleWord(false);
		area.getCaret().setBlinkRate(0);
		area.getCaret().setVisible(false);
		Font font = Font.getFont("Monospaced");
		if (font != null) {
			area.setFont(font);
		}
		return area;
	}
	
	public void appendText(String text) {
		if (this.paused || StringUtils.isEmpty(text)) {
			return;
		}
		synchronized(this) {
			Document doc = this.wireTextArea.getDocument();

			// Save current positions in case scroll lock is enabled
			final JScrollBar vScroll = this.scrollPane.getVerticalScrollBar();
			final int vPos = vScroll.getValue();
			final JScrollBar hScroll = this.scrollPane.getHorizontalScrollBar();
			final int hPos = hScroll.getValue(); 
			
			if (text.length() >= this.maximumTextLength) {
				StringContent content = new StringContent(text.length());
				try {
					content.insertString(0, text.substring(text.length()-this.maximumTextLength));
					doc = new PlainDocument(content);
					this.wireTextArea.setDocument(doc);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			} else {
				int proposedSize = text.length() + doc.getLength();
				if (proposedSize > this.maximumTextLength) {
					try {
						doc.remove(0, text.length());
					} catch (BadLocationException e) {
						GuiUtils.exceptionPopup(e.getMessage(), e);
					} 
				}
				this.wireTextArea.append(text.toString());
			}

			this.wireTextArea.setCaretPosition(doc.getLength());
			
			final boolean locked = this.scrollLocked.get();
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (locked) {
						vScroll.setValue(vPos);
						hScroll.setValue(hPos);
					} else {
						vScroll.setValue(vScroll.getMaximum());
						hScroll.setValue(hScroll.getMinimum());
					}
				}
			});
		}
	}
	
	public void scrollToBottom() {
		JScrollBar vScroll = this.scrollPane.getVerticalScrollBar();
		vScroll.setValue(vScroll.getMaximum());
		JScrollBar hScroll = this.scrollPane.getHorizontalScrollBar();
		hScroll.setValue(hScroll.getMinimum()); 
	}

	public void clearAllText() {
		synchronized (this) {
			Document doc = this.wireTextArea.getDocument();
			if (doc == null) {
				doc = new PlainDocument();
				this.wireTextArea.setDocument(doc);
			}
			try {
				doc.remove(0, doc.getLength());
			} catch (BadLocationException e) {
				GuiUtils.exceptionPopup(e.getMessage(), e);
			}
		}
	}
	
	public int getMaximumTextLength() {
		synchronized (this) {
			return this.maximumTextLength;
		}
	}
	
	public void setMaximumTextLength(int sizeLimit) {
		synchronized (this) {
			this.maximumTextLength = sizeLimit;	
		}
	}
	
	public boolean isScrollLocked() {
		return this.scrollLocked.get();
	}
	
	public void setScrollLocked(boolean scrollLocked) {
		this.scrollLocked.set(scrollLocked);
	}

	public int getColumns() {
		return this.wireTextArea.getColumns();
	}

	public int getRows() {
		return this.wireTextArea.getRows();
	}
	
	/** If column <= 0, then the number of columns to display will be auto calculated */
	public void setColumns(int columns) {
		if (columns <= 0) {
			int rowSave = this.wireTextArea.getRows();
			this.wireTextArea.setFont(this.wireTextArea.getFont()); // In JTextArea, this sets columns and rows to 0
			if (rowSave > 0) {
				this.wireTextArea.setRows(rowSave);
			}
			return;
		}
		this.wireTextArea.setColumns(columns);
	}

	/** If rows <= 0, then the number of rows to display will be auto calculated */
	public void setRows(int rows) {
		if (rows <= 0) {
			int colSave = this.wireTextArea.getColumns();
			this.wireTextArea.setFont(this.wireTextArea.getFont()); // In JTextArea, this sets columns and rows to 0
			if (colSave > 0) {
				this.wireTextArea.setColumns(colSave);
			}
			return;
		}
		this.wireTextArea.setRows(rows);
	}
	
	protected final JTextArea getWireTextArea() {
		return this.wireTextArea;
	}

}
