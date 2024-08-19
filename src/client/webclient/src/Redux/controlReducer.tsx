import { send } from "../WebSocket";

type ControlState = {
    clientID: number | undefined
}
const initialState = {
    clientID: undefined
}


export default function controlReducer (controlState: ControlState = initialState, action: {type: string, payload: any}) : ControlState {
    switch (action.type) {
        case 'initialMessage': {
            return {clientID: action.payload.body.clientID }
        }
        case 'sendToServer': {
            send(action.payload);
            return controlState;
        }
        default:
            return controlState
    }
}
