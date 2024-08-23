import { useSelector } from 'react-redux';
import store from '../Redux/store';
import './MenuBar.css';  // Import the CSS file for styling
import ToggleButton from './ToggleButton';
import { selectClientID } from '../Redux/controlSelectors';

const MenuBar = () => {
    const clientID = useSelector(selectClientID);
  return (
    <div className="menu-bar">
      <ul>
        <li><button className="menu-button" onClick={() => store.dispatch({type: 'requestView', payload: {clientID:clientID, viewType:"COMPONENT-VIEW"}})}>Component View</button></li>
      </ul>
      <div className="toggle-container">
        <ToggleButton/>
      </div>
    </div>
  );
};

export default MenuBar;
