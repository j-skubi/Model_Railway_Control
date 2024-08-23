import { connectionStatus, controlState, state } from "../definitions/types";

function controlSelector (state: state): controlState  {
    return state.control;
}


export function selectConnectionStatus(state: state): connectionStatus {
    return controlSelector(state).connectionStatus;
}
export function selectClientID(state: state): number {
    return controlSelector(state).clientID;
}