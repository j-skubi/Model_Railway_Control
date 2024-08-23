


export type state = {
    data: dataState,
    control: controlState
}

export type controlState = {
    connectionStatus: connectionStatus,
    clientID: number,
    viewType: string | undefined,
    editMode: boolean
}

export type connectionStatus = "loading" | "connected"

export type dataState = {
    visibleView: undefined | string
    viewData: {
        viewComponents: viewComponent[]
    }
}

export type viewComponent = turnoutComponent

export type basicViewComponent = {
    viewID: number,
    name: string,
    state: string,
    legalStates: string[],
    addressSpaceMappings: addressSpaceMapping[] | undefined
}

export type turnoutComponent = basicViewComponent & {
    type: "TURNOUT",
}

export type addressSpaceMapping = {
    addressSpace: string,
    stateMappings: {
        state: string,
        mapping: {
            address: number,
            mapping: number
        }[]
    }[]
}