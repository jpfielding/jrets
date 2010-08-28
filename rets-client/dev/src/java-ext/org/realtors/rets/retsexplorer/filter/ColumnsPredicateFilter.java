package org.realtors.rets.retsexplorer.filter;

import java.util.List;

import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.table.TableColumnExt;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * For each given columns, hides the column if all rows in the column fail to meet a predicate
 */
public class ColumnsPredicateFilter extends Filter {
	
	private JXTable target;
	
	private Predicate<Object> predicate;
	
	private int[] columns;
	
	private List<Integer> hiddenColumns = Lists.newArrayList();

	public ColumnsPredicateFilter(JXTable target,
			Predicate<Object> predicate, int... columns) {
		super();
		this.target = target;
		this.predicate = predicate;
		if (columns != null) {
			this.columns = new int[columns.length];
			System.arraycopy(columns, 0, this.columns, 0, columns.length);
		} else {
			this.columns = new int[0];
		}
	}

	private void setColumnVisible(int col, boolean visible) {
		String colName = this.target.getModel().getColumnName(col);
		if (colName == null) {
			return;
		}
		TableColumnExt colExt = this.target.getColumnExt(colName);
		if (colExt != null) {
			colExt.setVisible(visible);
		}
	}
	
	@Override
	protected void filter() {
		TableModel model = this.target.getModel();
		if (model == null) {
			return;
		}
		ColumnsLoop:
			for (int col : this.columns) {
				for (int row=0; row<model.getRowCount(); row++) {
					Object value = model.getValueAt(row, col);
					if (this.predicate.apply(value)) {
						continue ColumnsLoop;
					}
				}
				setColumnVisible(col, false);
				this.hiddenColumns.add(col);
			}
	}

	@Override
	public int getSize() {
		return getInputSize();
	}

	@Override
	protected void init() {
		// Noop
	}

	@Override
	protected int mapTowardModel(int row) {
		return row;
	}

	@Override
	protected void reset() {
		// Set all hidden columns to visible
		for (int col : this.hiddenColumns) {
			setColumnVisible(col, true);
		}
		this.hiddenColumns.clear();
	}

}