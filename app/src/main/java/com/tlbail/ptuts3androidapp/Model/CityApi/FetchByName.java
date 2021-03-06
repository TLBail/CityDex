package com.tlbail.ptuts3androidapp.Model.CityApi;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.tlbail.ptuts3androidapp.Model.City.CityData;
import com.tlbail.ptuts3androidapp.Model.CityApi.Department;
import com.tlbail.ptuts3androidapp.Model.CityApi.FetchCity;
import com.tlbail.ptuts3androidapp.Model.CityApi.Region;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FetchByName extends FetchCity {

    public FetchByName(FetchCityListener fecthCityListener, String name){
        super(fecthCityListener);
        if(name != null)
            this.request = "https://geo.api.gouv.fr/communes?nom=" + name +"&fields=nom,population,centre,surface,codeDepartement,region";
    }

    @Override
    public CityData transformJsonElementToCityObject(JsonElement jsonElement) {
        JsonObject cityJsonObject = jsonElement.getAsJsonObject();
        String name = cityJsonObject.get("nom").getAsString();
        Department department = Department.valueOfByCode(cityJsonObject.get("codeDepartement").getAsString());
        Region region = Region.valueOfByName(cityJsonObject.get("region").getAsJsonObject().get("nom").getAsString());
        float surface = cityJsonObject.get("surface").getAsFloat();
        int inhabitants = (cityJsonObject.get("population") == null) ? 0 : cityJsonObject.get("population").getAsInt();

        JsonArray jsonArrayCoord = cityJsonObject.get("centre").getAsJsonObject().get("coordinates").getAsJsonArray();
        double latitude = jsonArrayCoord.get(1).getAsDouble();
        double longitude = jsonArrayCoord.get(0).getAsDouble();
        LatLng latLng = new LatLng(latitude, longitude);

        return  new CityData(name, department, region, surface, inhabitants, latLng);


    }
}
