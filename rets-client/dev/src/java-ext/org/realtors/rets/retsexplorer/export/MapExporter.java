package org.realtors.rets.retsexplorer.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

import com.google.common.io.Closeables;

public class MapExporter extends SingleUseExporter<Map<?, ?>> {

	String filename;
	String delimeter;
	
	public MapExporter(String name, String description, Map<?, ?> target, String delimeter, String filename) {
		super(name, description, target);
		this.filename = (StringUtils.isEmpty(filename)) ? "map.txt" : filename;
		this.delimeter = (StringUtils.isEmpty(delimeter)) ? "=" : delimeter;
	}

	@Override
	protected void doExport(File path) throws Exception {
		File exportPath = new File( (path==null) ? SystemUtils.getUserHome() : path, this.filename);
		Map<?, ?> map = getTarget();
		if (map.isEmpty()) {
			return;
		}
		FileOutputStream fout = null;
		PrintWriter out = null;
		try {
			fout = new FileOutputStream(exportPath, false);
			out = new PrintWriter(new OutputStreamWriter(fout, Charset.defaultCharset()), true);
			for (Entry<?, ?> entry : map.entrySet()) {
				out.printf("%s%s%s", entry.getKey(), this.delimeter, entry.getValue());
				out.println();
			}
		} finally {
			this.filename = null;
			this.delimeter = null;
			if (out != null) {
				out.println();
				out.flush();
				Closeables.closeQuietly(out);
			}
			Closeables.closeQuietly(fout);
		}
	}

}
