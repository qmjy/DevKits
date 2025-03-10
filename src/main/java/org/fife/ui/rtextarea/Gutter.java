/*
 * 02/17/2009
 *
 * Gutter.java - Manages line numbers, icons, etc. on the left-hand side of
 * an RTextArea.
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE file for details.
 */
package org.fife.ui.rtextarea;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.ActiveLineRangeEvent;
import org.fife.ui.rsyntaxtextarea.ActiveLineRangeListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;


/**
 * The gutter is the component on the left-hand side of the text area that
 * displays optional information such as line numbers, fold regions, and icons
 * (for bookmarks, debugging breakpoints, error markers, etc.).<p>
 *
 * Icons can be added on a per-line basis to visually mark syntax errors, lines
 * with breakpoints set on them, etc.  To add icons to the gutter, you must
 * first call {@link RTextScrollPane#setIconRowHeaderEnabled(boolean)} on the
 * parent scroll pane, to make the icon area visible.  Then, you can add icons
 * that track either lines in the document, or offsets, via
 * {@link #addLineTrackingIcon(int, Icon)} and
 * {@link #addOffsetTrackingIcon(int, Icon)}, respectively.  To remove an
 * icon you've added, use {@link #removeTrackingIcon(GutterIconInfo)}.<p>
 *
 * In addition to support for arbitrary per-line icons, this component also
 * has built-in support for displaying icons representing "bookmarks;" that is,
 * lines a user can cycle through via F2 and Shift+F2.  Bookmarked lines are
 * toggled via Ctrl+F2.  In order to enable bookmarking, you must first assign
 * an icon to represent a bookmarked line, then actually enable the feature:
 *
 * <pre>
 * Gutter gutter = scrollPane.getGutter();
 * gutter.setBookmarkIcon(new ImageIcon("bookmark.png"));
 * gutter.setBookmarkingEnabled(true);
 * </pre>
 *
 * @author Robert Futrell
 * @version 1.0
 * @see GutterIconInfo
 */
public class Gutter extends JPanel {

	/**
	 * The color used to highlight active line ranges if none is specified.
	 */
	public static final Color DEFAULT_ACTIVE_LINE_RANGE_COLOR =
												new Color(51, 153, 255);

	/**
	 * The text area.
	 */
	private RTextArea textArea;

	/**
	 * Renders line numbers.
	 */
	private LineNumberList lineNumberList;

	/**
	 * The color used to render line numbers.
	 */
	private Color lineNumberColor;

	/**
	 * The color used to render the currently active line's
	 * line number.  If this is {@code null},
	 * {@link #lineNumberColor} is used.
	 */
	private Color currentLineNumberColor;

	/**
	 * The starting index for line numbers in the gutter.
	 */
	private int lineNumberingStartIndex;

	/**
	 * Formats line numbers into a string to be displayed.
	 */
	private LineNumberFormatter lineNumberFormatter;

	/**
	 * The font used to render line numbers.
	 */
	private Font lineNumberFont;

	/**
	 * Renders bookmark icons, breakpoints, error icons, etc.
	 */
	private IconRowHeader iconArea;

	/**
	 * Whether the icon area inherits the gutter background (as opposed to
	 * painting with its own, default "panel" color).
	 */
	private boolean iconRowHeaderInheritsGutterBackground;

	/**
	 * Optional additional spacing between the line number component and the fold
	 * indicator component.
	 */
	private int spacingBetweenLineNumbersAndFoldIndicator;

	/**
	 * Shows lines that are code-foldable.
	 */
	private FoldIndicator foldIndicator;

	/**
	 * Whether this gutter, or any child components, are armed. Used
	 * internally for e.g. the fold indicator's appearance.
	 */
	private boolean armed;

	/**
	 * Listens for events in our text area.
	 */
	private transient TextAreaListener listener;


	/**
	 * Constructor.
	 *
	 * @param textArea The parent text area.
	 */
	public Gutter(RTextArea textArea) {

		listener = new TextAreaListener();
		lineNumberColor = LineNumberList.DEFAULT_LINE_NUMBER_COLOR;
		lineNumberFont = RTextArea.getDefaultFont();
		lineNumberingStartIndex = 1;
		lineNumberFormatter = LineNumberList.DEFAULT_LINE_NUMBER_FORMATTER;
		iconRowHeaderInheritsGutterBackground = false;

		setTextArea(textArea);
		setLayout(new BorderLayout());
		if (this.textArea!=null) {
			// Enable line numbers our first time through if they give us
			// a text area.
			setLineNumbersEnabled(true);
			if (this.textArea instanceof RSyntaxTextArea) {
				RSyntaxTextArea rsta = (RSyntaxTextArea)this.textArea;
				setFoldIndicatorEnabled(rsta.isCodeFoldingEnabled());
			}
		}

		setBorder(new GutterBorder(0, 0, 0, 1)); // Assume ltr

		Color bg = null;
		if (textArea!=null) {
			bg = textArea.getBackground(); // May return null if image bg
		}
		setBackground(bg!=null ? bg : Color.WHITE);

	}


