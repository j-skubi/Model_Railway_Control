
export interface viewComponent {
    type: string;
    viewID: number;
    name: string;
    state: string;
}

const initialState : viewComponent[] = []

export default function dataReducer(state = initialState, action : {type: string, payload: any}) : viewComponent[] {
    switch (action.type) {
        case "ViewComponent":
            return [...state, action.payload]
        case "requestViewAnswer":
            return action.payload.body.viewComponents
    }
    return state
}