
import './App.css';
import { connect } from './WebSocket';
import { Provider, useSelector } from 'react-redux';
import { selectConnectionStatus } from './Redux/controlSelectors';

import LoadingSpinner from './MenuComponents/LoadingSpinner';
import store from './Redux/store';
import MenuBar from './MenuComponents/MenuBar';
import { connectionStatus } from './definitions/types';
import { selectVisibleViewType } from './Redux/dataSelectors';
import ComponentContainer from './componentView/componentContainer';


connect()

function Application() {
  const connectionStatus: connectionStatus = useSelector(selectConnectionStatus);
  const viewType: string | undefined = useSelector(selectVisibleViewType);
  var view = <></>;
  switch(viewType) {
    case "COMPONENT-VIEW": {view = 
    <div>
      <ComponentContainer></ComponentContainer>
    </div>;
    break;}
    case undefined: {view = <></>}
  }

  if (connectionStatus === "connected") {
    return (
      <div className="app-container">
        <MenuBar />
        <div className="content-container">
            {view}
        </div>
      </div>
    );
  } else {
    return (
      <LoadingSpinner></LoadingSpinner>
    )
  }
}

function App() {
  console.log("render");
  
  return (
    <Provider store={store}>
      <Application></Application>
    </Provider>
  )

}

export default App;