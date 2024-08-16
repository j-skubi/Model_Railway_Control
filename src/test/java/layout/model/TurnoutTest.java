package layout.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import layout.JsonSaveFileStrings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TurnoutTest {
    Turnout turnout;
    @BeforeEach
    void setup() {
        turnout = new Turnout(JsonParser.parseString(JsonSaveFileStrings.TestTurnout).getAsJsonObject());
    }
    @Test
    void save() {
        JsonObject oldJSON = JsonParser.parseString(JsonSaveFileStrings.TestTurnout).getAsJsonObject();
        JsonObject newJSON = turnout.save();

        assertEquals(oldJSON, newJSON);
    }

}