import { dataState, lokComponent, state, turnoutComponent, viewComponent } from "../definitions/types";


export function selectVisibleViewType(state: state) : string | undefined {
    return state.data.visibleView;
}
export function selectViewComponents(state: state): viewComponent[] {
    return state.data.viewData.viewComponents;
}
export function selectTurnoutComponents(state: state): turnoutComponent[] {
    return state.data.viewData.viewComponents.filter(component => component.type === "TURNOUT") as turnoutComponent[];
}
export function selectLokComponents(state: state): lokComponent[] {
    return state.data.viewData.viewComponents.filter(component => component.type === "LOK") as lokComponent[];
}