package rasterproject;
import org.apache.ignite.*;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.*;
import org.apache.ignite.configuration.*;
import org.geotools.coverage.CoverageFactoryFinder;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.*;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.util.factory.Hints;
import org.opengis.parameter.ParameterValue;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import javax.cache.*;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

public class LaunchIgniteRaster {
	//Cache Name
	static String RASTER_CACHE = LaunchIgniteRaster.class.getSimpleName();
	private static final boolean UPDATE = true;
	static IgniteCache<String, Raster> cache;
	//Grid Coverage is a type of 2d geographic map
	//static IgniteCache<Integer, GridCoverage2D> raster_cache;

	public static void main(String[] args) {
		GenerateRasterObjects.generate();
		try(Ignite ignite = Ignition.start("/home/seed/workspace/Raster_finalProject/config/raster-ignite-config.xml")){
			ignite.active(true);
			CacheConfiguration<String, Raster> rasterCfg = new CacheConfiguration<>(RASTER_CACHE);
			rasterCfg.setIndexedTypes(String.class, Raster.class);
			//rasterCfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
			//TODO
			rasterCfg.setSqlFunctionClasses(RasterQueries.class);
			rasterCfg.setCacheMode(CacheMode.PARTITIONED);		
			//rasterCfg.setAffinity(affFunc);
			rasterCfg.setBackups(0);
			cache = ignite.getOrCreateCache(rasterCfg);
			//Load Data into cache
			if(UPDATE){
				System.out.println("Populating the Cache. Below elements are being loaded into the cache: -");
				ArrayList<Raster> rastersList = GenerateRasterObjects.provideRasterObjects();
				for(Raster raster : rastersList){
					System.out.println("Raster ID: "+raster.getRasterID() + " Raster Score: "+raster.getRasterScore());
					cache.put(raster.getRasterID()+"", raster);
				}
				//cache load
				/*try(IgniteDataStreamer<Integer, Raster> streamer = ignite.dataStreamer(RASTER_CACHE)){
					streamer.allowOverwrite(true);
					for(Raster raster : rastersList){
						streamer.addData(raster.getRasterID(), raster);
					}
				}*/
			}
			try{
				RasterQueries.runRasterQueries();
			}catch(Exception e){
				ignite.destroyCache(RASTER_CACHE);
			}
		}
	}

	public static void executeQuery(IgniteBiPredicate<String, Raster> filter){
		System.out.println("Running Query on Ignite Cache");
		System.out.println();
		long startTime = System.currentTimeMillis();
		int resultSetSize = 0;
		try(QueryCursor<Cache.Entry<String, Raster>> cursor = cache.query(new ScanQuery<> (filter))){
			//cache.query(new ScanQuery<String, Raster>((k,r) -> r.getRasterScore() > 100))){
			for(Cache.Entry<String, Raster> entry : cursor){
				System.out.println("Raster ID" + entry.getKey() + ", Raster Score = " + entry.getValue().getRasterScore());
				resultSetSize ++;
			}
			System.out.println("--> "+resultSetSize + " number of records found");
			long elapsedTime = System.currentTimeMillis() - startTime;
			System.out.println("--> ELAPSED TIME = " + (1.0 * elapsedTime/1000.0) + " sec");
			System.out.println();
			System.out.println();
			System.out.println("--------------------------------------------");
		}
	}
}




//----------------------------------------------------------------------------------------------
/*public static void test(java.io.File file) throws Exception {
        ParameterValue<OverviewPolicy> policy = AbstractGridFormat.OVERVIEW_POLICY.createValue();
        policy.setValue(OverviewPolicy.IGNORE);
        //this will basically read 4 tiles worth of data at once from the disk...
        ParameterValue<String> gridsize = AbstractGridFormat.SUGGESTED_TILE_SIZE.createValue();
        //Setting read type: use JAI ImageRead (true) or ImageReaders read methods (false)
        ParameterValue<Boolean> useJaiRead = AbstractGridFormat.USE_JAI_IMAGEREAD.createValue();
        useJaiRead.setValue(true);
        GridCoverage2DReader reader = new GeoTiffReader(file);
        GridEnvelope dimensions = reader.getOriginalGridRange();
        GridCoordinates maxDimensions = dimensions.getHigh();
        int w = maxDimensions.getCoordinateValue(0)+1;
        int h = maxDimensions.getCoordinateValue(1)+1;
        int numBands = reader.getGridCoverageCount();
        GridCoverage2D coverage = reader.read(
            new GeneralParameterValue[]{policy, gridsize, useJaiRead}
        );
        GridGeometry2D geometry = coverage.getGridGeometry();
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                org.geotools.geometry.Envelope2D pixelEnvelop =
                geometry.gridToWorld(new GridEnvelope2D(i, j, 1, 1));
                double lat = pixelEnvelop.getCenterY();
                double lon = pixelEnvelop.getCenterX();
                double[] vals = new double[numBands];
                coverage.evaluate(new GridCoordinates2D(i, j), vals);
                //Do something!
            }
        }
    }
 */

