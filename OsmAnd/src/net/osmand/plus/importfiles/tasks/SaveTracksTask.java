package net.osmand.plus.importfiles.tasks;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.osmand.IndexConstants;
import net.osmand.gpx.GPXFile;
import net.osmand.gpx.GPXUtilities;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.configmap.tracks.TrackItem;
import net.osmand.plus.importfiles.SaveImportedGpxListener;
import net.osmand.plus.importfiles.ui.ImportTrackItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SaveTracksTask extends AsyncTask<Void, Void, List<String>> {

	private final OsmandApplication app;
	private final File importDir;
	private final List<ImportTrackItem> items;
	private final SaveImportedGpxListener listener;

	public SaveTracksTask(@NonNull OsmandApplication app,
	                      @NonNull List<ImportTrackItem> items,
	                      @NonNull File importDir,
	                      @Nullable SaveImportedGpxListener listener) {
		this.app = app;
		this.items = items;
		this.importDir = importDir;
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		if (listener != null) {
			listener.onGpxSavingStarted();
		}
	}

	@NonNull
	@Override
	protected List<String> doInBackground(Void... params) {
		//noinspection ResultOfMethodCallIgnored
		importDir.mkdirs();
		List<String> warnings = new ArrayList<>();
		if (importDir.exists() && importDir.isDirectory() && importDir.canWrite()) {
			for (ImportTrackItem trackItem : items) {
				GPXFile gpxFile = trackItem.selectedGpxFile.getGpxFile();
				gpxFile.addPoints(trackItem.selectedPoints);

				File file = new File(importDir, trackItem.name + IndexConstants.GPX_FILE_EXT);
				Exception warn = GPXUtilities.writeGpxFile(file, gpxFile);
				String error = warn != null ? warn.getMessage() : null;
				if (error != null) {
					warnings.add(error);
				} else {
					app.getSmartFolderHelper().addTrackItemToSmartFolder(new TrackItem(new File(gpxFile.path)));
				}
				if (listener != null) {
					listener.onGpxSaved(error, gpxFile);
				}
			}
		}
		return warnings;
	}

	@Override
	protected void onPostExecute(@NonNull List<String> warnings) {
		if (listener != null) {
			listener.onGpxSavingFinished(warnings);
		}
	}
}
