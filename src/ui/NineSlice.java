package ui;

import processing.core.PGraphics;
import processing.core.PImage;

/**
 * 
 * @author Intrebute
 *
 */
public class NineSlice {

	private int leftBorderThickness, rightBorderThickness, upperBorderThickness, lowerBorderThickness;
	PImage sourceImage;
	
	public NineSlice(PImage p, int leftThickness, int rightThickness, int upperThickness, int lowerThickness) {
		this.sourceImage = p;
		this.leftBorderThickness = leftThickness;
		this.rightBorderThickness = rightThickness;
		this.upperBorderThickness = upperThickness;
		this.lowerBorderThickness = lowerThickness;
	}
	
	/**
	 * Draw this 9-slice around the rectangle defined by x,y,w,h. This rectangle
	 * will be only the "center" section of the 9-slice. The borders are then fitted around this rectangle.
	 * If you wish to provide the total dimensions of the final image, see drawWithin.
	 */
	public void drawAround(PGraphics g, int x, int y, int w, int h) {
		int[] sourceVRules = {
				0, leftBorderThickness, sourceImage.width - rightBorderThickness, sourceImage.width,
		};
		int[] sourceHRules = {
				0, upperBorderThickness, sourceImage.height - lowerBorderThickness, sourceImage.height,
		};
		int[] targetVRules = {
				x - leftBorderThickness, x, x + w, x + w + rightBorderThickness,
		};
		int[] targetHRules = {
				y - upperBorderThickness, y, y + h, y + h + lowerBorderThickness,
		};
		int prevImageMode = g.imageMode;
		g.imageMode(PGraphics.CORNERS);
		for(int j = 0; j < 3; j++) {
			for(int i = 0; i < 3; i++) {
				g.image(sourceImage,
						sourceVRules[j],
						sourceHRules[i],
						sourceVRules[j+1],
						sourceHRules[i+1],
						targetVRules[j],
						targetHRules[i],
						targetVRules[j+1],
						targetHRules[i+1]);
			}
		}
		g.imageMode(prevImageMode);
	}
	
	public void drawWithin(PGraphics g, int x, int y, int w, int h) {
		throw new RuntimeException("TODO: implement drawWithin");
	}

}
