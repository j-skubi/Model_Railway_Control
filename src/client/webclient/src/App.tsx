import React from 'react';
import { Button } from 'react-native'

import './App.css';
import { connect,send } from './WebSocket';
import { controlStore } from './Stores/ControlStore';

connect()

function CustomButton() {
  const clickOnButton = () => {
    console.log(controlStore.getState().clientID)
    send(value)}
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

const value = {
  header: {
      messageType: "Request",
      clientID: (controlStore.getState().clientID)!
  },
  body: {
    text: "test"
  }
}