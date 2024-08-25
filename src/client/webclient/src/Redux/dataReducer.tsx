import { changeComponentState, setTrainSpeed, toggleLokFunction } from "../RequestBuilder";
import { send } from "../WebSocket";
import { action } from "../definitions/actions";
import { dataState, viewComponent } from "../definitions/types";

const initialState : dataState = {
    visibleView : undefined,
    viewData: {
        turnoutData: [],
        lokData: []
    }
}


export function dataReducer(state: dataState = initialState, action: action): dataState {
    switch (action.type) {
        case 'requestViewAnswer': {
            return {visibleView: action.payload.metadata.type, viewData: {
                turnoutData: action.payload.viewComponents.filter((viewComp : viewComponent) => viewComp.type === "TURNOUT"),
                lokData: action.payload.viewComponents.filter((viewComp: viewComponent) => viewComp.type === "LOK")
            }}
        }
        case 'changeComponentState': {
            send(changeComponentState(action.payload.viewType, action.payload.viewID));
            return state;
        }
        case 'setTrainSpeed': {
            send(setTrainSpeed(action.payload.viewType, action.payload.viewID, action.payload.speed))
            return state;
        }
        case 'toggleLokFunction': {
            send(toggleLokFunction(action.payload.viewType, action.payload.viewID, action.payload.index))
            return state;
        }
        case 'notifyChange': {
            switch (action.payload.type) {
                case 'TURNOUT': {
                    return {
                        ...state,
                        viewData: {
                            ...state.viewData,
                            turnoutData: state.viewData.turnoutData.map(viewComp =>
                                viewComp.viewID === action.payload.viewID 
                                ? {...viewComp, state: action.payload.newState}
                                : viewComp
                            )
                        }
                    }
                }
                case 'LOK': {
                    switch (action.payload.command) {
                        case 'setTrainSpeed': {
                            return {
                                ...state,
                                viewData: {
                                    ...state.viewData,
                                    lokData: state.viewData.lokData.map(viewComp =>
                                        viewComp.viewID === action.payload.viewID 
                                        ? {...viewComp, speed: action.payload.newSpeed}
                                        : viewComp
                                    )
                                }
                            }
                        }
                        case 'setTrainDirection': {
                            return {
                                ...state,
                                viewData: {
                                    ...state.viewData,
                                    lokData: state.viewData.lokData.map(viewComp =>
                                        viewComp.viewID === action.payload.viewID 
                                        ? {...viewComp, direction: action.payload.direction}
                                        : viewComp
                                    )
                                }
                            }
                        }
                    }
                    return state;
                }
            }
        }
    }

    return state;
}