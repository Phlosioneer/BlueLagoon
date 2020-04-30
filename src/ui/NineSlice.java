package ui;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * 
 * @author Intrebute
 *
 */
public class NineSlice {

	private int leftBorderThickness, rightBorderThickness, upperBorderThickness, lowerBorderThickness;
	PImage[][] slices;
	
	public NineSlice(PApplet a, PImage p, int leftThickness, int rightThickness, int upperThickness, int lowerThickness) {
		int[] vRules = {
				0, leftThickness, p.width - rightThickness, p.width,
		};
		
		int[] hRules = {
				0, upperThickness, p.height - lowerThickness, p.height,
		};
		this.slices = new PImage[3][3];
		for (int xi = 0; xi < 3; xi++) {
			for (int yi = 0; yi < 3; yi++) {
				PImage currentSlice = a.createImage(vRules[xi+1] - vRules[xi], hRules[yi+1] - hRules[yi], PConstants.ARGB);
				currentSlice.copy(p,
						vRules[xi],
						hRules[yi],
						vRules[xi+1] - vRules[xi],
						hRules[yi+1] - hRules[yi],
						0,
						0,
						currentSlice.width,
						currentSlice.height);
				this.slices[xi][yi] = currentSlice;
			}
		}
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
		int[] targetVRules = {
				x - leftBorderThickness, x, x + w, x + w + rightBorderThickness,
		};
		int[] targetHRules = {
				y - upperBorderThickness, y, y + h, y + h + lowerBorderThickness,
		};
		int hCount = closestCount(this.slices[1][1].width, w);
		int vCount = closestCount(this.slices[1][1].height, h);
		int prevImageMode = g.imageMode;
		g.imageMode(PGraphics.CORNERS);
		for(int xi = 0; xi < 3; xi++) {
			for(int yi = 0; yi < 3; yi++) {
				int xReps = xi == 1 ? hCount : 1; // If we are not in a middle section, only one repetition
				int yReps = yi == 1 ? vCount : 1; // otherwise, as many repetitions as the respective counts
				this.tileXY(this.slices[xi][yi], 
						g,
						targetVRules[xi],
						targetHRules[yi], 
						targetVRules[xi + 1], 
						targetHRules[yi + 1], 
						xReps, 
						yReps);
			}
		}
		g.imageMode(prevImageMode);
	}
	
	private void tileXY(PImage sourceImage, PGraphics g, int x1, int y1, int x2, int y2, int xSteps, int ySteps) {
		int dx = (x2 - x1) / xSteps;
		int dy = (y2 - y1) / ySteps;
		for(int xi = 0; xi < xSteps; xi++) {
			for(int yi = 0; yi < ySteps; yi++) {
				g.image(sourceImage,
						x1 + xi * dx,
						y1 + yi * dy, 
						x1 + (xi + 1) * dx, 
						y1 + (yi + 1) * dy);
			}	
		}
	}
	
	private int closestCount(double sourceWidth, double targetWidth) {
		if (targetWidth < sourceWidth) {
			return 1;
		} else {
			return (int)Math.round(Math.floor(targetWidth/sourceWidth - 0.5)) + 1; // These casts feel sketchy kinda
		}
	}
	
	public void drawWithin(PGraphics g, int x, int y, int w, int h) {
		throw new RuntimeException("TODO: implement drawWithin");
	}

}
