package com.nutiteq.advancedmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import org.mapsforge.android.maps.mapgenerator.JobTheme;
import org.mapsforge.android.maps.mapgenerator.databaserenderer.ExternalRenderTheme;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup.LayoutParams;

import com.nutiteq.MapView;
import com.nutiteq.advancedmap.AdvancedMapActivity.SingleMapFragment;
import com.nutiteq.components.Components;
import com.nutiteq.components.MapPos;
import com.nutiteq.components.Options;
import com.nutiteq.db.DBLayer;
import com.nutiteq.geometry.Marker;
import com.nutiteq.layers.raster.GdalMapLayer;
import com.nutiteq.layers.raster.MapsforgeMapLayer;
import com.nutiteq.layers.raster.WmsLayer;
import com.nutiteq.layers.vector.Polygon3DOSMLayer;
import com.nutiteq.layers.vector.SpatialLiteDb;
import com.nutiteq.layers.vector.SpatialiteLayer;
import com.nutiteq.log.Log;
import com.nutiteq.projections.EPSG3857;
import com.nutiteq.projections.Projection;
import com.nutiteq.rasterlayers.TMSMapLayer;
import com.nutiteq.style.LineStyle;
import com.nutiteq.style.MarkerStyle;
import com.nutiteq.style.ModelStyle;
import com.nutiteq.style.PointStyle;
import com.nutiteq.style.Polygon3DStyle;
import com.nutiteq.style.PolygonStyle;
import com.nutiteq.style.StyleSet;
import com.nutiteq.ui.DefaultLabel;
import com.nutiteq.ui.Label;
import com.nutiteq.utils.UnscaledBitmapLoader;
import com.nutiteq.vectorlayers.MarkerLayer;
import com.nutiteq.vectorlayers.NMLModelDbLayer;

public class CustomMap extends MapView {

	private static int cacheId = 9999;

	public CustomMap(Activity activity) {
		super(activity);
		
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		this.setComponents(new Components());
		
		// add event listener
		MapEventListener mapListener = new MapEventListener(activity);
		this.getOptions().setMapListener(mapListener);

		// Here we use MapQuest open tiles
		// Almost all online tiled maps use EPSG3857 projection.
		//TMSMapLayer mapLayer = new TMSMapLayer(new EPSG3857(), 0, 18, 0,
		//		"http://otile1.mqcdn.com/tiles/1.0.0/osm/", "/", ".png");

		//this.getLayers().setBaseLayer(mapLayer);
		
		addGdalLayer(new EPSG3857(), Environment.getExternalStorageDirectory().getPath()+"/Map/natural-earth-2-mercator.tif");

		// set initial map view camera - optional. "World view" is default
		// Location: San Francisco
		this.setFocusPoint(this.getLayers().getBaseLayer().getProjection().fromWgs84(-122.41666666667f, 37.76666666666f));

		// rotation - 0 = north-up
		this.setRotation(0f);
		// zoom - 0 = world, like on most web maps
		this.setZoom(4.0f);
        // tilt means perspective view. Default is 90 degrees for "normal" 2D map view, minimum allowed is 30 degrees.
		this.setTilt(90.0f);

		// Activate some mapview options to make it smoother - optional
		this.getOptions().setPreloading(true);
		this.getOptions().setSeamlessHorizontalPan(true);
		this.getOptions().setTileFading(true);
		this.getOptions().setKineticPanning(true);
		this.getOptions().setDoubleClickZoomIn(true);
		this.getOptions().setDualClickZoomOut(true);

		// set sky bitmap - optional, default - white
		this.getOptions().setSkyDrawMode(Options.DRAW_BITMAP);
		this.getOptions().setSkyOffset(4.86f);
		this.getOptions().setSkyBitmap(
				UnscaledBitmapLoader.decodeResource(getResources(),
						R.drawable.sky_small));

        // Map background, visible if no map tiles loaded - optional, default - white
		this.getOptions().setBackgroundPlaneDrawMode(Options.DRAW_BITMAP);
		this.getOptions().setBackgroundPlaneBitmap(
				UnscaledBitmapLoader.decodeResource(getResources(),
						R.drawable.background_plane));
		this.getOptions().setClearColor(Color.WHITE);

		// configure texture caching - optional, suggested
		//this.getOptions().setTextureMemoryCacheSize(40 * 1024 * 1024);
		//this.getOptions().setCompressedMemoryCacheSize(8 * 1024 * 1024);

        // define online map persistent caching - optional, suggested. Default - no caching
		//this.getOptions().setPersistentCachePath(activity.getDatabasePath("mapcache").getPath());
		// set persistent raster cache limit to 100MB
		//this.getOptions().setPersistentCacheSize(100 * 1024 * 1024);

		this.startMapping();
		
        // from http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/raster/NE2_HR_LC_SR_W.zip
		

        //addMarkerLayer(mapLayer.getProjection(),mapLayer.getProjection().fromWgs84(-122.416667f, 37.766667f));

		// addSpatialiteLayer(mapLayer.getProjection(),Environment.getExternalStorageDirectory().getPath()+"/mapxt/romania_sp3857.sqlite");

        // addMapsforgeLayer(mapLayer.getProjection(), Environment.getExternalStorageDirectory() + "/mapxt/california.map",
		
        // Environment.getExternalStorageDirectory() + "/mapxt/osmarender.xml");

		//addOsmPolygonLayer(mapView, mapLayer.getProjection());

		// add3dModelLayer(mapLayer.getProjection(),Environment.getExternalStorageDirectory() + "/buildings.sqlite");

        //addWmsLayer(mapView, mapLayer.getProjection(),"http://kaart.maakaart.ee/service?","osm", new EPSG4326());
	}

