package utils.teamcity.controller.api.json;

import com.google.gson.annotations.SerializedName;

/**
 * Date: 23/02/14
 *
 * @author Cedric Longo
 */
public final class Project {

    @SerializedName("id")
    private String _id;

    @SerializedName("name")
    private String _name;

    public String getId( ) {
        return _id;
    }

    public String getName( ) {
        return _name;
    }
}