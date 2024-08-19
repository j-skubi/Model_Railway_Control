import store from "./Redux/store";

let ws: WebSocket | undefined = undefined

export function connect () {
    ws =  new WebSocket("ws://localhost:50745");

    ws.onopen = onOpen;
    ws.onmessage = onMessage;

}

export function send (json: any): void {
    ws?.send(JSON.stringify(json));
}
function onOpen(event: any): void {
    console.log("connected");
}

function onMessage (messageEvent : MessageEvent) {
    console.log(messageEvent);

    const message = JSON.parse(messageEvent.data);
    switch (message.header.messageType) {
        case "controlData": {
            store.dispatch({type: message.body.header.commandType, payload: message.body});
            break;
        }
        case "RequestAnswer": {
            store.dispatch({type: message.body.header.commandType, payload: message.body});
        }
    }

    console.log(store.getState());
}

