package rasterproject;
import java.util.ArrayList;

import org.geotools.coverage.grid.GridCoverage2D;

import ij.ImagePlus;
public class GenerateRasterObjects {
	/**
	 * @param args
	 */
	private static boolean DATA_LOADED = false;
	private static ArrayList<float[][]> rastersList;
	private static float[][] thisRaster;
	//public static void main(String[] args) {
	public static void generate(){
		float Pixel_Score;
		int dividingFactor = 20; //sets up raster matrix dimensions
		ImagePlus image = new ImagePlus("/home/seed/workspace/Raster_finalProject/albers27.tif");
		//ImagePlus image = new ImagePlus("/home/dhruv/workspace/IgniteSpatial-master/tjpeg.tif");
		int imageHeight = image.getHeight();
		int imageWidth = image.getWidth();

		System.out.println("height is "+image.getHeight() +" width is "+image.getWidth());
		if(imageHeight ==0 && imageWidth == 0){
			System.out.println("Raster input cannot be loaded or is empty");
			return;
		}
		DATA_LOADED = true;
		// generating raster objects from pixels
		//storing all pixels in matrix scores in matrix
		float[][] scoreMatrix = new float[image.getHeight()][image.getWidth()];
		rastersList = new ArrayList<float[][]>();
		for(int i = 0 ; i < imageHeight ; i++){
			for(int j = 0 ; j < imageWidth ; j++){
				int[] c = image.getPixel(i, j);
				//System.out.print("length: " +c.length + " \n");
				int r=c[0];
				int g=c[1];
				int b=c[2];
				// c[3] is index dhruv
				Pixel_Score=(float)((r+g+b)/3.0);
				scoreMatrix[i][j] = Pixel_Score;
			}
		}
		//length of a raster matrix = image_height/dividingFactor ; width = imageWidth/dividingFactor
		int numberOfRasters=0;
		int startRow, startCol;  //sets up start indices for raster matrix
		int rasterHeight = imageHeight/dividingFactor;
		int rasterWidth = imageWidth/dividingFactor;
		A: for(startRow=0 ; startRow < imageHeight ; startRow+=rasterHeight){
			for(startCol=0 ; startCol < imageWidth ; startCol+=rasterWidth){
				thisRaster = new float[rasterHeight][rasterWidth];
				for(int i = 0 ; i < rasterHeight ; i++){
					for(int j = 0 ; j < rasterWidth ; j++){
						//System.out.println("i: "+i +" j: "+j);
						thisRaster[i][j] = scoreMatrix[startRow+i][startCol+j];
						//System.out.println(thisRaster[i][j]);
					}
				}
				rastersList.add(thisRaster);
				thisRaster = new float[rasterHeight][rasterWidth];
			}
		}
	}
	public static ArrayList<Raster> provideRasterObjects(){
		if(DATA_LOADED){
			ArrayList<Raster> rastersObjectList = new ArrayList<Raster>();
			for(float[][] raster:rastersList){
				//
				//System.out.println("hello this is raster's first element"+raster[0][0]);
				Raster rasterObject = new Raster(raster);
				rastersObjectList.add(rasterObject);
				//System.out.println("Dhruv" + rasterObject.getRasterID() + " " + rasterObject.getRasterScore());
				//rasterObject.printRaster();
			}
			return rastersObjectList;
		}
		return null;
	}
}

