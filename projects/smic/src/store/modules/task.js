import Vue from 'vue'
import task from '@/data/taskGroup'

export default {
  state: {
    taskList: task.tableData,
  },
  getters: {
    getTaskList(state) {
      return state.taskList
    },
  },
  mutations: {
    TASK_LIST(state, list) {
      state.taskList = list
    },
  },
  actions: {},
}
