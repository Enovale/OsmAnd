package net.osmand.data;
import net.osmand.util.MapUtils;

public class RotatedTileBox {

	/// primary fields
	private double lat;
	private double lon;
	private float rotate;
	private float density;
	private int zoom;
	private float zoomScale;
	private float zoomAnimation;
	private int cx;
	private int cy;
	private int pixWidth;
	private int pixHeight;

	// derived
	// all geometry math is done in tileX, tileY of phisycal given zoom
	// zoomFactor is conversion factor, from dtileX * zoomFactor = dPixelX
	private float zoomFactor;
	private float rotateCos;
	private float rotateSin;
	private float oxTile;
	private float oyTile;
	private QuadRect tileBounds;
	private QuadRect latLonBounds;
	private QuadPoint tileLT;
	private QuadPoint tileRT;
	private QuadPoint tileRB;
	private QuadPoint tileLB;


	private RotatedTileBox(){
	}

	public RotatedTileBox(RotatedTileBox r){
		this.pixWidth = r.pixWidth;
		this.pixHeight = r.pixHeight;
		this.lat = r.lat;
		this.lon = r.lon;
		this.zoom = r.zoom;
		this.zoomScale = r.zoomScale;
		this.zoomAnimation = r.zoomAnimation;
		this.rotate = r.rotate;
		this.density = r.density;
		this.cx = r.cx;
		this.cy = r.cy;
		copyDerivedFields(r);
	}

	private void copyDerivedFields(RotatedTileBox r) {
		zoomFactor = r.zoomFactor;
		rotateCos = r.rotateCos;
		rotateSin = r.rotateSin;
		oxTile = r.oxTile;
		oyTile =r.oyTile;
		tileBounds = new QuadRect(r.tileBounds);
		latLonBounds = new QuadRect(r.latLonBounds);
		tileLT = new QuadPoint(r.tileLT);
		tileRT = new QuadPoint(r.tileRT);
		tileRB = new QuadPoint(r.tileRB);
		tileLB = new QuadPoint(r.tileLB);
	}

	private void init(int pixWidth, int pixHeight, float centerX, float centerY, double lat, double lon,
	                  int zoom, float zoomScale, float rotate, float density) {
		this.pixWidth = pixWidth;
		this.pixHeight = pixHeight;
		this.lat = lat;
		this.lon = lon;
		this.zoom = zoom;
		this.zoomScale = zoomScale;
		this.rotate = rotate;
		this.density = density;
		cx = (int) (pixWidth * centerX);
		cy = (int) (pixHeight * centerY);
		// derived
		calculateDerivedFields();

	}

	public void calculateDerivedFields() {
		zoomFactor = (float) Math.pow(2, zoomScale + zoomAnimation) * 256;
		float rad = (float) Math.toRadians(this.rotate);
		rotateCos = (float) Math.cos(rad);
		rotateSin = (float) Math.sin(rad);
		oxTile = (float) MapUtils.getTileNumberX(zoom, lon);
		oyTile = (float) MapUtils.getTileNumberY(zoom, lat);
		while(rotate < 0){
			rotate += 360;
		}
		while(rotate > 360){
			rotate -= 360;
		}
		calculateTileRectangle();
	}

	public double getLatFromPixel(float x, float y) {
		return MapUtils.getLatitudeFromTile(zoom, getTileYFromPixel(x, y));
	}

	public double getLonFromPixel(float x, float y) {
		return MapUtils.getLongitudeFromTile(zoom, getTileXFromPixel(x, y));
	}

	public LatLon getLatLonFromPixel(float x, float y) {
		return new LatLon(getLatFromPixel(x, y), getLonFromPixel(x, y));
	}

	public LatLon getCenterLatLon() {
		return new LatLon(lat, lon);
	}

	public QuadPoint getCenterPixelPoint() {
		return new QuadPoint(cx, cy);
	}

	public int getCenterPixelX(){
		return cx;
	}