	private void addGdalLayer(Projection proj, String filePath) {
		// GDAL raster layer test. It is set Base layer, not overlay
		GdalMapLayer gdalLayer;
		try {
            gdalLayer = new GdalMapLayer(proj, 0, 18, nextId(), filePath, this, true);
			this.getLayers().setBaseLayer(gdalLayer);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ** Add simple marker to map.
	private void addMarkerLayer(Projection proj, MapPos markerLocation) {
		// define marker style (image, size, color)
        Bitmap pointMarker = UnscaledBitmapLoader.decodeResource(getResources(), R.drawable.olmarker);
        MarkerStyle markerStyle = MarkerStyle.builder().setBitmap(pointMarker).setSize(0.5f).setColor(Color.WHITE).build();
		// define label what is shown when you click on marker
        Label markerLabel = new DefaultLabel("San Francisco", "Here is a marker");
        

        // create layer and add object to the layer, finally add layer to the map. 
        // All overlay layers must be same projection as base layer, so we reuse it
		MarkerLayer markerLayer = new MarkerLayer(proj);
        markerLayer.add(new Marker(markerLocation, markerLabel, markerStyle, null));
		this.getLayers().addLayer(markerLayer);
	}

	// Load online simple building 3D boxes
	private void addOsmPolygonLayer(Projection proj) {
		// Set style visible from zoom 15
        Polygon3DStyle polygon3DStyle = Polygon3DStyle.builder().setColor(Color.BLACK | 0x40ffffff).build();
        StyleSet<Polygon3DStyle> polygon3DStyleSet = new StyleSet<Polygon3DStyle>(null);
		polygon3DStyleSet.setZoomStyle(15, polygon3DStyle);

        Polygon3DOSMLayer osm3dLayer = new Polygon3DOSMLayer(new EPSG3857(), 0.500f, 200, polygon3DStyleSet);
		this.getLayers().addLayer(osm3dLayer);
	}

	// load 3D models from special database
	private void add3dModelLayer(Projection proj, String filePath) {

		// define style for 3D to define minimum zoom = 14
		ModelStyle modelStyle = ModelStyle.builder().build();
		StyleSet<ModelStyle> modelStyleSet = new StyleSet<ModelStyle>(null);
		modelStyleSet.setZoomStyle(14, modelStyle);

		// ** 3D Model layer
		NMLModelDbLayer modelLayer = new NMLModelDbLayer(new EPSG3857(),
				filePath, modelStyleSet);

		this.getLayers().addLayer(modelLayer);

	}

	private void addSpatialiteLayer(Projection proj, String dbPath) {

		// ** Spatialite
		// print out list of tables first
		int minZoom = 10;

		SpatialLiteDb spatialLite = new SpatialLiteDb(dbPath);
		Vector<DBLayer> dbMetaData = spatialLite.qrySpatialLayerMetadata();

		for (DBLayer dbLayer : dbMetaData) {
            Log.debug("layer: "+dbLayer.table+" "+dbLayer.type+" geom:"+dbLayer.geomColumn);
		}

		// set styles for all 3 object types: point, line and polygon

		StyleSet<PointStyle> pointStyleSet = new StyleSet<PointStyle>();
        Bitmap pointMarker = UnscaledBitmapLoader.decodeResource(getResources(), R.drawable.point);
        PointStyle pointStyle = PointStyle.builder().setBitmap(pointMarker).setSize(0.05f).setColor(Color.BLACK).build();
		pointStyleSet.setZoomStyle(minZoom, pointStyle);

		StyleSet<LineStyle> lineStyleSet = new StyleSet<LineStyle>();
        lineStyleSet.setZoomStyle(minZoom, LineStyle.builder().setWidth(0.1f).setColor(Color.GREEN).build());

		StyleSet<LineStyle> lineStyleSetHw = new StyleSet<LineStyle>();
        lineStyleSetHw.setZoomStyle(minZoom, LineStyle.builder().setWidth(0.07f).setColor(Color.GRAY).build());

        PolygonStyle polygonStyle = PolygonStyle.builder().setColor(Color.BLUE).build();
        StyleSet<PolygonStyle> polygonStyleSet = new StyleSet<PolygonStyle>(null);
		polygonStyleSet.setZoomStyle(minZoom, polygonStyle);

        SpatialiteLayer spatialiteLayerPt = new SpatialiteLayer(proj, dbPath, "pt_tourism",
                "GEOMETRY", new String[]{"name"}, 500, pointStyleSet, null, null);

		this.getLayers().addLayer(spatialiteLayerPt);

        SpatialiteLayer spatialiteLayerLn = new SpatialiteLayer(proj, dbPath, "ln_railway",
                "GEOMETRY", new String[]{"sub_type"}, 500, null, lineStyleSet, null);

        this.getLayers().addLayer(spatialiteLayerLn);

        SpatialiteLayer spatialiteLayerHw = new SpatialiteLayer(proj, dbPath, "ln_highway",
                "GEOMETRY", new String[]{"name"}, 500, null, lineStyleSetHw, null);

        this.getLayers().addLayer(spatialiteLayerHw);

        SpatialiteLayer spatialiteLayerPoly = new SpatialiteLayer(proj, dbPath, "pg_boundary",
                "GEOMETRY", new String[]{"name"}, 500, null, null, polygonStyleSet);

        this.getLayers().addLayer(spatialiteLayerPoly);

	}

     private void addMapsforgeLayer(Projection proj, String mapFile, String renderThemeFile){
		try {
       JobTheme renderTheme = new ExternalRenderTheme(new File(renderThemeFile));

        MapsforgeMapLayer mapLayer = new MapsforgeMapLayer(new EPSG3857(), 0, 20, nextId(),
                 mapFile,renderTheme);

			this.getLayers().setBaseLayer(mapLayer);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

     private void addWmsLayer(Projection proj, String url, String layers, Projection dataProjection){
       WmsLayer wmsLayer = new WmsLayer(proj, 0, 19, nextId(), url, "", layers, "image/png", dataProjection);
		wmsLayer.setFetchPriority(-5);
		this.getLayers().addLayer(wmsLayer);
	}
     
     private static int nextId() {
    	 return cacheId++;
     }

}
