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
              "legalStates" : [
                "straight",
                "left"
              ],
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
