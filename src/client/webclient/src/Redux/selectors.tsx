import { ViewComponent } from "./dataReducer";


export function selectViewComponents (state: {data: ViewComponent[], control: any}) {
    return state.data;
  }