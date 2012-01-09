package org.realtors.rets.retsexplorer.export;


public abstract class AbstractExporter implements Exporter {

	private String description;

	private String name;

	public AbstractExporter(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public String getName() {
		return this.name;
	}

}
