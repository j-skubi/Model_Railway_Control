
export function connect () : void {
    const ws =  new WebSocket("ws://localhost:50745")

    ws.onopen = onOpen;
    ws.onmessage = onMessage;


}

function onOpen(event: any): void {
    console.log("connected");

}
function onMessage (messageEvent : any) {
    console.log(messageEvent);
    if (messageEvent.data.message === "Welcome") {
        console.log(messageEvent.data)
    }
}