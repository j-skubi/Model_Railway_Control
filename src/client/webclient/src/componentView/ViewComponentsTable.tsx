import { useSelector } from "react-redux";
import store from "../Redux/store";
import { selectViewComponents } from "../Redux/selectors";

import './TableStyles.css';

interface ViewComponentsTableProps {
}

const ViewComponentsTable: React.FC<ViewComponentsTableProps> = () => {

    const viewComponents = useSelector(selectViewComponents)

    const handleRowClick = (viewID: number) => {
        store.dispatch(createChangeStateAction(viewID));
    };

    return (
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
                <tr>
                    <th>Type</th>
                    <th>Name</th>
                    <th>State</th>
                </tr>
            </thead>
            <tbody>
                {viewComponents.map((component) => (
                    <tr key={component.viewID} onClick={() => handleRowClick(component.viewID)} style={{ cursor: 'pointer' }}>
                        <td>{component.type}</td>
                        <td>{component.name}</td>
                        <td>{component.state}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
};

export default ViewComponentsTable;

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