	/**
	 * Adds an icon that tracks an offset in the document, and is displayed
	 * adjacent to the line numbers.  This is useful for marking things such
	 * as source code errors.
	 *
	 * @param line The line to track (zero-based).
	 * @param icon The icon to display.  This should be small (say 16x16).
	 * @return A tag for this icon.  This can later be used in a call to
	 *         {@link #removeTrackingIcon(GutterIconInfo)} to remove this
	 *         icon.
	 * @throws BadLocationException If <code>offs</code> is an invalid offset
	 *         into the text area.
	 * @see #addLineTrackingIcon(int, Icon, String)
	 * @see #addOffsetTrackingIcon(int, Icon)
	 * @see #removeTrackingIcon(GutterIconInfo)
	 */
	public GutterIconInfo addLineTrackingIcon(int line, Icon icon)
											throws BadLocationException {
		return addLineTrackingIcon(line, icon, null);
	}


	/**
	 * Adds an icon that tracks an offset in the document, and is displayed
	 * adjacent to the line numbers.  This is useful for marking things such
	 * as source code errors.
	 *
	 * @param line The line to track (zero-based).
	 * @param icon The icon to display.  This should be small (say 16x16).
	 * @param tip An optional tool tip for the icon.
	 * @return A tag for this icon.  This can later be used in a call to
	 *         {@link #removeTrackingIcon(GutterIconInfo)} to remove this
	 *         icon.
	 * @throws BadLocationException If <code>offs</code> is an invalid offset
	 *         into the text area.
	 * @see #addLineTrackingIcon(int, Icon)
	 * @see #addOffsetTrackingIcon(int, Icon)
	 * @see #removeTrackingIcon(GutterIconInfo)
	 */
	public GutterIconInfo addLineTrackingIcon(int line, Icon icon, String tip)
											throws BadLocationException {
		int offs = textArea.getLineStartOffset(line);
		return addOffsetTrackingIcon(offs, icon, tip);
	}


	/**
	 * Adds an icon that tracks an offset in the document, and is displayed
	 * adjacent to the line numbers.  This is useful for marking things such
	 * as source code errors.
	 *
	 * @param offs The offset to track.
	 * @param icon The icon to display.  This should be small (say 16x16).
	 * @return A tag for this icon.
	 * @throws BadLocationException If <code>offs</code> is an invalid offset
	 *         into the text area.
	 * @see #addOffsetTrackingIcon(int, Icon, String)
	 * @see #addLineTrackingIcon(int, Icon)
	 * @see #removeTrackingIcon(GutterIconInfo)
	 */
	public GutterIconInfo addOffsetTrackingIcon(int offs, Icon icon)
												throws BadLocationException {
		return addOffsetTrackingIcon(offs, icon, null);
	}


	/**
	 * Adds an icon that tracks an offset in the document, and is displayed
	 * adjacent to the line numbers.  This is useful for marking things such
	 * as source code errors.
	 *
	 * @param offs The offset to track.
	 * @param icon The icon to display.  This should be small (say 16x16).
	 * @param tip An optional tool tip for the icon.
	 * @return A tag for this icon.
	 * @throws BadLocationException If <code>offs</code> is an invalid offset
	 *         into the text area.
	 * @see #addOffsetTrackingIcon(int, Icon)
	 * @see #addLineTrackingIcon(int, Icon)
	 * @see #removeTrackingIcon(GutterIconInfo)
	 */
	public GutterIconInfo addOffsetTrackingIcon(int offs, Icon icon, String tip)
												throws BadLocationException {
		return iconArea.addOffsetTrackingIcon(offs, icon, tip);
	}


	/**
	 * Clears the active line range.
	 *
	 * @see #setActiveLineRange(int, int)
	 */
	private void clearActiveLineRange() {
		iconArea.clearActiveLineRange();
	}


	/**
	 * Returns the color used to paint the active line range, if any.
	 *
	 * @return The color.
	 * @see #setActiveLineRangeColor(Color)
	 */
	public Color getActiveLineRangeColor() {
		return iconArea.getActiveLineRangeColor();
	}


	/**
	 * Returns the background color used by the (default) fold icons when they
	 * are armed.
	 *
	 * @return The background color.
	 * @see #setArmedFoldBackground(Color)
	 * @see #getFoldBackground()
	 */
	public Color getArmedFoldBackground() {
		return foldIndicator.getFoldIconArmedBackground();
	}


