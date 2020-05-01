package rasterproject;
public class RasterCategory {
	public static String determineRasterCategory(float rasterScore){
		if(rasterScore > 1500){
			return Constants.DENSE_LAND;
		}else if(rasterScore < 900){
			return Constants.DENSE_WATER;
		}else if(rasterScore > 1200 && rasterScore < 1500){
			return Constants.RARE_LAND;
		}else{
			return Constants.RARE_WATER;
		}
	}
}
