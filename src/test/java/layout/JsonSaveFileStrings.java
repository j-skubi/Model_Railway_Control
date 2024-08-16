package layout;

public class JsonSaveFileStrings {
    public static final String TestComponentViews = """
            {
                "metadata": {
                    "type": "COMPONENT-VIEW"
                },
                "viewComponents": [
                    {
                        "type": "TURNOUT-VIEW",
                        "modelID": 0,
                        "viewID": 0,
                        "name": "turn1"
                    }
                ]
            }
            """;
    public static final String TestTurnout = """
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
