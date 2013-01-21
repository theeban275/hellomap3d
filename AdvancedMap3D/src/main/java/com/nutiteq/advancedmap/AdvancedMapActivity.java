package com.nutiteq.advancedmap;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.nutiteq.layers.vector.WKBLayer;
import com.nutiteq.log.Log;
import com.nutiteq.projections.EPSG3857;
import com.nutiteq.style.LineStyle;
import com.nutiteq.style.PointStyle;
import com.nutiteq.style.PolygonStyle;
import com.nutiteq.style.StyleSet;
import com.nutiteq.utils.UnscaledBitmapLoader;

public class AdvancedMapActivity extends FragmentActivity {
	
	private List<Fragment> fragmentList;
	private int currentIndex;
	
	public static class VectorMapFragment extends Fragment {
		
		private TabHost tabHost;
		
		@Override
	    public View onCreateView(LayoutInflater inflater, 
	    		                  ViewGroup container,
	                              Bundle savedInstanceState) {	
			
			if (tabHost == null){
				tabHost = (TabHost) inflater.inflate(R.layout.tab_group, container, false);
				tabHost.setup();
				
				TabSpec spec = tabHost.newTabSpec("tab");
				spec.setContent(new TabHost.TabContentFactory() {
					
					@Override
					public View createTabContent(String tag) {
						CustomMap map = new CustomMap(VectorMapFragment.this.getActivity());

						int minZoom = 4;
						
						StyleSet<PointStyle> pointStyleSet = new StyleSet<PointStyle>();
				        Bitmap pointMarker = UnscaledBitmapLoader.decodeResource(VectorMapFragment.this.getActivity().getResources(), R.drawable.point);
				        PointStyle pointStyle = PointStyle.builder().setBitmap(pointMarker).setSize(0.05f).setColor(Color.BLACK).build();
						pointStyleSet.setZoomStyle(minZoom, pointStyle);

						StyleSet<LineStyle> lineStyleSet = new StyleSet<LineStyle>();
				        lineStyleSet.setZoomStyle(minZoom, LineStyle.builder().setWidth(0.1f).setColor(Color.GREEN).build());
				        
				        PolygonStyle polygonStyle = PolygonStyle.builder().setColor(Color.BLUE).build();
				        StyleSet<PolygonStyle> polygonStyleSet = new StyleSet<PolygonStyle>(null);
						polygonStyleSet.setZoomStyle(minZoom, polygonStyle);
						
						try {
							map.getLayers().addLayer(new WKBLayer(new EPSG3857(), Environment.getExternalStorageDirectory().getPath()+"/Maps/vectors.wkb",
									pointStyleSet, lineStyleSet, polygonStyleSet));
						} catch (Exception e) {
							
						}

						return map;
					}
				});
				spec.setIndicator("tab");
				tabHost.addTab(spec);
				
			}
			
			// fix issue with pressing back button
			if (tabHost.getParent() != null){
				((ViewGroup) tabHost.getParent()).removeView(tabHost);
			}
			
			return tabHost;
		}
		
	}
	
	public static class SingleMapFragment extends Fragment {
		
		private TabHost tabHost;
		
		@Override
	    public View onCreateView(LayoutInflater inflater, 
	    		                  ViewGroup container,
	                              Bundle savedInstanceState) {	
			
			if (tabHost == null){
				tabHost = (TabHost) inflater.inflate(R.layout.tab_group, container, false);
				tabHost.setup();
				
				TabSpec spec = tabHost.newTabSpec("tab");
				spec.setContent(new TabHost.TabContentFactory() {
					
					@Override
					public View createTabContent(String tag) {
						return new CustomMap(SingleMapFragment.this.getActivity());
					}
				});
				spec.setIndicator("tab");
				tabHost.addTab(spec);
				
			}
			
			// fix issue with pressing back button
			if (tabHost.getParent() != null){
				((ViewGroup) tabHost.getParent()).removeView(tabHost);
			}
			
			return tabHost;
		}
		
	}
	
	public static class SingleMapInLayoutFragment extends Fragment {
		
		private TabHost tabHost;
		
		@Override
	    public View onCreateView(LayoutInflater inflater, 
	    		                  ViewGroup container,
	                              Bundle savedInstanceState) {	
			
			if (tabHost == null){
				tabHost = (TabHost) inflater.inflate(R.layout.tab_group, container, false);
				tabHost.setup();
				
				TabSpec spec = tabHost.newTabSpec("tab");
				spec.setContent(new TabHost.TabContentFactory() {
					
					@Override
					public View createTabContent(String tag) {
						LinearLayout layout = new LinearLayout(SingleMapInLayoutFragment.this.getActivity());
						layout.setOrientation(LinearLayout.VERTICAL);
						TextView text = new TextView(SingleMapInLayoutFragment.this.getActivity());
						text.setText("Map Example");
						layout.addView(text);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
				        layout.addView(new CustomMap(SingleMapInLayoutFragment.this.getActivity()), params);
						Button button = new Button(SingleMapInLayoutFragment.this.getActivity());
						button.setText("Button");
						layout.addView(button);
						return layout;
					}
				});
				spec.setIndicator("tab");
				tabHost.addTab(spec);
				
			}
			
			// fix issue with pressing back button
			if (tabHost.getParent() != null){
				((ViewGroup) tabHost.getParent()).removeView(tabHost);
			}
			
			return tabHost;
		}
		
	}
	
