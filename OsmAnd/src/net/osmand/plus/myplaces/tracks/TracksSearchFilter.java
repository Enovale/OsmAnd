package net.osmand.plus.myplaces.tracks;

import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.osmand.CallbackWithObject;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.configmap.tracks.TrackItem;
import net.osmand.plus.myplaces.tracks.filters.BaseTrackFilter;
import net.osmand.plus.myplaces.tracks.filters.CityTrackFilter;
import net.osmand.plus.myplaces.tracks.filters.DateCreationTrackFilter;
import net.osmand.plus.myplaces.tracks.filters.TrackFiltersSettingsCollection;
import net.osmand.plus.myplaces.tracks.filters.TrackNameFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TracksSearchFilter extends Filter implements TrackFiltersSettingsCollection.FiltersSettingsListener {

	private final List<TrackItem> trackItems;
	private CallbackWithObject<List<TrackItem>> callback;
	private TrackFiltersSettingsCollection tracksFilterCollection;

	public TracksSearchFilter(@NonNull OsmandApplication app, @NonNull List<TrackItem> trackItems) {
		this.trackItems = trackItems;
		tracksFilterCollection = new TrackFiltersSettingsCollection(app);
		tracksFilterCollection.addListener(this);
		DateCreationTrackFilter dateFilter = tracksFilterCollection.getDateFilter();
		if (dateFilter != null) {
			long minDate = app.getGpxDbHelper().getTracksMinLastModifyDate();
			long now = (new Date()).getTime();
			dateFilter.setInitialValueFrom(minDate);
			dateFilter.setInitialValueTo(now);
			dateFilter.setValueFrom(minDate);
			dateFilter.setValueTo(now);
		}
		CityTrackFilter cityFilter = tracksFilterCollection.getCityFilter();
		if (cityFilter != null) {
			cityFilter.setFullCitiesList(app.getGpxDbHelper().getNearestCityList());
		}
	}

	public void setCallback(@Nullable CallbackWithObject<List<TrackItem>> callback) {
		this.callback = callback;
	}

	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		FilterResults results = new FilterResults();
		int filterCount = getAppliedFiltersCount();
		if (filterCount == 0) {
			results.values = trackItems;
			results.count = trackItems.size();
		} else {
			List<TrackItem> res = new ArrayList<>();
			for (TrackItem item : trackItems) {
				boolean needAddTrack = true;
				for (BaseTrackFilter filter : tracksFilterCollection.getCurrentFilters()) {
					if (filter.getEnabled() && filter.isTrackOutOfFilterBounds(item)) {
						needAddTrack = false;
						break;
					}
				}
				if (needAddTrack) {
					res.add(item);
				}
			}
			results.values = res;
			results.count = res.size();
		}
		return results;
	}

	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		if (callback != null) {
			callback.processResult((List<TrackItem>) results.values);
		}
	}

	public int getAppliedFiltersCount() {
		int appliedFiltersCount = 0;
		for (BaseTrackFilter filter :
				tracksFilterCollection.getCurrentFilters()) {
			if (filter.getEnabled()) {
				appliedFiltersCount++;
			}
		}
		return appliedFiltersCount;
	}

	@NonNull
	public List<BaseTrackFilter> getCurrentFilters() {
		return tracksFilterCollection.getCurrentFilters();
	}

	@Override
	public void onFilterChanged() {
		TrackNameFilter nameFilter = getNameFilter();
		if (nameFilter != null) {
			filter(nameFilter.getValue());
		}
	}

	public TrackNameFilter getNameFilter() {
		return tracksFilterCollection.getNameFilter();
	}

	public void addFiltersChangedListener(TrackFiltersSettingsCollection.FiltersSettingsListener listener) {
		tracksFilterCollection.addListener(listener);
	}

	public void resetCurrentFilters() {
		tracksFilterCollection.resetCurrentFilters();
	}
}

