import { combineReducers } from "redux";
import { configureStore } from "@reduxjs/toolkit";
import { controlReducer } from "./controlReducer";
import { dataReducer } from "./dataReducer";

const rootReducer = combineReducers({
    control: controlReducer,
    data: dataReducer
});

const store = configureStore({
    reducer: rootReducer
});

export default store;