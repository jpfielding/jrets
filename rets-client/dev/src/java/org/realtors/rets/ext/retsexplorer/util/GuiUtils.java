package org.realtors.rets.ext.retsexplorer.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jdesktop.swingx.util.WindowUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class GuiUtils {

	public static void exceptionPopup(String title, Component comp, Throwable t) {

		String dialogTitle = title;
		if (title==null) {
			dialogTitle = (t==null) ? "" : t.getMessage();
		} 
		
		JScrollPane scroll = null;
		if (t != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			pw.flush();
			
			JTextArea textArea = new JTextArea(sw.toString());
			textArea.setRows(25);
			textArea.setColumns(50);
			textArea.setBackground(UIManager.getColor("Panel.background"));
			
			textArea.setEditable(false);
			textArea.setTabSize(4);
			textArea.setLineWrap(false);
			textArea.setWrapStyleWord(false);
			
			Font font = Font.getFont(Font.MONOSPACED);
			if (font != null) {
				textArea.setFont(font);
			}
			
			scroll = new JScrollPane(textArea);
			scroll.setBorder(BorderFactory.createEmptyBorder());
			scroll.setViewportBorder(BorderFactory.createEmptyBorder());
		}

		JPanel panel = new JPanel(true);
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		Group hGroup = layout.createParallelGroup(Alignment.LEADING);
		Group vGroup = layout.createSequentialGroup();
		
		if (scroll != null) {
			hGroup.addComponent(scroll);
			vGroup.addComponent(scroll);
		}
		
		if (comp != null) {
			hGroup.addComponent(comp);
			vGroup.addComponent(comp);
		}
		
		
		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);
		
		JOptionPane.showMessageDialog(null, panel, dialogTitle, JOptionPane.WARNING_MESSAGE);
		
	}

	public static void exceptionPopup(Throwable t) {
		exceptionPopup(t==null ? null : t.getMessage(),t);
	}
	
	public static void exceptionPopup(String title, Throwable t) {
		exceptionPopup(title, null, t);
	}

	public static void setForeground(boolean recursive, Color color, Component... components) {
		if (color == null) {
			return;
		}
		List<Component> uncolored = Lists.newLinkedList();
		Component[] children = null;
		for (Component component : components) {
			if(component == null) {
				continue;
			}
			component.setForeground(color);
			if(recursive && component instanceof Container) {
				children = ((Container)component).getComponents();
				if(children != null) {
					uncolored.addAll(Arrays.asList(children));
				}
			}
		}
		Component component = null;
		while(recursive && !uncolored.isEmpty()) {
			component = uncolored.remove(0);
			if(component == null) {
				continue;
			}
			component.setForeground(color);
			if(component instanceof Container) {
				children = ((Container)component).getComponents();
				if(children != null) {
					uncolored.addAll(Arrays.asList(children));
				}
			}
		}
	}
	
	
	public static void setBackground(boolean recursive, Color color, Component... components) {
		if (color == null) {
			return;
		}
		List<Component> uncolored = Lists.newLinkedList();
		Component[] children = null;
		for (Component component : components) {
			if(component == null) {
				continue;
			}
			component.setBackground(color);
			if(recursive && component instanceof Container) {
				children = ((Container)component).getComponents();
				if(children != null) {
					uncolored.addAll(Arrays.asList(children));
				}
			}
		}
		Component component = null;
		while(recursive && !uncolored.isEmpty()) {
			component = uncolored.remove(0);
			if(component == null) {
				continue;
			}
			component.setForeground(color);
			if(component instanceof Container) {
				children = ((Container)component).getComponents();
				if(children != null) {
					uncolored.addAll(Arrays.asList(children));
				}
			}
		}
	}
	
	
	public static void setColor(boolean recursive, Color foreground, Color background, Component... components) {
		if (foreground == null && background == null) {
			return;
		}
		List<Component> uncolored = Lists.newLinkedList();
		Component[] children = null;
		for (Component component : components) {
			if(component == null) {
				continue;
			}
			if (background != null) {
				component.setBackground(background);
			}
			if (foreground != null) {
				component.setForeground(foreground);
			}
			if(recursive && component instanceof Container) {
				children = ((Container)component).getComponents();
				if(children != null) {
					uncolored.addAll(Arrays.asList(children));
				}
			}
		}
		Component component = null;
		while(recursive && !uncolored.isEmpty()) {
			component = uncolored.remove(0);
			if(component == null) {
				continue;
			}
			if (background != null) {
				component.setBackground(background);
			}
			if (foreground != null) {
				component.setForeground(foreground);
			}
			if(component instanceof Container) {
				children = ((Container)component).getComponents();
				if(children != null) {
					uncolored.addAll(Arrays.asList(children));
				}
			}
		}
	}
	
	/** If the current thread is the event dispatch thread, then the given
	 *  runnable is invoked immediately. Otherwise, if any of the affected
	 *  components are visible, then the runnable is invoked on the event
	 *  dispatch thread via the invokeAndWait construct.
	 *  
	 *  @return <tt>null</tt> or an exception if one was thrown by the run() method of the runnable argument
	 */
	public static Exception invokeOnDispatchThreadNow(Runnable runnable, Component... affected) {
		try {
			if (runnable == null) {
				return null;
			}
			if (affected == null || affected.length <= 0 || SwingUtilities.isEventDispatchThread()) {
				runnable.run();
				return null;
			}
			boolean safe = true;
			for (Component comp : affected) {
				if (comp != null && comp.isShowing()) {
					safe = false;
					break;
				}
			}
			if (safe) {
				runnable.run();
				return null;
			}
			SwingUtilities.invokeAndWait(runnable);
		} catch (Exception e) {
			return e;
		}
		return null;
	}
	
	
	public static Thread getEventDispatchThread() {
		if (SwingUtilities.isEventDispatchThread()) {
			return Thread.currentThread();
		}
		class EventDispatchThreadGetter implements Runnable {
			Thread eventDispatchThread;
			public void run() {
				this.eventDispatchThread = Thread.currentThread();
			}
		}
		EventDispatchThreadGetter getThread = new EventDispatchThreadGetter();
		try {
			SwingUtilities.invokeAndWait(getThread);
		} catch (Exception shhh) {
			// Fall through
		}
		return getThread.eventDispatchThread;
	}
	
	public static boolean isGtkLookAndFeel(LookAndFeel laf) {
		if (laf == null) {
			return false;
		}
		try {
			Class<?> gtkClass = Class.forName("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			return laf.getClass()==gtkClass || laf.getClass().equals(gtkClass);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
    public static <T> List<T> getAllChildren(final Class<T> clazz, Container c) {
    	if (clazz == null || c == null) {
            return Lists.newArrayList();
        }
        List<Component> components = WindowUtils.getAllComponents(c);
        if (components == null) {
        	return Lists.newArrayList();
        }
        List<T> children = Lists.newArrayList(Iterables.filter(Iterables.transform(components,
        	new Function<Component, T>() {
        	public T apply(Component from) {
        		if (from != null && clazz.isInstance(from)) {
        			return (T)from;
        		}
        		return null;
        	}
        }), Predicates.notNull()));
        return children;
    }
	
}
