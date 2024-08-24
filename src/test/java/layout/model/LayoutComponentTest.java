package layout.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;

import static org.junit.jupiter.api.Assertions.*;

class LayoutComponentTest {
    MockLayoutComponent mockLayoutComponent;
    @BeforeEach
    public void generateLayoutComponent() {
        mockLayoutComponent = new MockLayoutComponent(JsonParser.parseString(jsonString).getAsJsonObject());
    }
    @Test
    void testSaveJSON() {
        JsonObject oldJSON = JsonParser.parseString(jsonString).getAsJsonObject();
        JsonObject newJSON = mockLayoutComponent.save();

        assertEquals(oldJSON,newJSON);
    }
    @Test
    void testCalculateKey() {
        assertEquals(mockLayoutComponent.id, mockLayoutComponent.calculateKey());
    }

    private static class MockLayoutComponent extends MagnetArticle {

        public MockLayoutComponent(JsonObject json) {
            super(json);
        }

        @Override
        public void notifyChange(JsonObject command, PriorityBlockingQueueWrapper<Command> queue) {

        }
    }
    private static final String jsonString = """
            {
              "type": "MOCK",
              "id": 0,
              "addressSpaceMappings": [
                {
                  "addressSpace": "cs3",
                  "stateMappings": [
                    {
                      "state": "straight",
                      "mapping": [
                        {
                          "address": 16,
                          "mapping": 1
                        },
                        {
                          "address": 17,
                          "mapping": 1
                        }
                      ]
                    },
                    {
                      "state": "turnout",
                      "mapping": [
                        {
                          "address": 16,
                          "mapping": 1
                        }
                      ]
                    }
                  ]
                },
                {
                  "addressSpace": "virtual",
                  "stateMappings": [
                    {
                      "state": "straight",
                      "mapping": [
                        {
                          "address": 16,
                          "mapping": 1
                        }
                      ]
                    },
                    {
                      "state": "turnout",
                      "mapping": [
                        {
                          "address": 16,
                          "mapping": 1
                        }
                      ]
                    }
                  ]
                }
              ]
            }""";
}