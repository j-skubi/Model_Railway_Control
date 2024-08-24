import { useSelector } from "react-redux";
import { selectLokComponents } from "../Redux/dataSelectors";
import { lokComponent } from "../definitions/types";
import './LokView.css'
import { useState } from "react";
import store from "../Redux/store";

const LokList = () => {
    const [speedGoal, setSpeedGoal] = useState(0);
    const lokComponents = useSelector(selectLokComponents);

    const onSpeedGoalChange = (goal: number, viewID: number) => {
        console.log(goal);
        setSpeedGoal(goal);
        store.dispatch({type: 'setTrainSpeed', payload: {viewType: "COMPONENT-VIEW", viewID: viewID, speed: goal}});
    }

    return (
        <div className="lok-list">
            {lokComponents.map((component, index) => (
                <div key={index} className="lok-container">
                    <h2 className="lok-name">{component.name}</h2>
                    <div className="lok-controls">
                        <div className="lok-direction" onClick={() => console.log("hi")}>
                            Direction: {component.direction}
                        </div>
                        <div className="lok-speed">
                            <label htmlFor={`speed-${component.viewID}`}>Speed: {component.speed}</label>
                            <input
                                type="range"
                                id={`speed-${component.viewID}`}
                                min="0"
                                max="1000"
                                value={speedGoal}
                                onChange={(e) => onSpeedGoalChange(parseInt(e.target.value), component.viewID)}
                            />
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
};


export default LokList;
