package rasterproject;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.lang.IgniteBiPredicate;


public class RasterQueries {

	public static void runRasterQueries(){
		containsDenseLand();
		containsDenseWater();
		containsRareLand();
		containsRareWater();
	}

	private static IgniteBiPredicate<String, Raster> filterForDenseLand() {
		IgniteBiPredicate<String, Raster> filter = new IgniteBiPredicate<String, Raster>() {
			@Override public boolean apply(String key, Raster g) {
				if(RasterCategory.determineRasterCategory(g.getRasterScore()).equals(Constants.DENSE_LAND))
					return true;
				return false;
			}
		};
		return filter;
	}
	private static IgniteBiPredicate<String, Raster> filterForRareLand() {
		IgniteBiPredicate<String, Raster> filter = new IgniteBiPredicate<String, Raster>() {
			@Override public boolean apply(String key, Raster g) {
				if(RasterCategory.determineRasterCategory(g.getRasterScore()).equals(Constants.RARE_LAND))
					return true;
				return false;
			}
		};
		return filter;
	}
	private static IgniteBiPredicate<String, Raster> filterForDenseWater() {
		IgniteBiPredicate<String, Raster> filter = new IgniteBiPredicate<String, Raster>() {
			@Override public boolean apply(String key, Raster g) {
				if(RasterCategory.determineRasterCategory(g.getRasterScore()).equals(Constants.DENSE_WATER))
					return true;
				return false;
			}
		};
		return filter;
	}
	private static IgniteBiPredicate<String, Raster> filterForRareWater() {
		IgniteBiPredicate<String, Raster> filter = new IgniteBiPredicate<String, Raster>() {
			@Override public boolean apply(String key, Raster g) {
				if(RasterCategory.determineRasterCategory(g.getRasterScore()).equals(Constants.RARE_WATER))
					return true;
				return false;
			}
		};
		return filter;
	}

	public static void containsDenseLand(){
		System.out.println("Query: Select all the dense lands and their scores");
		String k;
		Raster r;
		IgniteBiPredicate<String, Raster> filter = filterForDenseLand();
		LaunchIgniteRaster.executeQuery(filter);
	}
	public static void containsDenseWater(){
		System.out.println("Query: Select all the dense water and their scores");
		String k;
		Raster r;
		IgniteBiPredicate<String, Raster> filter = filterForDenseWater();
		LaunchIgniteRaster.executeQuery(filter);
	}
	public static void containsRareLand(){
		System.out.println("Query: Get all the rare lands and their scores");
		String k;
		Raster r;
		IgniteBiPredicate<String, Raster> filter = filterForRareLand();
		LaunchIgniteRaster.executeQuery(filter);
	}
	public static void containsRareWater(){
		System.out.println("Query: Get all the rare water and their scores");
		String k;
		Raster r;
		IgniteBiPredicate<String, Raster> filter = filterForRareWater();
		LaunchIgniteRaster.executeQuery(filter);
	}
}