	public static class DoubleMapFragment extends Fragment {
		
		private TabHost tabHost;
		
		@Override
	    public View onCreateView(LayoutInflater inflater, 
	    		                  ViewGroup container,
	                              Bundle savedInstanceState) {	
			
			if (tabHost == null){
				tabHost = (TabHost) inflater.inflate(R.layout.tab_group, container, false);
				tabHost.setup();
				
				TabSpec spec1 = tabHost.newTabSpec("tab1");
				spec1.setContent(new TabHost.TabContentFactory() {
					
					@Override
					public View createTabContent(String tag) {
						return new CustomMap(DoubleMapFragment.this.getActivity());
					}
				});
				spec1.setIndicator("tab1");
				tabHost.addTab(spec1);
				
				TabSpec spec2 = tabHost.newTabSpec("tab2");
				spec2.setContent(new TabHost.TabContentFactory() {
					
					@Override
					public View createTabContent(String tag) {
						return new CustomMap(DoubleMapFragment.this.getActivity());
					}
				});
				spec2.setIndicator("tab2");
				tabHost.addTab(spec2);
				
			}
			
			// fix issue with pressing back button
			if (tabHost.getParent() != null){
				((ViewGroup) tabHost.getParent()).removeView(tabHost);
			}
			
			return tabHost;
		}
	}
	
	public static class DoubleMapInLayoutFragment extends Fragment {
		
		private TabHost tabHost;
		
		@Override
	    public View onCreateView(LayoutInflater inflater, 
	    		                  ViewGroup container,
	                              Bundle savedInstanceState) {	
			
			if (tabHost == null){
				tabHost = (TabHost) inflater.inflate(R.layout.tab_group, container, false);
				tabHost.setup();
				
				TabSpec spec1 = tabHost.newTabSpec("tab1");
				spec1.setContent(new TabHost.TabContentFactory() {
					
					@Override
					public View createTabContent(String tag) {
						LinearLayout layout = new LinearLayout(DoubleMapInLayoutFragment.this.getActivity());
						layout.setOrientation(LinearLayout.VERTICAL);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
				        layout.addView(new CustomMap(DoubleMapInLayoutFragment.this.getActivity()), params);
						return layout;
					}
				});
				spec1.setIndicator("tab1");
				tabHost.addTab(spec1);
				
				TabSpec spec2 = tabHost.newTabSpec("tab2");
				spec2.setContent(new TabHost.TabContentFactory() {
					
					@Override
					public View createTabContent(String tag) {
						LinearLayout layout = new LinearLayout(DoubleMapInLayoutFragment.this.getActivity());
						layout.setOrientation(LinearLayout.VERTICAL);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
				        layout.addView(new CustomMap(DoubleMapInLayoutFragment.this.getActivity()), params);
						return layout;
					}
				});
				spec2.setIndicator("tab2");
				tabHost.addTab(spec2);
				
			}
			
			// fix issue with pressing back button
			if (tabHost.getParent() != null){
				((ViewGroup) tabHost.getParent()).removeView(tabHost);
			}
			
			return tabHost;
		}
	}
	
	// force to load proj library for spatialite
	static {
		try {
			System.loadLibrary("proj");
		} catch (Throwable t) {
			System.err.println("Unable to load proj: " + t);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// spinner in status bar, for progress indication
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.main);

		// enable logging for troubleshooting - optional
		Log.enableAll();
		Log.setTag("hellomap");
		
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new VectorMapFragment());
		fragmentList.add(new SingleMapFragment());
		fragmentList.add(new SingleMapInLayoutFragment());
		fragmentList.add(new DoubleMapFragment());
		fragmentList.add(new DoubleMapInLayoutFragment());
		
		showFragment(0);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.previous_fragment:
				previousFragment();
				return (true);
			case R.id.next_fragment:
				nextFragment();
				return (true);
			default:
				return (super.onOptionsItemSelected(item));
		}
	}
	
	private void showFragment(int index) {
		currentIndex = index;
		Fragment fragment = fragmentList.get(index);
		FragmentManager fm = this.getSupportFragmentManager();
	    FragmentTransaction ft = fm.beginTransaction();
	    ft.replace(R.id.fragment_content, fragment);
        ft.commit();
	}
	
	private void previousFragment() {
		if (currentIndex <= 0) return;
		currentIndex--;
		showFragment(currentIndex);
	}
	
	private void nextFragment() {		
		if (currentIndex >= fragmentList.size() - 1) return;
		currentIndex++;
		showFragment(currentIndex);
	}
	
}

