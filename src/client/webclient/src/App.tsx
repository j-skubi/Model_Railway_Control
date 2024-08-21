import React from 'react';
import { Button } from 'react-native'

import './App.css';
import { connect,send } from './WebSocket';
import store from './Redux/store';
import AddTurnoutDataForm, { Shutdown, Turnout } from './componentView/viewComponents';
import { Provider, useSelector } from 'react-redux';
import { viewComponent } from './Redux/dataReducer';

connect()

function CustomButton() {
  const clickOnButton = () => { 
    send(value())
  }
  return (
      <Button onPress={clickOnButton} title="Press to Send"></Button>
  );
}

function selectViewComponents (state: {data: viewComponent[], control: any}) {
  return state.data;
}

const dynamicText : string = "This is dynamic Text"

function Menu() {
  const data = useSelector(selectViewComponents);
  const rows : React.ReactElement[] = [];

  data.forEach(viewComponent => {
    console.log(viewComponent);
    rows.push(<Turnout key={viewComponent.viewID} viewID={viewComponent.viewID} name={viewComponent.name} state={viewComponent.state}></Turnout>);
  });


  return (
    <div className="App">
      {rows}
      <Shutdown></Shutdown>
    </div>
  );
}

function App() {
  console.log("render");
  return (
    <Provider store={store}>
      <CustomButton></CustomButton>
      <Menu></Menu>
      <AddTurnoutDataForm/>
    </Provider>
  )

}

export default App;

function value (): any {

if (store.getState().control.clientID === undefined)
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
      clientID: store.getState().control.clientID
  },
  body: {
    header: {
      commandType: "requestView",
      from: "webClient",
      to: "server",
      clientID: store.getState().control.clientID
    },
    body: {
      viewType: "COMPONENT-VIEW"
    }
  }
}
}