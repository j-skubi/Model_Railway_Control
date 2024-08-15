import { configureStore } from "@reduxjs/toolkit"


interface viewComponent {
    type: string;
    viewID: number;
    name: string;
    state: string;
}

const initialState : viewComponent[] = []

function viewComponentsReducer(state = initialState, action : {type: string, payload: any}) : viewComponent[] {
    switch (action.type) {
        case "ViewComponent":
            return [...state, action.payload]
        case "CompleteView":
            return [action.payload.viewComponents]
    }
    return state
}

const dataStore = configureStore({reducer: viewComponentsReducer})