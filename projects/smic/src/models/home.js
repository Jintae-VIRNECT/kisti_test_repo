export const currentReportedInformationTabs = [
	{ label: '세부공정', name: 'detailProcess', link: '/process' },
	{ label: '리포트', name: 'report', link: '/reports' },
	{ label: '이슈', name: 'issue', link: '/issue' },
	{ label: '스마트툴', name: 'smartTool', link: '/' },
]

export const tableColSettings = {
	members: [],
	contents: [
		{
			prop: 'name',
			label: '콘텐츠 이름',
		},
		{
			prop: 'detailProcess',
			label: '세부공정 콘텐츠 수',
			width: 130,
		},
		{
			prop: 'auth',
			label: '등록 멤버',
			width: 130,
		},
		{
			prop: 'uploadedAt',
			label: '등록일시',
			width: 150,
		},
		{
			prop: 'contentPublish',
			label: '배포중',
			width: 70,
		},
	],
	// process: [
	// 	{
	// 		prop: 'progressId',
	// 		label: '공정 ID',
	// 	},
	// 	{
	// 		prop: 'progressName',
	// 		label: '공정 이름',
	// 	},
	// 	{ prop: 'reportedAt', label: '보고 시간', width: 150 },
	// 	{
	// 		prop: 'status',
	// 		label: '상태',
	// 	},
	// 	{
	// 		prop: 'auth',
	// 		label: '진행률',
	// 	},
	// 	{
	// 		prop: 'progressPercent',
	// 		label: '작업 수',
	// 	},
	// 	{
	// 		prop: 'workerNum',
	// 		label: '작업자',
	// 	},
	// ],
	detailProcess: [
		{
			prop: 'processName',
			label: '공정 이름',
			width: 300,
		},
		{
			prop: 'detailProcessName',
			label: '세부 공정 이름',
		},
		{
			prop: 'status',
			label: '진행 상태',
			width: 100,
		},
		{
			prop: 'auth',
			label: '작업 담당자',
			width: 130,
		},
		{
			prop: 'reportedAt',
			label: '보고일시',
			width: 150,
		},
	],
	report: [
		{
			prop: 'processName',
			label: '공정이름',
		},
		{
			prop: 'detailProcessName',
			label: '세부 공정 이름',
		},
		{
			prop: 'workName',
			label: '작업 이름',
		},
		{
			prop: 'auth',
			label: '작업 담당자',
			width: 130,
		},
		{
			prop: 'reportedAt',
			label: '보고일시',
			width: 150,
		},
	],
	issue: [
		{
			prop: 'type',
			label: '이슈 유형',
			width: 130,
		},
		{
			prop: 'processName',
			label: '공정이름',
		},
		{
			prop: 'detailProcessName',
			label: '세부 공정 이름',
		},
		{
			prop: 'workName',
			label: '작업 이름',
		},
		{
			prop: 'auth',
			label: '작업 담당자',
			width: 130,
		},
		{
			prop: 'reportedAt',
			label: '보고일시',
			width: 150,
		},
	],
	smartTool: [
		{
			prop: 'processName',
			label: '공정이름',
		},
		{
			prop: 'detailProcessName',
			label: '세부 공정 이름',
		},
		{
			prop: 'workName',
			label: '작업 이름',
		},
		{
			prop: 'jobId',
			label: 'Job ID',
			width: 120,
		},
		{
			prop: 'totalDone',
			label: '채결정보',
			width: 80,
		},
		{
			prop: 'auth',
			label: '작업 담당자',
			width: 130,
		},
		{
			prop: 'reportedAt',
			label: '보고일시',
			width: 150,
		},
	],
}
