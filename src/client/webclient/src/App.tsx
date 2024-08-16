import React from 'react';
import { Button } from 'react-native'

import './App.css';
import { connect,send } from './WebSocket';
import controlStore from './Stores/ControlStore';

connect()

function CustomButton() {
  const clickOnButton = () => { 
    send(value())
  }
  return (
      <Button onPress={clickOnButton} title="Press to Send"></Button>
  );
}

function App() {
  return (
    <div className="App">
      <CustomButton></CustomButton>
    </div>
  );
}

export default App;

function value (): any {

if (controlStore.getState().clientID === undefined)
{
    return {
      header: {
        messageType: "Ignore"
      }
    }
}
return {
  header: {
      messageType: "Request",
      clientID: controlStore.getState().clientID
  },
  body: {
    header: {
      commandType: "requestView",
      from: "webClient",
      to: "server",
      clientID: controlStore.getState().clientID
    },
    body: {
      viewType: "COMPONENT-VIEW"
    }
  }
}
}