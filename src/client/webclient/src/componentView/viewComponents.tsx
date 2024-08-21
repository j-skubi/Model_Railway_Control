import {Button, Text, TextInput} from 'react-native'
import store from '../Redux/store'
import { useState } from 'react'
import './FormStyles.css'

export type TurnoutData = {
    type: "TURNOUT",
    viewID: number,
    name: string,
    state: string,
    legalStates: string[],
    addressSpaceMappings: AddressSpaceMapping[]
}
export type AddressSpaceMapping = {
    addressSpace: string,
    stateMappings: {
        state: string,
        mapping: {
            address: number,
            mapping: number
        }[]
    }[]
}

export function Shutdown() {
    return (
        <div>
            <Button onPress={() => store.dispatch(shutdownCommand())} title='Shutdown'></Button>
        </div>
    )
}
export function Turnout(props: {viewID: number, name : string, state: string}) : JSX.Element {
    const title = "ViewID: " + props.viewID + " Name: " + props.name + " State: " + props.state;
    return (
        <div>
            <Button onPress={() => store.dispatch(createChangeStateAction(props.viewID))} title={title}></Button>
        </div>
    )
}


const AddTurnoutDataForm: React.FC = () => {
    const [viewID, setViewID] = useState<number>(0);
    const [name, setName] = useState<string>('');
    const [state, setState] = useState<string>('');
    const [legalStates, setLegalStates] = useState<string>('');
    const [addressSpaceMappings, setAddressMapping] = useState<AddressSpaceMapping[]>([]);


    const handleSubmit = () => {
        const legalStatesArray = legalStates.split(',').map(s => s.trim());

        const newTurnoutData: TurnoutData = {
            type: "TURNOUT",
            viewID,
            name,
            state,
            legalStates: legalStatesArray,
            addressSpaceMappings,
        };

        store.dispatch({type: "addComponent", payload: newTurnoutData})
    };

    return (
        <form onSubmit={e => {
            e.preventDefault();
            handleSubmit();
        }}>
            <div>
                <label>
                    View ID:
                    <input type="number" value={viewID} onChange={e => setViewID(Number(e.target.value))} />
                </label>
            </div>
            <div>
                <label>
                    Name:
                    <input type="text" value={name} onChange={e => setName(e.target.value)} />
                </label>
            </div>
            <div>
                <label>
                    State:
                    <input type="text" value={state} onChange={e => setState(e.target.value)} />
                </label>
            </div>
            <div>
                <label>
                    Legal States (comma-separated):
                    <input type="text" value={legalStates} onChange={e => setLegalStates(e.target.value)} />
                </label>
            </div>

            {/* AddressMappingForm component to handle address mappings */}
            <AddressMappingForm addressMapping={addressSpaceMappings} setAddressMapping={setAddressMapping} />

            <button type="submit">Submit</button>
        </form>
    );
};

interface AddressMappingFormProps {
    addressMapping: AddressSpaceMapping[];
    setAddressMapping: React.Dispatch<React.SetStateAction<AddressSpaceMapping[]>>;
}

const AddressMappingForm: React.FC<AddressMappingFormProps> = ({ addressMapping, setAddressMapping }) => {

    const addAddressMapping = () => {
        setAddressMapping([...addressMapping, {
            addressSpace: '',
            stateMappings: [],
        }]);
    };

    const updateAddressSpace = (index: number, value: string) => {
        const newMappings = [...addressMapping];
        newMappings[index].addressSpace = value;
        setAddressMapping(newMappings);
    };

    const addStateMapping = (index: number) => {
        const newMappings = [...addressMapping];
        newMappings[index].stateMappings.push({
            state: '',
            mapping: []
        });
        setAddressMapping(newMappings);
    };

    const updateState = (mappingIndex: number, stateIndex: number, value: string) => {
        const newMappings = [...addressMapping];
        newMappings[mappingIndex].stateMappings[stateIndex].state = value;
        setAddressMapping(newMappings);
    };

    const addAddressStateMapping = (mappingIndex: number, stateIndex: number) => {
        const newMappings = [...addressMapping];
        newMappings[mappingIndex].stateMappings[stateIndex].mapping.push({
            address: 0,
            mapping: 0
        });
        setAddressMapping(newMappings);
    };

    const updateAddressStateMapping = (mappingIndex: number, stateIndex: number, addressIndex: number, field: 'address' | 'mapping', value: number) => {
        const newMappings = [...addressMapping];
        newMappings[mappingIndex].stateMappings[stateIndex].mapping[addressIndex][field] = value;
        setAddressMapping(newMappings);
    };

    return (
        <div>
            <label>
                Address Mappings:
                <button type="button" onClick={addAddressMapping}>Add Address Space</button>
                {addressMapping.map((mapping, mappingIndex) => (
                    <div key={mappingIndex}>
                        <input
                            type="text"
                            placeholder="Address Space"
                            value={mapping.addressSpace}
                            onChange={e => updateAddressSpace(mappingIndex, e.target.value)}
                        />
                        <button type="button" onClick={() => addStateMapping(mappingIndex)}>Add State Mapping</button>
                        {mapping.stateMappings.map((stateMapping, stateIndex) => (
                            <div key={stateIndex}>
                                <input
                                    type="text"
                                    placeholder="State"
                                    value={stateMapping.state}
                                    onChange={e => updateState(mappingIndex, stateIndex, e.target.value)}
                                />
                                <button type="button" onClick={() => addAddressStateMapping(mappingIndex, stateIndex)}>Add Address Mapping</button>
                                {stateMapping.mapping.map((addressMapping, addressIndex) => (
                                    <div key={addressIndex}>
                                        <input
                                            type="number"
                                            placeholder="Address"
                                            value={addressMapping.address}
                                            onChange={e => updateAddressStateMapping(mappingIndex, stateIndex, addressIndex, 'address', Number(e.target.value))}
                                        />
                                        <input
                                            type="number"
                                            placeholder="Mapping"
                                            value={addressMapping.mapping}
                                            onChange={e => updateAddressStateMapping(mappingIndex, stateIndex, addressIndex, 'mapping', Number(e.target.value))}
                                        />
                                    </div>
                                ))}
                            </div>
                        ))}
                    </div>
                ))}
            </label>
        </div>
    );
};

export default AddTurnoutDataForm

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


function shutdownCommand() {
    return {
        type: "sendToServer",
        payload: {
            header : {
                messageType: "ServerShutdown"
            },
            body : {
                header : {
                    commandType: "ServerShutdown",
                    from: "webClient"
                },
                body : {

                }
            }
        }
    }
}

const standardTurnoutValues : TurnoutData = {
    type: "TURNOUT",
    viewID: 0,
    name: "turnout",
    state: "straight",
    legalStates: ["straight", "right"],
    addressSpaceMappings: [
        {
            addressSpace: "mock",
            stateMappings: [
                {
                    state: "straight",
                    mapping: [
                        {
                            address: 0,
                            mapping: 0
                        }
                    ]
                },
                {
                    state: "right",
                    mapping: [
                        {
                            address: 0,
                            mapping: 1
                        }
                    ]
                }
            ]
        }
    ]
}