	public int getCenterPixelY(){
		return cy;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	public double getCenterTileX(){
		return oxTile;
	}

	public double getCenterTileY(){
		return oyTile;
	}

	protected float getTileXFromPixel(float x, float y) {
		float dx = x - cx;
		float dy = y - cy;
		float dtilex;
		if(isMapRotateEnabled()){
			dtilex = (rotateCos * (float) dx + rotateSin * (float) dy);
		} else {
			dtilex = (float) dx;
		}
		return dtilex / zoomFactor + oxTile;
	}

	protected float getTileYFromPixel(float x, float y) {
		float dx = x - cx;
		float dy = y - cy;
		float dtiley;
		if(isMapRotateEnabled()){
			dtiley = (-rotateSin * (float) dx + rotateCos * (float) dy);
		} else {
			dtiley = (float) dy;
		}
		return dtiley / zoomFactor + oyTile;
	}


	public QuadRect getTileBounds() {
		return tileBounds;
	}

	public void calculateTileRectangle() {
		float x1 = getTileXFromPixel(0, 0);
		float x2 = getTileXFromPixel(pixWidth, 0);
		float x3 = getTileXFromPixel(pixWidth, pixHeight);
		float x4 = getTileXFromPixel(0, pixHeight);
		float y1 = getTileYFromPixel(0, 0);
		float y2 = getTileYFromPixel(pixWidth, 0);
		float y3 = getTileYFromPixel(pixWidth, pixHeight);
		float y4 = getTileYFromPixel(0, pixHeight);
		tileLT = new QuadPoint(x1, y1);
		tileRT = new QuadPoint(x2, y2);
		tileRB = new QuadPoint(x3, y3);
		tileLB = new QuadPoint(x4, y4);
		float l = Math.min(Math.min(x1, x2), Math.min(x3, x4)) ;
		float r = Math.max(Math.max(x1, x2), Math.max(x3, x4)) ;
		float t = Math.min(Math.min(y1, y2), Math.min(y3, y4)) ;
		float b = Math.max(Math.max(y1, y2), Math.max(y3, y4)) ;
		tileBounds = new QuadRect(l, t, r, b);
		float top = (float) MapUtils.getLatitudeFromTile(zoom, tileBounds.top);
		float left = (float) MapUtils.getLongitudeFromTile(zoom, tileBounds.left);
		float bottom = (float) MapUtils.getLatitudeFromTile(zoom, tileBounds.bottom);
		float right = (float) MapUtils.getLongitudeFromTile(zoom, tileBounds.right);
		latLonBounds = new QuadRect(left, top, right, bottom);
	}


	public int getPixWidth() {
		return pixWidth;
	}

	public int getPixHeight() {
		return pixHeight;
	}


	public int getPixXFromLatLon(double latitude, double longitude) {
		float xTile = (float) MapUtils.getTileNumberX(zoom, longitude);
		float yTile = (float) MapUtils.getTileNumberY(zoom, latitude);
		return getPixXFromTile(xTile, yTile);
	}

	protected int getPixXFromTile(float xTile, float yTile) {
		float rotX;
		final float dTileX = xTile - oxTile;
		final float dTileY = yTile - oyTile;
		if(isMapRotateEnabled()){
			rotX = (rotateCos * dTileX - rotateSin * dTileY);
		} else {
			rotX = dTileX;
		}
		float dx = rotX * zoomFactor;
		return (int) (dx + cx);
	}


	public int getPixYFromLatLon(double latitude, double longitude) {
		float xTile = (float) MapUtils.getTileNumberX(zoom, longitude);
		float yTile = (float) MapUtils.getTileNumberY(zoom, latitude);
		return getPixYFromTile(xTile, yTile);
	}

	protected int getPixYFromTile(float xTile, float yTile) {
		final float dTileX = xTile - oxTile;
		final float dTileY = yTile - oyTile;
		float rotY;
		if(isMapRotateEnabled()){
			rotY = (rotateSin * dTileX + rotateCos * dTileY);
		} else {
			rotY = dTileY;
		}
		float dy = rotY * zoomFactor;
		return (int) (dy + cy);
	}

	public int getPixXFromLonNoRot(double longitude) {
		float dTilex = (float) MapUtils.getTileNumberX(zoom, longitude) - oxTile;
		return (int) (dTilex * zoomFactor + cx);
	}

	public int getPixXFromTileXNoRot(double tileX) {
		float dTilex = (float) tileX - oxTile;
		return (int) (dTilex * zoomFactor + cx);
	}

	public int getPixYFromLatNoRot(double latitude) {
		float dTileY  = (float) MapUtils.getTileNumberY(zoom, latitude) - oyTile;
		return (int) ((dTileY * zoomFactor) + cy);
	}

	public int getPixYFromTileYNoRot(double tileY) {
		float dTileY  = (float) tileY - oyTile;
		return (int) ((dTileY * zoomFactor) + cy);
	}


	private boolean isMapRotateEnabled() {
		return rotate != 0;
	}

	public QuadRect getLatLonBounds() {
		return latLonBounds;
	}
	
	public float getRotateCos() {
		return rotateCos;
	}
	
	public float getRotateSin() {
		return rotateSin;
	}

	public int getZoom() {
		return zoom;
	}

	// Change lat/lon center
	public void setLatLonCenter(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
		calculateDerivedFields();
	}

	public void setRotate(float rotate) {
		this.rotate = rotate;
		calculateDerivedFields();
	}

	public void increasePixelDimensions(int dwidth, int dheight) {
		this.pixWidth += 2 * dwidth;
		this.pixHeight += 2 * dheight;
		this.cx += dwidth;
		this.cy += dheight;
		calculateDerivedFields();
	}

	public void setPixelDimensions(int width, int height) {
		setPixelDimensions(width, height, 0.5f, 0.5f);
	}

	public void setPixelDimensions(int width, int height, float ratiocx, float ratiocy) {
		this.pixHeight = height;
		this.pixWidth = width;
		this.cx = (int) (pixWidth * ratiocx);
		this.cy = (int) (pixHeight * ratiocy);
		calculateDerivedFields();
	}

	public boolean isZoomAnimated() {
		return zoomAnimation != 0;
	}

	public float getZoomAnimation() {
		return zoomAnimation;
	}

	public void setZoomAnimation(float z) {
		this.zoomAnimation = z;
		calculateDerivedFields();
	}

	public void setCenterLocation(float ratiocx, float ratiocy) {
		this.cx = (int) (pixWidth * ratiocx);
		this.cy = (int) (pixHeight * ratiocy);
		calculateDerivedFields();
	}

	public QuadPoint getLeftTopTilePoint() {
		return tileLT;
	}

	public LatLon getLeftTopLatLon() {
		return new LatLon(MapUtils.getLatitudeFromTile(zoom, tileLT.y),
				MapUtils.getLongitudeFromTile(zoom, tileLT.x));

	}

	public LatLon getRightBottomLatLon() {
		return new LatLon(MapUtils.getLatitudeFromTile(zoom, tileRB.y),
				MapUtils.getLongitudeFromTile(zoom, tileRB.x));
	}

	public void setZoom(int zoom, float zoomScale) {
		this.zoom = zoom;
		this.zoomScale = zoomScale;
		calculateDerivedFields();
	}

	public void setZoom(int zoom, float zoomScale, float zoomToAnimate) {
		this.zoom = zoom;
		this.zoomScale = zoomScale;
		this.zoomAnimation = zoomToAnimate;
		calculateDerivedFields();
	}

	public float getZoomScale() {
		return zoomScale;
	}
	
	public float getRotate() {
		return rotate;
	}

	public float getDensity() {
		return density;
	}

	public RotatedTileBox copy() {
		return new RotatedTileBox(this);
	}



	public boolean containsTileBox(RotatedTileBox box) {
		QuadPoint temp = new QuadPoint();
		if(box.zoom != zoom){
			throw new UnsupportedOperationException();
		}
		if(!containsTilePoint(box.tileLB)){
			return false;
		}
		if(!containsTilePoint(box.tileLT)){
			return false;
		}
		if(!containsTilePoint(box.tileRB)){
			return false;
		}
		if(!containsTilePoint(box.tileRT)){
			return false;
		}
		return true;
	}

	public boolean containsTilePoint(QuadPoint qp) {
		double tx = getPixXFromTile(qp.x, qp.y);
		double ty = getPixYFromTile(qp.x, qp.y);
		return tx >= 0 && tx <= pixWidth && ty >= 0 && ty <= pixHeight;
	}

	public boolean containsLatLon(double lat, double lon) {
		double tx = getPixXFromLatLon(lat, lon);
		double ty = getPixYFromLatLon(lat, lon);
		return tx >= 0 && tx <= pixWidth && ty >= 0 && ty <= pixHeight;
	}


	public double getDistance(int pixX, int pixY, int pixX2, int pixY2) {
		final double lat1 = getLatFromPixel(pixX, pixY);
		final double lon1 = getLonFromPixel(pixX, pixY);
		final double lat2 = getLatFromPixel(pixX2, pixY2);
		final double lon2 = getLonFromPixel(pixX2, pixY2);
		return MapUtils.getDistance(lat1,lon1, lat2, lon2);
	}

	public static class RotatedTileBoxBuilder {

		private RotatedTileBox tb;
		private boolean pixelDimensionsSet = false;
		private boolean locationSet = false;
		private boolean zoomSet = false;

		public RotatedTileBoxBuilder() {
			tb = new RotatedTileBox();
			tb.density = 1;
			tb.rotate = 0;
		}

		public RotatedTileBoxBuilder density(float d) {
			tb.density = d;
			return this;
		}

		public RotatedTileBoxBuilder setZoomAndScale(int zoom, float scale) {
			tb.zoom = zoom;
			tb.zoomScale = scale;
			zoomSet = true;
			return this;
		}

		public RotatedTileBoxBuilder setLocation(double lat, double lon) {
			tb.lat = lat;
			tb.lon = lon;
			locationSet = true;
			return this;
		}

		public RotatedTileBoxBuilder setRotate(float degrees) {
			tb.rotate = degrees;
			return this;
		}

		public RotatedTileBoxBuilder setPixelDimensions(int pixWidth, int pixHeight, float centerX, float centerY) {
			tb.pixWidth = pixWidth;
			tb.pixHeight = pixHeight;
			tb.cx = (int) (pixWidth * centerX);
			tb.cy = (int) (pixHeight * centerY);
			pixelDimensionsSet = true;
			return this;
		}

		public RotatedTileBoxBuilder setPixelDimensions(int pixWidth, int pixHeight) {
			return setPixelDimensions(pixWidth, pixHeight, 0.5f, 0.5f);
		}

		public RotatedTileBox build() {
			if(!pixelDimensionsSet) {
				throw new IllegalArgumentException("Please specify pixel dimensions");
			}
			if(!zoomSet) {
				throw new IllegalArgumentException("Please specify zoom");
			}
			if(!locationSet) {
				throw new IllegalArgumentException("Please specify location");
			}

			final RotatedTileBox local = tb;
			local.calculateDerivedFields();
			tb = null;
			return local;
		}
	}

	public double getLongitude() {
		return lon;
	}

	public double getLatitude() {
		return lat;
	}

}
