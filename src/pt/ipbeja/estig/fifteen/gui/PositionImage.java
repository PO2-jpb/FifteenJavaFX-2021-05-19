package pt.ipbeja.estig.fifteen.gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pt.ipbeja.estig.fifteen.model.Position;

/**
 * The fifteen image in position (knows its position in the grid)
 * 
 * @author João Paulo Barros e Rui Pais
 * @version 2021/05/19
 * images generated in https://text2image.com/en/
 */
public class PositionImage extends ImageView
{
	private final Position position;
	private String imageName;

	public PositionImage(String imageName, Position position)
	{
		this.position = position;
		this.setImage(imageName);
	}

	/**
	 * @return the position
	 */
	public Position getPosition()
	{
		return this.position;
	}

	public String getImageName() { return this.imageName; }

	/**
	 * sets the text and image for the button
	 * @param imageName image name to set
	 */
	public void setImage(String imageName)
	{
		this.imageName = imageName;
		String filename = "/resources/images/" + this.imageName + ".png";
		Image img = new Image(filename);
		this.setImage(img);
	}
}
