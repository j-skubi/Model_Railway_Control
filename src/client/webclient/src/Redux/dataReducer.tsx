import { changeComponentState, setTrainSpeed } from "../RequestBuilder";
import { send } from "../WebSocket";
import { action } from "../definitions/actions";
import { dataState } from "../definitions/types";

const initialState : dataState = {
    visibleView : undefined,
    viewData: {
        viewComponents: []
    }
}


export function dataReducer(state: dataState = initialState, action: action): dataState {
    switch (action.type) {
        case 'requestViewAnswer': {
            return {visibleView: action.payload.metadata.type, viewData: {viewComponents: action.payload.viewComponents}}
        }
        case 'changeComponentState': {
            send(changeComponentState(action.payload.viewType, action.payload.viewID));
            return state;
        }
        case 'setTrainSpeed': {
            send(setTrainSpeed(action.payload.viewType, action.payload.viewID, action.payload.speed))
            return state;
        }
        case 'notifyChange': {
            return {
                ...state,
                viewData: {
                    ...state.viewData,
                    viewComponents: state.viewData.viewComponents.map(viewComp =>
                        viewComp.viewID === action.payload.viewID 
                        ? {...viewComp, state: action.payload.newState}
                        : viewComp
                    )
                }
            }
        }
    }

    return state;
}