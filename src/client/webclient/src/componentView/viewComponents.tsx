import {Text} from 'react-native'
import store from '../Redux/store'

export function Turnout(props: {viewID: number, name : string, state: string}) {
    console.log(props.name)
    return (
        <div>
            <Text onPress={() => store.dispatch(createChangeStateAction(props.viewID))}> {props.viewID} {props.name} {props.state}</Text>
        </div>
    )
}


function createChangeStateAction(viewID: number) : any{ 
return {
    type: "a",
    payload: {
    header : {
        messageType: "StateChange"
    },
    body : {
        header : {
            commandType: "changeState",
            clientID: store.getState().control.clientID,
            from: "webClient",
            to: "model"
        },
        body : {
            componentID: viewID
        }
    }
}
}
}