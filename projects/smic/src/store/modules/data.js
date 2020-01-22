import issueData from '@/data/issue'
import currentReportedDetailProcessData from '@/data/currentReportedDetailProcess'
import currentReportedProcessData from '@/data/currentReportedProcess'
import sceneGroupData from '@/data/sceneGroup'

export default {
	state: {
		issue: issueData,
		currentReportedDetailProcess: currentReportedDetailProcessData,
		currentReportedProcess: currentReportedProcessData,
		sceneGroup: sceneGroupData,
	},
	getters: {
		issue(state) {
			return state.issue
		},
		currentReportedDetailProcess(state) {
			return state.currentReportedDetailProcess
		},
		currentReportedProcess(state) {
			return state.currentReportedProcess
		},
		sceneGroup(state) {
			return state.sceneGroup
		},
	},
	mutations: {
		set_issue(state, data) {
			state.issue = data
		},
		set_currentReportedDetailProcess(state, data) {
			state.currentReportedDetailProcess = data
		},
		set_currentReportedProcess(state, data) {
			state.currentReportedProcess = data
		},
		set_sceneGroup(state, data) {
			state.set_sceneGroup = data
		},
	},
}
