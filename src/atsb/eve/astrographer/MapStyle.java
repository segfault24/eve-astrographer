package atsb.eve.astrographer;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MapStyle {
	public static final Color BACKGROUND = new Color(0.1, 0.1, 0.1, 1);

	public static final Color SYSTEM = Color.DARKGREY;
	public static final Color SYSTEM_HIGHWAY = Color.YELLOW;
	public static final Color SYSTEM_SELECTED = Color.RED;
	public static final Color SYSTEM_SELECTED_SECONDARY = Color.RED;
	public static final Color SYSTEM_LABEL_HOVER = Color.WHITE;
	public static final Color SYSTEM_LABEL_SELECTED = Color.WHITE;

	public static final Color CONNECTION_NORMAL = Color.ROYALBLUE;
	public static final Color CONNECTION_CONSTELLATION = Color.LIGHTSKYBLUE;
	public static final Color CONNECTION_REGIONAL = Color.DARKRED;
	public static final Color CONNECTION_JUMPBRIDGE = Color.LIGHTGREEN;
	public static final Color CONNECTION_JB_HOSTILE = Color.RED;
	public static final Color CONNECTION_HIGHWAY = Color.ORANGE;
	public static final Color CONNECTION_SUPER_HIGHWAY = Color.YELLOW;

	public static final Font LABEL_FONT = new Font("courier new", 11.5);
}
