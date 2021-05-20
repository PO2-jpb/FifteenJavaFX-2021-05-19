package pt.ipbeja.estig.fifteen.gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pt.ipbeja.estig.fifteen.model.Position;

/**
 * The fifteen image in position (knows its position in the grid)
 * 
 * @author João Paulo Barros
 * @version 2021/05/20
 * images generated in https://text2image.com/en/
 */
public class PositionImage extends ImageView
{
	public final static int SIZE = 100;
	private Position lineCol;

	public PositionImage(String imageName, Position lineCol)
	{
		this.setImageByName(imageName);
		this.setLineColAndXY(lineCol);
	}

	public void setLineColAndXY(Position position) {
		this.lineCol = position;
		this.setX(position.getCol() * SIZE);
		this.setY(position.getLine() * SIZE);
	}

	public void updateLineColAndXY(int dCol, int dLine) {
		int col = this.lineCol.getCol() + dCol;
		int line = this.lineCol.getLine() + dLine;
		this.setLineColAndXY(new Position(line, col));
	}

	public void updateLineCol(int dCol, int dLine) {
		int col = this.lineCol.getCol() + dCol;
		int line = this.lineCol.getLine() + dLine;
		this.lineCol = new Position(line, col);
	}

	/**
	 * @return the position
	 */
	public Position getLineCol() {
		return this.lineCol;
	}

	/**
	 * sets the text and image for the button
	 * @param imageName image name to set
	 */
	public void setImageByName(String imageName)
	{
		assert(false);
		// https://stackoverflow.com/questions/27894945/how-do-i-resize-an-imageview-image-in-javafx
		String filename = "/resources/images/" + imageName + ".png";
		if (imageName.equals("empty")) {
			this.setImage(null);
		}
		else {
			Image img = new Image(filename, SIZE, SIZE, false, false);
			this.setImage(img);
		}
	}
}
