package org.realtors.rets.ext.retsexplorer.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.realtors.rets.client.SearchResultSet;
import org.realtors.rets.ext.retsexplorer.util.Csv;

import com.google.common.io.Closeables;

/** Can only be used once, then the internal reference to the table model is cleared */
public class SearchResultSetCsvExporter extends CsvExporter<SearchResultSet> implements Exporter {

	private String filename;
	
	public SearchResultSetCsvExporter(SearchResultSet results, String filename) {
		super(results);
		this.filename = (StringUtils.isEmpty(filename)) ? "data.csv" : filename;
	}
	
	@Override
	protected void doExport(File path) throws Exception {
		File exportPath = new File( (path==null) ? SystemUtils.getUserHome() : path, this.filename);
		SearchResultSet result = getTarget();

		String[] cols = result.getColumns();
		if (cols == null) {
			return;
		}
		FileOutputStream fout = null;
		PrintWriter out = null;
		String line = Csv.escape(',', false, cols);
		try {
			fout = new FileOutputStream(exportPath, false);
			out = new PrintWriter(new OutputStreamWriter(fout,Charset.defaultCharset()), true);
			out.printf("%s\r\n", line);

			while(result.hasNext()) {
				String[] row = result.next();
				line = Csv.escape(',', false, row);
				out.printf("%s\r\n", line);
			}

		} finally {
			this.filename = null;
			if (out != null) {
				out.print("\r\n");
				out.flush();
				Closeables.closeQuietly(out);
			}
			Closeables.closeQuietly(fout);
		}
	}

}
