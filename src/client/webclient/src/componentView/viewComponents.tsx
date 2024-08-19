import {Button, Text} from 'react-native'
import store from '../Redux/store'

export function Turnout(props: {viewID: number, name : string, state: string}) {
    const title = "ViewID: " + props.viewID + " Name: " + props.name + " State: " + props.state;
    return (
        <div>
            <Button onPress={() => store.dispatch(createChangeStateAction(props.viewID))} title={title}></Button>
        </div>
    )
}


function createChangeStateAction(viewID: number) : any{ 
return {
    type: "sendToServer",
    payload: {
    header : {
        messageType: "ChangeState"
    },
    body : {
        header : {
            commandType: "changeState",
            clientID: store.getState().control.clientID,
            from: "webClient",
            to: "model"
        },
        body : {
            viewType: "COMPONENT-VIEW",
            viewID: viewID
        }
    }
}
}
}