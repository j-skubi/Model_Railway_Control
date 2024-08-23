import store from "./Redux/store";

let ws: WebSocket | undefined = undefined

export function connect () {
    ws =  new WebSocket("ws://192.168.178.52:50745");

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
        case "initialMessage": {
            store.dispatch({type: message.body.header.commandType, payload: message.body.body});
            break;
        }
        case "RequestAnswer": {
            store.dispatch({type: message.body.header.commandType, payload: message.body.body});
            break;
        }
        case "notifyChange": {
            store.dispatch({type: message.body.header.commandType, payload: message.body.body})
            break;
        }
    }

    console.log(store.getState());
}


