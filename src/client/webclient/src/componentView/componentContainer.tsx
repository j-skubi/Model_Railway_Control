import React from 'react';
import TurnoutList from './TurnoutList';
import LokComponentList from './LokView';
import SensorList from './SensorList';
import './componentContainer.css';

const ComponentContainer = () => {
    return (
        <div className="component-container">
            <div className="lok-wrapper">
                <LokComponentList />
            </div>
            <div className="turnout-wrapper">
                <TurnoutList />
            </div>
            <div className="sensor-wrapper">
                <SensorList />
            </div>
        </div>
    );
};

export default ComponentContainer;