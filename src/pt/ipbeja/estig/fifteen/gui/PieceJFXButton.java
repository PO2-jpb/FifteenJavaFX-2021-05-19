package pt.ipbeja.estig.fifteen.gui;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pt.ipbeja.estig.fifteen.model.Position;

/**
 * The fifteen button (knows its position in the grid)
 * 
 * @author João Paulo Barros e Rui Pais
 * @version 2014/05/19 - 2016/04/03
 * images generated in https://text2image.com/en/
 */
public class PieceJFXButton extends Button
{
	private final Position position;

	public PieceJFXButton(String text, Position position)
	{
		super(text);
		this.position = position;
		this.setTextAndImage(text);
	}

	/**
	 * @return the position
	 */
	public Position getPosition()
	{
		return this.position;
	}

	/**
	 * sets the text and image for the button
	 * 
	 * @param newText
	 *            to set
	 */
	public void setTextAndImage(String newText)
	{
		this.setText(newText);
		if (newText.isEmpty())
		{
			newText = "empty";
		}
		String filename = "/resources/images/" + newText + ".png";
		Image img = new Image(filename);
		ImageView imgView = new ImageView(img);
		imgView.autosize();
		this.setGraphic(imgView);
	}
}
