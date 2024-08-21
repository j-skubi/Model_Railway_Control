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
        case 'addComponent': {
            send(addComponentCommand(action.payload))
            return controlState;
        }
        case 'sendToServer': {
            send(action.payload);
            return controlState;
        }
        default:
            return controlState
    }
}

function addComponentCommand(payload: any) {
    return {
        header: {
            messageType: "Edit"
        },
        body: {
            header: {
                commandType: "addViewComponent",
                from: "webClient",
                to: "broadcast"
            },
            body: {
                viewType: "COMPONENT-VIEW",
                component: payload
            }
        }
    }
}