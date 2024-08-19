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

    private static class MockLayoutComponent extends LayoutComponent {

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