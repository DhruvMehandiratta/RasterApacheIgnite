package rasterproject;
import java.util.concurrent.atomic.AtomicInteger;

public class Raster {
	
	
    private static final AtomicInteger count = new AtomicInteger(0); 
	private final int rasterID;
	private float[][] rasterGrid;
	private float rasterScore;
	private String RasterCat;
	
	public Raster(float[][] rasterGrid){
		this.rasterID = count.incrementAndGet();
		this.rasterGrid = rasterGrid;
		this.rasterScore = calculateRasterScore(rasterGrid);
		this.RasterCat = RasterCategory.determineRasterCategory(calculateRasterScore(rasterGrid));
	}
	private float calculateRasterScore(float[][] rasterGrid){
		float score = 0;
		for(int i = 0 ; i < rasterGrid.length ; i++){
			for(int j = 0 ; j < rasterGrid[0].length ; j++){
				score += rasterGrid[i][j];
			}
		}
		return score;
	}
	public int getRasterID(){
		return this.rasterID;
	}
	public float getRasterScore(){
		return this.rasterScore;
	}
	public int getRasterGridLength(){
		return this.rasterGrid.length;
	}
	public String getRasterCategory(){
		return this.RasterCat;
	}
	public void printRaster(){
		for(int i = 0 ; i < this.rasterGrid.length ; i++){
			for(int j = 0 ; j < this.rasterGrid[0].length ; j++){
				System.out.print(this.rasterGrid[i][j] + " ");
			}
		}
	}
	
}
