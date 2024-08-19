
export interface viewComponent {
    type: string;
    viewID: number;
    name: string;
    state: string;
}

const initialState : viewComponent[] = []

export default function dataReducer(state = initialState, action : {type: string, payload: any}) : viewComponent[] {
    switch (action.type) {

        case "requestViewAnswer":
            return action.payload.body.viewComponents
            
        case "notifyChange":
            return state.map(viewComp =>
                viewComp.viewID === action.payload.viewID
                    ? { ...viewComp, state: action.payload.newState }
                    : viewComp
            );

    }
    return state
}