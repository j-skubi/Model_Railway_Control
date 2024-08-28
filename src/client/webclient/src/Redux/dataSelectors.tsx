import { lokComponent, lokFunction, sensorComponent, state, turnoutComponent } from "../definitions/types";


export function selectVisibleViewType(state: state) : string | undefined {
    return state.data.visibleView;
}
export function selectTurnoutComponents(state: state): turnoutComponent[] {
    return state.data.viewData.turnoutData;
}
export function selectLokComponents(state: state): lokComponent[] {
    return state.data.viewData.lokData;
}
export function selectSensorComponents(state: state): sensorComponent[] {
    return state.data.viewData.sensorData;
}