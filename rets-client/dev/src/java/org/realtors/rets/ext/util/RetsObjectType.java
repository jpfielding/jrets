package org.realtors.rets.ext.util;


public class RetsObjectType extends CaseInsensitiveWellKnownName {
	public static final RetsObjectType Photo = new RetsObjectType("Photo");
	public static final RetsObjectType Plat = new RetsObjectType("Plat");
	public static final RetsObjectType Video = new RetsObjectType("Video");
	public static final RetsObjectType Audio = new RetsObjectType("Audio");
	public static final RetsObjectType Thumbnail = new RetsObjectType("Thumbnail");
	public static final RetsObjectType Map = new RetsObjectType("Map");
	public static final RetsObjectType VRImage = new RetsObjectType("VRImage");
	
	protected RetsObjectType(String name) {
		super(name);
	}
	
	public static RetsObjectType forName(String name) {
		return CaseInsensitiveWellKnownName.forName(name, RetsObjectType.class);
	}
}
