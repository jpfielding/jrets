package org.realtors.rets.retsexplorer.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JSplitPane;

import org.realtors.rets.client.RetsSession;
import org.realtors.rets.retsexplorer.retstabbedpane.RetsView;
import org.realtors.rets.retsexplorer.util.GuiUtils;
import org.realtors.rets.retsexplorer.util.RetsWorker;
import org.realtors.rets.retsexplorer.wirelog.WireLogConsole;
import org.realtors.rets.retsexplorer.wirelog.WireLogConsoleOutputStream;
import org.realtors.rets.util.RetsClient;
import org.realtors.rets.util.RetsClientConfig;
import org.realtors.rets.util.RetsTransaction;

public class SourceSplitView extends JSplitPane {
	private LoginView loginView;
	private WireLogConsole console;
	private RetsView retsView;
	private JButton loginSuccess = new JButton();
	private String sourceName;
	
	public SourceSplitView(List<RetsClientConfig> retsConfigs, ActionListener loginSuccess) {
		this.loginSuccess.addActionListener(loginSuccess);
		this.loginView = new LoginView(retsConfigs);
		setLoginButtonActionListener();
		this.console = new WireLogConsole();
//		JTabbedPane bottomTabbedPane = new JTabbedPane();
//		bottomTabbedPane.add(this.console, "Console");
//		bottomTabbedPane.add(this.console, "Some Other View");
		
		this.setOrientation(JSplitPane.VERTICAL_SPLIT);
		this.setDoubleBuffered(true);
		this.setTopComponent(this.loginView);
//		this.setBottomComponent(bottomTabbedPane);
		//XXX when this is released publicly, uncommenting this line, and commenting the one above will remove the matcher and code generation from display
		this.setBottomComponent(this.console); 
		this.setDividerLocation(.6);
	}
	
	private void setLoginButtonActionListener() {
		getLoginViewOperator().getLoginButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doLogin();
			}
		});
	}
	
	private void switchTopComponents() {
		if (this.retsView != null) {
			this.remove(this.leftComponent);
			this.setTopComponent(this.retsView);
			this.setDividerLocation(.6);
			this.loginSuccess.doClick(); //.....very sad hack...
		}
	}

	private void doLogin() {
		setSourceName(this.loginView.getRetsServiceName());
		RetsWorker work = new RetsWorker<Void, Void>() {
			private WireLogConsoleOutputStream wire = new WireLogConsoleOutputStream(SourceSplitView.this.console);
			@Override
			protected Void doInBackgroundWithPopup() throws Exception {
				RetsClientConfig retsConfig = SourceSplitView.this.loginView.getSelectedRetsConfig(this.wire);
				try {
					RetsClient client = retsConfig.createClient();
					client.executeRetsTransaction(new RetsTransaction() {
						public Object execute(RetsSession session) throws Exception {
							// just ensure a session was setup
							return null;
						}
					});
					SourceSplitView.this.retsView = new RetsView(retsConfig, client, SourceSplitView.this.console);
				} catch (Exception e) {
					setSourceName("login");
					GuiUtils.exceptionPopup("Error Attempting to Login", e);
				}
				return null;
			}

			@Override
			protected void doneWithPopup() {
				switchTopComponents();
			}
		};
		work.execute();
	}
	
	private void setSourceName(String name) {
		this.sourceName = name;
	}
	
	public String getSourceName() {
		return this.sourceName;
	}
	
	public LoginView getLoginViewOperator() {
		return this.loginView;
	}
	
	public RetsView getRetsView() {
		return this.retsView;
	}
}
