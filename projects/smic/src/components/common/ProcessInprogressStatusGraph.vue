<template lang="pug">
	el-card
		.process-inprogress-status-graph
			.process-inprogress-status-graph__header
				span 시간별 공정 진행 상태 그래프
			.process-inprogress-status-graph__body
				el-tabs(v-model='activeTab')
					el-tab-pane(
						v-for="(status, index) in tabs.processStatus" 
						:key="index" 
						:label="status.label" 
						:name="status.name"
						@click="onClickToggleTab(status.label)"
					)
			.bar-chart-wrapper
				el-date-picker(v-model='form.data' type='date' placeholder='Pick a day')
				el-time-select(
					:placeholder='form.startTime'
					v-model='form.startTime' 
					:picker-options="{ start: '00:00', step: '01:00', end: '23:00' }"
					@change="checkMinMaxTime"
				)
				el-time-select(
					:placeholder='form.endTime' 
					v-model='form.endTime' 
					:picker-options="{start: '00:00',step: '01:00',end: '23:00',minTime: this.form.startTime == '23:00' ? '22:59' : this.form.startTime}"
					@change="checkMinMaxTime"
				)


				#bar-chart
</template>
<script>
import bb from 'billboard.js'
import processStatus from '@/models/processStatus'
const jsonData = [
	{ time: '0', value: 30 },
	{ time: '1', value: 200 },
	{ time: '2', value: 100 },
	{ time: '3', value: 400 },
	{ time: '4', value: 150 },
	{ time: '5', value: 250 },
	{ time: '6', value: 30 },
	{ time: '7', value: 200 },
	{ time: '8', value: 100 },
	{ time: '9', value: 400 },
	{ time: '10', value: 150 },
	{ time: '11', value: 250 },
	{ time: '12', value: 30 },
	{ time: '13', value: 200 },
	{ time: '14', value: 100 },
	{ time: '15', value: 400 },
	{ time: '16', value: 150 },
	{ time: '17', value: 250 },
	{ time: '18', value: 30 },
	{ time: '19', value: 200 },
	{ time: '20', value: 100 },
	{ time: '21', value: 400 },
	{ time: '22', value: 150 },
	{ time: '23', value: 1 },
]
export default {
	data() {
		return {
			tabs: {
				processStatus,
			},
			activeTab: processStatus[0].name,
			form: {
				data: null,
				startTime: '00:00',
				endTime: '23:00',
			},
			barChart: null,
		}
	},
	mounted() {
		this.initProcessGraph()
	},
	methods: {
		checkMinMaxTime() {
			const startTime = 3 // Number(this.form.startTime.split(':')[0])
			const endTime = Number(this.form.endTime.split(':')[0])
			if (startTime > endTime) this.form.endTime = this.form.startTime
			this.initProcessGraph()
		},
		initProcessGraph() {
			const startTime = Number(this.form.startTime.split(':')[0])
			const endTime = Number(this.form.endTime.split(':')[0])
			this.barChart = bb.generate({
				data: {
					axes: {
						time: 'x',
					},
					json: jsonData,
					color(color, d) {
						console.log('qweqweqweqweqeqwewee')
						console.log('d.x : ', d.x)
						console.log('startTime : ', startTime)
						if (d.x >= startTime && d.x <= endTime) return 'rgba(24, 106, 226)'
						else return 'rgba(24, 106, 226, 0.3)'
					},
					keys: {
						value: ['value'],
					},
					type: 'bar',
				},
				axis: {
					min: {
						y: 0,
					},
					x: {
						type: 'category',
						tick: {
							show: false,
							text: {
								show: true,
							},
							values: [
								'0',
								'1',
								'2',
								'3',
								'4',
								'5',
								'6',
								'7',
								'8',
								'9',
								'10',
								'11',
								'12',
								'13',
								'14',
								'15',
								'16',
								'17',
								'18',
								'19',
								'20',
								'21',
								'22',
								'23',
							],
						},
					},
					y: {
						tick: {
							show: false,
							text: {
								show: true,
							},
						},
					},
				},
				legend: {
					show: false,
				},
				tooltip: {
					format: {
						title: d => {
							return `${d}:00 ~ ${d}:59`
						},
						name: () => '건수',
					},
				},
				grid: {
					y: {
						show: true,
					},
				},
				bindto: '#bar-chart',
			})
			const domains = document.querySelectorAll('path.domain')
			for (let i = 0; i < domains.length; i++) {
				domains[i].style.stroke = 'none'
			}
		},
		onClickToggleTab(label) {
			this.activeTab = label
		},
	},
}
</script>
<style lang="scss">
.process-inprogress-status-graph {
	&__header {
		padding: 22px 30px;
		font-size: 18px;
		font-weight: 500;
		font-stretch: normal;
		font-style: normal;
		line-height: 1.56;
		letter-spacing: normal;
		color: #0d2a58;
		box-shadow: inset 0 -1px 0 0 #eaedf3;
	}

	.bar-chart-wrapper {
		padding: 20px 30px 10px 15px;
	}
}
</style>
