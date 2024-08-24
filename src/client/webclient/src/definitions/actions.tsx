import { viewComponent } from "./types"

export type action = {
    type: string,
    payload: viewAction | any
}
export type viewAction = {
    tpye: "requestViewAnswer",
    payload: {
        viewComponents: viewComponent[]
    }
}
