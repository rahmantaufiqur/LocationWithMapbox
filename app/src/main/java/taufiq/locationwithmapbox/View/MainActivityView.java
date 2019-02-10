package taufiq.locationwithmapbox.View;

import barikoi.barikoilocation.PlaceModels.Place;

public interface MainActivityView {
    void onDataFetchSuccess(Place reverseGeoPlaceModel);
    void onDataFetchError(String Error);
}
