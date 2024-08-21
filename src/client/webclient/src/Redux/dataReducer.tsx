import { AddressSpaceMapping } from "../componentView/viewComponents";

export type ViewComponent = {
    type: string;
    viewID: number;
    name: string;
    state: string;
    addressSpaceMappings: AddressSpaceMapping;
}

const initialState : ViewComponent[] = []

export default function dataReducer(state: ViewComponent[] = initialState, action : {type: string, payload: any}) : ViewComponent[] {
    switch (action.type) {

        case "requestViewAnswer":
            return action.payload.body.viewComponents
        case "addViewComponent":
            return [...state, action.payload]
        case "notifyChange":
            return state.map(viewComp =>
                viewComp.viewID === action.payload.viewID
                    ? { ...viewComp, state: action.payload.newState }
                    : viewComp
            );

    }
    return state
}