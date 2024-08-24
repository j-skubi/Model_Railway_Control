import store from "./Redux/store";
import { requestViewMessage } from "./definitions/protocol";



export function requestView(viewType: string, clientID: number): requestViewMessage {
    return {
        header: {
            messageType: "Request"
        },
        body: {
            header: {
                commandType: "requestView",
                clientID: clientID,
                from: "webClient"
            },
            body:{
                viewType: viewType
            }
        }
    }
}

export function changeComponentState(viewType: string, viewID: number) {
    return {
        header: {
            messageType: "ChangeState"
        },
        body: {
            header: {
                commandType: "changeState",
                from: "webClient"
            },
            body: {
                viewType: viewType,
                viewID: viewID
            }
        }
    }
}

export function setTrainSpeed(viewType: string, viewID: number, speed: number) {
    return {
        header: {
            messageType: "ChangeState"
        },
        body: {
            header: {
                commandType: "setLokSpeed",
                from: "webClient"
            },
            body: {
                viewType: viewType,
                viewID: viewID,
                speed: speed,
            }
        }
    }
}