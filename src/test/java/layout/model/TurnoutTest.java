package layout.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TurnoutTest {
    Turnout turnout;
    @BeforeEach
    void setup() {
        turnout = new Turnout(JsonParser.parseString(jsonObject).getAsJsonObject());
    }
    @Test
    void save() {
        JsonObject oldJSON = JsonParser.parseString(jsonObject).getAsJsonObject();
        JsonObject newJSON = turnout.save();

        assertEquals(oldJSON, newJSON);
    }
    private static final String jsonObject = """
            {
              "type": "TURNOUT",
              "id": 0,
              "LegalStates" : [
                "straight",
                "left"
              ],
              "AddressSpaceMappings": [
                {
                  "AddressSpace": "cs3",
                  "StateMappings": [
                    {
                      "State": "straight",
                      "Mapping": [
                        {
                          "Address": 16,
                          "Mapping": 1
                        },
                        {
                          "Address": 17,
                          "Mapping": 1
                        }
                      ]
                    },
                    {
                      "State": "turnout",
                      "Mapping": [
                        {
                          "Address": 16,
                          "Mapping": 1
                        }
                      ]
                    }
                  ]
                },
                {
                  "AddressSpace": "virtual",
                  "StateMappings": [
                    {
                      "State": "straight",
                      "Mapping": [
                        {
                          "Address": 16,
                          "Mapping": 1
                        }
                      ]
                    },
                    {
                      "State": "turnout",
                      "Mapping": [
                        {
                          "Address": 16,
                          "Mapping": 1
                        }
                      ]
                    }
                  ]
                }
              ]
            }""";
}