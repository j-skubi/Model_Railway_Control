
import com.google.gson.JsonParser;
import exceptions.CorruptedSaveFile;
import server.Server;

public class Main {

    private static final String testSave = """
            {
                "components": [
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
                    }
                ],
                "views": {
                    "COMPONENT-VIEW": {
                        "viewComponents": [
                            {
                                "type": "TURNOUT-VIEW",
                                "modelID": 0,
                                "viewID": 1,
                                "name": "turn1"
                            }
                        ]
                    }
                }
            }""";

    public static void main(String[] args) {
        try {
            Server server = new Server(50745, JsonParser.parseString(testSave).getAsJsonObject());
            server.run();
        } catch (CorruptedSaveFile e) {
            throw new RuntimeException(e);
        }
    }
}
