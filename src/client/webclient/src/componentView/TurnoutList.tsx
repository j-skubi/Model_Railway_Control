import { useSelector } from "react-redux";
import { turnoutComponent, viewComponent } from "../definitions/types";
import { selectViewComponents } from "../Redux/dataSelectors";
import store from "../Redux/store";
import './TurnoutList.css'



const TurnoutList = () => {
    const components = useSelector(selectViewComponents);
    const turnoutComponents = components.filter(component => component.type === "TURNOUT") as turnoutComponent[];


    return (
        <table className="turnout-table">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>State</th>
                </tr>
            </thead>
            <tbody>
                {turnoutComponents.map((component, index) => (
                    <tr key={index} onClick={() => store.dispatch({type: 'changeComponentState', payload: {viewType: "COMPONENT-VIEW", viewID: component.viewID}})} className="turnout-row">
                        <td>{component.name}</td>
                        <td>{component.state}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
};

export default TurnoutList;