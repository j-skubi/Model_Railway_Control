
export type messageHeader = {
    messageType: string,
}

export type initalMessage = {
    header: messageHeader
    body: {
        header: {
            commandType: "initalMessage",
            from: string,
            to: number
        }
        body: {
            clientID: number
        }

    }
}

export type requestViewMessage = {
    header: messageHeader
    body: {
        header: {
            commandType: "requestView"
            from: "webClient",
            clientID: number
        },
        body: {
            viewType: string,
        }
    }
}