package org.realtors.rets.ext.retsexplorer.find;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXFindPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.search.Searchable;
import org.jdesktop.swingx.search.TableSearchable;
import org.realtors.rets.ext.retsexplorer.retstabbedpane.MetadataTable;
import org.realtors.rets.ext.retsexplorer.retstabbedpane.RetsDataTable;
import org.realtors.rets.ext.retsexplorer.retstabbedpane.RetsSourceTabbedPane;
import org.realtors.rets.ext.retsexplorer.retstabbedpane.RetsView;
import org.realtors.rets.ext.retsexplorer.util.GuiUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class RetsFindPanel extends JXFindPanel {
	
	private JCheckBox acrossAllCheck = new JCheckBox("",false);
	private List<Searchable> allSearchables;
	
	@Override
	protected void build() {
		super.build();
		this.acrossAllCheck.setVisible(true);
		this.wrapCheck.setSelected(false);
		this.wrapCheck.getParent().add(this.acrossAllCheck);
	}
	
	@Override
	public void setSearchable(Searchable searchable) {
		if (this.acrossAllCheck != null) {
			this.acrossAllCheck.setVisible(false);
		}
		if (searchable instanceof TableSearchable) {
			final JXTable target = ((TableSearchable)searchable).getTarget();
			this.acrossAllCheck.setVisible(false);
			Iterable<Searchable> searchables = null;
			if (target instanceof MetadataTable) {
				RetsView retsView = (RetsView)SwingUtilities.getAncestorOfClass(RetsView.class, target);
				if (retsView != null) {
					this.acrossAllCheck.setVisible(true);
					this.acrossAllCheck.setText("Across All Resources");
					Iterable<MetadataTable> metas = Iterables.filter(retsView.getMetadataTables(), new Predicate<MetadataTable>() {
						public boolean apply(MetadataTable input) {
							return (input != target);
						}
					});  
					searchables = Iterables.transform(metas, new Function<MetadataTable, Searchable>(){
						public Searchable apply(MetadataTable from) {
							return from.getSearchable();
						}
					});
				}
			} else if (target instanceof RetsDataTable) {
				RetsSourceTabbedPane retsTabbedPane = (RetsSourceTabbedPane)SwingUtilities.getAncestorOfClass(RetsSourceTabbedPane.class, target);
				if (retsTabbedPane != null) {
					this.acrossAllCheck.setVisible(false); // TODO: Not working, Disable this for now
					this.acrossAllCheck.setText("Across All Search Results");
					Iterable<RetsDataTable> data = Iterables.filter(GuiUtils.getAllChildren(RetsDataTable.class, retsTabbedPane), new Predicate<RetsDataTable>(){
						public boolean apply(RetsDataTable input) {
							return target == input;
						}});  
					searchables = Iterables.transform(data, new Function<RetsDataTable, Searchable>(){
						public Searchable apply(RetsDataTable from) {
							return from.getSearchable();
						}
					});
				}
			}
			if (this.allSearchables != null) {
				this.allSearchables.clear();
				Iterables.addAll(this.allSearchables, searchables);
			} else {
				this.allSearchables = Lists.newArrayList(searchables);
			}
			this.acrossAllCheck.setEnabled(this.allSearchables.size()>0);
		}
		if (searchable== null && this.allSearchables != null) {
			this.allSearchables.clear();
		}
		super.setSearchable(searchable);
	}

	@Override
	protected void doFind() {
		if (acrossAll()) {
			int previous = getPatternModel().getFoundIndex();
			Searchable save = this.searchable;
			for (Searchable toSearch : this.allSearchables) {
				this.searchable = toSearch;
				getPatternModel().setFoundIndex(-1);
				doSearch();
			}
			this.searchable = save;
			getPatternModel().setFoundIndex(previous);
		}
		super.doFind();
	}
	
	private boolean acrossAll() {
		return this.acrossAllCheck != null
			&& this.allSearchables != null
			&& !this.allSearchables.isEmpty()
			&& this.acrossAllCheck.isEnabled()
			&& this.acrossAllCheck.isSelected();
	}

}
