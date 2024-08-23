import React, { useEffect } from 'react';
import './loadingSpinner.css';  // Import the CSS file for styling

const LoadingSpinner = () => {
  useEffect(() => {
    const styleSheet = document.styleSheets[0];
    const keyframes =
      `@keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
      }`;
    styleSheet.insertRule(keyframes, styleSheet.cssRules.length);
  }, []);

  return (
    <div className="loadingSpinner-container">
      <div className="loadingSpinner"></div>
      <p className="connecting-text">connecting...</p>
    </div>
  );
};

export default LoadingSpinner;
