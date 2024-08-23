import { useState } from "react";
import './ToggleButton.css'

const ToggleButton = () => {
    const [isToggled, setIsToggled] = useState(false);
  
    const handleToggle = () => {
      setIsToggled(!isToggled);
    };
  
    return (
      <div className={`toggle-button ${isToggled ? 'toggled' : ''}`} onClick={handleToggle}>
        <div className="toggle-thumb"></div>
      </div>
    );
  };
  
  export default ToggleButton;