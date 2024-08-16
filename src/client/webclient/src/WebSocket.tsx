import controlStore from "./Stores/ControlStore";

let ws: WebSocket | undefined = undefined

export function connect () {
    ws =  new WebSocket("ws://localhost:50745")

    ws.onopen = onOpen;
    ws.onmessage = onMessage;

}

export function send (json: any): void {
    ws?.send(JSON.stringify(json))
}
function onOpen(event: any): void {
    console.log("connected");
}
function onMessage (messageEvent : any) {
    console.log(messageEvent)

    const data = JSON.parse(messageEvent.data)
    switch (data.store) {
        case "control":
            controlStore.dispatch(data.action)
    }
    console.log(controlStore.getState())
}