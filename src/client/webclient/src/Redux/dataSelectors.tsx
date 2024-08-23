import { dataState, state, viewComponent } from "../definitions/types";


export function selectVisibleViewType(state: state) : string | undefined {
    return state.data.visibleView;
}
export function selectViewComponents(state: state): viewComponent[] {
    return state.data.viewData.viewComponents;
}
