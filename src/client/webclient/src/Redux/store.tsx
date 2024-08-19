import { combineReducers } from "redux";
import { configureStore } from "@reduxjs/toolkit";
import controlReducer from "./controlReducer";
import dataReducer from "./dataReducer";

const rootReducer = combineReducers({
    data: dataReducer,
    control: controlReducer
});

const store = configureStore({
    reducer: rootReducer
});

export default store;