package taufiq.locationwithmapbox.Presenter;

import com.mapbox.mapboxsdk.geometry.LatLng;

import barikoi.barikoilocation.PlaceModels.Place;
import taufiq.locationwithmapbox.Model.MainActivityInteractor;
import taufiq.locationwithmapbox.View.MainActivityView;

public class MainActivityPresenter implements MainActivityInteractor.OnFetchDataListener{
    MainActivityView mainActivityView;
    MainActivityInteractor mainActivityInteractor;

    public MainActivityPresenter(MainActivityView mainActivityView, MainActivityInteractor mainActivityInteractor){
        this.mainActivityView=mainActivityView;
        this.mainActivityInteractor=mainActivityInteractor;
    }
    public void reverseGeoCode(LatLng point){
        if(mainActivityView!=null){
            mainActivityInteractor.getReverseAddress(point,this);
        }
    }
    public void onDestroy() {
        mainActivityView = null;
    }
    @Override
    public void onDataFetchSuccess(Place reverseGeoPlaceModel) {
        if(mainActivityView!=null){
            mainActivityView.onDataFetchSuccess(reverseGeoPlaceModel);
        }
    }

    @Override
    public void onDataFetchError(String error) {
        if(mainActivityView!=null){
            mainActivityView.onDataFetchError(error);
        }
    }
}