//Main Method
/*public static void main(String[] args) {
		//String datasetPath = "/home/dhruv/workspace/IgniteSpatial-master";
		//File file = new File(datasetPath);
		/* another attempt
        //using ImageReader.readRaster
        //Iterator readers = ImageIO.getImageReadersByFormatName("JPEG");
        Iterator readers = ImageIO.getImageReadersByFormatName("TIF"); 
        ImageReader reader = null;
         while(readers.hasNext()) {
           reader = (ImageReader)readers.next();
           if(reader.canReadRaster()) {
             break;
           }
         }
         ImageInputStream input;
        try {
            input = ImageIO.createImageInputStream(file);
             reader.setInput(input); 
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } 

        try {
            Raster raster = reader.readRaster(0,null);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        //another attempt ends
 * */


///
//try{
//GeoTiffReader reader = new GeoTiffReader(file, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
///System.out.println(reader);
//}catch(Exception e){

//}
//tiff to bufferedImage
//FileSeekableStream stream = new FileSeekableStream(filepath + filename);

//creating bufferedImage from tiff using JAI
/*FileSeekableStream stream;
		try {
			/*
            stream = new FileSeekableStream(datasetPath);
            TIFFDecodeParam decodeParam = new TIFFDecodeParam();
            decodeParam.setDecodePaletteAsShorts(true);
            ParameterBlock params = new ParameterBlock();
            params.add(stream);
            RenderedOp image1 = JAI.create("tiff", params);
            BufferedImage img = image1.getAsBufferedImage();

 */

/*BufferedImage bImage = ImageIO.read(file);
            //ImageIO.write(image, "bmp", new File(outputFile));

            //bufferedImage to pixel values
            //byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
            byte[] pixels = ((DataBufferByte) bImage.getRaster().getDataBuffer()).getData();
			File files = new File(datasetPath, "albers27.tif");
			byte[] bytes = Files.readAllBytes(files.toPath()); 
			System.out.println("DHRUVVVVVVVVVVVVVV size of array is "+bytes.length);
			for (int i=0;i<bytes.length;i++)
			{
				System.out.println(bytes[i]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Dhruv error in file stream");
			e.printStackTrace();
		}

		//cache configuration
		CacheConfiguration<Integer, GridCoverage2D> config = new CacheConfiguration<Integer, GridCoverage2D>(RASTER_CACHE);
		config.setIndexedTypes(Integer.class, GridCoverage2D.class);
		try (Ignite ignite = Ignition.start()) {
			ignite.cluster().active(true); //to activate cluster with persistence            
			// GET OR CREATE CACHES
			raster_cache = ignite.getOrCreateCache(config);
		}
		//Cache Creation
		//        static IgniteCache<String, GridCoverage2D> rasterCache = ignite.createCache();
	}*/


//
//public static void Query5(String query){
//	long startTime = System.currentTimeMillis();
//
//	SqlFieldsQuery qry = new SqlFieldsQuery(query);
//	qry.setCollocated(true);
//	qry.setLazy(true);
//
//	Collection<java.util.List<?>> entries = cache.query( qry ).getAll();
//
//	int counter = entries.size();
//
//	long elapsedTime = System.currentTimeMillis() - startTime;
//	System.out.println( " ELAPSED TIME = " + (1.0 * elapsedTime/1000.0) + "s, Result Size: " + counter);
//}