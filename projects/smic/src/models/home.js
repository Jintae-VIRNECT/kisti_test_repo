export const currentReportedInformationTabs = [
  {
    label: '세부공정',
    name: 'subProcessAll',
    link: '/process/{processId}/{subProcessId}',
  },
  {
    label: '리포트',
    name: 'report',
    link: '/process/{processId}/{subProcessId}?jobId={jobId}&modal=Report',
  },
  {
    label: '이슈',
    name: 'issue',
    link: '/issue?issueId={issueId}',
  },
  {
    label: '스마트툴',
    name: 'smartTool',
    link: '/process/{processId}/{subProcessId}?jobId={jobId}&modal=SmartTool',
  },
]

export const tableColSettings = {
  members: [],
  contents: [
    {
      prop: 'contentName',
      label: '콘텐츠 이름',
    },
    {
      prop: 'sceneGroupTotal',
      label: '세부공정 콘텐츠 수',
      width: 130,
    },
    {
      prop: 'uploaderName',
      label: '등록 멤버',
      width: 130,
    },
    {
      prop: 'createdDate',
      label: '등록일시',
      width: 150,
    },
    {
      prop: 'status',
      label: '배포상태',
      width: 100,
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
  subProcessAll: [
    {
      prop: 'processName',
      label: '공정 이름',
      width: 200,
    },
    {
      prop: 'name',
      label: '세부공정 이름',
    },
    {
      prop: 'status',
      label: '진행 상태',
      width: 100,
    },
    {
      prop: 'workerUUID',
      label: '작업 담당자',
      width: 160,
    },
    {
      prop: 'reportedDate',
      label: '보고일시',
      width: 150,
    },
  ],
  report: [
    {
      prop: 'processName',
      label: '공정 이름',
      width: 200,
    },
    {
      prop: 'subProcessName',
      label: '세부공정 이름',
      width: 200,
    },
    {
      prop: 'jobName',
      label: '작업 이름',
    },
    {
      prop: 'workerUUID',
      label: '작업 담당자',
      width: 160,
    },
    {
      prop: 'reportedDate',
      label: '보고일시',
      width: 150,
    },
  ],
  issue: [
    {
      prop: 'issueType',
      label: '이슈 유형',
      width: 130,
    },
    {
      prop: 'processName',
      label: '공정 이름',
      width: 200,
    },
    {
      prop: 'subProcessName',
      label: '세부공정 이름',
      width: 200,
    },
    {
      prop: 'jobName',
      label: '작업 이름',
    },
    {
      prop: 'workerUUID',
      label: '작업 담당자',
      width: 160,
    },
    {
      prop: 'reportedDate',
      label: '보고일시',
      width: 150,
    },
  ],
  smartTool: [
    {
      prop: 'processName',
      label: '공정 이름',
      width: 200,
    },
    {
      prop: 'subProcessName',
      label: '세부공정 이름',
      width: 200,
    },
    {
      prop: 'jobName',
      label: '작업 이름',
    },
    {
      prop: 'jobId',
      label: 'Job ID',
      width: 100,
    },
    {
      prop: 'smartToolItems',
      label: '체결정보',
      width: 100,
    },
    {
      prop: 'workerUUID',
      label: '작업 담당자',
      width: 160,
    },
    {
      prop: 'reportedDate',
      label: '보고일시',
      width: 150,
    },
  ],
}
