import { requestView } from "../RequestBuilder";
import { send } from "../WebSocket";
import { action } from "../definitions/actions";
import { controlState } from "../definitions/types";



const initalControlState: controlState = {
    clientID: -1,
    connectionStatus: "loading",
    viewType: undefined,
    editMode: false
}


export function controlReducer (state: controlState = initalControlState, action: action): controlState {
    switch (action.type) {
        case 'initialMessage': {
            return {...state, clientID: action.payload.clientID, connectionStatus: "connected"}
        }
        case 'requestView': {
            send(requestView(action.payload.viewType, action.payload.clientID));
            return state;
        }
    }
    return state;
}