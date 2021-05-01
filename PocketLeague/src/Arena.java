import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Arena 
{
	public static final File file = new File("img/mannfield.jpg"); 
	ImageView iv;
	
	Arena()
	{
		Image image = new Image(file.toURI().toString());
		this.iv = new ImageView(image);
		iv.setVisible(true);
	}

	public ImageView getIv() 
	{
		return iv;
	}

	public void setIv(ImageView iv) 
	{
		this.iv = iv;
	}
}
