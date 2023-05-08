package logic;

import org.json.simple.JSONObject;

public interface IObjectState {
    public void save(JSONObject config);
    public JSONObject load();
}
