import { useState } from "react"
import Header from './components/Header'
import Tasks from './components/Tasks'
import AddTask from "./components/AddTask"

function App() {

  const [showAddTask, setShowAddTask] = useState
  (false)

  const [tasks, setTasks] = useState([
    {
        id: 1,
        text: 'Grocery Shopping',
        day: 'Sept 23th at 12pm',
        reminder: true
    },
    {
        id: 2,
        text: 'Job Interview',
        day: 'Sept 30th at 1:30pm',
        reminder: false
    },
    {
        id: 3,
        text: 'Power bill due',
        day: 'Oct 1st 12:00am',
        reminder: true
    }
])
//adding task
const addTask = (task) => {
  const id = Math.floor(Math.random() * 10000) + 1
  const newTask = {id, ...task}
  setTasks([...tasks, newTask])
}

//Delete Task

const deleteTask =  (id) => {

  setTasks(tasks.filter((task)=>  task.id !==id),)

}

//Toggle Reminder
const toggleReminder = (id) => {
  setTasks(tasks.map((task) => task.id === id ? 
  {...task,reminder: !task.reminder} : task))
}

  return (
    <div className="Container">
      
        <Header  onAdd={() => setShowAddTask
          (!showAddTask) }showAdd={showAddTask}/>

        {showAddTask &&<AddTask onAdd ={addTask}/>}
        {tasks.length > 0 ? (
        <Tasks tasks ={tasks} 
        onDelete = {deleteTask}
        onToggle = {toggleReminder}
        />
        ) : ('No Tasks To Show')}
    </div>
  );
}

export default App;
