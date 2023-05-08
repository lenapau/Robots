package logic;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LocalConfig implements IObjectState {

    @Override
    public void save(JSONObject config) {
            // Save JSON to user home directory
            String userHomeDir = System.getProperty("user.home");
            File robotsDir = new File(userHomeDir + "/.Robots");
            if (!robotsDir.exists()) {
                robotsDir.mkdir();
            }
            File configFile = new File(userHomeDir + "/.Robots/config.json");
            try (FileWriter fileWriter = new FileWriter(configFile.getAbsolutePath())) {
                fileWriter.write(config.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public JSONObject load() {
        File configFile = new File(System.getProperty("user.home") + "/.Robots/config.json");
        JSONParser parser = new JSONParser();
        if (configFile.exists()) {
            try {
                return (JSONObject) parser.parse(new FileReader(configFile));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
