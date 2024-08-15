import { configureStore } from "@reduxjs/toolkit";

type ControlState = {
    clientID: number | undefined
}
const initialState = {
    clientID: undefined
}


function controlServerMessageReducer (controlState: ControlState = initialState, action: {type: string, payload: any}) : ControlState {
    switch (action.type) {
        case 'initialMessage': {
            return {clientID: action.payload.clientID }
        }
        default:
            return controlState
    }
}

export const controlStore = configureStore({reducer: controlServerMessageReducer});