	/**
	 * Returns the icon to use for bookmarks.
	 *
	 * @return The icon to use for bookmarks.  If this is <code>null</code>,
	 *         bookmarking is effectively disabled.
	 * @see #setBookmarkIcon(Icon)
	 * @see #isBookmarkingEnabled()
	 */
	public Icon getBookmarkIcon() {
		return iconArea.getBookmarkIcon();
	}


	/**
	 * Returns the bookmarks known to this gutter.
	 *
	 * @return The bookmarks.  If there are no bookmarks, an empty array is
	 *         returned.
	 * @see #toggleBookmark(int)
	 */
	public GutterIconInfo[] getBookmarks() {
		return iconArea.getBookmarks();
	}


	/**
	 * Returns the color of the "border" line.
	 *
	 * @return The color.
	 * @see #setBorderColor(Color)
	 */
	public Color getBorderColor() {
		return ((GutterBorder)getBorder()).getColor();
	}


	/**
	 * The color used to render the currently active line's line number.
	 * If this is {@code null}, {@link #getLineNumberColor()} is used.
	 *
	 * @return The color.
	 * @see #setCurrentLineNumberColor(Color)
	 * @see #getLineNumberColor()
	 */
	public Color getCurrentLineNumberColor() {
		return currentLineNumberColor;
	}


	/**
	 * Returns the strategy to use for rendering expanded folds.
	 *
	 * @return The strategy to use for rendering expanded folds.
	 * @see #setExpandedFoldRenderStrategy(ExpandedFoldRenderStrategy)
	 */
	public ExpandedFoldRenderStrategy getExpandedFoldRenderStrategy() {
		return foldIndicator.getExpandedFoldRenderStrategy();
	}


	/**
	 * Returns the background color used by the (default) fold icons.
	 *
	 * @return The background color.
	 * @see #setFoldBackground(Color)
	 */
	public Color getFoldBackground() {
		return foldIndicator.getFoldIconBackground();
	}


	/**
	 * Returns the foreground color of the fold indicator for armed
	 * folds.
	 *
	 * @return The foreground color of the fold indicator for armed
	 *         folds.
	 * @see #setFoldIndicatorArmedForeground(Color)
	 */
	public Color getFoldIndicatorArmedForeground() {
		return foldIndicator.getArmedForeground();
	}


	/**
	 * Returns the foreground color of the fold indicator.
	 *
	 * @return The foreground color of the fold indicator.
	 * @see #setFoldIndicatorForeground(Color)
	 */
	public Color getFoldIndicatorForeground() {
		return foldIndicator.getForeground();
	}


	/**
	 * Returns whether the icon area inherits the gutter background (as opposed
	 * to painting with its own, default "panel" color, which is the default).
	 *
	 * @return Whether the gutter background is used in the icon row header.
	 * @see #setIconRowHeaderInheritsGutterBackground(boolean)
	 */
	public boolean getIconRowHeaderInheritsGutterBackground() {
		return iconRowHeaderInheritsGutterBackground;
	}


	/**
	 * Returns the color to use to paint line numbers.
	 *
	 * @return The color used when painting line numbers.
	 * @see #setLineNumberColor(Color)
	 */
	public Color getLineNumberColor() {
		return lineNumberColor;
	}


	/**
	 * Returns the font used for line numbers.
	 *
	 * @return The font used for line numbers.
	 * @see #setLineNumberFont(Font)
	 */
	public Font getLineNumberFont() {
		return lineNumberFont;
	}


	/**
	 * Returns the starting line's line number.  The default value is
	 * <code>1</code>.
	 *
	 * @return The index
	 * @see #setLineNumberingStartIndex(int)
	 */
	public int getLineNumberingStartIndex() {
		return lineNumberingStartIndex;
	}

	/**
	 * Returns the line number formatter. The default value is
	 * {@link LineNumberList#DEFAULT_LINE_NUMBER_FORMATTER}
	 *
	 * @return The formatter.
	 * @see #setLineNumberFormatter(LineNumberFormatter)
	 */
	public LineNumberFormatter getLineNumberFormatter() {
		return lineNumberFormatter;
	}


