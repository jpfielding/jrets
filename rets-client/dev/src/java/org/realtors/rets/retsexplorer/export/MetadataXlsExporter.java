package org.realtors.rets.retsexplorer.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.lang.SystemUtils;
import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.ext.util.MetadataStreamSheet;

import com.google.common.io.Closeables;

public class MetadataXlsExporter extends XlsExporter<Metadata> implements Exporter {

	public MetadataXlsExporter(Metadata metadata) {
		super(metadata);
	}
	
	@Override
	protected void doExport(File path) {
		File exportPath = new File( (path==null) ? SystemUtils.getUserHome() : path, "metadata.xls");
		OutputStream out = null;
		try {
			out = new FileOutputStream(exportPath, false);
			new MetadataStreamSheet(getTarget()).writeFile(out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			Closeables.closeQuietly(out);
		}		
	}

}
