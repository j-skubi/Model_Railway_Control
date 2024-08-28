import { useSelector } from "react-redux";
import { selectSensorComponents } from "../Redux/dataSelectors";
import { sensorComponent } from "../definitions/types";
import { useEffect, useState } from "react";
import './SensorList.css'

const SensorList = () => {
    // Select the sensors from the Redux store
    const sensors = useSelector(selectSensorComponents);

    return (
        <table className="sensor-table">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                {sensors.map(sensor => (
                    <SensorRow key={sensor.viewID} sensor={sensor} />
                ))}
            </tbody>
        </table>
    );
};

const SensorRow = (props: { sensor: sensorComponent }) => {
    const [isBlinking, setIsBlinking] = useState(false);

    useEffect(() => {
        if (props.sensor.isOccupied) {
            setIsBlinking(true);
            const timer = setTimeout(() => {
                setIsBlinking(false);
            }, 1000); // Blink duration (1 second)

            return () => clearTimeout(timer); // Clean up the timer on unmount
        }
    }, [props.sensor.isOccupied]);

    return (
        <tr id={`sensor-${props.sensor.viewID}`} className={`sensor-row ${isBlinking ? 'blink' : ''}`}>
            <td className="sensor-name">{props.sensor.name}</td>
            <td className="sensor-status">{props.sensor.isOccupied ? 'Occupied' : 'Not Occupied'}</td>
        </tr>
    );
};

export default SensorList;