	/**
	 * Returns <code>true</code> if the line numbers are enabled and visible.
	 *
	 * @return Whether line numbers are visible.
	 */
	public boolean getLineNumbersEnabled() {
		for (int i=0; i<getComponentCount(); i++) {
			if (getComponent(i)==lineNumberList) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Returns whether tool tips are displayed showing the contents of
	 * collapsed fold regions when the mouse hovers over a +/- icon.
	 *
	 * @return Whether these tool tips are displayed.
	 * @see #setShowCollapsedRegionToolTips(boolean)
	 */
	public boolean getShowCollapsedRegionToolTips() {
		return foldIndicator.getShowCollapsedRegionToolTips();
	}


	/**
	 * Returns the additional spacing between the line number list and fold indicator.  By
	 * default this is a small amount; if you want something larger, you can increase it.
	 * Note this value takes effect whether both line numbers and the fold indicator
	 * are enabled, so use it only when both are enabled.
	 *
	 * @return The additional spacing.
	 * @see #setSpacingBetweenLineNumbersAndFoldIndicator(int)
	 */
	public int getSpacingBetweenLineNumbersAndFoldIndicator() {
		return spacingBetweenLineNumbersAndFoldIndicator;
	}


	/**
	 * Returns the tracking icons at the specified view position.
	 *
	 * @param p The view position.
	 * @return The tracking icons at that position.  If there are no tracking
	 *         icons there, this will be an empty array.
	 * @throws BadLocationException If <code>p</code> is invalid.
	 */
	public GutterIconInfo[] getTrackingIcons(Point p)
			throws BadLocationException {
		int offs = textArea.viewToModel(new Point(0, p.y));
		int line = textArea.getLineOfOffset(offs);
		return iconArea.getTrackingIcons(line);
	}


	/**
	 * Returns whether this gutter is "armed", that is, any child components
	 * are armed. This is used by the internal API and should not be called.
	 *
	 * @return Whether the gutter is armed.
	 * @see #setArmed(boolean)
	 */
	public boolean isArmed() {
		return armed;
	}


	/**
	 * Returns whether the fold indicator is enabled.
	 *
	 * @return Whether the fold indicator is enabled.
	 * @see #setFoldIndicatorEnabled(boolean)
	 */
	public boolean isFoldIndicatorEnabled() {
		for (int i=0; i<getComponentCount(); i++) {
			if (getComponent(i)==foldIndicator) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Returns whether bookmarking is enabled.
	 *
	 * @return Whether bookmarking is enabled.
	 * @see #setBookmarkingEnabled(boolean)
	 */
	public boolean isBookmarkingEnabled() {
		return iconArea.isBookmarkingEnabled();
	}


	/**
	 * Returns whether the icon row header is enabled.
	 *
	 * @return Whether the icon row header is enabled.
	 */
	public boolean isIconRowHeaderEnabled() {
		for (int i=0; i<getComponentCount(); i++) {
			if (getComponent(i)==iconArea) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Removes all tracking icons.
	 *
	 * @see #removeTrackingIcon(GutterIconInfo)
	 * @see #addOffsetTrackingIcon(int, Icon)
	 */
	public void removeAllTrackingIcons() {
		iconArea.removeAllTrackingIcons();
	}


	/**
	 * Removes the specified tracking icon.
	 *
	 * @param tag A tag for an icon in the gutter, as returned from either
	 *        {@link #addLineTrackingIcon(int, Icon)} or
	 *        {@link #addOffsetTrackingIcon(int, Icon)}.
	 * @see #removeAllTrackingIcons()
	 * @see #addLineTrackingIcon(int, Icon)
	 * @see #addOffsetTrackingIcon(int, Icon)
	 */
	public void removeTrackingIcon(GutterIconInfo tag) {
		iconArea.removeTrackingIcon(tag);
	}


	/**
	 * Sets the color to use to render active line ranges.
	 *
	 * @param color The color to use.  If this is null, then the default
	 *        color is used.
	 * @see #getActiveLineRangeColor()
	 * @see #DEFAULT_ACTIVE_LINE_RANGE_COLOR
	 */
	public void setActiveLineRangeColor(Color color) {
		iconArea.setActiveLineRangeColor(color);
	}


	/**
	 * Highlights a range of lines in the icon area.  This, of course, will
	 * only be visible if the icon area is visible.
	 *
	 * @param startLine The start of the line range.
	 * @param endLine The end of the line range.
	 * @see #clearActiveLineRange()
	 */
	private void setActiveLineRange(int startLine, int endLine) {
		iconArea.setActiveLineRange(startLine, endLine);
	}


	/**
	 * Toggles whether this gutter is "armed", that is, any child components
	 * are armed. This is used by the internal API and should not be called.
	 *
	 * @param armed Whether the gutter is armed.
	 * @see #isArmed()
	 */
	void setArmed(boolean armed) {
		if (armed != this.armed) {
			this.armed = armed;
			if (foldIndicator != null) {
				foldIndicator.gutterArmedUpdate(armed);
			}
		}
	}


	/**
	 * Sets the background color used by the (default) fold icons when they
	 * are armed.
	 *
	 * @param bg The new background color.  If this is {@code null}, then
	 *        armed fold icons will not render with a special color.
	 * @see #getArmedFoldBackground()
	 * @see #setFoldBackground(Color)
	 */
	public void setArmedFoldBackground(Color bg) {
		foldIndicator.setFoldIconArmedBackground(bg);
	}


	/**
	 * Sets the icon to use for bookmarks.
	 *
	 * @param icon The new bookmark icon.  If this is <code>null</code>,
	 *        bookmarking is effectively disabled.
	 * @see #getBookmarkIcon()
	 * @see #isBookmarkingEnabled()
	 */
	public void setBookmarkIcon(Icon icon) {
		iconArea.setBookmarkIcon(icon);
	}


	/**
	 * Sets whether bookmarking is enabled.  Note that a bookmarking icon
	 * must be set via {@link #setBookmarkIcon(Icon)} before bookmarks are
	 * truly enabled.
	 *
	 * @param enabled Whether bookmarking is enabled.
	 * @see #isBookmarkingEnabled()
	 * @see #setBookmarkIcon(Icon)
	 */
	public void setBookmarkingEnabled(boolean enabled) {
		iconArea.setBookmarkingEnabled(enabled);
		if (enabled && !isIconRowHeaderEnabled()) {
			setIconRowHeaderEnabled(true);
		}
	}


	/**
	 * Sets the color for the "border" line.
	 *
	 * @param color The new color.
	 * @see #getBorderColor()
	 */
	public void setBorderColor(Color color) {
		((GutterBorder)getBorder()).setColor(color);
		repaint();
	}


	@Override
	public void setComponentOrientation(ComponentOrientation o) {

		// Some LaFs might do fun stuff, resulting in this method being called
		// before a border is installed.

		if (getBorder() instanceof GutterBorder) {
			// Reuse the border to preserve its color.
			if (o.isLeftToRight()) {
				((GutterBorder)getBorder()).setEdges(0, 0, 0, 1);
			}
			else {
				((GutterBorder)getBorder()).setEdges(0, 1, 0, 0);
			}
		}
		super.setComponentOrientation(o);
	}


	/**
	 * Sets the color used to render the currently active line's line number.
	 * If this is {@code null}, {@link #getLineNumberColor()} is used.
	 *
	 * @param color The color to use.
	 * @see #getCurrentLineNumberColor()
	 * @see #setLineNumberColor(Color)
	 */
	public void setCurrentLineNumberColor(Color color) {
		if (!Objects.equals(color, currentLineNumberColor)) {
			currentLineNumberColor = color;
			if (lineNumberList!=null) {
				lineNumberList.setCurrentLineNumberColor(color);
			}
		}
	}


	/**
	 * Sets the strategy to use for rendering expanded folds.
	 *
	 * @param strategy The strategy to use. This cannot be {@code null}.
	 * @see #getExpandedFoldRenderStrategy()
	 */
	public void setExpandedFoldRenderStrategy(ExpandedFoldRenderStrategy strategy) {
		foldIndicator.setExpandedFoldRenderStrategy(strategy);
	}


	/**
	 * Sets the icons to use to represent collapsed and expanded folds.
	 * This method can be used for further customization after setting this
	 * component's general appearance via
	 * {@link #setFoldIndicatorStyle(FoldIndicatorStyle)}.
	 *
	 * @param collapsedIcon The collapsed fold icon.  This cannot be
	 *        <code>null</code>.
	 * @param expandedIcon The expanded fold icon.  This cannot be
	 *        <code>null</code>.
	 * @see #setFoldIndicatorStyle(FoldIndicatorStyle)
	 */
	public void setFoldIcons(FoldIndicatorIcon collapsedIcon, FoldIndicatorIcon expandedIcon) {
		if (foldIndicator!=null) {
			foldIndicator.setFoldIcons(collapsedIcon, expandedIcon);
		}
	}


	/**
	 * Toggles whether the fold indicator is enabled.
	 *
	 * @param enabled Whether the fold indicator should be enabled.
	 * @see #isFoldIndicatorEnabled()
	 */
	public void setFoldIndicatorEnabled(boolean enabled) {
		if (foldIndicator!=null) {
			if (enabled) {
				add(foldIndicator, BorderLayout.LINE_END);
			}
			else {
				remove(foldIndicator);
			}
			revalidate();
		}
	}


	/**
	 * Toggles the presentation of the fold region of this component.
	 * This method sets the icons used for fold regions to default values,
	 * amongst other configuration. To further customize those icons,
	 * see {@link #setFoldIcons(FoldIndicatorIcon, FoldIndicatorIcon)}.
	 *
	 * @param style The new presentation style.
	 * @see #setFoldIcons(FoldIndicatorIcon, FoldIndicatorIcon)
	 */
	public void setFoldIndicatorStyle(FoldIndicatorStyle style) {
		if (foldIndicator != null) {
			foldIndicator.setStyle(style);
			revalidate();
			repaint();
		}
	}


	/**
	 * Sets the background color used by the (default) fold icons.
	 *
	 * @param bg The new background color.
	 * @see #getFoldBackground()
	 * @see #setArmedFoldBackground(Color)
	 */
	public void setFoldBackground(Color bg) {
		if (bg==null) {
			bg = FoldIndicator.DEFAULT_FOLD_BACKGROUND;
		}
		foldIndicator.setFoldIconBackground(bg);
	}


	/**
	 * Sets the foreground color used by the fold indicator for
	 * armed folds.
	 *
	 * @param fg The new armed fold indicator foreground.
	 * @see #getFoldIndicatorArmedForeground()
	 */
	public void setFoldIndicatorArmedForeground(Color fg) {
		if (fg==null) {
			fg = FoldIndicator.DEFAULT_FOREGROUND;
		}
		foldIndicator.setArmedForeground(fg);
	}


	/**
	 * Sets the foreground color used by the fold indicator.
	 *
	 * @param fg The new fold indicator foreground.
	 * @see #getFoldIndicatorForeground()
	 */
	public void setFoldIndicatorForeground(Color fg) {
		if (fg==null) {
			fg = FoldIndicator.DEFAULT_FOREGROUND;
		}
		foldIndicator.setForeground(fg);
	}


	/**
	 * Toggles whether the icon row header (used for breakpoints, bookmarks,
	 * etc.) is enabled.<p>
	 *
	 * Most clients do not need to call this method directly. This is usually
	 * handled by `RTextScrollPane` directly. Calling this directly may
	 * require the caller to ensure this `gutter` is visible and sized
	 * properly in its parent container.
	 *
	 * @param enabled Whether the icon row header is enabled.
	 * @see #isIconRowHeaderEnabled()
	 */
	public void setIconRowHeaderEnabled(boolean enabled) {
		if (iconArea!=null) {
			if (enabled) {
				add(iconArea, BorderLayout.LINE_START);
			}
			else {
				remove(iconArea);
			}
			revalidate();
		}
	}


	/**
	 * Sets whether the icon area inherits the gutter background (as opposed
	 * to painting with its own, default "panel" color, which is the default).
	 *
	 * @param inherits Whether the gutter background should be used in the icon
	 *        row header.  If this is <code>false</code>, a default,
	 *        Look-and-feel-dependent color is used.
	 * @see #getIconRowHeaderInheritsGutterBackground()
	 */
	public void setIconRowHeaderInheritsGutterBackground(boolean inherits) {
		if (inherits!=iconRowHeaderInheritsGutterBackground) {
			iconRowHeaderInheritsGutterBackground = inherits;
			if (iconArea!=null) {
				iconArea.setInheritsGutterBackground(inherits);
			}
		}
	}


	/**
	 * Sets the color to use to paint line numbers.
	 *
	 * @param color The color to use when painting line numbers.
	 * @see #getLineNumberColor()
	 * @see #setCurrentLineNumberColor(Color)
	 */
	public void setLineNumberColor(Color color) {
		if (color!=null && !color.equals(lineNumberColor)) {
			lineNumberColor = color;
			if (lineNumberList!=null) {
				lineNumberList.setForeground(color);
			}
		}
	}


	/**
	 * Sets the font used for line numbers.
	 *
	 * @param font The font to use.  This cannot be <code>null</code>.
	 * @see #getLineNumberFont()
	 */
	public void setLineNumberFont(Font font) {
		if (font==null) {
			throw new IllegalArgumentException("font cannot be null");
		}
		if (!font.equals(lineNumberFont)) {
			lineNumberFont = font;
			if (lineNumberList!=null) {
				lineNumberList.setFont(font);
			}
		}
	}


	/**
	 * Sets the starting line's line number.  The default value is
	 * <code>1</code>.  Applications can call this method to change this value
	 * if they are displaying a subset of lines in a file, for example.
	 *
	 * @param index The new index.
	 * @see #getLineNumberingStartIndex()
	 */
	public void setLineNumberingStartIndex(int index) {
		if (index!=lineNumberingStartIndex) {
			lineNumberingStartIndex = index;
			lineNumberList.setLineNumberingStartIndex(index);
		}
	}


	/**
	 * Sets a custom line number formatter. Can be called when other number
	 * formats are needed like hindu-arabic numerals.
	 *
	 * @param formatter The new line number formatter.
	 * @see #getLineNumberFormatter()
	 */
	public void setLineNumberFormatter(LineNumberFormatter formatter) {
		if (formatter != lineNumberFormatter) {
			lineNumberFormatter = formatter;
			lineNumberList.setLineNumberFormatter(formatter);
		}
	}


	/**
	 * Toggles whether line numbers are visible.<p>
	 *
	 * Most clients do not need to call this method directly. This is usually
	 * handled by `RTextScrollPane` directly. Calling this directly may
	 * require the caller to ensure this `gutter` is visible and sized
	 * properly in its parent container.
	 *
	 * @param enabled Whether line numbers should be visible.
	 * @see #getLineNumbersEnabled()
	 */
	public void setLineNumbersEnabled(boolean enabled) {
		if (lineNumberList!=null) {
			if (enabled) {
				add(lineNumberList);
			}
			else {
				remove(lineNumberList);
			}
			revalidate();
		}
	}


	/**
	 * Toggles whether tool tips should be displayed showing the contents of
	 * collapsed fold regions when the mouse hovers over a +/- icon.
	 *
	 * @param show Whether to show these tool tips.
	 * @see #getShowCollapsedRegionToolTips()
	 */
	public void setShowCollapsedRegionToolTips(boolean show) {
		if (foldIndicator!=null) {
			foldIndicator.setShowCollapsedRegionToolTips(show);
		}
	}


	/**
	 * Sets additional spacing between the line number list and fold indicator.  By
	 * default this is a small amount; if you want something larger, you can increase it.
	 * Note this value takes effect whether both line numbers and the fold indicator
	 * are enabled, so use it only when both are enabled.
	 *
	 * @param spacing The additional spacing.  This should be {@code >= 0}.
	 * @see #getSpacingBetweenLineNumbersAndFoldIndicator()
	 */
	public void setSpacingBetweenLineNumbersAndFoldIndicator(int spacing) {
		if (spacing != spacingBetweenLineNumbersAndFoldIndicator) {
			spacingBetweenLineNumbersAndFoldIndicator = spacing;
			foldIndicator.setAdditionalLeftMargin(spacing);
			revalidate();
			repaint();
		}
	}


	/**
	 * Sets the text area being displayed.  This will clear any tracking
	 * icons currently displayed.<p>
	 *
	 * Most clients do not need to call this method directly. This is
	 * usually handled by `RTextScrollPane` directly.
	 *
	 * @param textArea The text area.
	 */
	public void setTextArea(RTextArea textArea) {

		if (this.textArea!=null) {
			listener.uninstall();
		}

		if (textArea!=null) {

			RTextAreaEditorKit kit = (RTextAreaEditorKit)textArea.getUI().
					getEditorKit(textArea);

			if (lineNumberList==null) {
				lineNumberList = kit.createLineNumberList(textArea);
				lineNumberList.setFont(getLineNumberFont());
				lineNumberList.setForeground(getLineNumberColor());
				lineNumberList.setCurrentLineNumberColor(
					getCurrentLineNumberColor());
				lineNumberList.setLineNumberingStartIndex(
						getLineNumberingStartIndex());
				lineNumberList.setLineNumberFormatter(
					getLineNumberFormatter());
			}
			else {
				lineNumberList.setTextArea(textArea);
			}
			if (iconArea==null) {
				iconArea = kit.createIconRowHeader(textArea);
				iconArea.setInheritsGutterBackground(
						getIconRowHeaderInheritsGutterBackground());
			}
			else {
				iconArea.setTextArea(textArea);
			}
			if (foldIndicator==null) {
				foldIndicator = new FoldIndicator(textArea);
			}
			else {
				foldIndicator.setTextArea(textArea);
			}

			listener.install(textArea);

		}

		this.textArea = textArea;

	}


	/**
	 * Programmatically toggles whether there is a bookmark for the specified
	 * line.  If bookmarking is not enabled, this method does nothing.
	 *
	 * @param line The line.
	 * @return Whether a bookmark is now at the specified line.
	 * @throws BadLocationException If <code>line</code> is an invalid line
	 *         number in the text area.
	 * @see #getBookmarks()
	 */
	public boolean toggleBookmark(int line) throws BadLocationException {
		return iconArea.toggleBookmark(line);
	}


	@Override
	public void setBorder(Border border) {
		if (border instanceof GutterBorder) {
			super.setBorder(border);
		}
	}


	/**
	 * The border used by the gutter.
	 */
	public static class GutterBorder extends EmptyBorder {

		private Color color;
		private Rectangle visibleRect;

		public GutterBorder(int top, int left, int bottom, int right) {
			super(top, left, bottom, right);
			color = new Color(221, 221, 221);
			visibleRect = new Rectangle();
		}

		public Color getColor() {
			return color;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y,
								int width, int height) {

			visibleRect = g.getClipBounds(visibleRect);
			if (visibleRect==null) { // ???
				visibleRect = ((JComponent)c).getVisibleRect();
			}

			g.setColor(color);
			if (left==1) {
				g.drawLine(0,visibleRect.y,
						0,visibleRect.y+visibleRect.height);
			}
			else {
				g.drawLine(width-1,visibleRect.y,
						width-1,visibleRect.y+visibleRect.height);
			}

		}

		public void setColor(Color color) {
			this.color = color;
		}

		/**
		 * Sets the edges of the gutter.
		 *
		 * @param top The top value.
		 * @param left The left value.
		 * @param bottom The bottom value.
		 * @param right The right value.
		 */
		public void setEdges(int top, int left, int bottom, int right) {
			this.top = top;
			this.left = left;
			this.bottom = bottom;
			this.right = right;
		}

	}


	/**
	 * Listens for the text area resizing.
	 */
	private final class TextAreaListener extends ComponentAdapter
						implements DocumentListener, PropertyChangeListener,
						ActiveLineRangeListener {

		// This is necessary to keep child components the same height as the text
		// area.  The worse case is when the user toggles word-wrap and it changes
		// the height of the text area. In that case, if we listen for the
		// "lineWrap" property change, we get notified BEFORE the text area
		// decides on its new size, thus we cannot resize properly.  We listen
		// instead for ComponentEvents so we change size after the text area has
		// resized.

		private boolean installed;

		/**
		 * Modifies the "active line range" that is painted in this component.
		 *
		 * @param e Information about the new "active line range."
		 */
		@Override
		public void activeLineRangeChanged(ActiveLineRangeEvent e) {
			if (e.getMin()==-1) {
				clearActiveLineRange();
			}
			else {
				setActiveLineRange(e.getMin(), e.getMax());
			}
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void componentResized(java.awt.event.ComponentEvent e) {
			revalidate();
		}

		protected void handleDocumentEvent(DocumentEvent e) {
			for (int i=0; i<getComponentCount(); i++) {
				AbstractGutterComponent agc =
							(AbstractGutterComponent)getComponent(i);
				agc.handleDocumentEvent(e);
			}
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
		}

		protected void install(RTextArea textArea) {
			if (installed) {
				uninstall();
			}
			textArea.addComponentListener(this);
			textArea.getDocument().addDocumentListener(this);
			textArea.addPropertyChangeListener(this);
			if (textArea instanceof RSyntaxTextArea) {
				RSyntaxTextArea rsta = (RSyntaxTextArea)textArea;
				rsta.addActiveLineRangeListener(this);
				rsta.getFoldManager().addPropertyChangeListener(this);
			}
			installed = true;
		}

		@Override
		public void propertyChange(PropertyChangeEvent e) {

			String name = e.getPropertyName();

			// If they change the text area's font, we need to update cell
			// heights to match the font's height.
			if ("font".equals(name) ||
					RSyntaxTextArea.SYNTAX_SCHEME_PROPERTY.equals(name)) {
				for (int i=0; i<getComponentCount(); i++) {
					AbstractGutterComponent agc =
								(AbstractGutterComponent)getComponent(i);
					agc.lineHeightsChanged();
				}
			}

			// If they toggle whether code folding is enabled...
			else if (RSyntaxTextArea.CODE_FOLDING_PROPERTY.equals(name)) {
				boolean foldingEnabled = (Boolean)e.getNewValue();
				if (lineNumberList!=null) { // Its size depends on folding
					//lineNumberList.revalidate();
					lineNumberList.updateCellWidths();
				}
				setFoldIndicatorEnabled(foldingEnabled);
			}

			// If code folds are updated...
			else if (FoldManager.PROPERTY_FOLDS_UPDATED.equals(name)) {
				repaint();
			}

			else if ("document".equals(name)) {
				// The document switched out from under us
				RDocument old = (RDocument)e.getOldValue();
				if (old != null) {
					old.removeDocumentListener(this);
				}
				RDocument newDoc = (RDocument)e.getNewValue();
				if (newDoc != null) {
					newDoc.addDocumentListener(this);
				}
				for (int i=0; i<getComponentCount(); i++) {
					AbstractGutterComponent agc =
						(AbstractGutterComponent)getComponent(i);
					agc.handleDocumentUpdated(old, newDoc);
				}
			}

		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
		}

		protected void uninstall() {
			if (installed) {
				textArea.removeComponentListener(this);
				textArea.getDocument().removeDocumentListener(this);
				textArea.removePropertyChangeListener(this);
				if (textArea instanceof RSyntaxTextArea) {
					RSyntaxTextArea rsta = (RSyntaxTextArea)textArea;
					rsta.removeActiveLineRangeListener(this);
					rsta.getFoldManager().removePropertyChangeListener(this);
				}
				installed = false;
			}
		}

	}


}
