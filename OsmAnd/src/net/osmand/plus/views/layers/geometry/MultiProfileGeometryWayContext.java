package net.osmand.plus.views.layers.geometry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import net.osmand.AndroidUtils;
import net.osmand.plus.UiUtilities;
import net.osmand.plus.views.OsmandMapLayer.RenderingLineAttributes;
import net.osmand.util.Algorithms;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public class MultiProfileGeometryWayContext extends GeometryWayContext {

	private final UiUtilities iconsCache;

	public final float minIconMargin;
	public final float circleSize;

	private RenderingLineAttributes multiProfileAttrs;

	private Bitmap pointIcon;
	private final Map<String, Bitmap> profileIconsBitmapCache;

	public MultiProfileGeometryWayContext(Context ctx, UiUtilities iconsCache, float density) {
		super(ctx, density);
		this.iconsCache = iconsCache;
		profileIconsBitmapCache = new HashMap<>();
		minIconMargin = density * 30;
		circleSize = density * 70;
	}

	public void updatePaints(boolean nightMode, @NonNull RenderingLineAttributes multiProfileAttrs) {
		this.multiProfileAttrs = multiProfileAttrs;
		super.updatePaints(nightMode, multiProfileAttrs);
	}

	@Override
	protected void recreateBitmaps() {
		float density = getDensity();
		float size = density * 12.5f;
		float outerRadius = density * 6.25f;
		float centerRadius = density * 6;
		float innerRadius = density * 4;
		float centerXY = size / 2;

		pointIcon = Bitmap.createBitmap((int) size, (int) size, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(pointIcon);
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);

		paint.setColor(Color.BLACK);
		canvas.drawCircle(centerXY, centerXY, outerRadius, paint);

		paint.setColor(Color.WHITE);
		canvas.drawCircle(centerXY, centerXY, centerRadius, paint);

		paint.setColor(Algorithms.parseColor("#637EFB"));
		canvas.drawCircle(centerXY, centerXY, innerRadius, paint);
	}

	@Override
	protected int getArrowBitmapResId() {
		return 0;
	}

	@NonNull
	public Bitmap getProfileIconBitmap(@DrawableRes int iconRes, @ColorInt int color) {
		String key = iconRes + "_" + color;
		Bitmap bitmap = profileIconsBitmapCache.get(key);
		if (bitmap == null) {
			bitmap = Bitmap.createBitmap((int) circleSize, (int) circleSize, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			float center = bitmap.getWidth() / 2f;

			canvas.drawCircle(center, center, center / 2, multiProfileAttrs.paint_1);
			multiProfileAttrs.paint3.setColor(color);
			canvas.drawCircle(center, center, center / 2, multiProfileAttrs.paint3);

			float iconSize = center - getDensity() * 10;
			Bitmap profileIconBitmap = AndroidUtils.createScaledBitmap(
					iconsCache.getPaintedIcon(iconRes, color), (int) iconSize, (int) iconSize);
			canvas.drawBitmap(profileIconBitmap, center - iconSize / 2, center - iconSize / 2, multiProfileAttrs.paint3);

			profileIconsBitmapCache.put(key, bitmap);
		}
		return bitmap;
	}

	@NonNull
	public Bitmap getPointIcon() {
		return pointIcon;
	}
}