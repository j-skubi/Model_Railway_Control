import { useSelector } from "react-redux";
import { selectLokComponents } from "../Redux/dataSelectors";
import { lokComponent } from "../definitions/types";
import './LokView.css'
import { useState } from "react";
import store from "../Redux/store";

const Lok = (props: {component: lokComponent, index:number}) => {
    const [speedGoal, setSpeedGoal] = useState(0);

    const onSpeedGoalChange = (goal: number, viewID: number) => {
        setSpeedGoal(goal);
        store.dispatch({type: 'setTrainSpeed', payload: {viewType: "COMPONENT-VIEW", viewID: viewID, speed: goal}});
    }
    return (
        <div key={props.index} className="lok-container">
            <h2 className="lok-name">{props.component.name}</h2>
            <div className="lok-controls">
                <div className="lok-direction" onClick={() => console.log("hi")}>
                    Direction: {props.component.direction}
                </div>
                <div className="lok-speed">
                    <label htmlFor={`speed-${props.component.viewID}`}>Speed: {props.component.speed}</label>
                    <input
                        type="range"
                        id={`speed-${props.component.viewID}`}
                        min="0"
                        max="1000"
                        value={speedGoal}
                        onChange={(e) => onSpeedGoalChange(parseInt(e.target.value), props.component.viewID)}
                    />
                </div>
            </div>
        </div>
    )
}
const LokList = () => {
    const lokComponents = useSelector(selectLokComponents);

    return (
        <div className="lok-list">
            {lokComponents.map((component, index) => (
                <Lok component={component} index={index}></Lok>
            ))}
        </div>
    );
};


export default LokList;
